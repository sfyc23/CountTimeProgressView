[![twitter](https://img.shields.io/badge/twitter-sfyc23-blue.svg)](https://twitter.com/sfyc23)
[![微博](https://img.shields.io/badge/%E5%BE%AE%E5%8D%9A-sfyc23-blue.svg)](https://weibo.com/sfyc23)
[![API](https://img.shields.io/badge/API-%2B21-green.svg)](https://android-arsenal.com/api?level=21)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-2.1.0-green.svg)]()
[![License](https://img.shields.io/badge/License-Apache%202.0-red.svg)]()
# CountTimeProgressView

**CountTimeProgressView** - 一个用 Kotlin 实现的有倒计时功能的，并能显现进度的 Android 库。


## Sample
![页面动图][1]


## 用法

**关于这个项目的实现功能，你可以通过 lib 包下的 [CountTimeProgressView][2] 查看实现**

### Step 1

你可以将该库作为本地库项目加入，或者在 build.gradle 中添加依赖项。

```groovy
dependencies {
    implementation 'com.sfyc.ctpv:counttimeprogressview:2.1.0'
}
```

### Step 2

在你的 layout 中添加 CountTimeProgressView 控件，并可以通过 XML 属性自定义样式。

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
    app:titleCenterText="点击跳过"
    app:titleCenterSize="14sp"
    app:countTime="5000"
    app:startAngle="0"
    app:textStyle="jump"
    app:clockwise="true"
    app:warningTime="3000"
    app:warningColor="#FF3B30"
    app:clickableAfter="2000"
    app:disabledText="请稍候"
    />
```
### Step 3

你可以在代码中改变 CountTimeProgressView 属性，并添加回调。例如：

Kotlin:
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
    titleCenterText = "跳过（%s）s"
    titleCenterTextColor = Color.BLACK
    titleCenterTextSize = 16f

    // v2.1 阈值提醒：最后 3 秒进度条变红
    warningTime = 3000L
    warningColor = Color.parseColor("#FF3B30")

    // v2.1 跳过延迟：前 2 秒不可点击
    clickableAfterMillis = 2000L
    disabledText = "请稍候"

    // Lambda 回调（推荐）
    setOnCountdownEndListener {
        Toast.makeText(this@SimpleActivity, "时间到", Toast.LENGTH_SHORT).show()
    }
    setOnClickCallback { overageTime ->
        if (isRunning) cancelCountTimeAnimation()
        else startCountTimeAnimation()
    }

    // v2.1 状态变更回调
    setOnStateChangedListener { state ->
        Log.d("Demo", "状态: $state")  // IDLE, RUNNING, PAUSED, CANCELED, FINISHED
    }

    // v2.1 按秒 Tick 回调（适合验证码倒计时、广告跳过）
    setOnTickListener { remainingMillis, remainingSeconds ->
        Log.d("Demo", "剩余 ${remainingSeconds}s")
    }

    // v2.1 警告阈值回调
    setOnWarningListener { remainingMillis ->
        Log.d("Demo", "即将结束！")
    }

    // 进度变化回调（每帧触发）
    addOnProgressChangedListener { progress, remainingMillis ->
        Log.d("Demo", "progress=$progress, remaining=$remainingMillis")
    }

    // v2.1 绑定 Lifecycle，进入后台自动暂停、恢复
    bindLifecycle(this@SimpleActivity)

    startCountTimeAnimation()

    // v2.1 也可以从指定进度或剩余时间启动
    // startCountTimeAnimation(fromProgress = 0.5f)
    // startCountTimeAnimationFromRemaining(3000L)
}
```

Java:
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

// v2.1 阈值提醒
countTimeProgressView.setWarningTime(3000L);
countTimeProgressView.setWarningColor(Color.parseColor("#FF3B30"));

// v2.1 跳过延迟
countTimeProgressView.setClickableAfterMillis(2000L);
countTimeProgressView.setDisabledText("请稍候");

countTimeProgressView.setOnCountdownEndListener(() -> {
    Toast.makeText(SimpleActivity.this, "时间到", Toast.LENGTH_SHORT).show();
});

// v2.1 状态回调
countTimeProgressView.setOnStateChangedListener(state -> {
    Log.d("Demo", "状态: " + state);
});

// v2.1 按秒 Tick 回调
countTimeProgressView.setOnTickListener((remainingMillis, remainingSeconds) -> {
    Log.d("Demo", "剩余 " + remainingSeconds + "s");
});

// v2.1 绑定 Lifecycle
countTimeProgressView.bindLifecycle(this);

countTimeProgressView.startCountTimeAnimation();

// v2.1 也可以从指定进度启动
// countTimeProgressView.startCountTimeAnimation(0.5f);
// countTimeProgressView.startCountTimeAnimationFromRemaining(3000L);
```

## 2.1.0 新特性

- **倒计时状态模型**：新增 `CountdownState` 枚举（`IDLE` / `RUNNING` / `PAUSED` / `CANCELED` / `FINISHED`），通过 `setOnStateChangedListener { state -> }` 统一监听
- **按秒 Tick 回调**：`setOnTickListener { remainingMillis, remainingSeconds -> }`，仅在秒数变化时触发，适合验证码倒计时、广告跳过按钮等场景
- **阈值提醒**：`warningTime = 3000L`、`warningColor = Color.RED`，剩余时间不足时自动切换进度条颜色并触发 `setOnWarningListener` 回调（支持 XML: `app:warningTime`、`app:warningColor`）
- **从指定进度启动**：`startCountTimeAnimation(fromProgress = 0.5f)` 或 `startCountTimeAnimationFromRemaining(3000L)`，支持列表复用、服务端剩余时间续播
- **跳过按钮可用延迟**：`clickableAfterMillis = 2000L`、`disabledText = "请稍候"`，前 N 秒不可点击（支持 XML: `app:clickableAfter`、`app:disabledText`）
- **修复 wasRunning 配置恢复**：屏幕旋转后动画自动从保存进度续播
- **Lifecycle 自动暂停/恢复**：`bindLifecycle(lifecycleOwner)`，进入后台自动暂停、回到前台自动恢复
- **代码健康度**：清理未使用的 `displayText` 死代码

## 2.0.0 新特性

- **暂停 / 恢复 / 重置** 动画：`pauseCountTimeAnimation()`、`resumeCountTimeAnimation()`、`resetCountTimeAnimation()`
- **进度 getter/setter**：`progress` 属性（0f..1f）
- **进度变化回调**：`addOnProgressChangedListener { progress, remainingMillis -> }`
- **自定义文本格式化器**：`textFormatter = { millis -> "自定义: ${millis/1000}s" }`
- **StrokeCap 配置**：`strokeCap = Paint.Cap.ROUND`（XML: `app:strokeCap="round"`）
- **渐变色进度条**：`setGradientColors(startColor, endColor)`（XML: `app:gradientStartColor`、`app:gradientEndColor`）
- **插值器配置**：`interpolator = AccelerateDecelerateInterpolator()`
- **自动启动**：`autoStart = true`（XML: `app:autoStart="true"`）
- **结束文本**：`finishedText = "完成"`（XML: `app:finishedText="完成"`）
- **显示/隐藏中心文字**：`showCenterText = false`（XML: `app:showCenterText="false"`）
- **明确单位 API**：`setBorderWidthPx()`、`setMarkBallWidthPx()`、`setTitleCenterTextSizePx()`
- **SavedState 支持**：屏幕旋转时保持进度
- **无障碍支持**：自动更新 contentDescription
- **wrap_content 支持**：默认尺寸 84dp
- **回调分离**：`OnCountdownEndListener` + `setOnClickCallback()`（支持 lambda）
- **Jetpack Compose 适配**：`CountTimeProgressViewCompose.create(context) { ... }` 用于 `AndroidView`
- **零运行时依赖**：library 模块不依赖 AppCompat

## 自定义属性说明

可自由灵活定制 :)

| 名称 | 格式 | 说明 | 默认值 |
| :---: | :---: | :---: | :---: |
| backgroundColorCenter | color | 圆环背景颜色 | #00BCD4 |
| borderWidth | dimension | 圆形轨迹边框宽度 | 3dp |
| borderDrawColor | color | 绘制颜色 | #4dd0e1 |
| borderBottomColor | color | 底部颜色 | #D32F2F |
| markBallWidth | dimension | 小球宽度 | 6dp |
| markBallColor | color | 小球颜色 | #536DFE |
| markBallFlag | boolean | 是否显示小球 | true |
| startAngle | float | 起始角度（0 = 顶部） | 0f |
| clockwise | boolean | 顺时针或逆时针 | true |
| countTime | integer | 倒计时时间（毫秒） | 5000 |
| textStyle | enum | 中心文字显示风格：jump、second、clock、none | jump |
| titleCenterText | string | 中心文字内容（仅 textStyle 为 jump 时显示） | "jump" |
| titleCenterColor | color | 中心文字颜色 | #FFFFFF |
| titleCenterSize | dimension | 中心文字字体大小 | 16sp |
| autoStart | boolean | 是否在 attach 后自动开始倒计时 | false |
| finishedText | string | 倒计时结束后显示的文本 | - |
| showCenterText | boolean | 是否显示中心文字 | true |
| strokeCap | enum | 进度条端点样式：butt、round、square | butt |
| gradientStartColor | color | 渐变色起始颜色 | - |
| gradientEndColor | color | 渐变色结束颜色 | - |
| warningTime | integer | 警告阈值（毫秒），剩余时间 ≤ 此值时触发 | 0 |
| warningColor | color | 警告状态下的进度条颜色 | #FF3B30 |
| clickableAfter | integer | 倒计时开始后经过多少毫秒才可点击 | 0 |
| disabledText | string | 不可点击期间显示的替代文本 | - |

> **单位说明**：`borderWidth`、`markBallWidth` 的 setter 接收 dp 值，内部存储为像素值。`titleCenterTextSize` 的 setter 接收 sp 值。如需以像素为单位设置，可使用 `setBorderWidthPx()`、`setMarkBallWidthPx()`、`setTitleCenterTextSizePx()`。

## Change Log

2.1.0 (2026-04-16)
- 新增倒计时状态模型 `CountdownState`（IDLE / RUNNING / PAUSED / CANCELED / FINISHED）及 `setOnStateChangedListener` 回调
- 新增按秒 Tick 回调 `setOnTickListener`，仅在秒数变化时触发
- 新增阈值提醒 `warningTime` / `warningColor` / `setOnWarningListener`，支持 XML 配置
- 新增从指定进度启动 `startCountTimeAnimation(fromProgress)` 和 `startCountTimeAnimationFromRemaining(millis)`
- 新增跳过按钮可用延迟 `clickableAfterMillis` / `disabledText`，支持 XML 配置
- 修复 `wasRunning` 配置恢复：屏幕旋转后动画自动续播
- 新增 Lifecycle 自动暂停/恢复 `bindLifecycle(lifecycleOwner)`
- 清理未使用的 `displayText` 死代码

2.0.0 (2026-04-15)
- 修复 CLOCK 模式分钟格式化 Bug
- 修复 startAngle getter/setter 语义不一致
- 修复 titleCenterText 空指针风险
- 修复 DEFAULT_COUNT_TIME 从 5ms 到 5000ms
- 修复 Demo 中 setBackgroundColor 调用错误
- 设置 LinearInterpolator 为默认插值器
- 新增暂停/恢复/重置动画
- 新增进度 getter/setter 和进度变化回调
- 新增自定义文本格式化器
- 新增 StrokeCap 配置（支持 XML）
- 新增渐变色进度条（SweepGradient，支持 XML）
- 新增插值器配置
- 新增 autoStart、finishedText、showCenterText XML 属性
- 新增显式单位 API：setBorderWidthPx()、setMarkBallWidthPx()、setTitleCenterTextSizePx()
- 新增 onMeasure wrap_content 支持
- 新增 SavedState 支持
- 新增无障碍支持
- 新增 Jetpack Compose 适配层（CountTimeProgressViewCompose）
- 重构 onDraw 为多个小方法
- 性能优化：缓存文本、postInvalidateOnAnimation
- library 模块移除 AppCompat 依赖
- 迁移至 AndroidX
- minSdk 提升至 21
- SplashActivity 转换为 Kotlin
- 回调拆分为 OnCountdownEndListener + setOnClickCallback
- 修复 lint 告警：left→start、ScrollView 子 View wrap_content、contentDescription
- 新增 CHANGELOG.md 和 CONTRIBUTING.md

1.1.3 (2017-11-11)
使用 Kotlin 重写代码

1.1.1 (2017-3-27)
更新：退出时停止运行

1.1.0 (2017-2-7)
更新：支持顺时针动画

1.0.0 (2016-12-20)
首次提交


## Thanks

Inspired by

[tangqi92][3] created by [WaveLoadingView][4]


  [1]: https://github.com/sfyc23/CountTimeProgressView/blob/master/screenshot/ctpv-video-to-gif.gif
  [2]: https://github.com/sfyc23/CountTimeProgressView/blob/master/library/src/main/java/com/sfyc/ctpv/CountTimeProgressView.kt
  [3]: https://github.com/tangqi92
  [4]: https://github.com/tangqi92/WaveLoadingView
