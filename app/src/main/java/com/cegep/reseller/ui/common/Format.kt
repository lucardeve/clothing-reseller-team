package com.cegep.reseller.ui.common

import java.text.NumberFormat
import java.util.Locale

private val priceFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.CANADA)

fun formatPrice(cents: Long): String = priceFormat.format(cents / 100.0)

fun parsePriceToCents(input: String): Long? {
    val cleaned = input.replace(",", ".").trim()
    val value = cleaned.toDoubleOrNull() ?: return null
    if (value < 0.0) return null
    return (value * 100.0).toLong()
}
