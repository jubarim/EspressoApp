# The Digital Barista — Android App

A local-first Android app for logging espresso shots. Helps home and prosumer baristas reproduce their best shots by tracking every variable: beans, grinder, machine, basket, dose, yield, and time.

---

## Authentication

The app uses **passkeys** (WebAuthn) for authentication — no passwords, no email/OTP codes. Users register once with their biometrics and sign in with a fingerprint or face scan on every return visit.

### How it works at a high level

A passkey is a public/private key pair created inside your device's secure chip:

- **Registration:** the device creates a key pair, keeps the private key locked behind biometrics, and sends the public key to the server. No secret ever leaves the device.
- **Sign-in:** the server sends a random challenge; the device signs it with the private key (after biometric confirmation); the server verifies the signature with the stored public key. If it matches, access is granted.

Because the private key never leaves the device and every challenge is a one-time nonce, passkeys are phishing-proof and replay-proof by design.

---

## Firebase services used

| Service | Purpose |
|---|---|
| **Firebase Auth** | Holds user identities and issues auth sessions via custom tokens |
| **Firebase Functions** | Hosts the WebAuthn extension as a callable Cloud Function |
| **Firestore** | Stores per-user challenges and public keys (managed by the extension) |
| **Firebase App Check** | Ensures only genuine app builds can call the Cloud Function |
| **Firebase Hosting** | Serves `assetlinks.json` so Android trusts the app's passkey origin |

---

## Firebase project setup

### 1. Create the Firebase project

1. Go to [console.firebase.google.com](https://console.firebase.google.com) and create a project.
2. Add an **Android app** with package name `org.juba.espressoapp`.
3. Download `google-services.json` and place it at `app/google-services.json`.

### 2. Enable Firebase services

In the Firebase Console:

- **Authentication → Sign-in method:** enable **Anonymous** sign-in (required by the extension — it needs an auth token on every call, even before the user registers).
- **Firestore Database:** create a database in production mode; the extension manages its own collections.
- **App Check:** see the [App Check section](#app-check) below.

### 3. Install the `firebase-web-authn` extension

The WebAuthn protocol server-side logic is handled by [`gavinsawyer/firebase-web-authn`](https://github.com/gavinsawyer/firebase-web-authn). The upstream extension does not support Android origins (`android:apk-key-hash:...`) out of the box. This project ships a patch that adds `ALLOWED_ORIGINS` support.

**Patch summary (`web/android-changes-5d57dd3f6a409bad.diff`):**
- Adds `allowedOrigins?: string[]` to the `FirebaseWebAuthnConfig` interface
- Passes it as part of `expectedOrigin` for all three verify operations (registration, authentication, reauthentication)
- Reads `ALLOWED_ORIGINS` from the environment and passes it to the config
- Declares `ALLOWED_ORIGINS` as an optional `string` param in `extension.yaml`

The patch was generated against upstream commit `25d57dd3f6a409bad18d087b1a3422de80f5bcab` (v10.4.5, July 19 2026). Since the compiled `dist/` is included in the diff, no separate build step is needed.

**Apply the patch and deploy:**

```bash
# 1. Clone the upstream extension
git clone https://github.com/gavinsawyer/firebase-web-authn
cd firebase-web-authn
git checkout 25d57dd3f6a409bad18d087b1a3422de80f5bcab

# 2. Apply the Android patch
git apply /path/to/ExpressoApp/web/android-changes-5d57dd3f6a409bad.diff

# 3. Update firebase.json to point to the patched dist
#    "extensions": { "firebase-web-authn": "/path/to/firebase-web-authn/dist/libs/extension" }

# 4. Deploy from the web/ directory
cd /path/to/ExpressoApp/web
firebase deploy --only extensions,hosting
```

**Extension parameters** are configured in `web/extensions/firebase-web-authn.env`:

```env
AUTHENTICATOR_ATTACHMENT=any
AUTHENTICATOR_ATTACHMENT_2FA=cross-platform
LOCATION=us-central1
RELYING_PARTY_ID=espresso-app-89866.web.app
RELYING_PARTY_NAME=Espresso App
USER_VERIFICATION_REQUIREMENT=preferred
ALLOWED_ORIGINS=android:apk-key-hash:<base64-sha256-of-debug-signing-cert>
```

`ALLOWED_ORIGINS` is a comma-separated list of additional WebAuthn origins to accept beyond the browser origin. Android passkeys identify themselves with `android:apk-key-hash:<cert>` — without this, all passkey verifications fail.

### 4. Grant the IAM `Service Account Token Creator` role

The extension calls `admin.auth().createCustomToken()` to issue Firebase custom tokens. This requires the extension's service account to have the **Service Account Token Creator** IAM role.

1. Open [Google Cloud Console → IAM](https://console.cloud.google.com/iam-admin/iam).
2. Find the service account named `ext-firebase-web-authn@<project-id>.iam.gserviceaccount.com`.
3. Click **Edit** → **Add another role** → search for **Service Account Token Creator** → Save.

Without this role, `completeRegistration` and `completeLogin` will fail with `auth/insufficient-permission`.

### 5. Digital Asset Links

Android verifies that the app is associated with the Relying Party domain before creating or using passkeys. This association is declared in `web/public/assetlinks.json`:

```json
[{
  "relation": ["delegate_permission/common.get_login_creds"],
  "target": {
    "namespace": "android_app",
    "package_name": "org.juba.espressoapp",
    "sha256_cert_fingerprints": [
      "84:74:8F:B9:78:21:8A:81:08:B0:6D:F8:1B:DB:70:11:92:E9:BB:A1:59:5F:3C:8F:D8:F2:03:79:31:1A:15:B6"
    ]
  }
}]
```

This file is served at `https://espresso-app-89866.web.app/.well-known/assetlinks.json` via Firebase Hosting. Deploying the `hosting` target makes it live.

To find the SHA-256 fingerprint of your signing certificate:

```bash
# Debug keystore (default location)
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Or via Gradle
./gradlew signingReport
```

For the `ALLOWED_ORIGINS` env var you need the **base64-encoded SHA-256** of the same cert (different format from the colon-separated hex above):

```bash
keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -storepass android | \
  openssl sha256 -binary | \
  openssl base64 | \
  tr '+/' '-_' | \
  tr -d '='
```

---

## App Check

App Check prevents anyone from calling the Cloud Function directly (bypassing the app). The extension is deployed with `enforceAppCheck: true`.

### Debug builds

`app/src/debug/java/org/juba/espressoapp/AppCheckHelper.kt` installs the **Debug App Check provider**, which logs a one-time token to Logcat on first run.

**One-time setup per machine:**
1. Run the debug build once and look for a log line containing `DebugAppCheckProvider` — it prints a UUID token.
2. Open Firebase Console → **App Check → Apps → Debug tokens**.
3. Add that UUID as a debug token.

The debug provider is excluded from release builds via source sets.

### Release builds (TODO)

`app/src/release/java/org/juba/espressoapp/AppCheckHelper.kt` is a placeholder. Before shipping to production, wire **Play Integrity**:

```kotlin
// Replace the placeholder with:
FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
    PlayIntegrityAppCheckProviderFactory.getInstance(),
)
```

You will also need to add the release certificate's key hash to `ALLOWED_ORIGINS` and its SHA-256 fingerprint to `assetlinks.json`, then redeploy.

---

## Adding a new signing certificate (e.g. release key)

When you create a release keystore, update three places:

1. **`web/public/assetlinks.json`** — add the SHA-256 fingerprint (colon-separated hex) to `sha256_cert_fingerprints`.
2. **`web/extensions/firebase-web-authn.env`** — append the base64 key hash to `ALLOWED_ORIGINS`, comma-separated.
3. Redeploy both hosting and extensions:
   ```bash
   cd web && firebase deploy --only extensions,hosting
   ```

---

## Android code structure

```
app/src/main/java/org/juba/espressoapp/
├── EspressoAppApplication.kt       — installs App Check on startup
├── data/
│   ├── di/AuthModule.kt            — Hilt: CredentialManager, FirebaseAuth,
│   │                                  FirebaseFunctions, DataStore<Preferences>
│   └── repository/AuthRepositoryImpl.kt
├── domain/repository/AuthRepository.kt
└── ui/auth/
    ├── AuthUiState.kt
    ├── AuthViewModel.kt
    └── AuthScreen.kt               — two-state screen (returning user / new user)
```

### Auth screen behaviour

- **Returning user** (display name persisted in DataStore): shows `Hello, [name]` with a fingerprint icon. Tapping the icon triggers sign-in immediately — no extra confirmation.  A **Switch Account** button at the bottom clears the saved name and shows the new-user screen.
- **New user**: shows a name text field with **Sign in with Passkey** and **Create Passkey** buttons.

The display name is saved locally in DataStore after every successful registration or sign-in, and cleared on **Switch Account**.

### Registration flow

```
AuthViewModel.register(displayName)
  ├─ AuthRepository.prepareRegistration(displayName)
  │    ├─ firebaseAuth.signInAnonymously()    ← required by the extension on every call
  │    └─ functions.call("create registration challenge")
  │
  ├─ CredentialManager.createCredential(...)
  │    └─ Android shows passkey manager picker → biometric → key pair created in secure chip
  │
  └─ AuthRepository.completeRegistration(registrationResponseJson)
       ├─ functions.call("verify registration", ...)
       └─ firebaseAuth.signInWithCustomToken(customToken)
```

### Sign-in flow

```
AuthViewModel.signIn(displayName)
  ├─ AuthRepository.getLoginChallenge(displayName)
  │    ├─ firebaseAuth.signInAnonymously()    ← required on every call
  │    └─ functions.call("create authentication challenge")
  │
  ├─ CredentialManager.getCredential(...)
  │    └─ Android shows passkey picker → biometric → signs challenge with stored private key
  │
  └─ AuthRepository.completeLogin(authResponseJson)
       ├─ functions.call("verify authentication", ...)
       └─ firebaseAuth.signInWithCustomToken(customToken)
```

---

## Known gotchas

**Android uses a non-standard WebAuthn origin.** Browsers use `https://example.com`; Android uses `android:apk-key-hash:<base64-sha256>`. The upstream `firebase-web-authn` extension doesn't support this — the local fork adds the `ALLOWED_ORIGINS` parameter to patch it in.

**Null fields in challenge JSON crash CredentialManager.** The server library serializes unused fields (`allowCredentials`, `extensions`) as JavaScript `undefined`, which Firebase Callable serializes as JSON `null`. CredentialManager rejects `null` for those fields. Fix already applied in `AuthRepositoryImpl.getLoginChallenge`:

```kotlin
val challengeJson = JSONObject(
    data["requestOptions"].asMap().filterValues { it != null }
).toString()
```

**Anonymous sign-in is required before every Cloud Function call.** The extension rejects unauthenticated requests even during registration. Both `prepareRegistration` and `getLoginChallenge` call `firebaseAuth.signInAnonymously()` before the Callable.

**Firebase BoM must be declared per configuration.** `debugImplementation` does not inherit the `implementation` BoM constraint; it needs its own `debugImplementation(platform(libs.firebase.bom))` line.
