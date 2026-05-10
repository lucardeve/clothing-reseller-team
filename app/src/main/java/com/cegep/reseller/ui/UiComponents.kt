package com.cegep.reseller.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cegep.reseller.R
import com.cegep.reseller.data.entity.Listing
import com.cegep.reseller.ui.common.formatPrice
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(titleId: Int, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(titleId)) }) }) { padding ->
        content(padding)
    }
}

@Composable
fun FormColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelId: Int,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(labelId)) },
        minLines = minLines,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun ListingGrid(listings: List<Listing>, onListingClick: (Long) -> Unit, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(listings, key = { it.id }) { listing ->
            ListingCard(listing) { onListingClick(listing.id) }
        }
    }
}

@Composable
fun ListingCard(listing: Listing, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ListingImage(listing, Modifier.fillMaxWidth().aspectRatio(1f))
            Text(listing.brand, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(listing.title, maxLines = 2, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold)
            Text(formatPrice(listing.priceCents), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ListingRow(listing: Listing, actionText: String, onAction: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ListingImage(listing, Modifier.size(72.dp))
            Column(Modifier.weight(1f)) {
                Text(listing.title, fontWeight = FontWeight.SemiBold)
                Text(formatPrice(listing.priceCents), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = onAction) { Text(actionText) }
        }
    }
}

@Composable
fun ListingImage(listing: Listing, modifier: Modifier = Modifier) {
    if (listing.imagePath != null) {
        AsyncImage(
            model = File(listing.imagePath),
            contentDescription = stringResource(R.string.cd_listing_image),
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(RoundedCornerShape(8.dp))
        )
    } else {
        Box(
            modifier.clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                listing.brand.take(2).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmptyState(text: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ErrorMessage(error: FormError?) {
    val message = when (error) {
        FormError.REQUIRED -> R.string.error_required
        FormError.EMAIL_INVALID -> R.string.error_email_invalid
        FormError.PASSWORD_SHORT -> R.string.error_password_short
        FormError.WRONG_LOGIN -> R.string.error_credentials
        FormError.EMAIL_TAKEN -> R.string.error_email_taken
        FormError.PRICE_INVALID -> R.string.error_price_invalid
        FormError.CARD_INVALID -> R.string.error_card_invalid
        FormError.EXPIRY_INVALID -> R.string.error_expiry_invalid
        FormError.CVV_INVALID -> R.string.error_cvv_invalid
        null -> null
    }
    if (message != null) Text(stringResource(message), color = MaterialTheme.colorScheme.error)
}
