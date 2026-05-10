package com.cegep.reseller.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cegep.reseller.AppContainer
import com.cegep.reseller.data.entity.Listing
import com.cegep.reseller.data.entity.Order
import com.cegep.reseller.data.entity.User
import com.cegep.reseller.data.repository.AuthRepository
import com.cegep.reseller.ui.common.parsePriceToCents
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResellerUiState(
    val user: User? = null,
    val listings: List<Listing> = emptyList(),
    val myListings: List<Listing> = emptyList(),
    val cartItems: List<Listing> = emptyList(),
    val orders: List<Order> = emptyList(),
    val selectedListing: Listing? = null,
    val searchText: String = "",
    val error: FormError? = null
)

enum class FormError {
    REQUIRED, EMAIL_INVALID, PASSWORD_SHORT, WRONG_LOGIN, EMAIL_TAKEN,
    PRICE_INVALID, CARD_INVALID, EXPIRY_INVALID, CVV_INVALID
}

class AppViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(ResellerUiState())
    val state: StateFlow<ResellerUiState> = _state.asStateFlow()
    private var userJob: Job? = null

    init {
        viewModelScope.launch {
            container.listings.observeActive().collect { listings ->
                _state.update { it.copy(listings = listings) }
            }
        }
        viewModelScope.launch {
            container.auth.currentUser.collect { user ->
                _state.update { it.copy(user = user) }
                observeUserData(user)
            }
        }
    }

    private fun observeUserData(user: User?) {
        userJob?.cancel()
        if (user == null) {
            _state.update { it.copy(myListings = emptyList(), cartItems = emptyList(), orders = emptyList()) }
            return
        }

        userJob = viewModelScope.launch {
            launch { container.listings.observeBySeller(user.id).collect { list -> _state.update { it.copy(myListings = list) } } }
            launch { container.cart.observe(user.id).collect { cart -> _state.update { it.copy(cartItems = cart) } } }
            launch { container.orders.observeForUser(user.id).collect { orders -> _state.update { it.copy(orders = orders) } } }
        }
    }

    fun updateSearch(text: String) = _state.update { it.copy(searchText = text) }
    fun logout() = viewModelScope.launch { container.auth.logout() }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        val error = loginError(email, password)
        if (error != null) return showError(error)

        viewModelScope.launch {
            when (container.auth.login(email, password)) {
                is AuthRepository.AuthResult.Success -> onSuccess()
                AuthRepository.AuthResult.InvalidCredentials -> showError(FormError.WRONG_LOGIN)
                AuthRepository.AuthResult.EmailTaken -> Unit
            }
        }
    }

    fun register(username: String, email: String, password: String, onSuccess: () -> Unit) {
        val error = registerError(username, email, password)
        if (error != null) return showError(error)

        viewModelScope.launch {
            when (container.auth.register(username, email, password)) {
                is AuthRepository.AuthResult.Success -> onSuccess()
                AuthRepository.AuthResult.EmailTaken -> showError(FormError.EMAIL_TAKEN)
                AuthRepository.AuthResult.InvalidCredentials -> Unit
            }
        }
    }

    fun loadListing(id: Long) = viewModelScope.launch {
        _state.update { it.copy(selectedListing = container.listings.get(id)) }
    }

    fun addOrRemoveCart(listing: Listing) {
        val user = state.value.user ?: return
        viewModelScope.launch {
            if (state.value.cartItems.any { it.id == listing.id }) {
                container.cart.remove(user.id, listing.id)
            } else {
                container.cart.add(user.id, listing.id)
            }
        }
    }

    fun removeFromCart(listing: Listing) {
        val user = state.value.user ?: return
        viewModelScope.launch { container.cart.remove(user.id, listing.id) }
    }

    fun deleteListing(listing: Listing, onDone: () -> Unit) = viewModelScope.launch {
        container.listings.delete(listing)
        onDone()
    }

    fun saveListing(
        id: Long?,
        title: String,
        brand: String,
        price: String,
        description: String,
        imagePath: String?,
        onDone: () -> Unit
    ) {
        val user = state.value.user ?: return
        val cents = parsePriceToCents(price)
        val error = when {
            title.isBlank() || brand.isBlank() || description.isBlank() -> FormError.REQUIRED
            cents == null -> FormError.PRICE_INVALID
            else -> null
        }
        if (error != null) return showError(error)

        viewModelScope.launch {
            val old = id?.let { container.listings.get(it) }
            val listing = old?.copy(
                title = title.trim(),
                brand = brand.trim(),
                priceCents = cents ?: 0,
                description = description.trim(),
                imagePath = imagePath ?: old.imagePath
            ) ?: Listing(
                sellerId = user.id,
                title = title.trim(),
                brand = brand.trim(),
                priceCents = cents ?: 0,
                description = description.trim(),
                imagePath = imagePath
            )

            if (old == null) container.listings.create(listing) else container.listings.update(listing)
            _state.update { it.copy(error = null) }
            onDone()
        }
    }

    fun checkout(address: String, card: String, expiry: String, cvv: String, onDone: () -> Unit) {
        val user = state.value.user ?: return
        val cleanCard = card.filter(Char::isDigit)
        val error = when {
            address.isBlank() -> FormError.REQUIRED
            cleanCard.length != 16 -> FormError.CARD_INVALID
            !Regex("""\d{2}/\d{2}""").matches(expiry) -> FormError.EXPIRY_INVALID
            cvv.filter(Char::isDigit).length != 3 -> FormError.CVV_INVALID
            else -> null
        }
        if (error != null) return showError(error)

        viewModelScope.launch {
            val cart = state.value.cartItems
            container.orders.checkout(
                userId = user.id,
                listingIds = cart.map { it.id },
                totalCents = cart.sumOf { it.priceCents },
                shippingAddress = address.trim(),
                paymentMasked = "**** ${cleanCard.takeLast(4)}"
            )
            _state.update { it.copy(error = null) }
            onDone()
        }
    }

    private fun showError(error: FormError) {
        _state.update { it.copy(error = error) }
    }

    private fun loginError(email: String, password: String) = when {
        email.isBlank() || password.isBlank() -> FormError.REQUIRED
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> FormError.EMAIL_INVALID
        else -> null
    }

    private fun registerError(username: String, email: String, password: String) = when {
        username.isBlank() || email.isBlank() || password.isBlank() -> FormError.REQUIRED
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> FormError.EMAIL_INVALID
        password.length < 6 -> FormError.PASSWORD_SHORT
        else -> null
    }
}

class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AppViewModel(container) as T
}
