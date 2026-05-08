# Teammate Guide — Reseller Final Project

Hey Ryan and Noah. The Android app is **already built and working** on the main repo:

→ https://github.com/lucardeve/clothing-reseller

Clone it, run it, and you'll see a full marketplace with sign-in, listings, search, cart, checkout, and account screens. 27 tests pass. There are 6 seeded listings on first launch.

This repo is **only for coordinating what's left**. All code work happens on `clothing-reseller`.

## Run the app first (15 minutes)

1. `git clone https://github.com/lucardeve/clothing-reseller`
2. Open the folder in **Android Studio Koala** (2024.1) or newer.
3. Let Gradle sync (downloads ~500 MB of dependencies the first time).
4. Plug in a phone with USB debugging on, or start an emulator (Android 8.0+).
5. Hit **Run ▶**.
6. The app installs. Register a new account. Browse the 6 listings. Add one to cart. Check out. You should see the order in Account → Order history.

If anything doesn't work for you, message Luca before doing anything else.

## What's left to ship

The app works. What's missing is the **submission package**: the polish, the demo, the docs that the rubric grades. Each item below is "done" when it's checked into `clothing-reseller`'s `main` branch (or attached to the final submission for the video/PDF).

### Ryan — UI polish (estimate 3–4 hours)

- [ ] **Take 4–6 screenshots** of the running app (Home, listing detail, Cart, Checkout, Account, Sell form). Put them in `clothing-reseller/screenshots/` and embed them in the README under a "## Screenshots" section.
- [ ] **App icon** — replace `@android:drawable/sym_def_app_icon` in `AndroidManifest.xml` with a real icon. Use Android Studio's **Image Asset** wizard (right-click `app/src/main/res` → New → Image Asset). Pick anything clean — a hanger, a tag, a shopping bag.
- [ ] **Pass through the screens once** and look for visual bugs: weird spacing, text getting cut off, overlapping bottom-bar. Fix what you spot.
- [ ] **Optional but nice:** add a brand color accent (currently the theme is pure black/white). Pick one accent color, plug it into `ui/theme/Color.kt`.

### Noah — Testing + QA (estimate 2–3 hours)

- [ ] **Run the existing tests** to confirm they still pass on your machine:
  ```
  ./gradlew testDebugUnitTest
  ./gradlew connectedDebugAndroidTest
  ```
  (the second one needs an emulator or device connected)
- [ ] **Manual QA pass on a real phone.** Walk this exact flow and write down anything that breaks:
  1. Register a new account → verify field validation (empty fields, bad email, short password)
  2. Home shows 6 listings → tap one → Add to cart
  3. Bottom nav → Cart → tap an item → Checkout
  4. Checkout: try invalid card/expiry/CVV → see errors → fix → Place order
  5. Verify the listing is gone from Home (it's now SOLD)
  6. Account → Order history shows the order with masked card
  7. Sell tab → create a listing with a photo → see it on My listings
  8. Edit and delete the listing
  9. Sign out → sign back in → state still there
  10. Kill app from recents → reopen → still signed in
- [ ] **Write a short QA report** (`clothing-reseller/QA.md`) listing every step, ✅ or ❌, plus any bug you saw and how to reproduce it.
- [ ] **Optional:** add 1-2 Compose UI tests (e.g. for `LoginScreen` showing field errors when submitted empty).

### Luca — Final deliverables (will handle these last)

- [ ] **Build signed APK** — `./gradlew assembleRelease`. Submit the file from `app/build/outputs/apk/release/`.
- [ ] **Convert REPORT.md to PDF.** Open it in any markdown viewer, print to PDF.
- [ ] **Record 5-min demo video.** Screen-record the app while narrating: register → browse → buy → sell → account. Upload to Google Drive (unlisted), put the link in the README.
- [ ] **Final README polish** — make sure team names, screenshots, video link, and APK link are all correct.

## Deliverables checklist (course rubric)

| Item | Owner | Done? |
|---|---|---|
| GitHub repo (the `clothing-reseller` link) | Luca | ✅ |
| Working APK file | Luca | — |
| README.md with screenshots and setup | Ryan + Luca | — |
| 1-page PDF report | Luca | — |
| 5-minute demo video | Luca | — |
| Manual QA pass | Noah | — |
| App icon | Ryan | — |
| Code reviewed & merged | All | — |

## Workflow

1. **Don't push directly to `main`** on `clothing-reseller`.
2. Create a branch like `ryan/screenshots` or `noah/qa-doc`.
3. Open a PR. Tag Luca as reviewer.
4. Once approved, merge.

## Help

- Stuck on git/Android Studio? Message Luca.
- Stuck on what to write/test? Message Luca.
- App is broken on your machine? Message Luca **before** changing anything — most likely it's a setup issue, not a code bug.
