package com.cegep.reseller.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cegep.reseller.data.entity.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert
    suspend fun insert(order: Order): Long

    @Query("SELECT * FROM orders WHERE buyerId = :userId ORDER BY createdAt DESC")
    fun observeForUser(userId: Long): Flow<List<Order>>
}
