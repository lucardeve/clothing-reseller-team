package com.cegep.reseller.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.cegep.reseller.data.dao.CartDao
import com.cegep.reseller.data.dao.ListingDao
import com.cegep.reseller.data.dao.OrderDao
import com.cegep.reseller.data.dao.UserDao
import com.cegep.reseller.data.entity.CartItem
import com.cegep.reseller.data.entity.Listing
import com.cegep.reseller.data.entity.ListingStatus
import com.cegep.reseller.data.entity.Order
import com.cegep.reseller.data.entity.User

class ListingStatusConverter {
    @TypeConverter fun toName(value: ListingStatus): String = value.name
    @TypeConverter fun fromName(value: String): ListingStatus = ListingStatus.valueOf(value)
}

@Database(
    entities = [User::class, Listing::class, CartItem::class, Order::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListingStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun listingDao(): ListingDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "reseller.db"
            ).build().also { instance = it }
        }
    }
}
