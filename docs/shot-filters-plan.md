# Shot List Filters Plan

Status: implemented.

## Goal

Long pressing a shot opens a floating toolbar with Edit, Delete, Copy and Filter. Filtering by a
coffee shows only that coffee's shots and adds a clearable chip under the list title.

The same long-press pattern is used by the roaster list, where View shots switches to the Shots tab
with a roaster filter applied.

---

## 1. Decisions

| Decision | Choice |
|---|---|
| Shot toolbar filters | Coffee bean only |
| Roaster list action | View shots, which applies a roaster filter internally |
| Coffee identity | Exact coffee bean record (`coffeeBeanId`) |
| Active filter indicator | Chip under the top bar title, tapping it clears |
| Filter lifetime | Until process death, one filter, new replaces old |
| Contextual toolbar | Material 3 `HorizontalFloatingToolbar` in the FAB slot |
| Detail screens | Keep the existing custom toolbar |
| Copy from list | `QuickCopyDialog`, then stay in the list |
| Delete from list | Confirmation dialog, then stay in the list |
| Selection | Row highlight, back or tap outside clears |
| Roaster toolbar | Edit, Delete, View shots |

The roaster filter variant is required by the View shots action even though the shot toolbar only
exposes the coffee filter.

---

## 2. UX flow

### Shots

1. Long press a shot: the row is highlighted and the Add FAB expands into a toolbar with Edit,
   Delete, Copy and Filter. The FAB crossfades to a Close button that clears the selection.
2. Tapping a row while the toolbar is open clears the selection instead of opening the detail.
3. Back clears the selection first, then the active filter, before leaving the screen.
4. Edit opens the shot form.
5. Delete opens the existing confirmation dialog and stays in the list.
6. Copy opens `QuickCopyDialog`, creates the copy, shows a snackbar and stays in the list.
7. Filter clears the selection and applies a coffee filter.
8. A chip under the title shows the coffee name with a close icon.
9. An empty result shows a message and a Clear filter action. The no-gear and no-shots empty states
   still take priority.

Selection is transient. The filter survives navigation and tab switches until the process dies.

### Roasters

1. Long press a roaster: the row is highlighted and the Add FAB expands into a toolbar with Edit,
   Delete and View shots. The FAB crossfades to a Close button that clears the selection.
2. Edit opens the roaster form.
3. Delete uses the existing confirmation dialog.
4. View shots applies a roaster filter and switches to the Shots tab using the same navigation block
   as the bottom bar.

---

## 3. Architecture

### Filter model

`ui/filter/ShotFilter.kt`

```kotlin
sealed interface ShotFilter {
    val label: String

    data class CoffeeBean(val coffeeBeanId: String, override val label: String) : ShotFilter
    data class Roaster(val roasterId: String, override val label: String) : ShotFilter
}
```

The label is a snapshot taken when the filter is applied. Renaming a bean or roaster while the
filter is active leaves the chip label stale until the filter is re-applied.

### Filter store

`ui/filter/ShotFilterStore.kt`

A Hilt `@Singleton` holding a `StateFlow<ShotFilter?>`. It survives navigation and tab switches and
dies with the process. No DataStore.

### Filtering

`ui/filter/ShotFiltering.kt`

A pure extension over `List<ShotLog>`:

- `null` returns the list unchanged.
- Coffee matches `coffeeBeanId` exactly.
- Roaster resolves the roaster's bean IDs from the loaded coffee beans, then keeps the shots in that
  set.

In memory, because the list already loads every shot sorted by `shotAt` and the roaster case needs
the beans anyway.

### Shots state and ViewModel

`ShotLogListUiState.Success` gains `activeFilter` and `totalShotCount`; `shots` is the filtered list.

`ShotLogListViewModel` splits the database flows into a `ShotListData` holder and combines it with
the filter store, because `combine` only has typed overloads up to five flows.

New methods: `setFilter`, `clearFilter`, `deleteShot`, `copyShot`, plus an event flow for copy
success and action failures.

`ShotLog.duplicate(shotAt)` is extracted so the detail and list ViewModels share the copy logic.

### Roaster ViewModel

`RoasterListViewModel` injects `ShotFilterStore`, gains `setShotFilter(roaster)`, `delete(id)` and a
failure event.

### Navigation

The repeated bottom bar navigation block is extracted so View shots can reuse it. The Shots entry is
the graph root, so popping up to it keeps the ShotLogListViewModel alive and the filter applies
immediately.

---

## 4. Reusable UI

### ContextActionsToolbar

`designsystem/toolbar/ContextActionsToolbar.kt`

A wrapper over `HorizontalFloatingToolbar` that takes the existing `FloatingNavItem` list and
renders icon buttons. The toolbar is anchored to a vibrant FAB: collapsed it shows the screen's
primary action, and expanded the FAB crossfades to Close and the actions expand with the built-in
animation. The FAB is 56dp and morphs from a rounded square while collapsed to a circle while
expanded. It is end-aligned so it keeps its position while the toolbar expands. The Material3
`FloatingToolbarDefaults` import is aliased to avoid the app's internal class of the same name.

### ActiveFilterChip

`designsystem/ActiveFilterChip.kt`

An `InputChip` with an optional leading icon, the filter label and a close icon. The whole chip
clears the filter. It takes a full content description for accessibility.

### Long-press pattern

Each list keeps a `selectedId` in `remember`, uses `combinedClickable`, a highlighted `ListItem`,
`BackHandler` and a background tap detector. The selection resets on rotation and tab changes.

---

## 5. Files

### New

| File | Purpose |
|---|---|
| `ui/filter/ShotFilter.kt` | Sealed filter type |
| `ui/filter/ShotFilterStore.kt` | App-scoped active filter |
| `ui/filter/ShotFiltering.kt` | Pure filter function |
| `ui/shots/ShotListEvent.kt` | Copy success and failure events |
| `extensions/ShotLogExtensions.kt` | `duplicate(shotAt)` |
| `designsystem/toolbar/ContextActionsToolbar.kt` | Expressive toolbar wrapper |
| `designsystem/ActiveFilterChip.kt` | Active filter chip |
| `test/.../ui/filter/ShotFilteringTest.kt` | Filter behavior |
| `test/.../ui/filter/ShotFilterStoreTest.kt` | Store behavior |

### Modified

| File | Change |
|---|---|
| `ui/shots/ShotsScreen.kt` | Long press, toolbar, dialogs, chip, empty state |
| `ui/shots/ShotLogListUiState.kt` | Filter and total count |
| `ui/shots/ShotLogListViewModel.kt` | Filter combine, delete, copy, events |
| `ui/shots/ShotLogDetailViewModel.kt` | Shared duplicate helper |
| `ui/coffee/roaster/RoasterListScreen.kt` | Long press, toolbar, edit/delete/view shots |
| `ui/coffee/roaster/RoasterListViewModel.kt` | Filter store, delete, events |
| `ui/main/EspressoApp.kt` | New callbacks and tab navigation helper |
| `res/values/strings.xml` | Filter, toolbar and error strings |

---

## 6. Edge cases

- Deleted bean while filtered: the chip remains, the list is empty, Clear filter recovers.
- Renamed bean or roaster: the chip label is stale until re-applied.
- Remote delete while selected: the selection clears.
- Copy while filtered: the copy inherits the bean and appears in the list.
- Delete the last match: the filtered empty state appears.
- No gear: the gear empty state wins regardless of filter.
- Back clears the selection first, then the active filter, before leaving the screen.
- Tab switch: the filter stays, the selection resets.
- Process death: the filter resets.
- Guest mode: filtering is local UI only, no sync impact.

---

## 7. Verification

- `./gradlew :app:testDebugUnitTest :app:assembleDebug`
- Manual device pass:
  - Long press and all four shot actions.
  - Chip appears, clears and is replaced by another filter.
  - Rotation and tab-switch behavior.
  - Roaster View shots lands on the Shots tab with the roaster chip.
  - Filtered empty state.
  - TalkBack reads the toolbar buttons and the chip.
  - Delete and copy failure paths with airplane mode.

---

## 8. Out of scope

- Grinder, machine, basket, rating or date filters.
- Persisting the filter across restarts.
- Migrating detail screens to `HorizontalFloatingToolbar`.
- Multi-select, undo delete or a filter panel.
- Filters for other lists.

---

## 9. Implementation notes

- The active filter chip sits in the screen top bar slot, below the standard `TopAppBar`, so the
  app bar height only changes while a filter is active.
- The selection is kept with `remember` because it is transient. Dialog IDs use `rememberSaveable`
  to survive rotation, matching the detail screens.
- `ShotLog.duplicate(shotAt)` is shared by the list and detail ViewModels.
- The roaster action calls `NavHostController.navigateToTab`, the same helper used by the bottom
  bar, so the Coffee tab state is preserved.
- The contextual toolbar is anchored to a vibrant FAB at the end. The FAB stays in place, crossfades
  from Add to Close, and the actions expand with the Material 3 expressive animation. While expanded,
  the FAB clears the selection.
- The FAB is a 56dp vibrant button drawn in the toolbar's FAB slot. It morphs from a rounded square
  while collapsed to a circle while expanded. Its size is the `ContextFabSize` constant in
  `ContextActionsToolbar.kt`, and the collapsed corner is `CollapsedCornerRatio` times that size.
- Filter state is not cleared on sign-out or account change. A stale filter shows the filtered empty
  state and is cleared with the chip.
- Detail screens still use the custom `DetailActionsToolbar`.
