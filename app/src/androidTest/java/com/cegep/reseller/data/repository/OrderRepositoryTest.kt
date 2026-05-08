package com.cegep.reseller.data.repository

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
class OrderRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var cart: CartRepository
    private lateinit var orders: OrderRepository
    private var sellerId: Long = 0L
    private var buyerId: Long = 0L

    @Before
    fun setup() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        cart = CartRepository(db.cartDao())
        orders = OrderRepository(db.orderDao(), db.listingDao(), cart)

        sellerId = db.userDao().insert(User(username = "Seller", email = "s@s.com", passwordHash = "x"))
        buyerId = db.userDao().insert(User(username = "Buyer", email = "b@b.com", passwordHash = "x"))
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun checkoutMarksListingsSoldAndClearsCart() = runTest {
        val id1 = db.listingDao().insert(makeListing("A", 1500))
        val id2 = db.listingDao().insert(makeListing("B", 2500))
        cart.add(buyerId, id1)
        cart.add(buyerId, id2)

        orders.checkout(
            userId = buyerId,
            listingIds = listOf(id1, id2),
            totalCents = 4000,
            shippingAddress = "1 Demo Way",
            paymentMasked = "•••• 4242"
        )

        assertEquals(ListingStatus.SOLD, db.listingDao().findById(id1)!!.status)
        assertEquals(ListingStatus.SOLD, db.listingDao().findById(id2)!!.status)
        assertTrue(cart.observe(buyerId).first().isEmpty())
    }

    @Test
    fun checkoutRecordsOrdersInHistory() = runTest {
        val id = db.listingDao().insert(makeListing("Solo", 9900))
        cart.add(buyerId, id)

        orders.checkout(
            userId = buyerId,
            listingIds = listOf(id),
            totalCents = 9900,
            shippingAddress = "10 Rue Test",
            paymentMasked = "•••• 0000"
        )

        val history = orders.observeForUser(buyerId).first()
        assertEquals(1, history.size)
        val order = history.first()
        assertEquals(buyerId, order.buyerId)
        assertEquals(id, order.listingId)
        assertEquals(9900L, order.totalCents)
        assertEquals("10 Rue Test", order.shippingAddress)
        assertNotNull(order.paymentMasked)
    }

    @Test
    fun soldListingIsExcludedFromActiveBrowse() = runTest {
        val id = db.listingDao().insert(makeListing("Will be sold", 1000))
        cart.add(buyerId, id)
        orders.checkout(buyerId, listOf(id), 1000, "x", "y")

        val active = db.listingDao().observeAll().first()
        assertTrue(active.none { it.id == id })
    }

    private fun makeListing(title: String, price: Long) = Listing(
        sellerId = sellerId,
        title = title,
        brand = "TestBrand",
        priceCents = price,
        description = "",
        imagePath = null
    )
}
