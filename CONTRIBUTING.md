# Contributing Guide

Thanks for your interest in CountTimeProgressView. Contributions of all kinds are welcome.

## How to Contribute

### Report a Bug

1. Search [Issues](https://github.com/sfyc23/CountTimeProgressView/issues) for an existing report.
2. If no matching issue exists, create one and include:
   - Problem description
   - Steps to reproduce
   - Expected behavior vs actual behavior
   - Device information, including Android version and device model
   - Relevant code snippets or screenshots

### Submit Code

1. Fork this repository.
2. Create a feature branch: `git checkout -b feature/your-feature`.
3. Make your changes and ensure:
   - Code follows the existing Kotlin style.
   - Comments and public messages are written in English.
   - New public APIs include KDoc when needed.
   - New features update README and CHANGELOG as appropriate.
4. Run build verification: `./gradlew :library:assembleDebug :library:testDebugUnitTest`.
5. Commit changes: `git commit -m "feat: your feature description"`.
6. Push your branch and open a pull request.

### Commit Style

Recommended prefixes:
- `feat:` new feature
- `fix:` bug fix
- `docs:` documentation update
- `refactor:` refactor without behavior changes
- `perf:` performance improvement
- `test:` tests
- `chore:` build or tooling change

## Development Environment

- Android Studio Hedgehog or newer
- JDK 17
- Gradle 7.6+
- Kotlin 1.8.22+

## Project Structure

```text
CountTimeProgressView/
├── library/          # Core library module with no third-party runtime dependency
│   └── src/main/java/com/sfyc/ctpv/
│       ├── CountTimeProgressView.kt        # Core custom view
│       ├── CountTimeProgressViewCompose.kt # Compose helper
│       └── ClockTimeFormatter.kt           # Clock formatting utility
├── app/              # Sample app module
└── .AI/              # Refactor analysis notes
```

## Coding Guidelines

- Prefer Kotlin for new code.
- Add KDoc to public APIs when it clarifies usage.
- Document dp/sp conversion behavior when adding public size APIs.
- Avoid allocating objects in `onDraw`.
- Update `attrs.xml` when adding XML attributes.

## License

Contributed code follows this project's [Apache License 2.0](LICENSE).
