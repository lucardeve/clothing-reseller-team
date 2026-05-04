package com.cegep.reseller.data.repository

import com.cegep.reseller.data.dao.ListingDao
import com.cegep.reseller.data.entity.Listing
import com.cegep.reseller.data.entity.ListingStatus
import kotlinx.coroutines.flow.Flow

class ListingRepository(private val dao: ListingDao) {

    fun observeActive(): Flow<List<Listing>> = dao.observeAll(ListingStatus.ACTIVE)

    fun observeBySeller(sellerId: Long): Flow<List<Listing>> = dao.observeBySeller(sellerId)

    fun observe(id: Long): Flow<Listing?> = dao.observeById(id)

    fun search(query: String): Flow<List<Listing>> = dao.search(query.trim())

    suspend fun create(listing: Listing): Long = dao.insert(listing)

    suspend fun update(listing: Listing) = dao.update(listing)

    suspend fun delete(listing: Listing) = dao.delete(listing)

    suspend fun get(id: Long): Listing? = dao.findById(id)

    suspend fun markSold(id: Long) = dao.setStatus(id, ListingStatus.SOLD)
}
