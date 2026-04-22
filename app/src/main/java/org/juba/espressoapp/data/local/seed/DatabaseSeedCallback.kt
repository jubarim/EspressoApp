package org.juba.espressoapp.data.local.seed

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Seeds the database with sample roasters on first install (debug builds only).
 * Only runs inside [RoomDatabase.Callback.onCreate], so it will not re-run on subsequent
 * launches or after destructive migrations unless the app data is cleared.
 */
internal object DatabaseSeedCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        SEED_ROASTERS.forEach { db.execSQL(it) }
        SEED_COFFEE_BEANS.forEach { db.execSQL(it) }
        SEED_GRINDERS.forEach { db.execSQL(it) }
        SEED_ESPRESSO_MACHINES.forEach { db.execSQL(it) }
        SEED_FILTER_BASKETS.forEach { db.execSQL(it) }
    }

    // Unix ms timestamp used for all seed rows — 2024-01-01T00:00:00Z
    private const val SEED_TS = 1704067200000L

    private val SEED_ROASTERS = listOf(
        // ── Brazilian ──────────────────────────────────────────────────────────
        roasterSql(
            id = "00000000-0000-0000-0000-100000000001",
            name = "Do Coado ao Espresso",
            country = "BR",
            city = "Lauro de Freitas",
            website = "https://docoadoaoespresso.com.br",
            imageUri = "https://acdn-us.mitiendanube.com/stores/002/074/422/themes/common/logo-1233672698-1717031155-029973e23b9c59a29c7581f4d3199a531717031155-480-0.webp",
            notes = "Champion of the World Roasting Championship. Specialty roaster from Bahia.",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-100000000002",
            name = "Five Roasters",
            country = "BR",
            city = "Rio de Janeiro",
            website = "https://fiveroasters.com.br",
            imageUri = "https://acdn-us.mitiendanube.com/stores/001/226/985/themes/common/logo-2026287311-1592445488-5a01b6f319e5eec0b8520ca0ac3fdf471592445488-480-0.webp",
            notes = "Specialty roaster with the greatest diversity of coffees in Brazil.",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-100000000003",
            name = "UTI Roast Cafés",
            country = "BR",
            city = "Campinas",
            website = "https://www.instagram.com/utiroastcafes/",
            notes = null,
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-100000000004",
            name = "Roast Cafés",
            country = "BR",
            city = "São Paulo",
            website = "https://roastcafes.com",
            imageUri = "https://roastcafes.com/wp-content/uploads/2018/01/logo-roast-2-e1517364676282.png.webp",
            notes = "Micro roastery of specialty coffees founded in 2017.",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-100000000005",
            name = "Moinho de Ouro",
            country = "BR",
            city = "Montes Claros",
            website = "https://www.instagram.com/cafemoinhodeouro/",
            notes = "Specialty roaster from northern Minas Gerais; traceable and sustainable.",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-100000000006",
            name = "Coffee Lab",
            country = "BR",
            city = "São Paulo",
            website = "https://coffeelab.com.br",
            imageUri = "https://images.squarespace-cdn.com/content/v1/5f52e377d6a3865a30a9ca83/1621941358507-OAK3H3GPASES25Z47VIX/LOGO+-+Principal.png?format=1500w",
            notes = "Founded by Isabela Raposeiras. Roasting lab, barista school and specialty café in Vila Madalena.",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-100000000007",
            name = "Garagem do Café",
            country = "BR",
            city = "São Paulo",
            website = "https://garagemdocafe.com.br",
            imageUri = "https://garagemdocafe.com.br/wp-content/uploads/2025/10/cropped-logo-garagem-16-10-25-1-118x163.jpg",
            notes = "Co-roasting space roasting over 7 tons per month.",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-100000000008",
            name = "Torra Fresca Cafés",
            country = "BR",
            city = "São Paulo",
            website = "https://torrafrescacafes.com.br",
            notes = "Fresh-roast specialty coffees sourced from family farms in south-eastern Minas Gerais.",
        ),
        // ── International ─────────────────────────────────────────────────────
        roasterSql(
            id = "00000000-0000-0000-0000-000000000001",
            name = "Onyx Coffee Lab",
            country = "US",
            city = "Rogers",
            website = "https://onyxcoffeelab.com",
            notes = "Award-winning specialty roaster from Arkansas.",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-000000000002",
            name = "Tim Wendelboe",
            country = "NO",
            city = "Oslo",
            website = "https://timwendelboe.no",
            imageUri = "https://timwendelboe.no/cdn/shop/files/timwendelboeLogo.png?v=1756719442&width=170",
            notes = "World Barista Champion; focuses on light Nordic-style roasts.",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-000000000003",
            name = "Square Mile Coffee Roasters",
            country = "GB",
            city = "London",
            website = "https://shop.squaremilecoffee.com",
            imageUri = "https://shop.squaremilecoffee.com/cdn/shop/files/SQM_Logos_1.svg?v=1700495500&width=50",
            notes = "Co-founded by James Hoffmann; known for transparent sourcing.",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-000000000004",
            name = "Illy",
            country = "IT",
            city = "Trieste",
            website = "https://illy.com",
            imageUri = "https://www.illy.com/on/demandware.static/Sites-illy_Global_SFRA-Site/-/default/dw79b4ff5e/images/logo-illy.svg",
            notes = "Italian icon; blends of 9 Arabica origins.",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-000000000005",
            name = "Nomad Coffee",
            country = "ES",
            city = "Barcelona",
            website = "https://nomadcoffee.es",
            imageUri = "https://cdn.shopify.com/s/files/1/0772/5485/2893/files/LOGO_NOMAD_COFFEE-03_copia_200x60@2x.jpg?v=1695650679",
            notes = "Specialty roaster and café in Barcelona.",
        ),
    )

    private val SEED_COFFEE_BEANS = listOf(
        coffeeBeanSql(
            id = "00000000-0000-0000-0000-200000000001",
            roasterId = "00000000-0000-0000-0000-000000000003", // Square Mile Coffee Roasters
            name = "Red Brick",
            origin = "Brazil / Colombia blend",
            process = "Washed",
            roastLevel = "Medium",
            roastDate = 1768780800000L, // 2026-01-19
            notes = "Espresso blend with milk chocolate, hazelnut, and caramel sweetness.",
        )
    )

    private val SEED_ESPRESSO_MACHINES = listOf(
        espressoMachineSql(
            id = "00000000-0000-0000-0000-400000000001",
            brand = "ECM",
            model = "Synchronika",
            boilerType = "HX",
            pumpType = "Rotary",
            groupHead = "E61",
            hasPressureGauge = true,
            purchaseDate = 1711929600000L, // 2024-04-01
            notes = "Dual manometers for pump and boiler pressure.",
            imageUri = "https://www.ecm.de/wp-content/uploads/2025/04/ECM_Synchronika_II-frontal-768x504-1.jpg",
        ),
        espressoMachineSql(
            id = "00000000-0000-0000-0000-400000000002",
            brand = "La Marzocco",
            model = "Linea Mini",
            boilerType = "Dual Boiler",
            pumpType = "Rotary",
            groupHead = "Saturated",
            hasPressureGauge = true,
            purchaseDate = 1724025600000L, // 2024-08-20
            notes = "Commercial saturated group. Excellent temperature stability.",
            imageUri = "https://www.koacafes.com.br/cdn/shop/files/b2.jpg?v=1732023450&width=1445",
        ),
        espressoMachineSql(
            id = "00000000-0000-0000-0000-400000000003",
            brand = "Strietman",
            model = "CT2",
            boilerType = "No Boiler",
            pumpType = "Direct Lever",
            groupHead = "Lever",
            hasPressureGauge = false,
            purchaseDate = 1739145600000L, // 2025-02-10
            notes = "Manual lever, no boiler — supply your own hot water.",
            imageUri = "https://images.squarespace-cdn.com/content/v1/60d0e3f1becdc5075d5d6b83/f9964413-46d7-4f8c-8eab-5f6ae3e2f0c0/_MG_4775.JPG?format=2500w",
        ),
        espressoMachineSql(
            id = "00000000-0000-0000-0000-400000000004",
            brand = "Decent",
            model = "DE1",
            boilerType = "Thermoblock",
            pumpType = "Vibratory",
            groupHead = "Commercial",
            hasPressureGauge = false,
            purchaseDate = 1733270400000L, // 2024-12-05
            notes = "App-controlled pressure profiling. Flow and pressure sensors built in.",
            imageUri = "https://fast.decentespresso.com/img/acc-07.avif",
        ),
        espressoMachineSql(
            id = "00000000-0000-0000-0000-400000000005",
            brand = "Rocket",
            model = "Evoluzione Giotto v2",
            boilerType = "HX",
            pumpType = "Rotatory",
            groupHead = "E61",
            hasPressureGauge = true,
            purchaseDate = null,
            notes = "HX machine with E61 group. Classic Italian prosumer design.",
            imageUri = "https://i.ytimg.com/vi/k03Pfbtk9hw/hq720.jpg",
        ),
        espressoMachineSql(
            id = "00000000-0000-0000-0000-400000000006",
            brand = "Rocket",
            model = "R Nine One",
            boilerType = "Dual Boiler",
            pumpType = "Rotary",
            groupHead = "E61",
            hasPressureGauge = true,
            purchaseDate = null,
            notes = "Dual boiler flagship from Rocket. Independent brew and steam temperature control.",
            imageUri = "https://cdn.sanity.io/images/zhlpxy7s/production/fd2257e9be8882e3d20656c535e7eb4b66f7161a-2000x1125.webp?q=65&auto=format",
        ),
        espressoMachineSql(
            id = "00000000-0000-0000-0000-400000000007",
            brand = "Rocket",
            model = "Giotto Cronometro R",
            boilerType = "HX",
            pumpType = "Rotary",
            groupHead = "E61",
            hasPressureGauge = true,
            purchaseDate = null,
            notes = "HX with rotary pump and built-in shot timer.",
            imageUri = "https://pasqualimaquinas.fbitsstatic.net/img/p/giotto-cronometro-r-inox-220v-70330/256768-1.jpg?w=935&h=800&v=202602071743&qs=ignore",
        ),
        espressoMachineSql(
            id = "00000000-0000-0000-0000-400000000008",
            brand = "ECM",
            model = "Classika II PID",
            boilerType = "Single Boiler",
            pumpType = "Vibratory",
            groupHead = "E61",
            hasPressureGauge = true,
            purchaseDate = null,
            notes = "Single boiler with PID and E61 group. Compact prosumer machine.",
            imageUri = "https://cdn.shopify.com/s/files/1/0564/3288/5835/files/ecm-case-on-3.jpg?v=1710609269",
        ),
        espressoMachineSql(
            id = "00000000-0000-0000-0000-400000000009",
            brand = "Flair",
            model = "58 Plus",
            boilerType = "No Boiler",
            pumpType = "Direct Lever",
            groupHead = "Lever",
            hasPressureGauge = true,
            purchaseDate = null,
            notes = "Manual lever with 58mm portafilter and built-in pressure gauge. Supply your own hot water.",
            imageUri = "https://cafino.com.br/cdn/shop/files/58_V2Main_065e1c6f-9320-4436-97e2-cadd7abdcb63.jpg?v=1734393107&width=1200",
        ),
        espressoMachineSql(
            id = "00000000-0000-0000-0000-400000000010",
            brand = "La Spaziale",
            model = "Mini Vivaldi S1",
            boilerType = "Dual Boiler",
            pumpType = "Vibratory",
            groupHead = "Commercial",
            hasPressureGauge = true,
            purchaseDate = null,
            notes = "Compact dual boiler with independent temperature control for brew and steam.",
            imageUri = "https://img.archiexpo.com/pt/images_ae/photo-mg/49369-20334982.jpg",
        ),
    )

    private fun espressoMachineSql(
        id: String,
        brand: String,
        model: String,
        boilerType: String?,
        pumpType: String?,
        groupHead: String?,
        hasPressureGauge: Boolean,
        purchaseDate: Long?,
        notes: String?,
        imageUri: String? = null,
    ): String {
        fun String?.toSqlValue() = if (this == null) "NULL" else "'${replace("'", "''")}'"
        fun Long?.toSqlValue() = this?.toString() ?: "NULL"
        val gaugeValue = if (hasPressureGauge) "1" else "0"
        return """
            INSERT OR IGNORE INTO espresso_machines
                (id, brand, model, boiler_type, pump_type, group_head, has_pressure_gauge, purchase_date, image_uri, notes, is_deleted, created_at, updated_at)
            VALUES
                ('$id', ${brand.toSqlValue()}, ${model.toSqlValue()}, ${boilerType.toSqlValue()},
                 ${pumpType.toSqlValue()}, ${groupHead.toSqlValue()}, $gaugeValue,
                 ${purchaseDate.toSqlValue()}, ${imageUri.toSqlValue()}, ${notes.toSqlValue()}, 0, $SEED_TS, $SEED_TS)
        """.trimIndent()
    }

    private fun grinderSql(
        id: String,
        brand: String,
        model: String,
        burrType: String?,
        burrSize: String?,
        purchaseDate: Long?,
        burrInstallDate: Long?,
        notes: String?,
        imageUri: String? = null,
    ): String {
        fun String?.toSqlValue() = if (this == null) "NULL" else "'${replace("'", "''")}'"
        fun Long?.toSqlValue() = this?.toString() ?: "NULL"
        return """
            INSERT OR IGNORE INTO grinders
                (id, brand, model, burr_type, burr_size, purchase_date, burr_install_date, image_uri, notes, is_deleted, created_at, updated_at)
            VALUES
                ('$id', ${brand.toSqlValue()}, ${model.toSqlValue()}, ${burrType.toSqlValue()},
                 ${burrSize.toSqlValue()}, ${purchaseDate.toSqlValue()}, ${burrInstallDate.toSqlValue()},
                 ${imageUri.toSqlValue()}, ${notes.toSqlValue()}, 0, $SEED_TS, $SEED_TS)
        """.trimIndent()
    }

    private fun roasterSql(
        id: String,
        name: String,
        country: String?,
        city: String?,
        website: String?,
        notes: String?,
        imageUri: String? = null,
    ): String {
        fun String?.toSqlValue() = if (this == null) "NULL" else "'${replace("'", "''")}'"
        return """
            INSERT OR IGNORE INTO roasters
                (id, name, country, city, website, image_uri, notes, is_deleted, created_at, updated_at)
            VALUES
                ('$id', ${name.toSqlValue()}, ${country.toSqlValue()}, ${city.toSqlValue()},
                 ${website.toSqlValue()}, ${imageUri.toSqlValue()}, ${notes.toSqlValue()}, 0, $SEED_TS, $SEED_TS)
        """.trimIndent()
    }

    private val SEED_GRINDERS = listOf(
        grinderSql(
            id = "00000000-0000-0000-0000-300000000001",
            brand = "Niche",
            model = "Zero",
            burrType = "Conical",
            burrSize = "63 mm",
            purchaseDate = 1709251200000L, // 2024-03-01
            burrInstallDate = 1709251200000L, // 2024-03-01
            notes = "Single dose, zero retention. Grind ~20 for espresso.",
            imageUri = "https://www.nichecoffee.co.uk/cdn/shop/files/In-use-espresso.jpg?v=1717485718&width=740",

        ),
        grinderSql(
            id = "00000000-0000-0000-0000-300000000002",
            brand = "Mahlkönig",
            model = "EK43",
            burrType = "Flat",
            burrSize = "98 mm",
            purchaseDate = 1721001600000L, // 2024-07-15
            burrInstallDate = 1721001600000L, // 2024-07-15
            notes = "Commercial-grade all-purpose grinder. Coarser settings for espresso.",
            imageUri = "https://www.koacafes.com.br/cdn/shop/files/sss.png?v=1707222562&width=1445",
        ),
        grinderSql(
            id = "00000000-0000-0000-0000-300000000003",
            brand = "Eureka",
            model = "Mignon Specialita",
            burrType = "Flat",
            burrSize = "55 mm",
            purchaseDate = 1737331200000L, // 2025-01-20
            burrInstallDate = 1737331200000L, // 2025-01-20
            notes = "Silent grinder with stepless adjustment.",
        ),
        grinderSql(
            id = "00000000-0000-0000-0000-300000000004",
            brand = "Comandante",
            model = "C40 MK4",
            burrType = "Conical",
            burrSize = "39 mm",
            purchaseDate = 1732060800000L, // 2024-11-20
            burrInstallDate = null,
            notes = "High-quality hand grinder. Great for travel espresso.",
        ),
        grinderSql(
            id = "00000000-0000-0000-0000-300000000005",
            brand = "Timemore",
            model = "Sculptor 064S",
            burrType = "Flat",
            burrSize = "64 mm",
            purchaseDate = null,
            burrInstallDate = null,
            notes = "Single dose flat burr with stepless adjustment and low retention.",
            imageUri = "https://img.kavosdraugas.lt/5692603a-b44a-47df-8be5-a28ba7437b18/1000x1000/tmsculptor064swhtupd5png.jpg",
        ),
        grinderSql(
            id = "00000000-0000-0000-0000-300000000006",
            brand = "Turin",
            model = "DF64",
            burrType = "Flat",
            burrSize = "64 mm",
            purchaseDate = null,
            burrInstallDate = null,
            notes = "Single dose 64mm flat burr. Popular budget-friendly option with alignment adjustment.",
            imageUri = "https://espressooutlet.com/cdn/shop/files/DSC01351.jpg?v=1714658176&width=1080",
        ),
    )

    private val SEED_FILTER_BASKETS = listOf(
        filterBasketSql(
            id = "00000000-0000-0000-0000-500000000001",
            brand = "IMS",
            model = "Competition",
            sizeGrams = "18g",
            type = "Precision",
            diameter = "58mm",
            purchaseDate = 1711929600000L, // 2024-04-01
            notes = "Precision laser-cut holes for improved flow uniformity.",
        ),
        filterBasketSql(
            id = "00000000-0000-0000-0000-500000000002",
            brand = "VST",
            model = "Ridgeless",
            sizeGrams = "18g",
            type = "Ridgeless",
            diameter = "58mm",
            purchaseDate = 1724025600000L, // 2024-08-20
            notes = "Industry reference for precision espresso extraction.",
        ),
        filterBasketSql(
            id = "00000000-0000-0000-0000-500000000003",
            brand = "La Marzocco",
            model = "Standard",
            sizeGrams = "14g",
            type = "Ridged",
            diameter = "58mm",
            purchaseDate = 1704067200000L, // 2024-01-01
            notes = "Stock basket shipped with Linea Mini.",
        ),
        filterBasketSql(
            id = "00000000-0000-0000-0000-500000000004",
            brand = "Graph Coffee",
            model = "58mm to 46mm",
            sizeGrams = "16 to 22g",
            type = "Ridgeless",
            diameter = "58mm",
            purchaseDate = 1704067200000L, // 2024-01-01
            notes = "Sweet and full-bodied espresso MOD - a basket that steps the from 58mm to 46mm. Sized for 16g-22g (though exact weights can vary depending on bean and roast level)",
            imageUri = "https://static.wixstatic.com/media/88d502_216cf7e8a073424c8114a29c0557aeb0~mv2.jpg",
        ),
    )

    private fun filterBasketSql(
        id: String,
        brand: String,
        model: String?,
        sizeGrams: String?,
        type: String?,
        diameter: String?,
        purchaseDate: Long?,
        notes: String?,
        imageUri: String? = null,
    ): String {
        fun String?.toSqlValue() = if (this == null) "NULL" else "'${replace("'", "''")}'"
        fun Long?.toSqlValue() = this?.toString() ?: "NULL"
        return """
            INSERT OR IGNORE INTO filter_baskets
                (id, brand, model, size_grams, type, diameter, purchase_date, image_uri, notes, is_deleted, created_at, updated_at)
            VALUES
                ('$id', ${brand.toSqlValue()}, ${model.toSqlValue()}, ${sizeGrams.toSqlValue()},
                 ${type.toSqlValue()}, ${diameter.toSqlValue()}, ${purchaseDate.toSqlValue()},
                 ${imageUri.toSqlValue()}, ${notes.toSqlValue()}, 0, $SEED_TS, $SEED_TS)
        """.trimIndent()
    }

    private fun coffeeBeanSql(
        id: String,
        roasterId: String,
        name: String,
        origin: String?,
        process: String?,
        roastLevel: String?,
        roastDate: Long?,
        notes: String?,
        imageUri: String? = null,
    ): String {
        fun String?.toSqlValue() = if (this == null) "NULL" else "'${replace("'", "''")}'"
        fun Long?.toSqlValue() = this?.toString() ?: "NULL"
        return """
            INSERT OR IGNORE INTO coffee_beans
                (id, roaster_id, name, origin, process, roast_level, roast_date, image_uri, notes, is_deleted, created_at, updated_at)
            VALUES
                ('$id', '$roasterId', ${name.toSqlValue()}, ${origin.toSqlValue()}, ${process.toSqlValue()},
                 ${roastLevel.toSqlValue()}, ${roastDate.toSqlValue()}, ${imageUri.toSqlValue()}, ${notes.toSqlValue()}, 0, $SEED_TS, $SEED_TS)
        """.trimIndent()
    }
}
