# Yannick Patches

Private collection of independent custom Android patches, built with the
[Morphe Patcher](https://github.com/MorpheApp/morphe-patcher).

> **Development status:** experimental. Patches are not considered stable until
> their target app version, fingerprint matching, patch generation, and on-device
> behavior have been verified.

## Patch projects

### WetterOnline — remove Werbefrei promotional cards

- Package: `de.wetteronline.wetterapp`
- Analyzed version: `2026.22`
- Source: `patches/src/main/kotlin/app/yannick/patches/wetteronline/`
- Status: experimental / incomplete
- Two call sites have been confirmed in the analyzed APK: `Lcma;->invoke(...)`
  and `Lj97;->invoke(...)`. The patch source now targets both. It still needs
  a new build and on-device verification; removal of the reserved card height
  has not yet been demonstrated.

No release is advertised as functional until this is resolved and tested.

## Development

This repository follows the official Morphe patches template structure.

### Build locally

Run `./gradlew :patches:buildAndroid`. The generated `.mpp` bundle is written
to `patches/build/libs/`. Load that file into Morphe Desktop for local testing.

### GitHub Packages authentication

The Morphe Gradle plugin is retrieved from GitHub Packages. Authenticate with
a GitHub token that has the `read:packages` scope. Never commit the token.

```bash
gh auth refresh --hostname github.com --scopes read:packages
GITHUB_ACTOR="$(gh api user --jq .login)" \
GITHUB_TOKEN="$(gh auth token)" \
  ./gradlew :patches:buildAndroid
```

## Morphe source metadata

The app compatibility metadata is declared in Kotlin under
`patches/src/main/kotlin/app/yannick/patches/shared/Constants.kt`.
Project identity and source information are declared in
`patches/build.gradle.kts`.

`patches-list.json` and `patches-bundle.json` are generated release metadata.
They must be updated by the Morphe release tooling, not maintained manually.

This repository is **private**. Morphe Manager sources need to be publicly
accessible, so this private repository is currently for source control and
local development; use the built `.mpp` file with Morphe Desktop.

## Disclaimer

This is an independent, unofficial project. It is not affiliated with or
endorsed by Morphe, WetterOnline, or the app publisher. Patches are intended
for local research and customization. App updates may break them.
