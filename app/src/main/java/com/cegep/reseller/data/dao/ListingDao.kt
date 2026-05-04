package com.cegep.reseller.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cegep.reseller.data.entity.Listing
import com.cegep.reseller.data.entity.ListingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(listing: Listing): Long

    @Update
    suspend fun update(listing: Listing)

    @Delete
    suspend fun delete(listing: Listing)

    @Query("SELECT * FROM listings WHERE id = :id")
    suspend fun findById(id: Long): Listing?

    @Query("SELECT * FROM listings WHERE id = :id")
    fun observeById(id: Long): Flow<Listing?>

    @Query("SELECT * FROM listings WHERE status = :status ORDER BY createdAt DESC")
    fun observeAll(status: ListingStatus = ListingStatus.ACTIVE): Flow<List<Listing>>

    @Query("SELECT * FROM listings WHERE sellerId = :sellerId ORDER BY createdAt DESC")
    fun observeBySeller(sellerId: Long): Flow<List<Listing>>

    @Query("""
        SELECT * FROM listings
        WHERE status = :status
          AND (title LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
    """)
    fun search(query: String, status: ListingStatus = ListingStatus.ACTIVE): Flow<List<Listing>>

    @Query("UPDATE listings SET status = :status WHERE id = :id")
    suspend fun setStatus(id: Long, status: ListingStatus)
}
