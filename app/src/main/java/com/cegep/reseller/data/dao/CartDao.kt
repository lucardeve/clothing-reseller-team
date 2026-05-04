package com.cegep.reseller.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cegep.reseller.data.entity.CartItem
import com.cegep.reseller.data.entity.Listing
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: CartItem)

    @Query("DELETE FROM cart_items WHERE userId = :userId AND listingId = :listingId")
    suspend fun remove(userId: Long, listingId: Long)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearForUser(userId: Long)

    @Query("""
        SELECT l.* FROM listings l
        INNER JOIN cart_items c ON c.listingId = l.id
        WHERE c.userId = :userId
        ORDER BY c.addedAt DESC
    """)
    fun observeCart(userId: Long): Flow<List<Listing>>

    @Query("""
        SELECT EXISTS(SELECT 1 FROM cart_items WHERE userId = :userId AND listingId = :listingId)
    """)
    fun isInCart(userId: Long, listingId: Long): Flow<Boolean>
}
