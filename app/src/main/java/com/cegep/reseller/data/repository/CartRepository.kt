package com.cegep.reseller.data.repository

import com.cegep.reseller.data.dao.CartDao
import com.cegep.reseller.data.entity.CartItem
import com.cegep.reseller.data.entity.Listing
import kotlinx.coroutines.flow.Flow

class CartRepository(private val dao: CartDao) {

    fun observe(userId: Long): Flow<List<Listing>> = dao.observeCart(userId)

    fun isInCart(userId: Long, listingId: Long): Flow<Boolean> = dao.isInCart(userId, listingId)

    suspend fun add(userId: Long, listingId: Long) =
        dao.insert(CartItem(userId = userId, listingId = listingId))

    suspend fun remove(userId: Long, listingId: Long) = dao.remove(userId, listingId)

    suspend fun clear(userId: Long) = dao.clearForUser(userId)
}
