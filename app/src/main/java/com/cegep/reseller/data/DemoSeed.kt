package com.cegep.reseller.data

import com.cegep.reseller.data.entity.Listing
import com.cegep.reseller.data.entity.User

object DemoSeed {

    private const val CURATOR_EMAIL = "curator@reseller.app"
    private const val SAMPLE_SELLER_EMAIL = "seller@reseller.app"

    suspend fun runIfMissing(db: AppDatabase) {
        val userDao = db.userDao()
        val listingDao = db.listingDao()

        val curatorId = userDao.findByEmail(CURATOR_EMAIL)?.id ?: userDao.insert(
            User(
                username = "Curator",
                email = CURATOR_EMAIL,
                passwordHash = PasswordHasher.hash("demo1234")
            )
        )

        val sampleSellerId = userDao.findByEmail(SAMPLE_SELLER_EMAIL)?.id ?: userDao.insert(
            User(
                username = "Sample Seller",
                email = SAMPLE_SELLER_EMAIL,
                passwordHash = PasswordHasher.hash("demo1234")
            )
        )

        if (listingDao.countBySeller(curatorId) == 0) {
            curatorListings(curatorId).forEach { listingDao.insert(it) }
        }

        if (listingDao.countBySeller(sampleSellerId) == 0) {
            sampleSellerListings(sampleSellerId).forEach { listingDao.insert(it) }
        }
    }

    private fun curatorListings(sellerId: Long) = listOf(
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

    private fun sampleSellerListings(sellerId: Long) = listOf(
        Listing(
            sellerId = sellerId,
            title = "Designer Knit Cardigan",
            brand = "Jacquemus",
            priceCents = 19500,
            description = "Cream knit cardigan, relaxed fit. Very clean condition, size M.",
            imagePath = null
        ),
        Listing(
            sellerId = sellerId,
            title = "Leather Chelsea Boots",
            brand = "Saint Laurent",
            priceCents = 42000,
            description = "Black leather Chelsea boots, size 10. Worn a few times, no major marks.",
            imagePath = null
        ),
        Listing(
            sellerId = sellerId,
            title = "Washed Denim Jacket",
            brand = "A.P.C.",
            priceCents = 16000,
            description = "Light blue denim jacket with a straight fit. Size L.",
            imagePath = null
        ),
        Listing(
            sellerId = sellerId,
            title = "Logo Canvas Tote",
            brand = "Maison Kitsune",
            priceCents = 7500,
            description = "Canvas tote bag, natural color. Good everyday bag.",
            imagePath = null
        ),
        Listing(
            sellerId = sellerId,
            title = "Pleated Wide-Leg Pants",
            brand = "Issey Miyake",
            priceCents = 31000,
            description = "Black pleated pants, easy fit. Excellent condition.",
            imagePath = null
        ),
        Listing(
            sellerId = sellerId,
            title = "Wool Overcoat",
            brand = "Sandro",
            priceCents = 27500,
            description = "Charcoal wool overcoat, warm and structured. Size M.",
            imagePath = null
        )
    )
}
