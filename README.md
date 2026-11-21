# Yannick Patches

Independent custom Android patches built with the
[Morphe Patcher](https://github.com/MorpheApp/morphe-patcher).

> **Development status:** experimental. Patches are not stable until target
> versions, fingerprint matching, bundle generation, and on-device behavior
> have been verified.

## WetterOnline — remove Werbefrei promotional cards

- Package: `de.wetteronline.wetterapp`
- Analyzed version: `2026.22`
- Status: experimental / incomplete
- Two call sites are confirmed in the analyzed APK: `Lcma;->invoke(...)`
  and `Lj97;->invoke(...)`. Both must be removed and tested, including the
  reserved layout space.

## Morphe Manager source metadata

Morphe Manager reads `patches-bundle.json` from the repository root:
- `main/patches-bundle.json` for stable releases
- `dev/patches-bundle.json` when pre-releases are enabled

The JSON must point to an actual downloadable `.mpp` release and contain
`version`, `download_url`, `created_at`, and `description`. The optional
`public_key` is used when releases are signed.

The release workflow generates this metadata together with the GitHub release.
Do not fill the manifest with placeholder URLs. Until the first release is
published, this repository cannot yet provide a downloadable Morphe source.

`patches-list.json` is generated for the repository's patch-list/README
display; it is not a replacement for `patches-bundle.json`.

## Build and release

Build locally with `./gradlew :patches:buildAndroid`; the `.mpp` file is
written to `patches/build/libs/`.

The Morphe Gradle plugin is hosted on GitHub Packages. Authenticate with a
GitHub token having `read:packages` scope. Never commit the token.

The repository is public. It is independent and is not affiliated with or
endorsed by Morphe, WetterOnline, or the app publisher.
