package com.cegep.reseller.data

import com.cegep.reseller.data.entity.Listing
import com.cegep.reseller.data.entity.User

object DemoSeed {

    private const val CURATOR_EMAIL = "curator@reseller.app"

    suspend fun runIfMissing(db: AppDatabase) {
        val userDao = db.userDao()
        if (userDao.findByEmail(CURATOR_EMAIL) != null) return

        val curatorId = userDao.insert(
            User(
                username = "Curator",
                email = CURATOR_EMAIL,
                passwordHash = PasswordHasher.hash("demo1234")
            )
        )

        val listingDao = db.listingDao()
        seedListings(curatorId).forEach { listingDao.insert(it) }
    }

    private fun seedListings(sellerId: Long) = listOf(
        Listing(
            sellerId = sellerId,
            title = "Vintage Suede Bomber",
            brand = "Schott NYC",
            priceCents = 38000,
            description = "Tan suede bomber, late-90s. Buttery soft, no scuffs. Size M.",
            imagePath = null
        ),
        Listing(
            sellerId = sellerId,
            title = "Air Jordan 1 Retro High",
            brand = "Nike",
            priceCents = 24500,
            description = "OG Chicago colorway. Size 10.5 US. Light creasing, original box included.",
            imagePath = null
        ),
        Listing(
            sellerId = sellerId,
            title = "Box Logo Hoodie",
            brand = "Supreme",
            priceCents = 56000,
            description = "FW18 Black on Black. Size L. Worn twice, tags removed.",
            imagePath = null
        ),
        Listing(
            sellerId = sellerId,
            title = "Slim-Fit Wool Trousers",
            brand = "Acne Studios",
            priceCents = 18000,
            description = "Charcoal grey, Italian wool. Size 30. Tailored hem at 31\".",
            imagePath = null
        ),
        Listing(
            sellerId = sellerId,
            title = "Monogram Scarf",
            brand = "Burberry",
            priceCents = 22000,
            description = "Classic check, 100% lambswool. New with tags.",
            imagePath = null
        ),
        Listing(
            sellerId = sellerId,
            title = "Stan Smith Primegreen",
            brand = "Adidas",
            priceCents = 9000,
            description = "White / green tabs, size 9 US. Clean uppers, soles fresh.",
            imagePath = null
        )
    )
}
