# Reseller — Team Starter Guide

This is the starting point for our Android final project. The **data layer** is fully built: Room database, all DAOs, repositories, password hashing, image storage, session persistence, and seed data are wired up. The tests that prove it works are also included and passing. **What's missing is the UI and navigation** — that's what you'll build.

When the app launches today, it shows a placeholder screen pointing you to this guide. By submission day it should be a full marketplace with sign-in, listings, search, cart, checkout, and an account page.

## Roles (from the proposal)

| Member | Role |
|---|---|
| Luca Rarau | Backend + Database (DONE — see `data/` and tests) |
| Ryan Liautaud | Frontend / UI/UX |
| Noah Bouffard | Frontend UX + final testing |

## What's already built (don't rewrite this)

```
app/src/main/java/com/cegep/reseller/
├── MainActivity.kt              ← currently shows a placeholder; replace it
├── ResellerApp.kt               ← Application class, builds the AppContainer
├── data/
│   ├── entity/                  ← User, Listing (with status), CartItem, Order
│   ├── dao/                     ← UserDao, ListingDao, CartDao, OrderDao
│   ├── repository/              ← AuthRepository, ListingRepository, CartRepository, OrderRepository
│   ├── AppDatabase.kt           ← Room database singleton
│   ├── PasswordHasher.kt        ← SHA-256 hashing
│   ├── SessionManager.kt        ← DataStore-backed current-user persistence
│   ├── ImageStore.kt            ← copies picked images to internal storage
│   └── DemoSeed.kt              ← inserts a demo "Curator" user + 6 listings on first run
└── ui/
    ├── theme/                   ← Color, Type, Theme — use ResellerTheme {} as the root
    ├── navigation/Routes.kt     ← string constants for every route — use these everywhere
    └── common/Format.kt         ← formatPrice(cents) and parsePriceToCents(string)
```

The `ResellerApp.AppContainer` exposes the repositories you'll call from your ViewModels:

- `container.auth: AuthRepository` — `currentUser: Flow<User?>`, `register(...)`, `login(...)`, `logout()`
- `container.listings: ListingRepository` — `observeActive()`, `observeBySeller(id)`, `observe(id)`, `search(query)`, `create(listing)`, `update(listing)`, `delete(listing)`, `markSold(id)`
- `container.cart: CartRepository` — `observe(userId)`, `isInCart(userId, listingId)`, `add(...)`, `remove(...)`, `clear(userId)`
- `container.orders: OrderRepository` — `observeForUser(userId)`, `checkout(userId, listingIds, totalCents, address, paymentMasked)`

In a Composable, get to the container with:
```kotlin
val container = (LocalContext.current.applicationContext as ResellerApp).container
```

Or build a ViewModelFactory (recommended — see Ryan's task #3 below).

---

## Ryan — Frontend / UI/UX

You own every Compose screen, navigation, and shared UI components. Use Material 3, `ResellerTheme`, and the routes defined in `Routes.kt`. Read every label from `strings.xml` (don't hardcode any visible text). Use `formatPrice(cents)` for prices.

### Screens to build

1. **Auth flow**
   - `ui/auth/LoginScreen.kt` — email + password fields, "Sign in" button, "No account? Create one" link. Show field errors inline. Use `imePadding()` so the keyboard doesn't cover the button.
   - `ui/auth/RegisterScreen.kt` — username + email + password. Same patterns.
   - `ui/auth/AuthViewModel.kt` — wraps `AuthRepository`, exposes `state: StateFlow<AuthUiState>` with `user`, errors, `submitting`. Methods: `login(email, password, onSuccess)`, `register(username, email, password, onSuccess)`, `logout()`.

2. **Home grid**
   - `ui/home/HomeScreen.kt` — `LazyVerticalGrid` (2 columns) of active listings using `ListingCard`. Empty state message when no listings.
   - `ui/home/HomeViewModel.kt` — exposes `listings: StateFlow<List<Listing>>` from `ListingRepository.observeActive()`.

3. **Search**
   - `ui/search/SearchScreen.kt` — text field with leading search icon, trailing clear-X button, debounced query. Same grid as Home for results.
   - `ui/search/SearchViewModel.kt` — debounce the query (200 ms), call `ListingRepository.search()`.

4. **Listing detail**
   - `ui/listing/ListingDetailScreen.kt` — large image (or brand-initials placeholder), brand, title, price, description.
     - If `state.isOwner` → show **Edit** and **Delete** buttons.
     - Else → **Add to cart** button (toggle: text changes to "Remove from cart" when already in cart, OR navigate the user to cart on add).
   - `ui/listing/ListingDetailViewModel.kt` — combine the listing flow, current user flow, and `isInCart` flow into one state. Methods: `load(id)`, `toggleCart()`, `delete(onDone)`.

5. **Listing form (create + edit)**
   - `ui/listing/ListingFormScreen.kt` — title, brand, price (decimal keyboard), description (multi-line), photo picker via `ActivityResultContracts.PickVisualMedia()`. Save button. Use `imePadding()`.
   - `ui/listing/ListingFormViewModel.kt` — state with all fields + per-field errors, `loadForEdit(id?)`, `save(onDone)`. Use `parsePriceToCents` and `ImageStore.saveFromUri()`.

6. **My listings**
   - `ui/listing/MyListingsScreen.kt` — grid of the current user's listings. FAB to create a new one. Empty state.
   - `ui/listing/MyListingsViewModel.kt` — observe `listings.observeBySeller(currentUser.id)`.

7. **Cart**
   - `ui/cart/CartScreen.kt` — `LazyColumn` of cart rows (thumbnail + title + price + remove X). Bottom bar shows running total + Checkout button. Empty state.
   - `ui/cart/CartViewModel.kt` — observe `cart.observe(userId)`, derive total.

8. **Checkout**
   - `ui/checkout/CheckoutScreen.kt` — shipping address (multi-line), card number (16 digits, number keyboard), MM/YY expiry, 3-digit CVV. "Place order" button.
   - `ui/checkout/CheckoutViewModel.kt` — validate format only, call `orders.checkout(...)` with `paymentMasked = "•••• " + last4`.

9. **Account & orders**
   - `ui/account/AccountScreen.kt` — username + email at the top, list rows for **My listings**, **Order history**, **Sign out** (red).
   - `ui/account/OrdersScreen.kt` — list of past orders with date, price, address, masked card.
   - `ui/account/AccountViewModel.kt` — expose user + orders, `logout(onDone)`.

### Navigation

10. `ui/navigation/NavGraph.kt` — wire every screen above into a `NavHost`. Use the routes in `Routes.kt`. Start at `Routes.LOGIN` if not signed in, else `Routes.HOME`.
11. `ui/navigation/BottomBar.kt` — five tabs: Home, Search, Sell, Cart, Account. **Important pattern that works**:
    ```kotlin
    navController.navigate(tab.route) {
        popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
        launchSingleTop = true
    }
    ```
    Don't use `saveState` / `restoreState` — they cause the cart-tab to refuse to switch back to Home.

### Reusable components

12. `ui/components/ListingCard.kt` — square image area + brand (small label) + title + price. Used by Home, Search, MyListings.
13. `ui/components/EmptyState.kt` — centered text block for empty grids/lists.

### Shared infrastructure

14. `ui/common/AppViewModelFactory.kt` — single factory that builds every ViewModel from `container`. Pattern:
    ```kotlin
    class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = when (modelClass) {
        AuthViewModel::class.java -> AuthViewModel(container.auth) as T
        // ... one branch per VM
      }
    }
    ```

### Acceptance criteria for Ryan's work

- App launches → if not signed in, shows Login. Register works (validates email format + password ≥ 6).
- Home shows the 6 seeded listings. Tapping a card opens detail.
- Search filters by title or brand (case-insensitive).
- A signed-in non-seller can Add to cart, see the item in Cart, and check out.
- A seller sees Edit/Delete on their own listing instead of Add to cart.
- All keyboards respect `imePadding()` — buttons never get covered.
- All bottom-nav tabs always work, regardless of where the back stack is sitting.

---

## Noah — Frontend UX + final testing

Two concurrent jobs: polish the UI Ryan ships, and own the test/QA story.

### Polish

1. **Empty states** — every grid/list has a helpful empty message (already keyed in `strings.xml` as `empty_*`).
2. **Error states** — `OutlinedTextField` with `isError = ... != null` and `supportingText = { Text(...) }`. Already keyed in `strings.xml` as `error_*`.
3. **Loading states** — `CircularProgressIndicator` in buttons during `submitting`.
4. **Keyboard behavior** — every form scrolls (`verticalScroll`) and pads (`imePadding`) so nothing is covered. IME actions: Next between fields, Done/Search dismisses on the last field.
5. **Material 3 polish** — use `MaterialTheme.colorScheme` for colors, `MaterialTheme.typography` for text. No hardcoded `Color(0xFF...)` outside `theme/`.
6. **Image fallback** — when `imagePath == null`, show the first 2 letters of the brand on a `surfaceVariant` background. The seed data has no images, so this matters.
7. **Manifest** — `android:windowSoftInputMode="adjustResize"` is already set so your `imePadding()` works.

### Testing (this is the deliverable that proves the app works)

8. **Run the existing tests first** to confirm nothing broke after merging Ryan's UI.
   - `./gradlew testDebugUnitTest` — should be green (`PasswordHasherTest`, `FormatTest`).
   - `./gradlew connectedDebugAndroidTest` with a device/emulator running — should be green (`ListingDaoTest`, `AuthRepositoryTest`, `OrderRepositoryTest`).
9. **Add a Compose UI test** for the Login screen — assert that submitting empty fields shows the "required" error.
10. **Add a Compose UI test** for the Home screen — assert that 6 cards render after seeding.
11. **Manual QA pass** before submission. Walk the full flow on a real phone:
    - Register → Home shows seeded listings → tap → detail → Add to cart → Cart → Checkout (try invalid card, then valid) → order recorded → listing removed from Home.
    - Sell tab → create listing with photo → My listings → Edit → Delete.
    - Search by brand and by title (case-insensitive).
    - Sign out → Sign in → state persists.
    - Kill app from recents → reopen → still signed in.

### Acceptance criteria for Noah's work

- Existing 27 tests still pass after every PR.
- At least 2 new Compose UI tests added, both green.
- A short manual QA checklist (in `QA.md` or in the PR description) is run and signed off before the demo.

---

## Project deliverables (course rubric)

- [ ] **GitHub repo** — this one. Link the URL in your submission.
- [ ] **README.md** — keep updated with current setup steps + screenshots once UI exists.
- [ ] **APK** — `./gradlew assembleRelease` puts a signed APK in `app/build/outputs/apk/release/`. Submit that file.
- [ ] **One-page report (PDF)** — problem, features, architecture diagram, screenshots, lessons learned, future improvements.
- [ ] **5-minute demo video** — narrated walkthrough of the full flow (register → list → buy → checkout → account). Upload anywhere accessible (Drive, YouTube unlisted), put the link in the README.

---

## How to run

1. Open this folder in Android Studio Koala (2024.1) or newer.
2. Let Gradle sync.
3. Connect a phone (USB debugging) or start an emulator (API 26+).
4. Hit Run ▶.

Right now you'll see a placeholder. Once the screens are wired up via `MainActivity.kt` and `NavGraph.kt`, you'll see the real app.

## Branch / PR conventions

- Branch off `main`. Use names like `ryan/home-screen`, `noah/login-tests`.
- One PR per screen or feature. Keep them small.
- Don't merge if `./gradlew testDebugUnitTest` is red.
- Tag the other person + Luca as reviewer.
