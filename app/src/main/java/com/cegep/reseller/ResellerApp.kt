package com.cegep.reseller

import android.app.Application
import com.cegep.reseller.data.AppDatabase
import com.cegep.reseller.data.DemoSeed
import com.cegep.reseller.data.SessionManager
import com.cegep.reseller.data.repository.AuthRepository
import com.cegep.reseller.data.repository.CartRepository
import com.cegep.reseller.data.repository.ListingRepository
import com.cegep.reseller.data.repository.OrderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ResellerApp : Application() {

    lateinit var container: AppContainer
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        appScope.launch { DemoSeed.runIfMissing(container.db) }
    }
}

class AppContainer(application: Application) {
    val db = AppDatabase.get(application)
    private val session = SessionManager(application)

    val auth = AuthRepository(db.userDao(), session)
    val listings = ListingRepository(db.listingDao())
    val cart = CartRepository(db.cartDao())
    val orders = OrderRepository(db.orderDao(), db.listingDao(), cart)
    val userDao = db.userDao()
}
