package com.cegep.reseller.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cegep.reseller.R
import com.cegep.reseller.data.entity.Listing
import com.cegep.reseller.ui.common.formatPrice

@Composable
fun CartScreen(state: ResellerUiState, onRemove: (Listing) -> Unit, onCheckout: () -> Unit) {
    Screen(R.string.title_cart) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (state.cartItems.isEmpty()) {
                EmptyState(stringResource(R.string.empty_cart), Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.cartItems, key = { it.id }) {
                        ListingRow(it, stringResource(R.string.action_remove)) { onRemove(it) }
                    }
                }
                CartBottomBar(total = state.cartItems.sumOf { it.priceCents }, onCheckout = onCheckout)
            }
        }
    }
}

@Composable
private fun CartBottomBar(total: Long, onCheckout: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${stringResource(R.string.label_total)}: ${formatPrice(total)}", fontWeight = FontWeight.Bold)
        Button(onClick = onCheckout) { Text(stringResource(R.string.action_checkout)) }
    }
}

@Composable
fun CheckoutScreen(state: ResellerUiState, onCheckout: (String, String, String, String) -> Unit) {
    var address by remember { mutableStateOf("") }
    var card by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    Screen(R.string.title_checkout) { padding ->
        FormColumn(Modifier.padding(padding)) {
            Text(
                "${stringResource(R.string.label_total)}: ${formatPrice(state.cartItems.sumOf { it.priceCents })}",
                style = MaterialTheme.typography.titleMedium
            )
            AppTextField(address, { address = it }, R.string.label_shipping_address, minLines = 3)
            AppTextField(card, { card = it.filter(Char::isDigit).take(16) }, R.string.label_card_number, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            AppTextField(expiry, { expiry = it.take(5) }, R.string.label_card_expiry)
            AppTextField(cvv, { cvv = it.filter(Char::isDigit).take(3) }, R.string.label_card_cvv, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            ErrorMessage(state.error)
            Button(
                onClick = { onCheckout(address, card, expiry, cvv) },
                enabled = state.cartItems.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.action_pay)) }
        }
    }
}

@Composable
fun AccountScreen(state: ResellerUiState, onMyListings: () -> Unit, onOrders: () -> Unit, onLogout: () -> Unit) {
    Screen(R.string.title_account) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(state.user?.username.orEmpty(), style = MaterialTheme.typography.headlineSmall)
            Text(state.user?.email.orEmpty(), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onMyListings, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.title_my_listings)) }
            Button(onClick = onOrders, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.title_orders)) }
            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.action_logout)) }
        }
    }
}

@Composable
fun OrdersScreen(state: ResellerUiState) {
    Screen(R.string.title_orders) { padding ->
        if (state.orders.isEmpty()) {
            EmptyState(stringResource(R.string.empty_orders), Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.orders, key = { it.id }) { order ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(formatPrice(order.totalCents), fontWeight = FontWeight.Bold)
                            Text(order.shippingAddress, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text(order.paymentMasked, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
