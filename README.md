[![twitter](https://img.shields.io/badge/twitter-sfyc23-blue.svg)](https://twitter.com/sfyc23)
[![微博](https://img.shields.io/badge/%E5%BE%AE%E5%8D%9A-sfyc23-blue.svg)](https://weibo.com/sfyc23)
[![API](https://img.shields.io/badge/API-%2B21-green.svg)](https://android-arsenal.com/api?level=21)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-2.1.0-green.svg)]()
[![License](https://img.shields.io/badge/License-Apache%202.0-red.svg)]()
# CountTimeProgressView

**CountTimeProgressView** - An Android library that provides a count time circular progress view effect.

[中文版][1]

## Sample
![页面动图][2]


## Usage

**For a working implementation of this project see the [CountTimeProgressView.kt][3] .**

### Step 1

Include the library as a local library project or add the dependency in your build.gradle.

```groovy
dependencies {
    implementation 'com.sfyc.ctpv:counttimeprogressview:2.1.0'
}
```

### Step 2

Include the CountTimeProgressView widget in your layout. And you can customize it like this.

```xml
<com.sfyc.ctpv.CountTimeProgressView
    android:id="@+id/countTimeProgressView"
    android:layout_width="84dp"
    android:layout_height="84dp"
    android:layout_alignParentEnd="true"
    app:backgroundColorCenter="#FF7F00"
    app:borderWidth="3dp"
    app:borderBottomColor="#D60000"
    app:borderDrawColor="#CDC8EA"
    app:markBallColor="#002FFF"
    app:markBallFlag="true"
    app:markBallWidth="3dp"
    app:titleCenterColor="#000000"
    app:titleCenterText="Click to skip"
    app:titleCenterSize="14sp"
    app:countTime="5000"
    app:startAngle="0"
    app:textStyle="jump"
    app:clockwise="true"
    app:warningTime="3000"
    app:warningColor="#FF3B30"
    app:clickableAfter="2000"
    app:disabledText="Please wait"
    />
```
### Step 3

You can write some animation codes to the callbacks such as addOnEndListener, etc in your Activity.

in Kotlin:
```kotlin
with(countTimeProgressView) {
    startAngle = 0f
    countTime = 6000L
    textStyle = CountTimeProgressView.TEXT_STYLE_SECOND
    borderWidth = 4f
    borderBottomColor = Color.GRAY
    borderDrawColor = Color.RED
    backgroundColorCenter = Color.WHITE
    markBallFlag = true
    markBallWidth = 6f
    markBallColor = Color.GREEN
    titleCenterText = "Skip（%s）s"
    titleCenterTextColor = Color.BLACK
    titleCenterTextSize = 16f

    // v2.1 Warning threshold: progress bar turns red in last 3 seconds
    warningTime = 3000L
    warningColor = Color.parseColor("#FF3B30")

    // v2.1 Skip delay: not clickable for the first 2 seconds
    clickableAfterMillis = 2000L
    disabledText = "Please wait"

    // Lambda-based callbacks (recommended)
    setOnCountdownEndListener {
        Toast.makeText(this@SimpleActivity, "Time's up", Toast.LENGTH_SHORT).show()
    }
    setOnClickCallback { overageTime ->
        if (isRunning) cancelCountTimeAnimation()
        else startCountTimeAnimation()
    }

    // v2.1 State change callback
    setOnStateChangedListener { state ->
        Log.d("Demo", "State: $state")  // IDLE, RUNNING, PAUSED, CANCELED, FINISHED
    }

    // v2.1 Per-second tick callback (ideal for verification code, ad skip button)
    setOnTickListener { remainingMillis, remainingSeconds ->
        Log.d("Demo", "Remaining: ${remainingSeconds}s")
    }

    // v2.1 Warning threshold callback
    setOnWarningListener { remainingMillis ->
        Log.d("Demo", "About to finish!")
    }

    // Progress listener (fires every frame)
    addOnProgressChangedListener { progress, remainingMillis ->
        Log.d("Demo", "progress=$progress, remaining=$remainingMillis")
    }

    // v2.1 Bind lifecycle for automatic pause/resume
    bindLifecycle(this@SimpleActivity)

    startCountTimeAnimation()

    // v2.1 Start from a specific progress or remaining time
    // startCountTimeAnimation(fromProgress = 0.5f)
    // startCountTimeAnimationFromRemaining(3000L)
}
```

in Java:
```java
countTimeProgressView.setBackgroundColorCenter(Color.WHITE);
countTimeProgressView.setBorderWidth(3);
countTimeProgressView.setBorderBottomColor(Color.GRAY);
countTimeProgressView.setBorderDrawColor(Color.RED);
countTimeProgressView.setMarkBallColor(Color.GREEN);

countTimeProgressView.setMarkBallFlag(true);
countTimeProgressView.setMarkBallWidth(4);
countTimeProgressView.setTitleCenterText("");
countTimeProgressView.setTitleCenterTextSize(16);
countTimeProgressView.setTitleCenterTextColor(Color.BLACK);

countTimeProgressView.setCountTime(5000L);
countTimeProgressView.setStartAngle(0);
countTimeProgressView.setTextStyle(CountTimeProgressView.TEXT_STYLE_CLOCK);
countTimeProgressView.setClockwise(true);

// v2.1 Warning threshold
countTimeProgressView.setWarningTime(3000L);
countTimeProgressView.setWarningColor(Color.parseColor("#FF3B30"));

// v2.1 Skip delay
countTimeProgressView.setClickableAfterMillis(2000L);
countTimeProgressView.setDisabledText("Please wait");

countTimeProgressView.setOnCountdownEndListener(() -> {
    Toast.makeText(SimpleActivity.this, "Time's up", Toast.LENGTH_SHORT).show();
});

// v2.1 State callback
countTimeProgressView.setOnStateChangedListener(state -> {
    Log.d("Demo", "State: " + state);
});

// v2.1 Per-second tick callback
countTimeProgressView.setOnTickListener((remainingMillis, remainingSeconds) -> {
    Log.d("Demo", "Remaining: " + remainingSeconds + "s");
});

// v2.1 Bind lifecycle
countTimeProgressView.bindLifecycle(this);

countTimeProgressView.startCountTimeAnimation();

// v2.1 Start from specific progress
// countTimeProgressView.startCountTimeAnimation(0.5f);
// countTimeProgressView.startCountTimeAnimationFromRemaining(3000L);
```

## New in 2.1.0

- **Countdown state model**: New `CountdownState` enum (`IDLE` / `RUNNING` / `PAUSED` / `CANCELED` / `FINISHED`) with `setOnStateChangedListener { state -> }` for unified state tracking
- **Per-second tick callback**: `setOnTickListener { remainingMillis, remainingSeconds -> }` — fires only on second change, ideal for verification codes, ad skip buttons, and logging
- **Warning threshold**: `warningTime = 3000L`, `warningColor = Color.RED` — automatically changes progress bar color and fires `setOnWarningListener` when remaining time drops below threshold (XML: `app:warningTime`, `app:warningColor`)
- **Start from specific progress**: `startCountTimeAnimation(fromProgress = 0.5f)` or `startCountTimeAnimationFromRemaining(3000L)` — supports list recycling, server-provided remaining time
- **Skip button delay**: `clickableAfterMillis = 2000L`, `disabledText = "Please wait"` — prevents clicks for the first N seconds (XML: `app:clickableAfter`, `app:disabledText`)
- **Fixed wasRunning restore**: animation now auto-resumes from saved progress after screen rotation
- **Lifecycle auto pause/resume**: `bindLifecycle(lifecycleOwner)` — automatically pauses on background, resumes on foreground
- **Code cleanup**: removed unused `displayText` dead code

## New in 2.0.0

- **Pause / Resume / Reset** animation: `pauseCountTimeAnimation()`, `resumeCountTimeAnimation()`, `resetCountTimeAnimation()`
- **Progress getter/setter**: `progress` property (0f..1f)
- **Progress change callback**: `addOnProgressChangedListener { progress, remainingMillis -> }`
- **Custom text formatter**: `textFormatter = { millis -> "Custom: ${millis/1000}s" }`
- **StrokeCap configuration**: `strokeCap = Paint.Cap.ROUND` (XML: `app:strokeCap="round"`)
- **Gradient progress bar**: `setGradientColors(startColor, endColor)` (XML: `app:gradientStartColor`, `app:gradientEndColor`)
- **Interpolator configuration**: `interpolator = AccelerateDecelerateInterpolator()`
- **Auto-start**: `autoStart = true` (XML: `app:autoStart="true"`)
- **Finished text**: `finishedText = "Done"` (XML: `app:finishedText="Done"`)
- **Show/hide center text**: `showCenterText = false` (XML: `app:showCenterText="false"`)
- **Explicit unit APIs**: `setBorderWidthPx()`, `setMarkBallWidthPx()`, `setTitleCenterTextSizePx()`
- **SavedState support**: progress is preserved across configuration changes
- **Accessibility support**: automatic contentDescription updates
- **wrap_content support**: default size 84dp
- **Separated callbacks**: `OnCountdownEndListener` + `setOnClickCallback()` (lambda-friendly)
- **Jetpack Compose adapter**: `CountTimeProgressViewCompose.create(context) { ... }` for use in `AndroidView`
- **Zero library dependencies**: library module has no runtime dependency on AppCompat

## Customization

Please feel free to :)

| name | format | description | default |
|:---:|:---:|:---:|:---:|
| backgroundColorCenter | color | Background color of the circle | #00BCD4 |
| borderWidth | dimension | Circular locus border width | 3dp |
| borderDrawColor | color | Draw color | #4dd0e1 |
| borderBottomColor | color | Bottom color | #D32F2F |
| markBallWidth | dimension | Ball width | 6dp |
| markBallColor | color | Ball color | #536DFE |
| markBallFlag | boolean | Ball is displayed | true |
| startAngle | float | Start angle (0 = top) | 0f |
| clockwise | boolean | Clockwise or counter-clockwise | true |
| countTime | integer | Count time in milliseconds | 5000 |
| textStyle | enum | Center text style: jump, second, clock, none | jump |
| titleCenterText | string | Center text (displayed when textStyle is "jump") | "jump" |
| titleCenterColor | color | Center text color | #FFFFFF |
| titleCenterSize | dimension | Center text size | 16sp |
| autoStart | boolean | Start animation on attach | false |
| finishedText | string | Text to display when countdown ends | - |
| showCenterText | boolean | Whether to show center text | true |
| strokeCap | enum | Progress bar end cap: butt, round, square | butt |
| gradientStartColor | color | Gradient start color for progress bar | - |
| gradientEndColor | color | Gradient end color for progress bar | - |
| warningTime | integer | Warning threshold in ms, triggers when remaining ≤ value | 0 |
| warningColor | color | Progress bar color in warning state | #FF3B30 |
| clickableAfter | integer | Milliseconds after start before clicks are accepted | 0 |
| disabledText | string | Text shown during non-clickable period | - |

> **Unit note**: `borderWidth`, `markBallWidth` setter accepts dp value and stores pixel internally. `titleCenterTextSize` setter accepts sp value. Use `setBorderWidthPx()`, `setMarkBallWidthPx()`, `setTitleCenterTextSizePx()` for pixel-based values.

## Change Log

2.1.0 (2026-04-16)
- Added countdown state model `CountdownState` (IDLE / RUNNING / PAUSED / CANCELED / FINISHED) and `setOnStateChangedListener` callback
- Added per-second tick callback `setOnTickListener`, fires only on second change
- Added warning threshold `warningTime` / `warningColor` / `setOnWarningListener` with XML support
- Added start from specific progress `startCountTimeAnimation(fromProgress)` and `startCountTimeAnimationFromRemaining(millis)`
- Added skip button delay `clickableAfterMillis` / `disabledText` with XML support
- Fixed `wasRunning` restore: animation auto-resumes from saved progress after screen rotation
- Added lifecycle auto pause/resume `bindLifecycle(lifecycleOwner)`
- Removed unused `displayText` dead code

2.0.0 (2026-04-15)
- Fixed CLOCK mode minute formatting bug
- Fixed startAngle getter/setter semantic inconsistency
- Fixed titleCenterText null pointer risk
- Fixed DEFAULT_COUNT_TIME from 5ms to 5000ms
- Fixed Demo setBackgroundColor call
- Added LinearInterpolator as default
- Added pause/resume/reset animation
- Added progress getter/setter and progress change callback
- Added custom text formatter
- Added StrokeCap configuration (XML support)
- Added gradient progress bar (SweepGradient, XML support)
- Added interpolator configuration
- Added autoStart, finishedText, showCenterText XML attributes
- Added explicit unit APIs: setBorderWidthPx(), setMarkBallWidthPx(), setTitleCenterTextSizePx()
- Added onMeasure wrap_content support
- Added SavedState support
- Added accessibility support
- Added Jetpack Compose adapter (CountTimeProgressViewCompose)
- Refactored onDraw into smaller methods
- Performance: cached text, postInvalidateOnAnimation
- Removed AppCompat dependency from library
- Migrated to AndroidX
- Raised minSdk to 21
- Converted SplashActivity to Kotlin
- Separated OnEndListener into OnCountdownEndListener + setOnClickCallback
- Fixed lint warnings: left→start, ScrollView child wrap_content, contentDescription
- Added CHANGELOG.md and CONTRIBUTING.md

1.1.3 (2017-11-11)
Rebuild code by Kotlin

1.1.1 (2017-3-27)
Update: stop running when exiting

1.1.0 (2017-2-7)
Update: anim clockwise

1.0.0 (2016-12-20)
First commit


