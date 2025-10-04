# Repository Guidelines

## Project Structure & Module Organization
- `server/` contains the Ktor backend; feature wiring lives in small top-level files in `src/main/kotlin`, loaded by `Application.module`.
- Environment config and logging sit under `server/src/main/resources` (`application.yaml`, `logback.xml`); fork these per environment instead of tracking secrets.
- `web/` hosts Kotlin/Wasm UI code (`src/wasmJsMain/kotlin/main.kt`) that compiles to `web/build/dist/wasmJs`; root Gradle files and `gradle/libs.versions.toml` define shared dependencies.

## Build, Test, and Development Commands
- `./gradlew build` — compile all modules and execute their tests.
- `./gradlew :server:run` — start the Netty server on http://localhost:8080; pair with `./gradlew -t :web:build` for live WASM rebuilds.
- `./gradlew :server:buildFatJar` and `:server:buildImage` — create deployment artifacts; call `publishImageToLocalRegistry` when you need a local Docker image.
- `./gradlew :server:test` — JVM test suite; add `-i` when debugging failing tasks.

## Agent Notes
- Set a writable Gradle cache for linting/builds with `export GRADLE_USER_HOME=$PWD/.gradle` before invoking wrapper commands.

## Coding Style & Naming Conventions
- Stick to Kotlin's four-space indentation, `val` by default, and concise expression bodies where they aid readability.
- Align filenames with their primary type (`Routing.kt`, `Templating.kt`); use `UpperCamelCase` for types, `lowerCamelCase` for functions/vals, and kebab-case for static assets.
- Keep Ktor feature setup modular (one concern per file) and inject cross-cutting services with Koin modules when they appear.

## Testing Guidelines
- Use `kotlin-test` plus `ktor-server-test-host`; place JVM tests in `server/src/test/kotlin/...` with the `*Test.kt` suffix and `testApplication {}` harnesses for routes.
- Seed databases inside Exposed transactions to keep tests isolated and reset state with in-memory H2 when possible.
- Run `./gradlew :server:test` before opening a PR; target meaningful coverage of routing, serialization, authentication, and any future WASM helpers.

## Commit & Pull Request Guidelines
- Write present-tense, imperative commit subjects (e.g., `Add account summary route`) and include a short body for migrations or manual steps.
- Reference issues with `#id`, summarize behavioural changes, and list commands or screenshots that prove the change in the PR description.
- Keep PRs tight in scope; split unrelated clean-ups so reviewers can focus on the functional change.

## Configuration & Environment
- Update environment values via copies or overrides of `server/src/main/resources/application.yaml`; avoid committing real credentials.
- `logback.xml` controls logging; lower noisy categories locally rather than changing global levels for everyone.
- When new env vars or config files are required, document the setup in the PR so deployments and agents stay in sync.
