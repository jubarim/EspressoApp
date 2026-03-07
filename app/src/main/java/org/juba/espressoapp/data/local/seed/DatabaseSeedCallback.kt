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
}
