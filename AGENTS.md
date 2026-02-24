# AGENTS.md

This file summarizes all major work completed to revive and optimize this Android-only LibGDX game.

## Scope and Goal
- Revive the game for modern Android devices.
- Keep ads enabled with current best-practice formats.
- Improve stability, compatibility, performance, and maintainability.
- Keep gameplay and UI faithful, with requested UX refinements.

## Build and Toolchain Modernization
- Migrated from legacy Android Gradle plugin to modern Android/Gradle setup.
- Upgraded root and module build scripts for current Gradle/AGP workflows.
- Upgraded LibGDX dependencies from legacy versions to `1.14.0`.
- Updated Java toolchain and compile options to Java 17 for both `core` and `android`.
- Simplified project modules to Android + Core only (desktop module removed from active build).
- Updated Gradle wrapper to `8.10.2`.
- Tuned Gradle properties for modern builds and AndroidX-compatible behavior.

## Android Compatibility and Runtime Fixes
- Updated Android app config:
- `compileSdk = 35`
- `targetSdk = 35`
- `minSdk = 23`
- Added modern manifest metadata for AdMob app ID.
- Removed legacy billing/AIDL utility code and old support-v4 jar integration.
- Reworked native library packaging:
- Added `natives` configuration in Android module.
- Added `copyAndroidNatives` task to extract `.so` per ABI.
- Wired `preBuild` to native copy task.
- Ensured `jniLibs` points to `android/libs`.
- Fixed startup crash caused by missing `libgdx.so` on device.

## Ads Architecture (Android Only)
- Replaced old ad flow with modern Google Mobile Ads SDK usage.
- Implemented Anchored Adaptive Banner ads with dynamic width recalculation on:
- app start
- layout width changes
- orientation/configuration changes
- split-screen / multi-window
- Implemented interstitial lifecycle with:
- preload
- cooldown and frequency guard (`INTERSTITIAL_EVERY_BREAKS`, time interval)
- show on natural gameplay breaks
- auto-reload after dismiss/failure
- Added clean `ActionResolver` contract used by core gameplay:
- `preloadInterstitial()`
- `onNaturalBreak()`
- `showBanner()`
- `hideBanner()`
- `getBannerHeightPx()`
- Integrated banner visibility policy in core screens:
- banner shown on pause/game-over states
- banner hidden during active gameplay

## Gameplay, Physics, and Performance Optimizations
- Reduced ball speed to improve playability on modern high-refresh devices:
- Lowered `vitesseBalleMin/Normale/Max` in `GameConstants`.
- Reduced initial launch impulse in `Balle`.
- Removed unnecessary static world/camera references in several gameplay classes.
- Passed `World`/viewport values explicitly to destroy helpers.
- Reduced allocations and unsafe shared-state patterns in object/laser/ball logic.
- Removed debug `System.out` noise and dead ad-trigger state.
- Added safer string comparison (`equals`) where needed.
- Improved disposal hygiene for game resources and screen resources.
- Added one-time listener binding guards on screens to prevent duplicate listeners.

## Screen Ratio and Layout Behavior Improvements
- Updated screens to better react to resize and modern aspect ratios.
- Kept stage viewport updates in `resize`.
- Added pause button dynamic positioning logic based on screen size.
- Added adaptive UI behavior where needed for bottom controls and title measurements.

## Requested UI/UX Changes Implemented
- Removed social buttons from main menu (Facebook, Twitter, Apprentice Soft logo).
- Added visible in-game pause button in top-right corner.
- Pause menu changes:
- removed Restart row
- kept Resume/Menu/Quit with equal vertical spacing
- Level complete/lost text and flow changes:
- increased `Level Cleared` and `You Lost` message size
- kept labels centered
- preserved spacing to buttons
- replaced level-complete Replay action with Menu action to return to main menu
- Pause button style changes:
- replaced filled style with generated transparent/outlined square style
- kept `II` icon text visible and consistent
- adjusted button size as requested:
- first reduced by 2x
- then increased by 30% from that reduced size

## Device Testing and Validation Performed
- Built repeatedly with:
- `./gradlew :android:assembleDebug`
- Installed repeatedly on physical Samsung device with:
- `adb install -r android/build/outputs/apk/debug/android-debug.apk`
- Launched app from ADB and checked logcat for fatal/runtime crashes.
- Verified native-library startup issue resolved (no more missing `libgdx.so` crash).
- Confirmed app launches and runs after latest UI updates.

## Current Status
- Android revival work is implemented in source and tested via console + physical device deployment.
- Project is ready for continued gameplay QA and store-readiness pass (signing/release policy/content checks).

## HTML/Web Version (Raspberry Pi)
- Added and maintained a dedicated `:html` module (GWT backend) for browser deployment.
- Build/deploy workflow:
  - Build: `./gradlew :html:dist`
  - Deploy target on Pi: `/home/marc/apps/brick-breaker-web/`
  - Deployed via `scp` sync to Pi host `192.168.68.65`.
- Web layout and screen ratio:
  - Final active layout: centered portrait game viewport with black side bars.
  - Ratio set to standard phone portrait width (`9/16`) while filling browser height.
  - Fixed persistent bottom gap on high-DPI displays by disabling fixed-size physical-pixel downscaling in GWT config.
- Web font pipeline:
  - Added build-time font atlas generation (`:html:generateWebFonts`) using free fonts:
    - `Noto Sans` -> `Fonts/web_ui.fnt/png`
    - `Bebas Neue` -> `Fonts/web_title.fnt/png`
  - Web `LoadingScreen` maps game font keys to these atlases with linear filtering and non-integer glyph positioning for clean scaling.
- Web typography updates:
  - Button text increased and tuned multiple times; latest change applies an additional +20%.
  - Title text increased and tuned multiple times; latest change applies an additional +20%.
  - Added explicit text centering for button labels (horizontal and vertical) through recursive stage traversal of all `TextButton` actors in web screens.
- Web gameplay/controls already applied:
  - Removed visible pause button on web.
  - Pause/resume toggles on `Esc`, `Space`, `P`.
  - Web-only ball speed multiplier set to `1.5x`.
  - Removed rate/new-game promo UI from web flow.

## Latest Update (Brick - Web + Android)
- End-screen button spacing parity (web):
  - `Level Cleared` horizontal spacing now matches `You Lost` by constraining the title row width to the exact button-row width.
  - Keeps both action buttons fully on-screen on web/HTTPS layout.
- Power-up spawn rate increased by +20% (relative) in core gameplay logic (applies to Android and HTML):
  - Ball hits: `26%` -> `31%`
  - Ball-laser hits: `17%` -> `20%`
  - Laser projectile hits: `24%` -> `29%`
  - Implemented through an explicit `rollObjectDrop(int dropChancePercent)` helper in `GameScreen`.
- Android validation for the above:
  - Built successfully with `./gradlew :android:assembleDebug`.
- Web deployment:
  - Rebuilt with `./gradlew :core:compileJava :html:dist`.
  - Deployed to Pi and synced Apache web root (`/var/www/brick`) for HTTPS host `brick.marcvidal.ca`.
