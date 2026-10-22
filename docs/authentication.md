# Authentication — Google Sign-In

Authentication is **optional**. The app is local-first and fully usable without an account. Users can sign in with Google to keep a stable identity for a future sync feature; users who never sign in stay in **guest mode**, where all data stays on the device.

## Why Google Sign-In instead of passkeys

An earlier version of this branch used passkeys (WebAuthn) through the [`gavinsawyer/firebase-web-authn`](https://github.com/gavinsawyer/firebase-web-authn) Firebase extension. That extension runs as a Cloud Function and requires the **Blaze (pay-as-you-go)** plan. Google Sign-In uses only **Firebase Auth**, which works on the free **Spark** plan. No Cloud Functions, Firestore, or App Check are required.

## How it works

The client uses Android Credential Manager to obtain a Google ID token, then exchanges it for a Firebase session:

```
AuthViewModel.signInWithGoogle(activity, serverClientId)
  ├─ CredentialManager.getCredential(...)              → Google account picker
  │    └─ GoogleIdTokenCredential.createFrom(...).idToken
  │
  └─ AuthRepository.signInWithGoogle(idToken)
       └─ firebaseAuth.signInWithCredential(
              GoogleAuthProvider.getCredential(idToken, null)
          )
```

`serverClientId` is the OAuth **web** client ID. The Google Services Gradle plugin generates it as `R.string.default_web_client_id` from `google-services.json`, so it is never hardcoded.

## Guest mode

- Signed-out users have no Firebase user at all. `AuthRepository.currentUser()` emits null.
- The first launch shows the welcome screen. The user either signs in with Google or taps **Continue without an account**. Either choice sets an `onboarding_complete` flag in DataStore, so the welcome screen is not shown again.
- Signed-in users see their email and a **Sign Out** button in **Settings**. Guests see a **Sign in with Google** button there instead.
- Signing out clears the `onboarding_complete` flag and returns the app to the welcome screen, clearing the back stack. The welcome screen is then shown again on the next launch until the user signs in or chooses guest once more.
- Data is not synced to Firebase yet. Sync is a separate, future feature and will apply only to signed-in users.

## Firebase project setup

### 1. Create the Firebase project

1. Go to [console.firebase.google.com](https://console.firebase.google.com) and create a project (the free Spark plan is enough).
2. Add an **Android app** with package name `org.juba.espressoapp`.

### 2. Enable the Google sign-in provider

In the Firebase Console: **Authentication → Sign-in method → Google → Enable**, then save.

### 3. Add your signing certificate fingerprints

Google Sign-In requires the SHA-1 (and SHA-256) of the certificate used to sign the build. Add them under **Project settings → Your apps → Android app → Add fingerprint**.

```bash
# Print the debug keystore fingerprints
./gradlew signingReport
```

For a release build, add the fingerprints of the release keystore too. Every signing certificate used to build the app needs its own fingerprint registered.

### 4. Download `google-services.json`

Download the updated `google-services.json` and place it at `app/google-services.json`. It must include an OAuth client (`client_type: 3`); the Google Services plugin turns that into `R.string.default_web_client_id`, which the app reads at runtime. Without it the module does not compile.

`google-services.json` is excluded from version control, so each developer provides their own copy.

### Not required

Firestore, Cloud Functions, Firebase Hosting, App Check, and Digital Asset Links are **not** needed for Google Sign-In. If an earlier setup created them for passkeys, they can be removed.

## Android code structure

```
app/src/main/java/org/juba/espressoapp/
├── data/
│   ├── di/AuthModule.kt                 — Hilt: CredentialManager, FirebaseAuth,
│   │                                       DataStore<Preferences>
│   ├── repository/AuthRepositoryImpl.kt
│   └── repository/PreferencesRepositoryImpl.kt
├── domain/repository/AuthRepository.kt
├── domain/repository/PreferencesRepository.kt
└── ui/
    ├── auth/
    │   ├── AuthUiState.kt
    │   ├── AuthViewModel.kt
    │   └── AuthScreen.kt                — welcome screen (Google / guest)
    ├── main/AppViewModel.kt             — decides the start destination
    └── settings/SettingsScreen.kt       — account section (sign in / sign out)
```

## Known gotchas

**`R.string.default_web_client_id` must exist.** The Google Services Gradle plugin only generates it when `google-services.json` contains an OAuth web client. Enable the Google provider and add a fingerprint, then re-download the file. Until then the build fails with an unresolved resource.

**The fingerprint must match the signing certificate.** A debug build signed with a different keystore than the one registered in Firebase returns a developer error on sign-in. Re-run `./gradlew signingReport` and compare.

**The emulator needs a Google account.** The Credential Manager account picker shows nothing and throws `NoCredentialException` if the device or emulator has no Google account signed in. The app surfaces this as "No Google account found on this device".

**Credential Manager account picker.** `GetGoogleIdOption` is built with `setFilterByAuthorizedAccounts(false)` so the picker always lists every Google account on the device. This avoids the empty-picker case for first-time users.
