package com.cegep.reseller.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val HOME = "home"
    const val SEARCH = "search"
    const val SELL = "sell"
    const val CART = "cart"
    const val ACCOUNT = "account"

    const val LISTING_DETAIL = "listing/{listingId}"
    fun listingDetail(id: Long) = "listing/$id"

    const val LISTING_EDIT = "listing/{listingId}/edit"
    fun listingEdit(id: Long) = "listing/$id/edit"
    const val LISTING_CREATE = "listing/new"

    const val MY_LISTINGS = "my-listings"
    const val ORDERS = "orders"
    const val CHECKOUT = "checkout"
}
