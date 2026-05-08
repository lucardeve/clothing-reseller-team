package com.cegep.reseller.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cegep.reseller.data.AppDatabase
import com.cegep.reseller.data.entity.Listing
import com.cegep.reseller.data.entity.ListingStatus
import com.cegep.reseller.data.entity.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListingDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ListingDao
    private var sellerId: Long = 0L

    @Before
    fun setup() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.listingDao()
        sellerId = db.userDao().insert(
            User(username = "Seller", email = "s@s.com", passwordHash = "x")
        )
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndFetchListing() = runTest {
        val id = dao.insert(makeListing(title = "Tee", brand = "Levis", price = 2500))
        val fetched = dao.findById(id)
        assertNotNull(fetched)
        assertEquals("Tee", fetched!!.title)
        assertEquals("Levis", fetched.brand)
        assertEquals(2500L, fetched.priceCents)
    }

    @Test
    fun searchMatchesByBrandCaseInsensitive() = runTest {
        dao.insert(makeListing(title = "Hoodie", brand = "Nike", price = 5000))
        dao.insert(makeListing(title = "Cap", brand = "Adidas", price = 3000))

        val results = dao.search("nike").first()

        assertEquals(1, results.size)
        assertEquals("Hoodie", results.first().title)
    }

    @Test
    fun searchMatchesByTitle() = runTest {
        dao.insert(makeListing(title = "Bomber Jacket", brand = "Schott", price = 30000))
        dao.insert(makeListing(title = "Sneakers", brand = "Nike", price = 12000))

        val results = dao.search("Bomber").first()

        assertEquals(1, results.size)
        assertEquals("Bomber Jacket", results.first().title)
    }

    @Test
    fun observeAllExcludesSoldListings() = runTest {
        val activeId = dao.insert(makeListing(title = "Active", brand = "X", price = 1000))
        val soldId = dao.insert(makeListing(title = "Sold", brand = "X", price = 1000))
        dao.setStatus(soldId, ListingStatus.SOLD)

        val active = dao.observeAll().first()

        assertTrue(active.any { it.id == activeId })
        assertTrue(active.none { it.id == soldId })
    }

    @Test
    fun updatePersistsChanges() = runTest {
        val id = dao.insert(makeListing(title = "Old", brand = "X", price = 1000))
        val original = dao.findById(id)!!
        dao.update(original.copy(title = "New", priceCents = 2000))

        val updated = dao.findById(id)!!
        assertEquals("New", updated.title)
        assertEquals(2000L, updated.priceCents)
    }

    @Test
    fun deleteRemovesListing() = runTest {
        val id = dao.insert(makeListing(title = "Goner", brand = "X", price = 500))
        val listing = dao.findById(id)!!
        dao.delete(listing)
        assertTrue(dao.findById(id) == null)
    }

    private fun makeListing(title: String, brand: String, price: Long) = Listing(
        sellerId = sellerId,
        title = title,
        brand = brand,
        priceCents = price,
        description = "test",
        imagePath = null
    )
}
