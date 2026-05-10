package com.cegep.reseller.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cegep.reseller.R
import com.cegep.reseller.data.ImageStore
import com.cegep.reseller.data.entity.Listing
import com.cegep.reseller.ui.common.formatPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingGridScreen(title: String, emptyText: String, listings: List<Listing>, onListingClick: (Long) -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text(title) }) }) { padding ->
        if (listings.isEmpty()) EmptyState(emptyText, Modifier.padding(padding))
        else ListingGrid(listings, onListingClick, Modifier.padding(padding))
    }
}

@Composable
fun SearchScreen(
    state: ResellerUiState,
    listings: List<Listing>,
    onSearchChange: (String) -> Unit,
    onListingClick: (Long) -> Unit
) {
    val results = listings.filter {
        state.searchText.isBlank() ||
            it.title.contains(state.searchText, true) ||
            it.brand.contains(state.searchText, true)
    }

    Screen(R.string.title_search) { padding ->
        Column(Modifier.padding(padding).padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppTextField(
                value = state.searchText,
                onValueChange = onSearchChange,
                labelId = R.string.label_search,
                modifier = Modifier.fillMaxWidth(),
            )
            if (results.isEmpty()) EmptyState(stringResource(R.string.empty_search))
            else ListingGrid(results, onListingClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListingsScreen(state: ResellerUiState, onCreate: () -> Unit, onListingClick: (Long) -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.title_my_listings)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreate) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.title_create_listing))
            }
        }
    ) { padding ->
        if (state.myListings.isEmpty()) EmptyState(stringResource(R.string.empty_my_listings), Modifier.padding(padding))
        else ListingGrid(state.myListings, onListingClick, Modifier.padding(padding))
    }
}

@Composable
fun ListingDetailScreen(
    listing: Listing?,
    isOwner: Boolean,
    isInCart: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCartClick: () -> Unit
) {
    Screen(R.string.title_listing_detail) { padding ->
        if (listing == null) {
            EmptyState(stringResource(R.string.title_listing_detail), Modifier.padding(padding))
            return@Screen
        }

        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ListingImage(listing, Modifier.fillMaxWidth().aspectRatio(1f))
            Text(listing.brand, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(listing.title, style = MaterialTheme.typography.headlineSmall)
            Text(formatPrice(listing.priceCents), style = MaterialTheme.typography.titleLarge)
            Text(listing.description)

            if (isOwner) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onEdit, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.action_edit)) }
                    OutlinedButton(onClick = onDelete, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.action_delete)) }
                }
            } else {
                val buttonText = if (isInCart) R.string.action_remove_from_cart else R.string.action_add_to_cart
                Button(onClick = onCartClick, modifier = Modifier.fillMaxWidth()) { Text(stringResource(buttonText)) }
            }
        }
    }
}

@Composable
fun ListingFormScreen(
    error: FormError?,
    listing: Listing?,
    onSave: (String, String, String, String, String?) -> Unit
) {
    var title by remember(listing?.id) { mutableStateOf(listing?.title.orEmpty()) }
    var brand by remember(listing?.id) { mutableStateOf(listing?.brand.orEmpty()) }
    var price by remember(listing?.id) { mutableStateOf(listing?.priceCents?.let { "%.2f".format(it / 100.0) }.orEmpty()) }
    var description by remember(listing?.id) { mutableStateOf(listing?.description.orEmpty()) }
    var imagePath by remember(listing?.id) { mutableStateOf(listing?.imagePath) }

    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) imagePath = ImageStore.saveFromUri(context, uri)
    }
    val screenTitle = if (listing == null) R.string.title_create_listing else R.string.title_edit_listing

    Screen(screenTitle) { padding ->
        FormColumn(Modifier.padding(padding)) {
            if (imagePath != null) {
                ListingImage(Listing(0, 0, title, brand, 0, "", imagePath), Modifier.fillMaxWidth().aspectRatio(1.5f))
            }
            Button(
                onClick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.action_pick_image)) }

            AppTextField(title, { title = it }, R.string.label_title)
            AppTextField(brand, { brand = it }, R.string.label_brand)
            AppTextField(price, { price = it }, R.string.label_price, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            AppTextField(description, { description = it }, R.string.label_description, minLines = 4)
            ErrorMessage(error)
            Button(
                onClick = { onSave(title, brand, price, description, imagePath) },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.action_save)) }
        }
    }
}
