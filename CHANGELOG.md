# Changelog

This file records notable CountTimeProgressView changes.

## [2.0.0] - 2026-04-15

### Bug Fixes
- Fixed `CLOCK` formatting when `minute >= 10` incorrectly appended `hour`
- Fixed inconsistent `startAngle` getter/setter semantics
- Fixed a possible `titleCenterText` null crash in SECOND mode
- Changed `DEFAULT_COUNT_TIME` from 5ms to 5000ms
- Fixed the demo `setBackgroundColor()` call; it now uses `backgroundColorCenter`

### Features
- Pause / resume / reset APIs: `pauseCountTimeAnimation()`, `resumeCountTimeAnimation()`, `resetCountTimeAnimation()`
- `progress` getter/setter in the 0f..1f range
- Progress callback: `addOnProgressChangedListener { progress, remainingMillis -> }`
- Custom text formatter: `textFormatter = { millis -> "..." }`
- Progress stroke cap: `strokeCap` (BUTT / ROUND / SQUARE), with XML support
- Configurable animation interpolator via `interpolator`
- Gradient progress stroke via `gradientStartColor` / `gradientEndColor`, with XML support
- XML attributes: `autoStart`, `finishedText`, `showCenterText`, `strokeCap`
- `wrap_content` support in `onMeasure` with an 84dp default size
- SavedState support for restoring progress after configuration changes
- Accessibility support via AccessibilityNodeInfo
- Explicit pixel APIs: `setBorderWidthPx()`, `setMarkBallWidthPx()`, `setTitleCenterTextSizePx()`
- Public `remainingTime` and `isPaused` properties
- `CountTimeProgressViewCompose` helper for Compose AndroidView usage

### Code Quality
- Set `LinearInterpolator` as the default interpolator
- Removed all `Log.e` calls from the library
- Removed the redundant `mContext` field
- Removed the dead `attr != null` branch
- Made `calcRadius()` private
- Added `@IntDef` type safety for `TextStyle`
- Deprecated `OnEndListener` and added `OnCountdownEndListener` + `setOnClickCallback`
- Stopped clearing listeners in `onDetachedFromWindow`
- Split `onDraw` into focused drawing methods
- Improved drawing performance with text caching and `postInvalidateOnAnimation()`

### Project
- Removed AppCompat runtime dependency from the library module
- Migrated to AndroidX
- Raised `minSdk` from 14 to 21, and `targetSdk` / `compileSdk` to 34
- Switched repositories to `google()` + `mavenCentral()`
- Migrated `lintOptions {}` to `lint {}`
- Switched to `proguard-android-optimize.txt`
- Migrated Gradle DSL names to `compileSdk` and `minSdk`
- Changed `artifactId` from `library` to `counttimeprogressview`
- Converted SplashActivity from Java to Kotlin
- Replaced `left/right` layout attributes with `start/end` for RTL support
- Changed ScrollView child height to `wrap_content`
- Added ImageView `contentDescription`
- Removed the hard-coded JDK path from `gradle.properties`
- Added the standalone `ClockTimeFormatter` utility for unit testing
- Added 9 unit tests for `formatClockTime`

### Documentation
- Updated README / README-ZH from `compile` to `implementation`
- Fixed mixed-language snippets, default value notes, and typos
- Added CHANGELOG.md
- Added CONTRIBUTING.md
- Normalized source comments and public messages to English

## [1.1.3] - 2017-11-11
- Rewrote the code in Kotlin

## [1.1.1] - 2017-03-27
- Stopped running when exiting

## [1.1.0] - 2017-02-07
- Added clockwise animation support

## [1.0.0] - 2016-12-20
- Initial commit
