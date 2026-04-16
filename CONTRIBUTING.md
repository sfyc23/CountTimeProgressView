# 贡献指南

感谢你对 CountTimeProgressView 项目的关注！欢迎任何形式的贡献。

## 如何贡献

### 报告 Bug

1. 在 [Issues](https://github.com/sfyc23/CountTimeProgressView/issues) 中搜索是否已有相同问题
2. 如果没有，创建新 Issue，请包含：
   - 问题描述
   - 复现步骤
   - 期望行为 vs 实际行为
   - 设备信息（Android 版本、设备型号）
   - 相关代码片段或截图

### 提交代码

1. Fork 本项目
2. 创建功能分支：`git checkout -b feature/your-feature`
3. 编写代码，确保：
   - 遵循现有代码风格（Kotlin）
   - 添加必要的中文注释
   - 新增公开 API 需添加 KDoc
   - 如有新功能，更新 README 和 CHANGELOG
4. 运行构建验证：`./gradlew :library:assembleDebug :library:testDebugUnitTest`
5. 提交变更：`git commit -m "feat: 你的功能描述"`
6. 推送并创建 Pull Request

### Commit 规范

建议使用以下前缀：
- `feat:` 新功能
- `fix:` Bug 修复
- `docs:` 文档更新
- `refactor:` 重构（不改变行为）
- `perf:` 性能优化
- `test:` 测试相关
- `chore:` 构建/工具链变更

## 开发环境

- Android Studio Hedgehog 或更新版本
- JDK 17
- Gradle 7.6+
- Kotlin 1.8.22+

## 项目结构

```
CountTimeProgressView/
├── library/          # 核心库模块（零第三方依赖）
│   └── src/main/java/com/sfyc/ctpv/
│       ├── CountTimeProgressView.kt       # 核心控件
│       ├── CountTimeProgressViewCompose.kt # Compose 适配
│       └── ClockTimeFormatter.kt          # 时钟格式化工具
├── app/              # 示例 App 模块
└── .AI/              # 重构分析文档
```

## 编码规范

- 使用 Kotlin，避免 Java
- 公开 API 添加 KDoc 注释（中文）
- 属性 setter 中 dp/sp 转换需在注释中标明单位
- `onDraw` 中避免创建对象
- 新增 XML 属性需同步更新 `attrs.xml`

## 许可证

贡献的代码将遵循本项目的 [Apache License 2.0](LICENSE) 许可证。
