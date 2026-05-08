package com.cegep.reseller.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ListingStatus { ACTIVE, SOLD }

@Entity(
    tableName = "listings",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["sellerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sellerId"), Index("status")]
)
data class Listing(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sellerId: Long,
    val title: String,
    val brand: String,
    val priceCents: Long,
    val description: String,
    val imagePath: String?,
    val status: ListingStatus = ListingStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis()
)
