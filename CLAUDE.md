# CLAUDE.md — Espresso App

Project-level instructions and context for AI-assisted development sessions.

---

## 0. Main instructions

- The app has a design system folder (package org.juba.espressoapp.designsystem). When creating new UI, always verify it there is some
reusable component there. If necessary, new ones can be added so they can be reused later.
- When creating new composables files/functions, create at least one preview for the important ones, specially the ones that can be reused
or are screens.


---

## 1. Project Overview

**App name:** The Digital Barista
**Package:** `org.juba.espressoapp`

A local-first Android app for logging espresso shots. The core mission is helping the user
reproduce their best shots ("God Shot") by tracking every variable that influences the result:
beans, grinder, machine, basket, dose, yield, and time.

Target user: the home or prosumer barista who wants data-driven repeatability.

---

## 2. Tech Stack

All versions are pinned in `gradle/libs.versions.toml`.

| Component | Version / Detail |
|---|---|
| Kotlin | 2.3.10 (K2 compiler enabled) |
| AGP | 9.0.1 |
| KSP | 2.3.5 |
| Min SDK | 26 |
| Target SDK | 36 |
| Compose BOM | 2026.02.01 |
| Material 3 | 1.5.0-alpha14 (override — required for `HorizontalFloatingToolbar`) |
| NavigationSuiteScaffold | `material3-adaptive-navigation-suite` |
| Room | 2.8.4 — local DB, offline-first |
| Hilt | 2.59.2 — DI |
| Navigation Compose | 2.9.7 |
| Hilt Navigation Compose | 1.3.0 |
| Coil | 3.1.0 — image loading (`coil-compose` + `coil-network-okhttp`) |
| Architecture | MVVM (prefer MVI for complex screens) |

Dynamic Color is wired in `Theme.kt` (Android 12+ / API 31+, guarded by `Build.VERSION.SDK_INT >= S`).

`NavigationSuiteScaffold` is wired in `MainActivity.kt` with the real 4-tab `AppDestination` enum (Shots / Coffee / Gear / Settings).

---

## 3. Architecture

### Layer rules

```
presentation/   — Composables, ViewModels, UiState
domain/         — Entities, use cases, repository interfaces
data/           — Room entities, DAOs, repository implementations, mappers
```

- Repository **interfaces** live in `domain/`. Implementations live in `data/`.
- Each layer boundary uses explicit **Mapper** classes — never pass Room entities to ViewModels.
  - `RoomEntity ↔ DomainModel` (data layer mapper)
  - `DomainModel ↔ UiState` (presentation layer mapper)
- All repository calls return `Result<T>` — never throw from a repo.
- A future `:core:designsystem` module will hold reusable M3 components; build it when the second
  screen reuses a component from the first.

### Module structure (target)

```
:app
:core:designsystem       (reusable M3 components, shared theme)
:feature:shotlog
:feature:equipment
:feature:settings
```

Start with everything in `:app`, extract modules when boundaries become clear.

---

## 4. Domain Entities & DB Schema

All tables use:
- UUID v4 primary keys stored as `TEXT`
- `created_at` and `updated_at` as Unix epoch milliseconds (`INTEGER`)
- `is_deleted` `INTEGER` (0/1) for soft delete — never hard-delete records that have been
  referenced in a shot log

### Tables

#### `roasters`
| Column | Type | Notes |
|---|---|---|
| `id` | TEXT PK | UUID v4 |
| `name` | TEXT NOT NULL | |
| `country` | TEXT | |
| `website` | TEXT | |
| `image_uri` | TEXT | Optional local URI |
| `notes` | TEXT | |
| `is_deleted` | INTEGER | 0 / 1, default 0 |
| `created_at` | INTEGER | Unix ms |
| `updated_at` | INTEGER | Unix ms |

#### `coffee_beans`
| Column | Type | Notes |
|---|---|---|
| `id` | TEXT PK | UUID v4 |
| `roaster_id` | TEXT FK → roasters.id | RESTRICT on delete when referenced in shots |
| `name` | TEXT NOT NULL | |
| `origin` | TEXT | Single-origin or blend description |
| `process` | TEXT | Washed / Natural / Honey / etc. |
| `roast_level` | TEXT | Light / Medium / Dark / etc. |
| `roast_date` | INTEGER | Unix ms, nullable |
| `image_uri` | TEXT | Optional local URI |
| `notes` | TEXT | |
| `is_deleted` | INTEGER | 0 / 1, default 0 |
| `created_at` | INTEGER | Unix ms |
| `updated_at` | INTEGER | Unix ms |

#### `grinders`
| Column | Type | Notes |
|---|---|---|
| `id` | TEXT PK | UUID v4 |
| `brand` | TEXT NOT NULL | e.g. Niche, Mahlkönig, Eureka |
| `model` | TEXT NOT NULL | e.g. Zero, EK43, Mignon |
| `burr_type` | TEXT | Flat / Conical |
| `burr_size_mm` | REAL | nullable |
| `image_uri` | TEXT | Optional local URI |
| `notes` | TEXT | |
| `is_deleted` | INTEGER | 0 / 1, default 0 |
| `created_at` | INTEGER | Unix ms |
| `updated_at` | INTEGER | Unix ms |

#### `espresso_machines`
| Column | Type | Notes |
|---|---|---|
| `id` | TEXT PK | UUID v4 |
| `brand` | TEXT NOT NULL | e.g. ECM, La Marzocco, Strietman |
| `model` | TEXT NOT NULL | e.g. Synchronika, Linea Mini, CT2 |
| `boiler_type` | TEXT | Single / HX / Dual |
| `has_pressure_gauge` | INTEGER | 0 / 1 |
| `image_uri` | TEXT | Optional local URI |
| `notes` | TEXT | |
| `is_deleted` | INTEGER | 0 / 1, default 0 |
| `created_at` | INTEGER | Unix ms |
| `updated_at` | INTEGER | Unix ms |

#### `filter_baskets`
| Column | Type | Notes |
|---|---|---|
| `id` | TEXT PK | UUID v4 |
| `brand` | TEXT NOT NULL | e.g. IMS, VST, Decent |
| `model` | TEXT | |
| `size_grams` | REAL | Rated dose size |
| `type` | TEXT | Ridged / Ridgeless / Precision |
| `image_uri` | TEXT | Optional local URI |
| `notes` | TEXT | |
| `is_deleted` | INTEGER | 0 / 1, default 0 |
| `created_at` | INTEGER | Unix ms |
| `updated_at` | INTEGER | Unix ms |

#### `shot_logs` ← central fact table
| Column | Type | Notes |
|---|---|---|
| `id` | TEXT PK | UUID v4 |
| `coffee_bean_id` | TEXT FK → coffee_beans.id | NOT NULL |
| `grinder_id` | TEXT FK → grinders.id | NOT NULL |
| `machine_id` | TEXT FK → espresso_machines.id | NOT NULL |
| `basket_id` | TEXT FK → filter_baskets.id | NOT NULL |
| `grind_setting` | TEXT | Free text (e.g. "3.2", "notch 14") |
| `dose_grams` | REAL NOT NULL | Input weight |
| `yield_grams` | REAL NOT NULL | Output weight |
| `extraction_time_seconds` | INTEGER | Manual entry — not in source CSV |
| `brew_temperature_celsius` | REAL | nullable |
| `pre_infusion_seconds` | INTEGER | nullable |
| `rating` | INTEGER | 1–5, nullable |
| `notes` | TEXT | |
| `shot_at` | INTEGER | Unix ms, when the shot was pulled |
| `is_deleted` | INTEGER | 0 / 1, default 0 |
| `created_at` | INTEGER | Unix ms |
| `updated_at` | INTEGER | Unix ms |

**Ratio** = `yield_grams / dose_grams` — always computed at read time, never stored.

---

## 5. Entity Relationships

```
roasters ──< coffee_beans ──< shot_logs >── grinders
                                    │
                                    ├──── espresso_machines
                                    └──── filter_baskets
```

- `roasters` 1:N `coffee_beans`
- `shot_logs` N:1 `coffee_beans`, `grinders`, `espresso_machines`, `filter_baskets`
- FK delete rule: RESTRICT — prevent deletion of equipment or beans that appear in any shot log.
  Use soft delete (`is_deleted = 1`) instead of hard delete for referenced rows.

---

## 6. UI / UX Requirements

### Navigation
`NavigationSuiteScaffold` is wired in `MainActivity` with the real 4-tab `AppDestination` enum.
Each tab's label comes from a `@param:StringRes` resource (see Section 11).

| Tab | Label | Content | Icon |
|---|---|---|---|
| 0 | Shots | Shot log list | `Icons.AutoMirrored.Filled.List` |
| 1 | Coffee | Roasters, Coffee Beans | `Icons.Default.Favorite` ← placeholder |
| 2 | Gear | Grinders, Espresso Machines, Filter Baskets | `Icons.Default.Build` |
| 3 | Settings | App preferences | `Icons.Default.Settings` |

> Coffee tab icon uses `Icons.Default.Favorite` as a placeholder.
> Replace with a proper bean/cafe icon once `material-icons-extended` is added.

### Screens

**Shots (tab 0)**
- List of past shot logs, default sort `shot_at` DESC.
- Empty state guides the user to set up gear first (via the Gear tab) before logging a shot.
- FAB to add a new shot (only enabled once all 5 required equipment entries exist).

**Shot Detail / Add Shot**
- Form to log a new shot or view/edit an existing one.
- Requires selecting one entry from each: Coffee Beans, Grinder, Machine, Basket.

**Coffee (tab 1)**
- Categorized list screen with one row per category:
  1. Roasters
  2. Coffee Beans
- Each row shows the count of active entries and navigates to a sub-list screen.

**Gear (tab 2)**
- Categorized list screen with one row per category:
  1. Grinders
  2. Espresso Machines
  3. Filter Baskets
- Same list → detail/add/edit pattern as Coffee.

**Settings (tab 2)**
- Placeholder screen for now. Future: units, locale, theme override.

**Splash screen**
- Implemented in Compose (not the legacy resource-based `SplashScreen` API).
- Shown on cold start only.

### Theme
- Light / Dark + Dynamic Color already wired — do not replace `Theme.kt`.
- Placeholder colors in `Theme.kt` (`Purple80`, etc.) should be replaced with an espresso-inspired
  palette when the design is finalised.
- Use M3 Expressive style: large display typography for key metrics (ratio, yield), rounded shapes
  (`RoundedCornerShape(16.dp)` or higher), generous padding.

### Locale / Decimal input
- Normalize comma → dot when parsing decimal user input (e.g. `"18,5"` → `18.5`).
- Use `Locale.US` for all internal number formatting; respect the device locale only for display.

---

## 7. Implementation Roadmap

### ✅ Phase 0 — Navigation shell
Real 4-tab navigation (Shots / Coffee / Gear / Settings) is live in `MainActivity.kt`.
- `AppDestination` enum uses `@param:StringRes labelRes` for tab labels.
- Each tab has its own `Scaffold` + `TopAppBar` with `contentWindowInsets = WindowInsets(0,0,0,0)`.
- `CoffeeScreen` and `GearScreen` show categorized list rows (counts still hardcoded to 0).
- All UI strings are in `res/values/strings.xml` — use `stringResource()` everywhere.

### ✅ Phase 1 — Roaster module (vertical slice)
Full CRUD vertical slice is complete and establishes patterns for all subsequent modules:
- **Data:** `RoasterEntity`, `RoasterDao`, `AppDatabase` (v1), `RoasterMapper`, `RoasterRepositoryImpl`, `DataModule`
- **Domain:** `Roaster`, `RoasterRepository`
- **UI:** `RoasterListScreen` (FAB, round avatar with initial fallback), `RoasterDetailScreen` (M3 Expressive `HorizontalFloatingToolbar`, full-width logo header), `RoasterFormScreen` (name / country / website / logo URL / notes)
- **Design system:** `EspressoTextField`, `EmptyStateContent` in `ui/designsystem/`
- **Navigation:** `CoffeeScreen` is a `NavHost` with routes for list → detail → form
- **Logo:** URL text field; loaded with Coil 3 (`AsyncImage`); INTERNET permission added
- **Tests:** `RoasterMapperTest`, `RoasterRepositoryTest` (Robolectric + in-memory Room)

### Phase 2 — Remaining equipment entities ← next
Follow the same vertical slice pattern for:
1. **Coffee Beans** (FK → roasters, additional fields: origin, process, roast\_level, roast\_date)
2. **Grinders** (brand + model required)
3. **Espresso Machines** (brand + model required)
4. **Filter Baskets** (brand required)

Each new entity needs a Room DB version bump and migration script.

### Phase 3 — Shot Log module
The most complex module. Depends on all equipment entities being queryable.
Includes ratio calculation, extraction time input, and the main home screen list.

### Future — Firebase remote data source
The repository interface in domain must be written without any Room-specific assumptions so that
a `FirebaseRepository` implementation can be added alongside the Room one later.

### Future — Global snackbar with undo for destructive actions
Replace confirmation dialogs on delete with a snackbar + Undo action (leveraging soft-delete:
undo = set `is_deleted = 0`). Requires a shared `SnackbarHostState` hoisted to the app root,
exposed via `CompositionLocal` or an app-level ViewModel `Channel`. Current behaviour: confirmation
dialog before delete.

---

## 8. Offline-First & Future Firebase Sync

Design principles to apply now so that sync is possible later:

- **Local-first writes:** all writes go to Room first; Firebase is eventual.
- **Conflict resolution:** Last Write Wins using `updated_at`. Keep the record with the higher
  timestamp.
- **Soft delete propagation:** `is_deleted = 1` must be synced to remote; never filter deleted
  records before sync.
- **Auto-push on reconnect:** use `ConnectivityManager` callback or `WorkManager` to trigger
  pending sync when connectivity is restored.
- Do not add any Firebase dependencies until Phase 3+ is complete — just keep the interfaces clean.

---

## 9. Testing Requirements

| Area | Requirement |
|---|---|
| Ratio calculation | Unit test: `yield / dose` for various values including edge cases (0 dose) |
| Room repositories | In-memory `Room.inMemoryDatabaseBuilder` tests per entity |
| Decimal normalization | Unit test: comma-to-dot conversion, `Locale` edge cases |
| Repository return type | All repo methods return `Result<T>` — test both success and failure paths |
| Error UI | Snackbar for transient errors; `EmptyState` composable for empty lists |

---

## 10. Reference Data

### Sample equipment (real-world examples used in context)
- **Grinders:** Niche Zero (conical), Mahlkönig EK43, Eureka Mignon Specialita, KMax
- **Machines:** ECM Synchronika, La Marzocco Linea Mini, Strietman CT2, Decent DE1
- **Baskets:** IMS precision, VST ridgeless, Decent 18 g

### Sample shot parameters (from user's CSV log)
| Dose (g) | Yield (g) | Ratio | Grind | Notes |
|---|---|---|---|---|
| 18.0 | 36.0 | 1:2.0 | — | Textbook double |
| 18.5 | 40.0 | 1:2.16 | — | Slightly long |
| 17.0 | 34.0 | 1:2.0 | — | Standard dose |

> Note: extraction time was not present in the source CSV — it must be entered manually by the
> user when logging shots. The field is required for meaningful comparison.

---

## 11. Key Decisions & Notes

- **Navigation tabs are Shots / Coffee / Gear / Settings** — `AppDestination` enum in
  `AppDestination.kt`; labels come from `@param:StringRes` resources.
- **Min SDK is 26** (set in `app/build.gradle.kts`) — requirement doc said 24+, 26 is fine.
- **Codebase language is English** — source notes were in Portuguese; all code, comments and
  strings must be in English.
- **All UI strings live in `strings.xml`** — always use `stringResource()` in Composables;
  never hardcode user-visible strings in Kotlin.
- **`@param:StringRes` on enum constructors** — Kotlin 2.x requires the explicit `@param:`
  target when annotating primary constructor parameters, otherwise the compiler emits a warning.
- **Kotlin 2.3.10 / K2** — use K2-compatible patterns; avoid deprecated APIs.
- **`compileSdk` uses `release(36) { minorApiLevel = 1 }`** — AGP 9 syntax, do not change to the
  old integer form.
- **Do not add `kotlin.android` plugin** — AGP 9.0 includes Kotlin support built-in; applying
  `org.jetbrains.kotlin.android` explicitly causes a build error.
- **Material 3 Expressive override** — BOM ships M3 stable; `HorizontalFloatingToolbar` requires
  M3 1.5.0-alpha14+. Override only `androidx-compose-material3` in `libs.versions.toml`.
- **Coil 3 for images** — use `coil-compose` + `coil-network-okhttp`. `AsyncImage` for URL-based
  images; letter-initial `Box` as fallback when `imageUri` is null.
- **Room DB migrations** — `AppDatabase` is at version 1 (Roasters only). Every new entity
  requires a version bump and a `Migration` object added to the `databaseBuilder` in `DataModule`.
- **`hiltViewModel` import** — use `androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel`,
  not the deprecated `androidx.hilt.navigation.compose.hiltViewModel`.
