# Firebase Sync Plan

Status: implemented. See [firebase-sync.md](firebase-sync.md) for the as-built reference.
Date: 2026-10-23.
Owner: João Assis.

Two-way sync of the local Room database to Cloud Firestore, per signed-in user,
keeping Room as the single source of truth.

---

## 1. Context

Current state of the app before this plan:

- Local-first Room database with six tables: `roasters`, `coffee_beans`, `grinders`,
  `espresso_machines`, `filter_baskets`, `shot_logs`.
- Soft delete everywhere (`is_deleted`), and every DAO filters `is_deleted = 0`,
  including `getAllEntities`, so tombstones are currently unreadable.
- Domain models carry no sync metadata (no `isDeleted`, no version, no etag).
- Repositories: `getAll(): Flow<List<T>>` (active only), `getById`, `insert`, `update`,
  `delete` (soft). IDs are random UUID v4 generated in the ViewModel, `updated_at` is
  `System.currentTimeMillis()`.
- Firebase Auth is already wired (Google Sign-In) plus a guest mode where there is no
  Firebase user at all. `AuthUser(uid, displayName, email)`.
- A manual JSON export/import exists in Settings (`BackupDto`, `schemaVersion = 8`), which
  is the closest existing analogue of a sync payload.
- No Firestore or Storage dependency yet; only `firebase-auth` plus the BOM.
- Images are URL strings stored in `image_uri` (the forms take a Logo URL / Image URL),
  not uploaded files.
- `signOut()` does not clear local Room data.
- A DEBUG-only seed inserts fixed UUIDs on database creation with a fixed
  `SEED_TS = 1704067200000` (2024-01-01).

---

## 2. Locked decisions

| # | Decision | Choice |
|---|---|---|
| 1 | Goal | Two-way sync across devices |
| 2 | Local architecture | Room stays the source of truth, Firestore is a mirror |
| 3 | Sync scope | All six entity types |
| 4 | Guest to sign-in | Merge local data into the account |
| 5 | Identity change | Wipe local data on any identity change (see section 8) |
| 6 | Conflicts | Last Write Wins on device `updated_at` only |
| 7 | Transport | Firestore offline write queue plus snapshot listeners |
| 8 | Remote model | Firestore per-user subcollections `users/{uid}/...` |
| 9 | Seed data | No special handling, keep the old 2024 timestamp |
| 10 | Images | URL strings only, no Firebase Storage |
| 11 | Manual backup | Keep the JSON export/import |
| 12 | Visibility | Sync status in Settings |
| 13 | Sign-out with pending writes | Block sign-out until the queue flushes |
| 14 | Opt-in | Automatic for signed-in users, guests never sync |
| 15 | Background | Foreground only, no WorkManager |
| 16 | App Check | Skipped for now |

---

## 3. Non-goals for this version

- No rewrite to Firestore-only storage, Room stays.
- No background or scheduled sync (no WorkManager).
- No App Check.
- No Firebase Storage or photo uploads.
- No sharing, publishing, or cross-user reads.
- No field-level merge, conflicts resolve per record.
- No removal of the JSON export/import.
- No server-side clock ordering, `updated_at` from the device is authoritative.

---

## 4. Architecture

```
                 write path
 UI / ViewModel ─────────────▶ Repository impl
                                    │
                                    ├─▶ Room (via DAO)          <- source of truth
                                    └─▶ SyncTarget.enqueue()    <- only when signed in
                                             │
                                             ▼
                                  Firestore users/{uid}/...

                 read path
 Firestore snapshot listener ─▶ SyncApplier (ordered, LWW) ─▶ Room ─▶ UI (Flows)
```

Rules that keep this simple:

- Every write goes to Room first. Room remains the only thing the UI observes.
- The remote write is fire-and-forget. The Firestore SDK persists its own write queue and
  retries on reconnect, so no outbox table is needed.
- Merges are idempotent and order independent because both directions use the same LWW rule
  on `updated_at`.

New packages:

```
data/remote/     Firestore data sources, one per collection
data/sync/       SyncEngine, SyncApplier, SyncStatus
domain/repository/SyncRepository.kt   interface exposed to the UI
```

Interfaces so this stays testable without Firebase:

- `SyncTarget` (push one record, push many, await pending writes)
- `RemoteSource` (snapshot stream per collection)
- `SyncEngine` depends on both, so a fake implementation can drive unit tests.

---

## 5. Remote data model

Path per collection, scoped to the account:

```
users/{uid}/roasters/{id}
users/{uid}/coffee_beans/{id}
users/{uid}/grinders/{id}
users/{uid}/espresso_machines/{id}
users/{uid}/filter_baskets/{id}
users/{uid}/shot_logs/{id}
```

Document shape mirrors the Room entity, camelCase, plus remote-only metadata:

```json
{
  "id": "...",
  "isDeleted": false,
  "createdAt": 1704067200000,
  "updatedAt": 1750000000000,
  "docVersion": 1,
  "schemaVersion": 8
}
```

- `docVersion` lets a future migration detect old shapes.
- `schemaVersion` mirrors `BackupDto.schemaVersion`, so the JSON backup and the remote
  document can share mapping code where useful.
- The document ID is the record UUID, so a push is a `set()` and is idempotent.
- Whole document replace per record. Two devices editing different fields of the same
  record means the later `updatedAt` wins entirely. This is accepted.

Referential order matters, both directions:

```
roasters, coffee_beans, grinders, espresso_machines, filter_baskets, shot_logs
```

`shot_logs` has `ON DELETE RESTRICT` foreign keys to the other five tables.

---

## 6. Local schema changes

No new columns are required, so no Room version bump for sync itself.

Changes needed:

1. DAO reads that include tombstones (sync only, never the UI):
   - e.g. `getAllIncludingDeleted(): List<T>` and `getAllIncludingDeletedFlow()`.
   - Keep existing filtered queries for the UI and leave the JSON backup behavior as is.
2. DAO upserts for the applier, using `@Upsert` (Room 2.5+) rather than `INSERT OR REPLACE`.
   Why: `INSERT OR REPLACE` deletes the conflicting row first, and with
   `ON DELETE RESTRICT` that delete fails while children reference it. `@Upsert` performs a
   real `ON CONFLICT DO UPDATE`, so updating a parent while its shots exist is safe.
3. DataStore keys, added to `PreferencesRepository`:
   - `data_owner_uid: String?` which account the local data belongs to
   - `last_sync_at: Long?` for the Settings status row
4. Wipe helper: a single `AppDatabase.clearAllTables()` or explicit deletes in reverse FK
   order, exposed through a repository, used only by the identity logic.

Note on `fallbackToDestructiveMigration(true)`: a destructive migration would wipe local data
but Firestore can restore it, which actually makes sync a safety net. Worth keeping in mind
when reading bug reports.

---

## 7. Write path (local to cloud)

In each repository implementation (`insert`, `update`, `delete`):

1. Write to Room.
2. If a uid is present, enqueue the remote write (`set()` on `users/{uid}/{collection}/{id}`).
3. If there is no uid (guest), do nothing. Data stays local and is merged at sign-in.

Bulk operations (initial merge, import) use batched writes, 500 documents per batch, which
is the Firestore limit.

Push order for a batch follows the dependency order in section 5, so a shot never reaches
the cloud before the equipment it points to (not strictly required remotely, but keeps the
cloud consistent for anything that reads it directly).

---

## 8. Identity and lifecycle

`data_owner_uid` decides when local data must be wiped. The rule is "wipe on any identity
change", but the wipe is deferred so that re-signing into the same account needs no
re-download.

States:

| Event | Local data | `data_owner_uid` | Notes |
|---|---|---|---|
| App start, guest, owner null | keep | stays null | first launch, nothing to wipe |
| Sign in, owner null | keep, then merge/upload | set to uid | first sign-in, uploads existing DB |
| Sign in, owner == uid | keep, resume sync | unchanged | cheap re-sign-in, no re-download |
| Sign in, owner != uid | wipe, then pull | set to uid | different account on the same device |
| Sign out | keep | unchanged | welcome screen shows no data |
| Continue as guest after being signed in | wipe | clear | a guest must never see the previous account's data |

Important: the wipe happens when the next identity is established, not at sign-out, so a
quick sign-out and sign-in with the same account is free.

Sign-out flow:

1. `awaitPendingWrites()` with a timeout.
2. On success or timeout, sign out. On timeout, surface a message so the user knows.
3. Detach listeners.
4. Do not wipe. Keep `data_owner_uid`.

---

## 9. Read path (cloud to local)

1. On sign-in, attach one snapshot listener per collection.
2. The first snapshot is the full collection, later ones are deltas.
3. Apply each incoming document with LWW:
   - `remote.updatedAt > local.updatedAt` or no local row: upsert.
   - `remote.updatedAt <= local.updatedAt`: skip. The local state is newer and either already
     pushed or about to be.
4. Tombstones travel with `isDeleted = true` and are applied as a normal upsert, so a delete
   on one device propagates.
5. Detach listeners on sign-out.

### Ordering problem and fix

Listeners are per collection and Firestore does not guarantee that a shot document arrives
after the basket it references. Inserting a `shot_logs` row whose `basket_id` is not present
fails because Room enforces foreign keys.

Fix: a `SyncApplier` that buffers incoming documents and flushes them in dependency order
inside one Room transaction. If a document still fails on a foreign key (parent not received
yet), it is re-queued for the next flush. This is self-healing and avoids pragma tricks.

Alternative considered: `PRAGMA defer_foreign_keys = 1` inside the transaction, which defers
checks to commit. It works but is less obvious to maintain, so it is the fallback if the
ordered applier proves awkward.

---

## 10. First sign-in merge

This is the path that must not lose the existing database.

1. Ensure a uid. If the owner is different, the wipe in section 8 runs first.
2. Attach listeners. The initial snapshot is the full cloud data for the account.
3. Reconcile by record id with LWW on `updated_at`:
   - only local: push
   - only remote: upsert into Room
   - both: higher `updated_at` wins, push if local wins
4. Push the local-only and local-newer records in dependency order, batched.

Why this is safe:

- LWW makes the result independent of the order in which the two sides are merged.
- Every remote write is a `set()` with a deterministic document id, so it is idempotent.
- The local side is never wiped on first sign-in (the identity did not change), so a crash
  mid-merge can only leave the merge half done, never data lost.

Cost note: the first sign-in on a device does a full read plus a full write. On the Spark
free tier this is fine for personal data sizes. Subsequent launches only read deltas.

---

## 11. Status and errors

Settings gets a new row under Account:

- the signed-in email
- a state: Idle, Syncing, or Error
- last synced time (`last_sync_at`, device clock)
- a manual "Sync now" action that re-attaches listeners

No pending-write count is shown. The Firestore SDK does not expose a cheap count, and the
state plus last synced time is enough for the user to understand what is happening.

Errors to surface: `PERMISSION_DENIED`, `UNAVAILABLE`, quota exceeded, and the sign-out
flush timeout. Errors never block the UI, since Room already holds the data.

---

## 12. Security rules

```js
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{uid}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == uid;
    }
  }
}
```

- No cross-user reads, so no sharing in this version.
- The project has no `firebase.json`, so rules and database creation are done in the Firebase
  console, or a `firebase.json` can be added to deploy rules from the repo.

---

## 13. Dependencies and configuration

- Add `firebase-firestore` to `gradle/libs.versions.toml` and `app/build.gradle.kts`
  (version comes from the existing Firebase BoM `35.1.0`).
- No new Android permissions, `INTERNET` already exists.
- Enable Cloud Firestore in the Firebase console for `juba-espressoapp` (production mode, then
  paste the rules above).
- Firestore offline persistence is on by default on Android.
- `google-services.json` is already required by the build, so no new setup step for developers.

---

## 14. Testing

Make the engine testable without Firebase:

- `SyncTarget` and `RemoteSource` interfaces with fakes.
- Unit tests:
  - LWW merge for newer, older, and equal `updated_at`, and for tombstones
  - dependency-ordered apply, including a shot arriving before its basket
  - push only when signed in, no push as a guest
  - identity table in section 8, every row
  - sign-out flush timeout path
- Robolectric test with an in-memory Room database plus a fake remote: first sign-in merge
  with data on both sides.
- Manual: two emulators on the same account, edit offline on both, reconnect, confirm LWW.

---

## 15. Rollout phases

Each phase should compile and be shippable on its own.

1. Foundation
   - Firestore dependency, console setup, security rules
   - `SyncTarget` / `RemoteSource` interfaces plus the Firestore implementation
   - tombstone DAO reads and `@Upsert` DAO methods
   - `data_owner_uid` and `last_sync_at` in DataStore
2. Push
   - repository write paths enqueue remote writes when signed in
   - batched push helper in dependency order
3. Pull
   - snapshot listeners plus the ordered `SyncApplier`
   - LWW comparison per record
4. Lifecycle
   - identity table in section 8, wipe helper
   - first sign-in merge
5. UX
   - Settings status row and manual sync
   - sign-out flush with timeout and message
6. Hardening
   - error surfacing, quota handling, tests, docs
   - update `CLAUDE.md` sections 8 and the roadmap, and `README.md` if needed

---

## 16. Risks and mitigations

| Risk | Mitigation |
|---|---|
| Shot arrives before its equipment on pull, foreign key fails | Ordered applier with re-queue (section 9) |
| `INSERT OR REPLACE` breaks FK RESTRICT on parents | Use `@Upsert` instead (section 6) |
| Device clock skew makes LWW wrong | Accepted. `updated_at` only, per decision 6 |
| Losing the existing local database on first sign-in | Local is never wiped on first sign-in, merge is additive and idempotent |
| Guest sees the previous account's data | Wipe on identity change (section 8) |
| Pending offline edits lost on sign-out | Block sign-out until the queue flushes (section 11) |
| Deleted parent still referenced by a shot on another device | Referential integrity is not enforced remotely. The list already tolerates a null basket name |
| Spark free tier quota (50k reads, 20k writes per day) | Full read and write only on first sign-in, deltas after that |
| Two devices editing different fields of one record | Whole record LWW, the later `updatedAt` wins. Accepted |
| JSON backup does not include tombstones | Known gap. Restoring a backup will not delete records deleted since the export. Could be addressed later |
| Firestore document 1 MiB limit | `notes` are free text but small. Not a practical concern |

---

## 17. Files expected to change

- `gradle/libs.versions.toml`, `app/build.gradle.kts`
- `data/remote/*` (new, one data source per collection)
- `data/sync/*` (new: `SyncEngine`, `SyncApplier`, `SyncStatus`)
- `domain/repository/SyncRepository.kt` (new)
- `data/local/dao/*Dao.kt` (tombstone reads, `@Upsert` methods)
- `data/repository/*RepositoryImpl.kt` (enqueue remote writes)
- `data/di/SyncModule.kt` (new) or `DataModule.kt`
- `domain/repository/PreferencesRepository.kt` plus the DataStore implementation
- `ui/main/AppViewModel.kt` (identity and wipe lifecycle)
- `ui/settings/SettingsViewModel.kt`, `SettingsScreen.kt` (status row, sign-out flush)
- `CLAUDE.md`, optionally `README.md` and `docs/`

---

## 18. Open questions for later

- Whether the identity wipe should also run when the app is opened by a guest after a
  destructive migration.
- Whether to expose a per-record sync state in the UI at all, or keep it to the Settings row.
- Whether to add a `firebase.json` so rules are versioned and deployed from the repo.
  Deferred to the implementation phase.
- Whether the JSON backup should include tombstones once sync exists.

---

## 19. Deliverables at completion

This document is the design record and stays as written. When the implementation is
finished, the following must also happen:

1. Write `docs/firebase-sync.md` describing what was actually built: final architecture,
   remote document model, identity and wipe rules, security rules, and the Firebase setup
   and deploy steps. This becomes the reference document for the feature.
2. Reference it from `README.md`, next to the existing `docs/authentication.md` link in the
   Setup section, and mention sync in the Features and Tech stack tables.
3. Update `CLAUDE.md` section 8 and the roadmap to match the shipped behavior.
4. Commit the document as part of the sync work, not as a separate follow-up.

