### Description

Replaced passkey authentication with Google Sign-In, and made authentication optional.

The passkey flow depended on the `firebase-web-authn` Cloud Function extension, which requires the paid Blaze plan. Google Sign-In uses only Firebase Auth and works on the free Spark plan. The Cloud Function, App Check, Firebase Hosting, and the patched extension were removed.

The app no longer gates content behind auth. On first launch it shows a welcome screen with **Continue with Google** and **Continue without an account**. Either choice is remembered, so the welcome screen appears only once. Guests use the app in local-only mode with no Firebase account. Signed-in users see their email and a **Sign Out** button in Settings; guests see a **Sign in with Google** button there instead. Signing out returns the app to the welcome screen and asks for the choice again on the next launch. Data sync to Firebase is a separate, future feature and will apply only to signed-in users.

### Related task

### Changelog
- Replaced the passkey (WebAuthn) flow with Google Sign-In via Credential Manager and `GoogleAuthProvider`
- Added a guest mode: the app is fully usable without an account, with no Firebase user created
- Added a first-launch welcome screen with a remembered choice (onboarding flag in DataStore)
- Removed the auth gate on cold start; sign-in is now reachable from Settings
- Added an account section to Settings showing the signed-in email and sign-out, or a sign-in button for guests
- Sign-out returns the app to the welcome screen and requires the first-launch choice again on the next launch
- Removed `FirebaseFunctions` and the `firebase-web-authn` Cloud Function calls
- Removed Firebase App Check (debug and release providers)
- Removed the `web/` directory (extension config, Firebase Hosting, Digital Asset Links)
- Added `PreferencesRepository` for the onboarding flag
- Added `email` to `AuthUser`
- Rewrote `docs/authentication.md` for the Google Sign-In setup (free Spark plan)

### Screenshots
<img src="xxxURLPASTEHERExxxxxxx" width="300">
