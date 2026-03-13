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
    }

    // Unix ms timestamp used for all seed rows — 2024-01-01T00:00:00Z
    private const val SEED_TS = 1704067200000L

    private val SEED_ROASTERS = listOf(
        roasterSql(
            id = "00000000-0000-0000-0000-100000000001",
            name = "Do Coado Ao Espresso",
            country = "BR",
            city = "Bahia",
            website = "https://docoadoaoespresso.com.br/",
            imageUri = "https://static.wikia.nocookie.net/starwars/images/9/9b/Princessleiaheadwithgun.jpg/revision/latest?cb=20240522043127",
            notes = "Award-winning specialty roaster from Arkansas.",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-100000000002",
            name = "Five Roasters",
            country = "BR",
            city = "Rio de Janeiro",
            website = "https://fiveroasters.com.br/",
            imageUri = "https://acdn-us.mitiendanube.com/stores/001/226/985/themes/common/logo-2026287311-1592445488-5a01b6f319e5eec0b8520ca0ac3fdf471592445488-480-0.webp",
            notes = "Boa torra do Rio",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-100000000003",
            name = "UTI Roast Cafés",
            country = "BR",
            city = "Campinas",
            website = "https://www.instagram.com/utiroastcafes/",
            imageUri = "https://scontent-gru1-1.cdninstagram.com/v/t51.2885-19/89221577_141719977153396_8459604951864180736_n.jpg?efg=eyJ2ZW5jb2RlX3RhZyI6InByb2ZpbGVfcGljLmRqYW5nby40NzEuYzIifQ&_nc_ht=scontent-gru1-1.cdninstagram.com&_nc_cat=104&_nc_oc=Q6cZ2QE8YhliR03UNGb8_1c4lU6BM3xOh_05J9q9UiLhRacAybQFzGMAfo2rmkKl1NYkbAU&_nc_ohc=8XpG4bUfBMsQ7kNvwErsOhm&_nc_gid=B1PqmEi2x7yWz4hoWNx2_g&edm=AP4sbd4BAAAA&ccb=7-5&oh=00_AfyXwgje9JVa_CCrVcH8o08yrdI1WmtB_aMpP1uVwvY3vg&oe=69B24EFA&_nc_sid=7a9f4b",
            notes = "Melhor torra de Campinas",
        ),
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
            notes = "World Barista Champion; focuses on light Nordic-style roasts.",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-000000000003",
            name = "Square Mile Coffee Roasters",
            country = "GB",
            city = "London",
            website = "https://squaremilecoffee.com",
            notes = "Co-founded by James Hoffmann; known for transparent sourcing.",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-000000000004",
            name = "Five Elephant",
            country = "DE",
            city = "Berlin",
            website = "https://fiveelephant.com",
            notes = "Specialty roaster and café in Kreuzberg.",
        ),
        roasterSql(
            id = "00000000-0000-0000-0000-000000000005",
            name = "Morgon Coffee Roasters",
            country = "SE",
            city = "Gothenburg",
            website = "https://morgon.coffee",
            notes = "Scandinavian roaster with a focus on single-origins.",
        ),
    )

    private val SEED_COFFEE_BEANS = listOf(
        coffeeBeanSql(
            id = "00000000-0000-0000-0000-200000000001",
            roasterId = "00000000-0000-0000-0000-000000000001", // Onyx Coffee Lab
            name = "Southern Weather",
            origin = "Ethiopia, Yirgacheffe",
            process = "Washed",
            roastLevel = "Light",
            roastDate = 1767744000000L, // 2026-01-07
            notes = "Jasmine, stone fruit, and sparkling citrus acidity.",
        ),
        coffeeBeanSql(
            id = "00000000-0000-0000-0000-200000000002",
            roasterId = "00000000-0000-0000-0000-000000000002", // Tim Wendelboe
            name = "Ethiopia Idido",
            origin = "Ethiopia, Yirgacheffe",
            process = "Natural",
            roastLevel = "Light",
            roastDate = 1770076800000L, // 2026-02-03
            notes = "Blueberry, dark chocolate, and floral complexity.",
        ),
        coffeeBeanSql(
            id = "00000000-0000-0000-0000-200000000003",
            roasterId = "00000000-0000-0000-0000-000000000003", // Square Mile Coffee Roasters
            name = "Red Brick",
            origin = "Brazil / Colombia blend",
            process = "Washed",
            roastLevel = "Medium",
            roastDate = 1768780800000L, // 2026-01-19
            notes = "Espresso blend with milk chocolate, hazelnut, and caramel sweetness.",
        ),
        coffeeBeanSql(
            id = "00000000-0000-0000-0000-200000000004",
            roasterId = "00000000-0000-0000-0000-000000000004", // Five Elephant
            name = "Kenya Kiambu AA",
            origin = "Kenya, Kiambu",
            process = "Washed",
            roastLevel = "Light",
            roastDate = 1771718400000L, // 2026-02-22
            notes = "Blackcurrant, tomato, and bright malic acidity.",
        ),
        coffeeBeanSql(
            id = "00000000-0000-0000-0000-200000000005",
            roasterId = "00000000-0000-0000-0000-000000000005", // Morgon Coffee Roasters
            name = "Kayon Mountain",
            origin = "Ethiopia, Guji",
            process = "Natural",
            roastLevel = "Light",
            roastDate = 1769558400000L, // 2026-01-28
            notes = "Strawberry jam, rose water, and creamy mouthfeel.",
        ),
        coffeeBeanSql(
            id = "00000000-0000-0000-0000-200000000006",
            roasterId = "00000000-0000-0000-0000-100000000001", // Do Coado Ao Espresso
            name = "Fazenda Recanto",
            origin = "Brazil, Minas Gerais",
            process = "Natural",
            roastLevel = "Medium",
            roastDate = 1770768000000L, // 2026-02-11
            notes = "Dark chocolate, dried fruit, and smooth low acidity.",
        ),
        coffeeBeanSql(
            id = "00000000-0000-0000-0000-200000000007",
            roasterId = "00000000-0000-0000-0000-100000000002", // Five Roasters
            name = "Sítio Santa Maria",
            origin = "Brazil, Sul de Minas",
            process = "Pulped Natural",
            roastLevel = "Medium",
            roastDate = 1772668800000L, // 2026-03-05
            notes = "Caramel, nuts, and mild fruit sweetness.",
        ),
        coffeeBeanSql(
            id = "00000000-0000-0000-0000-200000000008",
            roasterId = "00000000-0000-0000-0000-100000000003", // UTI Roast Cafés
            name = "Colombia El Paraíso",
            origin = "Colombia, Huila",
            process = "Washed",
            roastLevel = "Light",
            roastDate = 1768348800000L, // 2026-01-14
            notes = "Red apple, brown sugar, and balanced citric brightness.",
        ),
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
    )

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
