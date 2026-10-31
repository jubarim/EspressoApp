# Firebase Sync

How espresso data is mirrored to Cloud Firestore and back. This is the reference document for the
feature; see [firebase-sync-plan.md](firebase-sync-plan.md) for the original design and decisions.

The app stays local-first. Room is the single source of truth, the UI only reads from Room, and
Firestore is a mirror that follows the signed-in Google account across devices. Guests never sync.

---

## 1. Setup

1. **Create the database.** Firebase console, Firestore Database, create a database in **Native
   mode**, ID `(default)`, in a location close to you. The location is permanent.
2. **Publish the rules.**

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

3. **No other Firebase products are needed.** No Cloud Functions, no Storage (images are URL
   strings), no App Check, no Realtime Database.
4. **Dependency.** `firebase-firestore` is declared in `gradle/libs.versions.toml` and versioned by
   the existing Firebase BoM.
5. **Debug logging.** `SyncModule` calls `FirebaseFirestore.setLoggingEnabled(BuildConfig.DEBUG)`,
   so SDK logs appear in debug builds under the `firebase` tags. Application logs use the
   `EspressoSync` tag, prefixed with `jm_` by `AppLog`.

Filter the app logs with:

```
adb logcat -s jm_EspressoSync
```

---

## 2. Remote model

One subtree per account, one collection per entity, one document per row:

```
users/{uid}/roasters/{id}
users/{uid}/coffee_beans/{id}
users/{uid}/grinders/{id}
users/{uid}/espresso_machines/{id}
users/{uid}/filter_baskets/{id}
users/{uid}/shot_logs/{id}
```

The document id is the record UUID, so every push is an idempotent `set()`.

Field names are camelCase and mirror the Room entity, plus:

| Field | Type | Purpose |
|---|---|---|
| `isDeleted` | boolean | Soft delete tombstone |
| `createdAt` | number (Long, epoch ms) | Same as the local column |
| `updatedAt` | number (Long, epoch ms) | Last write wins key |
| `docVersion` | number (Long) | Remote shape version, currently `1` |

Numbers are written as `Long` so the pull side never has to branch on `Int` versus `Long`. Nulls are
written explicitly.

Collections are declared in dependency order in `SyncCollection`, which is also the order used to
apply and push. `shot_logs` references the other tables with `ON DELETE RESTRICT`, so a shot must be
applied after its equipment.

---

## 3. Sync lifecycle

`SyncEngine` is a singleton started from `EspressoAppApplication.onCreate()`. It derives the session
from the auth state plus the onboarding flag:

| Session | Action |
|---|---|
| Signed in | Claim the local data for the uid, merge, then attach listeners |
| Guest after an account | Wipe Room and clear the owner so the guest sees nothing from the previous account |
| Welcome screen (after sign-out) | Nothing, so signing out keeps the data and signing back in is cheap |

The owning uid is stored in DataStore as `data_owner_uid`. If a **different** uid signs in, Room is
wiped before the new account is pulled in.

Sign-out resets the onboarding flag **before** calling `authRepository.signOut()`, so the engine sees
the welcome screen rather than a guest session and does not wipe.

`SyncEngine.status` exposes `Idle`, `Syncing` and `Error` to the Settings screen.

---

## 4. Push, local to cloud

Every repository write goes to Room first and then queues a remote write:

- `insert` and `update` push the entity
- `delete` reads the row back including tombstones and pushes it with `isDeleted = true`
- a failed Room write never pushes, because the dispatch is inside the same `runCatching`

`FirestoreRemoteWriteDispatcher` runs the push on an application scope. This is deliberate: a
Firestore batch commit does not complete until the backend acknowledges it, so awaiting it inside the
repository would freeze the UI action while offline. Firestore buffers the write and flushes it on
reconnect.

Writes are skipped entirely when there is no signed-in user.

`FirestoreSyncTarget` writes in batches of 500, the Firestore limit, sorted by collection order.

---

## 5. Pull, cloud to Room

`SyncEngine` attaches one snapshot listener per collection through `FirestoreRemoteSource`, which
exposes the changes as a `Flow<RemoteChange>`.

`SyncApplier` merges them:

- **Last write wins on `updatedAt`.** A remote document is applied only when it is **strictly newer**
  than the local row. Equal timestamps are the echo of our own push and are skipped silently.
- **Tombstones** travel through the same path, so a delete on one device hides the row on the others.
- **Remote hard deletions** are ignored, because the app never deletes remotely and a console delete
  should not erase local data.
- **Foreign keys.** Documents are buffered and applied in dependency order. A document whose parents
  have not arrived yet stays pending and is retried on the next change. This is what makes a shot
  arriving before its equipment harmless.
- **Malformed documents** are skipped when a required field is missing or has the wrong type.

All applications go through one `Mutex`, since six listeners deliver concurrently.

---

## 6. Merge and reconciliation

On every session start, and on first sign-in, `SyncMergerImpl` reads the cloud once per collection
with `RemoteSource.fetchAll`, compares it with Room, and pushes the records the cloud is missing or
holds an older copy of. Remote-newer records stay for the listeners.

This is what uploads a database that existed before sync, and it covers the window where the process
could die between the Room write and the Firestore enqueue.

`last_sync_at` is written after every successful merge, including the "nothing to push" case, and is
shown in Settings. Pushes are idempotent, so an interrupted merge simply resumes next time.

The JSON backup import writes through DAOs rather than repositories, so imported data reaches the
cloud on the next app start through this merge.

---

## 7. Settings

Under **Account** there is a **Sync** section:

- Status: `Everything is synced`, `Syncing…`, `Sync error: …`, or the guest prompt
- `Last synced: …`, formatted with `DateTime.kt` in the device timezone
- **Sync now**, which restarts the session (merge plus listeners) and is disabled while syncing

**Sign-out** waits up to 5 seconds for queued Firestore writes. If they do not flush, the sign-out is
blocked and a message asks the user to check the connection, so the last offline edits are not
silently dropped. With nothing pending, `waitForPendingWrites` returns immediately.

---

## 8. Debug health check

In debug builds, **Settings, Debug, Run sync health check** writes a tombstoned roaster
(`sync-health-check`) under the signed-in user, waits for the server to acknowledge it, then waits
for it to arrive through a listener. One action proves the project configuration, the auth token, the
document path and the security rules.

The document is a tombstone, so it stays invisible if the pull ever applies it.

---

## 9. Known limitations

- **Device clocks.** Conflicts use the device `updatedAt`. A device with a wrong clock wins ties it
  should lose. Ties are skipped, so editing a document in the Firebase console also requires bumping
  `updatedAt`.
- **Whole-document LWW.** Two devices editing different fields of the same record keep only the later
  write.
- **Offline merge is slow.** `fetchAll` uses the default source, which retries the server before
  falling back to the local cache. A manual sync while offline can take tens of seconds per
  collection even though it succeeds.
- **The `Error` status is rare offline**, because the cache usually covers the read. It shows up on
  rules failures and other errors.
- **Foreground only.** There is no WorkManager job. Listeners live as long as the process does, and
  the Firestore queue resumes on the next launch.
- **Backup import is not pushed immediately.** See section 6.
- **Images are URL strings.** No Firebase Storage.

---

## 10. Code map

| Area | Files |
|---|---|
| Remote transport | `data/remote/SyncTarget.kt`, `FirestoreSyncTarget.kt`, `RemoteSource.kt`, `FirestoreRemoteSource.kt`, `FirestorePaths.kt` |
| Remote model | `data/remote/SyncCollection.kt`, `RemoteDocument.kt` |
| Mapping | `data/remote/SyncMappers.kt` (entity to document), `data/remote/RemoteEntityMappers.kt` (document to entity) |
| Push | `data/sync/RemoteWriteDispatcher.kt`, `FirestoreRemoteWriteDispatcher.kt`, `data/di/CoroutinesModule.kt` |
| Pull | `data/sync/SyncApplier.kt` |
| Merge | `data/sync/SyncMerger.kt`, `SyncMergerImpl.kt` |
| Lifecycle | `data/sync/SyncEngine.kt`, `SyncStatus.kt`, `EspressoAppApplication.kt` |
| Diagnostics | `data/sync/SyncHealthCheck.kt`, `logging/AppLog.kt` |
| Wiring | `data/di/SyncModule.kt`, `data/di/DataModule.kt` (DAOs) |
| Local support | every DAO has `getAllIncludingDeleted`, `getByIdIncludingDeleted` and `upsert`; `PreferencesRepository` stores `data_owner_uid` and `last_sync_at` |
| UI | `ui/settings/SettingsViewModel.kt`, `SettingsScreen.kt`, `SettingsUiState.kt` |

---

## 11. Tests

```
./gradlew :app:testDebugUnitTest
```

Covered: both mapper directions, the write dispatcher, the applier (LWW, tombstones, foreign-key
retries), the merger, the engine lifecycle (claim, wipe, guest, sync now) and the collection order.
`SyncHealthCheckTest` covers the debug tool with fakes.

---

## 12. Related documents

- [authentication.md](authentication.md) for Google Sign-In setup
- [firebase-sync-plan.md](firebase-sync-plan.md) for the design record and the decisions taken
