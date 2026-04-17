[![twitter](https://img.shields.io/badge/twitter-sfyc23-blue.svg)](https://twitter.com/sfyc23)
[![微博](https://img.shields.io/badge/%E5%BE%AE%E5%8D%9A-sfyc23-blue.svg)](https://weibo.com/sfyc23)
[![API](https://img.shields.io/badge/API-%2B21-green.svg)](https://android-arsenal.com/api?level=21)
[![JitPack](https://jitpack.io/v/sfyc23/CountTimeProgressView.svg)](https://jitpack.io/#sfyc23/CountTimeProgressView)
[![License](https://img.shields.io/badge/License-Apache%202.0-red.svg)](LICENSE)

# CountTimeProgressView

> **中文** ｜ [English](README-EN.md)

**CountTimeProgressView** 是一个用 Kotlin 实现的 Android 圆形倒计时进度控件。开箱即用、零运行时依赖、同时支持传统 **View (XML)** 与 **Jetpack Compose**，并内置了完整的状态机、生命周期自动暂停/恢复、阈值提醒、断点续播等生产级能力。

最低支持 **Android 5.0 (API 21)**。

---

## 预览

<p align="center">
  <img src="screenshot/menu.png" width="300" alt="Demo 菜单" />
  &nbsp;&nbsp;
  <img src="screenshot/features.png" width="300" alt="动画控制与实时状态" />
</p>

Demo 内置了常见的真实场景：**广告跳过 / 验证码倒计时 / 进度恢复 / 考试计时器**，并分别提供 `Java + XML`、`Kotlin + XML`、`Jetpack Compose` 三种接入示例。

---

## 特性亮点

- **一行接入**：传统 View / Jetpack Compose 双支持，零 AppCompat 依赖
- **完整状态机**：`IDLE` → `RUNNING` → `PAUSED` / `CANCELED` / `FINISHED`，一个回调跟踪全部状态
- **多种文字风格**：`jump`（跳过文案）、`second`（秒数）、`clock`（时钟 mm:ss）、`none`（无文字），还支持自定义 `textFormatter`
- **阈值提醒**：倒计时进入最后 N 秒自动变色并触发回调（例如最后 3 秒变红）
- **按秒 Tick 回调**：仅在秒数变化时触发，完美适配验证码按钮、广告跳过按钮
- **断点续播**：支持 `fromProgress` 或 `fromRemaining` 启动，屏幕旋转 / 列表复用 / 服务端续播一把梭
- **生命周期感知**：`bindLifecycle(owner)` 进入后台自动暂停、回到前台自动恢复
- **丰富视觉定制**：渐变色进度条、`StrokeCap` 端点样式、小球标记、自定义插值器
- **点击延迟**：前 N 秒禁止点击，配合 `disabledText` 防止误触（适合 SplashAd）
- **无障碍友好**：自动更新 `contentDescription`，`wrap_content` 默认 84dp

---

## 安装

### Step 1. 添加 JitPack 仓库

项目根目录的 `settings.gradle`（或 `build.gradle`）中加入 JitPack：

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2. 添加依赖

在 app 模块的 `build.gradle` 中加入：

```groovy
dependencies {
    implementation 'com.github.sfyc23:CountTimeProgressView:2.1.0'
}
```

> Release 会随 git tag 自动发布到 JitPack，最新版本见顶部徽章。

---

## 快速开始

### 方式一：XML 布局

```xml
<com.sfyc.ctpv.CountTimeProgressView
    android:id="@+id/countTimeProgressView"
    android:layout_width="84dp"
    android:layout_height="84dp"
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
    app:disabledText="请稍候" />
```

### 方式二：Kotlin 代码

```kotlin
with(countTimeProgressView) {
    countTime = 6000L
    textStyle = CountTimeProgressView.TEXT_STYLE_SECOND
    titleCenterText = "跳过（%s）s"

    // v2.1 最后 3 秒变红
    warningTime = 3000L
    warningColor = Color.parseColor("#FF3B30")

    // v2.1 前 2 秒不可点击
    clickableAfterMillis = 2000L
    disabledText = "请稍候"

    // 倒计时结束
    setOnCountdownEndListener {
        Toast.makeText(context, "时间到", Toast.LENGTH_SHORT).show()
    }

    // 点击回调（带剩余时间）
    setOnClickCallback { overageTime ->
        if (isRunning) cancelCountTimeAnimation() else startCountTimeAnimation()
    }

    // 状态机统一监听
    setOnStateChangedListener { state ->
        Log.d("CTPV", "state=$state") // IDLE / RUNNING / PAUSED / CANCELED / FINISHED
    }

    // 按秒触发，适合验证码、广告跳过
    setOnTickListener { remainingMillis, remainingSeconds ->
        Log.d("CTPV", "剩余 ${remainingSeconds}s")
    }

    // 阈值提醒
    setOnWarningListener { remainingMillis ->
        Log.d("CTPV", "即将结束！")
    }

    // 每一帧的进度回调
    addOnProgressChangedListener { progress, remainingMillis ->
        Log.d("CTPV", "progress=$progress, remaining=$remainingMillis")
    }

    // 生命周期自动暂停 / 恢复
    bindLifecycle(this@SimpleActivity)

    startCountTimeAnimation()
    // 也可以从指定进度 / 剩余时间启动：
    // startCountTimeAnimation(fromProgress = 0.5f)
    // startCountTimeAnimationFromRemaining(3000L)
}
```

### 方式三：Java 代码

```java
countTimeProgressView.setCountTime(5000L);
countTimeProgressView.setTextStyle(CountTimeProgressView.TEXT_STYLE_CLOCK);
countTimeProgressView.setWarningTime(3000L);
countTimeProgressView.setWarningColor(Color.parseColor("#FF3B30"));
countTimeProgressView.setClickableAfterMillis(2000L);
countTimeProgressView.setDisabledText("请稍候");

countTimeProgressView.setOnCountdownEndListener(() ->
    Toast.makeText(this, "时间到", Toast.LENGTH_SHORT).show());

countTimeProgressView.setOnStateChangedListener(state ->
    Log.d("CTPV", "state=" + state));

countTimeProgressView.setOnTickListener((remainingMillis, remainingSeconds) ->
    Log.d("CTPV", "剩余 " + remainingSeconds + "s"));

countTimeProgressView.bindLifecycle(this);
countTimeProgressView.startCountTimeAnimation();
```

### 方式四：Jetpack Compose

库自带轻量 Compose 适配层 `CountTimeProgressViewCompose`，无需额外依赖 Compose，直接在 `AndroidView` 中使用：

```kotlin
AndroidView(
    modifier = Modifier.size(84.dp),
    factory = { ctx ->
        CountTimeProgressViewCompose.create(ctx) {
            countTime = 5000L
            textStyle = CountTimeProgressView.TEXT_STYLE_SECOND
            warningTime = 3000L
            warningColor = Color.RED
            setOnStateChangedListener { state -> /* ... */ }
            setOnTickListener { _, sec -> /* ... */ }
            startCountTimeAnimation()
        }
    },
    update = { view ->
        CountTimeProgressViewCompose.update(view) {
            // 响应 Compose State 变化
        }
    }
)
```

---

## 典型应用场景

| 场景 | 推荐配置 |
| :--- | :--- |
| **闪屏广告跳过按钮** | `textStyle=jump` + `clickableAfterMillis` + `disabledText` 防止误触 |
| **验证码倒计时按钮** | `textStyle=second` + `setOnTickListener`（仅秒变化触发） |
| **服务端剩余时间续播** | `startCountTimeAnimationFromRemaining(remainMs)` |
| **列表项复用倒计时** | `startCountTimeAnimation(fromProgress = 0.7f)` + `bindLifecycle` |
| **考试 / 长时间计时** | `textStyle=clock`（mm:ss）+ `warningTime` 最后阶段变红提醒 |
| **Compose 页面接入** | `AndroidView` + `CountTimeProgressViewCompose.create` |

---

## 状态机说明

```
          startCountTimeAnimation()
  IDLE ─────────────────────────────▶ RUNNING
   ▲                           │  ▲  │
   │ resetCountTimeAnimation() │  │  │ pause / resume
   │                           │  │  ▼
   └─────────── CANCELED / FINISHED ─── PAUSED
```

- `IDLE`：初始 / 重置后
- `RUNNING`：正在计时
- `PAUSED`：手动或生命周期暂停
- `CANCELED`：被 `cancelCountTimeAnimation()` 中止
- `FINISHED`：自然结束，此时触发 `OnCountdownEndListener`

使用 `setOnStateChangedListener { state -> ... }` 即可在同一处监听全部状态变化。

---

## 全部 XML 属性

| 属性 | 类型 | 说明 | 默认值 |
| :--- | :--- | :--- | :--- |
| `backgroundColorCenter` | color | 圆环背景色 | `#00BCD4` |
| `borderWidth` | dimension | 轨迹边框宽度 | `3dp` |
| `borderDrawColor` | color | 进度绘制色 | `#4dd0e1` |
| `borderBottomColor` | color | 轨迹底色 | `#D32F2F` |
| `markBallWidth` | dimension | 标记小球宽度 | `6dp` |
| `markBallColor` | color | 标记小球颜色 | `#536DFE` |
| `markBallFlag` | boolean | 是否显示小球 | `true` |
| `startAngle` | float | 起始角度（0 = 顶部） | `0f` |
| `clockwise` | boolean | 顺/逆时针 | `true` |
| `countTime` | integer | 倒计时总时长（毫秒） | `5000` |
| `textStyle` | enum | `jump` / `second` / `clock` / `none` | `jump` |
| `titleCenterText` | string | 中心文案（`jump` 模式） | `"jump"` |
| `titleCenterColor` | color | 中心文字颜色 | `#FFFFFF` |
| `titleCenterSize` | dimension | 中心文字字号 | `16sp` |
| `autoStart` | boolean | attach 后自动开始 | `false` |
| `finishedText` | string | 结束后显示的文案 | `-` |
| `showCenterText` | boolean | 是否显示中心文字 | `true` |
| `strokeCap` | enum | `butt` / `round` / `square` | `butt` |
| `gradientStartColor` | color | 渐变起始色 | `-` |
| `gradientEndColor` | color | 渐变结束色 | `-` |
| `warningTime` | integer | 警告阈值（毫秒，剩余 ≤ 此值触发） | `0` |
| `warningColor` | color | 警告状态进度条颜色 | `#FF3B30` |
| `clickableAfter` | integer | 开始后 N 毫秒才允许点击 | `0` |
| `disabledText` | string | 不可点击期间替代文案 | `-` |

> **单位说明**：`borderWidth`、`markBallWidth` 的 setter 接收 **dp** 值，内部转为像素；`titleCenterTextSize` 接收 **sp** 值。如需直接传像素，请使用 `setBorderWidthPx()`、`setMarkBallWidthPx()`、`setTitleCenterTextSizePx()`。

---

## 常用 API 速查

| 方法 | 说明 |
| :--- | :--- |
| `startCountTimeAnimation()` | 从头开始倒计时 |
| `startCountTimeAnimation(fromProgress: Float)` | 从 `0f..1f` 指定进度开始 |
| `startCountTimeAnimationFromRemaining(millis: Long)` | 按剩余时间续播 |
| `pauseCountTimeAnimation()` / `resumeCountTimeAnimation()` | 暂停 / 恢复 |
| `cancelCountTimeAnimation()` | 取消（进入 `CANCELED`） |
| `resetCountTimeAnimation()` | 重置回 `IDLE` |
| `bindLifecycle(owner)` | 绑定生命周期，自动暂停 / 恢复 |
| `setOnCountdownEndListener { }` | 倒计时自然结束回调 |
| `setOnStateChangedListener { state -> }` | 状态机统一回调 |
| `setOnTickListener { ms, sec -> }` | 秒变化时回调 |
| `setOnWarningListener { ms -> }` | 进入警告阈值回调 |
| `setOnClickCallback { overage -> }` | 控件被点击，带剩余时间 |
| `addOnProgressChangedListener { p, ms -> }` | 每帧进度回调 |
| `setGradientColors(start, end)` | 设置渐变进度条 |
| `textFormatter = { millis -> "..." }` | 自定义中心文案 |

---

## 版本历史

### 2.1.0 (2026-04-16)

- 新增倒计时状态机 `CountdownState`（IDLE / RUNNING / PAUSED / CANCELED / FINISHED）及 `setOnStateChangedListener`
- 新增按秒 Tick 回调 `setOnTickListener`，仅在秒数变化时触发
- 新增阈值提醒 `warningTime` / `warningColor` / `setOnWarningListener`（XML 可配）
- 新增从指定进度 / 剩余时间启动：`startCountTimeAnimation(fromProgress)`、`startCountTimeAnimationFromRemaining(millis)`
- 新增跳过按钮可用延迟 `clickableAfterMillis` / `disabledText`（XML 可配）
- 修复 `wasRunning` 配置恢复，屏幕旋转后动画自动续播
- 新增 `bindLifecycle(owner)` 生命周期自动暂停 / 恢复
- 清理未使用的 `displayText` 死代码

### 2.0.0 (2026-04-15)

- 新增 **暂停 / 恢复 / 重置** 动画 API
- 新增 `progress` getter/setter 与每帧进度回调
- 新增 **渐变色进度条**（SweepGradient，XML 可配）
- 新增 **StrokeCap**、**Interpolator**、**autoStart**、**finishedText**、**showCenterText**
- 新增 **Jetpack Compose 适配层** `CountTimeProgressViewCompose`
- 新增 **SavedState** 与 **无障碍** 支持
- 新增 `wrap_content` 默认 84dp
- 回调拆分为 `OnCountdownEndListener` + `setOnClickCallback()`（支持 lambda）
- 迁移至 AndroidX，`minSdk` 提升至 21
- library 模块移除 AppCompat 依赖
- 其余修复与重构详见 [CHANGELOG.md](CHANGELOG.md)

### 历史版本

- **1.1.3 (2017-11-11)** — 使用 Kotlin 重写
- **1.1.1 (2017-03-27)** — 修复：退出时停止运行
- **1.1.0 (2017-02-07)** — 支持顺时针动画
- **1.0.0 (2016-12-20)** — 首次发布

---

## 贡献

欢迎提 Issue 与 PR！提交前请阅读 [CONTRIBUTING.md](CONTRIBUTING.md)。

## 许可

本项目基于 [Apache License 2.0](LICENSE) 开源。
