package com.cegep.reseller.data.repository

import com.cegep.reseller.data.dao.ListingDao
import com.cegep.reseller.data.dao.OrderDao
import com.cegep.reseller.data.entity.ListingStatus
import com.cegep.reseller.data.entity.Order
import kotlinx.coroutines.flow.Flow

class OrderRepository(
    private val orderDao: OrderDao,
    private val listingDao: ListingDao,
    private val cart: CartRepository
) {

    fun observeForUser(userId: Long): Flow<List<Order>> = orderDao.observeForUser(userId)

    suspend fun checkout(
        userId: Long,
        listingIds: List<Long>,
        totalCents: Long,
        shippingAddress: String,
        paymentMasked: String
    ) {
        listingIds.forEach { listingId ->
            val listing = listingDao.findById(listingId) ?: return@forEach
            orderDao.insert(
                Order(
                    buyerId = userId,
                    listingId = listingId,
                    totalCents = listing.priceCents,
                    shippingAddress = shippingAddress,
                    paymentMasked = paymentMasked
                )
            )
            listingDao.setStatus(listingId, ListingStatus.SOLD)
        }
        cart.clear(userId)
    }
}
