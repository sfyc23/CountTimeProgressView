# Changelog

本文件记录 CountTimeProgressView 的所有重要版本变更。

## [2.0.0] - 2026-04-15

### Bug 修复
- 修复 `CLOCK` 模式下 `minute >= 10` 时错误拼接 `hour` 的格式化 Bug
- 修复 `startAngle` getter/setter 语义不一致（用户设 0 读回 270 的问题）
- 修复 `titleCenterText` 在 SECOND 模式下的空指针崩溃风险
- 修复 `DEFAULT_COUNT_TIME` 从 5ms 修正为 5000ms
- 修复 Demo 中 `setBackgroundColor()` 误调用（应为 `backgroundColorCenter`）

### 新增功能
- 暂停 / 恢复 / 重置动画：`pauseCountTimeAnimation()`、`resumeCountTimeAnimation()`、`resetCountTimeAnimation()`
- 进度 getter/setter：`progress` 属性（0f..1f）
- 进度变化回调：`addOnProgressChangedListener { progress, remainingMillis -> }`
- 自定义文本格式化器：`textFormatter = { millis -> "..." }`
- 进度条端点样式：`strokeCap`（BUTT / ROUND / SQUARE），支持 XML 配置
- 动画插值器配置：`interpolator` 属性
- 渐变色进度条：`gradientStartColor` / `gradientEndColor`，支持 XML 配置
- XML 属性扩展：`autoStart`、`finishedText`、`showCenterText`、`strokeCap`
- `onMeasure` 支持 `wrap_content`（默认尺寸 84dp）
- SavedState 支持（屏幕旋转保持进度）
- 无障碍支持（AccessibilityNodeInfo）
- 显式单位 API：`setBorderWidthPx()`、`setMarkBallWidthPx()`、`setTitleCenterTextSizePx()`
- 公开 `remainingTime` 和 `isPaused` 属性
- Jetpack Compose 适配工具类 `CountTimeProgressViewCompose`

### 代码质量
- 设置 `LinearInterpolator` 为默认插值器
- 删除库中所有 `Log.e` 调用
- 移除冗余 `mContext` 字段
- 移除 `attr != null` 死代码分支
- `calcRadius()` 改为 `private`
- `TextStyle` 添加 `@IntDef` 类型安全注解
- `OnEndListener` 标记 `@Deprecated`，新增 `OnCountdownEndListener` + `setOnClickCallback`
- `onDetachedFromWindow` 不再清空 listener
- 重构 `onDraw` 为多个独立绘制方法
- 性能：文本缓存、`postInvalidateOnAnimation()` 替代 `invalidate()`

### 工程化
- library 模块移除 AppCompat 运行时依赖（零第三方依赖）
- 迁移至 AndroidX
- `minSdk` 14 → 21，`targetSdk` / `compileSdk` → 34
- 仓库改为 `google()` + `mavenCentral()`
- `lintOptions {}` → `lint {}`
- `proguard-android.txt` → `proguard-android-optimize.txt`
- `compileSdkVersion` → `compileSdk`，`minSdkVersion` → `minSdk`
- `artifactId` 从 `library` 改为 `counttimeprogressview`
- SplashActivity 从 Java 转换为 Kotlin
- 布局文件 `left/right` → `start/end`（RTL 支持）
- ScrollView 子 View 高度改为 `wrap_content`
- ImageView 添加 `contentDescription`
- 移除 `gradle.properties` 中硬编码的 JDK 路径
- 新增 `ClockTimeFormatter` 独立工具类（便于单元测试）
- 新增 `formatClockTime` 单元测试（9 个用例）

### 文档
- README / README-ZH：`compile` → `implementation`，修复中英文残片、默认值说明、拼写错误
- 新增 CHANGELOG.md
- 新增 CONTRIBUTING.md
- 添加完整 KDoc 中文注释

## [1.1.3] - 2017-11-11
- 使用 Kotlin 重写代码

## [1.1.1] - 2017-03-27
- 更新：退出时停止运行

## [1.1.0] - 2017-02-07
- 更新：支持顺时针动画

## [1.0.0] - 2016-12-20
- 首次提交
