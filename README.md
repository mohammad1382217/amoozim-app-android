# Amoozim Creator — Android (Jetpack Compose)

A native Android port of the **Amoozim Creator** web app (React 19 + Vite + TS),
built with **Kotlin + Jetpack Compose** in a modular, feature-based, clean
architecture.

This repository is the **foundation + a core vertical slice**: a compile-ready,
well-architected base that runs the real auth and networking stack and a handful of
real end-to-end screens. The remaining ~17 web features are scaffolded to drop in on
top of this foundation (see [Roadmap](#roadmap)).

> **Status / build note:** this project was authored without an Android toolchain on
> the authoring machine, so it has **not been compiled here**. Open it in Android
> Studio (which provisions the Gradle wrapper + SDK) or run `./gradlew assembleDebug`
> with a JDK 17 + Android SDK 35 installed. The `gradle/wrapper/gradle-wrapper.jar`
> binary is **not** included — Android Studio regenerates it on first open, or run
> `gradle wrapper --gradle-version 8.11.1` once with a system Gradle.

---

## Prerequisites

- **JDK 17**
- **Android SDK** — `compileSdk 35`, `minSdk 26` (Android 8.0+)
- **Android Studio** (Ladybug or newer recommended) or Gradle 8.11.x

## Getting started

1. `cp local.properties.example local.properties` and set `sdk.dir` (Android Studio
   does this automatically) and, optionally, the API hosts.
2. Open the project in Android Studio and let it sync, **or**:
   ```bash
   gradle wrapper --gradle-version 8.11.1   # once, if you don't use Android Studio
   ./gradlew assembleDebug
   ```
3. Run the app. On first launch you'll see the **session bootstrap (entry) screen** —
   see [Authentication](#authentication).

## Authentication

Amoozim has **no username/password login**. The web app runs inside the Eitaa
messenger and exchanges a host-provided `initData` blob for a JWT via
`POST auth/jwt/callback`. On a standalone Android install there is no Eitaa host, so
the entry screen lets you bootstrap a session two ways:

- **initData** (faithful): paste the `Miniapp UUID` + an `initData` string; the app
  performs the real callback exchange.
- **توکن (dev)**: paste an existing access + refresh token pair to exercise the UI
  directly.

A `401` triggers a single-flight refresh (`POST auth/jwt/refresh`); a terminal refresh
failure clears the session and returns to the entry screen. Tokens are persisted in
`SharedPreferences` (swap for `EncryptedSharedPreferences` before production — the
`TokenStore` API doesn't change).

---

## Architecture

Modular, feature-based clean architecture. Dependencies point inward; features depend
only on `:core:*`, never on each other (except `:feature:course` reuses the mini-app
header from `:feature:miniapp`).

```
:app                      Application, MainActivity, navigation host, shell + bottom nav
│
├── :core:common          ApiResult / ErrorCategory, Paged, Persian number utils
├── :core:model           serializable DTOs/models (BaseDto envelope, Profile, auth)
├── :core:network         Retrofit/OkHttp, interceptors, auth contracts, safe-call, DI
├── :core:session         TokenStore + SessionManager (implements the network auth
│                          contracts), AuthState, RoleAccess, auth APIs
├── :core:designsystem     Material3 theme (brand tokens, RTL, light-only) + components
│
├── :feature:entry        session bootstrap (initData → JWT)
├── :feature:miniapp      mini-app details + identity header
├── :feature:course       courses list (search + paging) + course detail (lessons)
└── :feature:profile      current user (auth/me)
```

### Key decisions

- **Dependency inversion across the auth seam.** `:core:network` declares
  `TokenProvider` / `TokenRefresher`; `:core:session` implements them. This lets the
  OkHttp `Authenticator` drive a single-flight refresh through `SessionManager`
  without `:core:network` depending on `:core:session` (the cycle is further broken
  with a lazy `ProfileApi`).
- **`ApiResult<T>` everywhere.** A central `NetworkCaller` replicates the web client's
  dual-layer success check (HTTP ok **and** envelope `success`) and its
  status → category mapping; repositories never leak transport exceptions.
- **State.** Zustand stores → `SessionManager` (singleton) + per-screen
  `@HiltViewModel`s exposing `StateFlow`. No global mutable state beyond the session.
- **DI.** Hilt, with one `@Module` per layer/feature. `ViewModel`s read the
  mini-app id from `SessionManager`; `CourseDetailViewModel` reads `courseId` from the
  nav `SavedStateHandle`.

### Tech stack

Kotlin 2.1 · Compose (BOM) + Material3 · Navigation Compose · Hilt · Retrofit +
OkHttp + kotlinx.serialization · Coil · Coroutines/Flow. Versions are centralized in
`gradle/libs.versions.toml`.

---

## What's implemented (this slice)

| Area | Status |
|---|---|
| Gradle multi-module project, version catalog, DI graph | ✅ |
| Material3 theme — brand palette, RTL, light-only (matches web) | ✅ |
| Networking — 3 hosts, interceptors, retry, single-flight 401 refresh | ✅ |
| Session/auth — initData exchange, token store, role resolution | ✅ |
| Entry screen | ✅ real |
| Mini-app shell + role-gated bottom navigation | ✅ real |
| Home tab — mini-app header + course list (debounced search, infinite scroll) | ✅ real |
| Course detail — course info + ordered lessons (lock state) | ✅ real |
| Profile tab — `auth/me` | ✅ real |
| Users / Publish / Wallet / My Courses tabs | 🟡 navigable placeholders |

## Roadmap

Built on this foundation, in rough priority order: lesson contents + ExoPlayer HLS
playback (the storage `media/playlist` exchange), course buy/paywall, wallet +
statistics + withdrawals, publishing/quiz builder, analytics, certificates,
pre-registration, discounts, subscriptions. Each is a new `:feature:*` module
following the same model → data → ui layering.

## Fonts

The web app uses **IRANYekanXFaNum**, which is proprietary and ships as `.woff2`
(unsupported by Android font resources), so it is not bundled. The app renders Persian
correctly with the system font, and numbers are converted to Persian digits in code
(`PersianText.kt`) to mimic the web's FaNum behavior.

For exact brand parity, drop `.ttf`/`.otf` weights into
`core/designsystem/src/main/res/font/` (e.g. **Vazirmatn** from Google Fonts — the
closest free equivalent) and point `AppFontFamily` in
`core/designsystem/.../theme/Type.kt` at a real `FontFamily`.

## Divergences from the web app (intentional)

- **Local sign-out** exists on the profile screen (the web app has none) purely as a
  dev convenience for standalone installs, so you can re-bootstrap a session.
- **Light-only** theme matches the web app; a derived dark scheme is provided but off
  by default (`AmoozimTheme(darkTheme = false)`).
- Query persistence/offline cache is in-memory only, matching the web app's current
  `PERSISTENCE_ENABLED = false`.
