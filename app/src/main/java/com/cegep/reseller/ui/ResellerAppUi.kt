package com.cegep.reseller.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cegep.reseller.R
import com.cegep.reseller.ui.navigation.Routes

@Composable
fun ResellerAppUi(viewModel: AppViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    if (state.user == null) {
        AuthNav(navController, state, viewModel)
    } else {
        MainNav(navController, state, viewModel)
    }
}

@Composable
private fun AuthNav(navController: NavHostController, state: ResellerUiState, viewModel: AppViewModel) {
    NavHost(navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                state = state,
                onLogin = { email, password ->
                    viewModel.login(email, password) { navController.goHomeFromLogin() }
                },
                onRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                state = state,
                onRegister = { username, email, password ->
                    viewModel.register(username, email, password) { navController.goHomeFromLogin() }
                },
                onLogin = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun MainNav(navController: NavHostController, state: ResellerUiState, viewModel: AppViewModel) {
    val marketplaceListings = state.listings.filter { it.sellerId != state.user?.id }

    Scaffold(bottomBar = { BottomBar(navController) }) { padding ->
        NavHost(navController, startDestination = Routes.HOME, modifier = Modifier.padding(padding)) {
            mainTabs(navController, state, marketplaceListings, viewModel)
            listingRoutes(navController, state, viewModel)
            checkoutRoutes(navController, state, viewModel)
        }
    }
}

private fun NavGraphBuilder.mainTabs(
    navController: NavHostController,
    state: ResellerUiState,
    marketplaceListings: List<com.cegep.reseller.data.entity.Listing>,
    viewModel: AppViewModel
) {
    composable(Routes.HOME) {
        ListingGridScreen(
            title = stringResource(R.string.title_home),
            emptyText = stringResource(R.string.empty_home),
            listings = marketplaceListings,
            onListingClick = { navController.openListing(it, viewModel) }
        )
    }
    composable(Routes.SEARCH) {
        SearchScreen(state, marketplaceListings, viewModel::updateSearch) {
            navController.openListing(it, viewModel)
        }
    }
    composable(Routes.SELL) {
        MyListingsScreen(state, { navController.navigate(Routes.LISTING_CREATE) }) {
            navController.openListing(it, viewModel)
        }
    }
    composable(Routes.CART) {
        CartScreen(state, viewModel::removeFromCart) { navController.navigate(Routes.CHECKOUT) }
    }
    composable(Routes.ACCOUNT) {
        AccountScreen(
            state = state,
            onMyListings = { navController.navigate(Routes.SELL) },
            onOrders = { navController.navigate(Routes.ORDERS) },
            onLogout = viewModel::logout
        )
    }
}

private fun NavGraphBuilder.listingRoutes(
    navController: NavHostController,
    state: ResellerUiState,
    viewModel: AppViewModel
) {
    composable(Routes.LISTING_DETAIL, listOf(navArgument("listingId") { type = NavType.LongType })) {
        val listing = state.selectedListing
        ListingDetailScreen(
            listing = listing,
            isOwner = listing?.sellerId == state.user?.id,
            isInCart = state.cartItems.any { it.id == listing?.id },
            onEdit = { listing?.let { navController.navigate(Routes.listingEdit(it.id)) } },
            onDelete = { listing?.let { viewModel.deleteListing(it) { navController.popBackStack() } } },
            onCartClick = { listing?.let(viewModel::addOrRemoveCart) }
        )
    }
    composable(Routes.LISTING_CREATE) {
        ListingFormScreen(state.error, null) { title, brand, price, description, imagePath ->
            viewModel.saveListing(null, title, brand, price, description, imagePath) {
                navController.popBackStack()
            }
        }
    }
    composable(Routes.LISTING_EDIT, listOf(navArgument("listingId") { type = NavType.LongType })) { backStack ->
        val id = backStack.arguments?.getLong("listingId") ?: 0
        LaunchedEffect(id) { viewModel.loadListing(id) }
        ListingFormScreen(state.error, state.selectedListing) { title, brand, price, description, imagePath ->
            viewModel.saveListing(id, title, brand, price, description, imagePath) {
                navController.popBackStack()
            }
        }
    }
}

private fun NavGraphBuilder.checkoutRoutes(
    navController: NavHostController,
    state: ResellerUiState,
    viewModel: AppViewModel
) {
    composable(Routes.CHECKOUT) {
        CheckoutScreen(state) { address, card, expiry, cvv ->
            viewModel.checkout(address, card, expiry, cvv) {
                navController.navigate(Routes.ORDERS) { popUpTo(Routes.CART) { inclusive = true } }
            }
        }
    }
    composable(Routes.ORDERS) { OrdersScreen(state) }
}

private fun NavHostController.goHomeFromLogin() {
    navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
}

private fun NavHostController.openListing(id: Long, viewModel: AppViewModel) {
    viewModel.loadListing(id)
    navigate(Routes.listingDetail(id))
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val tabs = listOf(
        Triple(Routes.HOME, R.string.tab_home, Icons.Default.Home),
        Triple(Routes.SEARCH, R.string.tab_search, Icons.Default.Search),
        Triple(Routes.SELL, R.string.tab_sell, Icons.Default.Store),
        Triple(Routes.CART, R.string.tab_cart, Icons.Default.ShoppingCart),
        Triple(Routes.ACCOUNT, R.string.tab_account, Icons.Default.AccountCircle)
    )

    NavigationBar {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.first,
                onClick = {
                    navController.navigate(tab.first) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(tab.third, contentDescription = null) },
                label = { Text(stringResource(tab.second)) }
            )
        }
    }
}
