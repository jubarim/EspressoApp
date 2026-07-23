# The Digital Barista

A local-first Android app for logging espresso shots. Helps home and prosumer baristas reproduce their best shots by tracking every variable: beans, grinder, machine, basket, dose, yield, and time.

## Features

- Log espresso shots with dose, yield, extraction time, grind setting, brew temperature, and rating
- Manage equipment: grinders, espresso machines, and filter baskets
- Manage coffee inventory: roasters and coffee beans
- Ratio calculated automatically from dose and yield
- Passwordless sign-in with passkeys (biometrics)

## Tech stack

| | |
|---|---|
| Language | Kotlin 2.3 (K2 compiler) |
| UI | Jetpack Compose + Material 3 Expressive |
| Architecture | MVVM / MVI, single-module |
| Database | Room (local, offline-first) |
| DI | Hilt |
| Auth | Firebase Passkeys (WebAuthn) |
| Image loading | Coil 3 |

## Project structure

```
app/src/main/java/org/juba/espressoapp/
├── data/           — Room entities, DAOs, repository implementations, mappers
├── domain/         — Domain models, repository interfaces, use cases
├── ui/             — Composables, ViewModels, UiState
│   ├── auth/       — Passkey registration and sign-in
│   ├── coffee/     — Roasters and coffee beans
│   ├── gear/       — Grinders, machines, baskets
│   ├── shots/      — Shot log list and form
│   ├── settings/   — App preferences and sign-out
│   └── designsystem/ — Reusable M3 components
└── web/            — Firebase Hosting + WebAuthn extension config
```

## Setup

### Android

1. Clone the repo.
2. Open in Android Studio.
3. Add your `google-services.json` at `app/google-services.json` (see Firebase setup below).
4. Run on a physical device or emulator with API 26+.

### Firebase

Passkey authentication requires a Firebase project with several services configured. See **[docs/authentication.md](docs/authentication.md)** for the full setup guide, including:

- Firebase project creation
- Enabling Anonymous Auth, Firestore, and App Check
- Installing the patched `firebase-web-authn` extension
- Digital Asset Links configuration
- Adding new signing certificates

## Building

```bash
# Compile check
./gradlew compileDebugKotlin

# Run tests
./gradlew test
```
