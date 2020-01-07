package helpers

import java.io.FileReader
import java.util.*

class Environment {
    companion object {
        private val rates = Properties()
        val databaseUrl: String
        val databaseDriver: String
        const val service = "Slot Machine"
        val players = setOf(7)
        val banned = setOf(1)

        init {
            val properties = Properties()
            properties.load(FileReader("src/main/resources/database.properties"))
            databaseUrl = properties.getProperty("db.url")
            databaseDriver = properties.getProperty("db.driver")

            rates.load(FileReader("src/main/resources/rates.properties"))
        }
    }

    enum class Rates(val rate: Int) {
        LOSE(rates.getProperty("rate_lose").toInt()),
        TWO_NUMBERS(rates.getProperty("rate_two_numbers").toInt()),
        THREE_NUMBERS(rates.getProperty("rate_three_numbers").toInt()),
        THREE_NUMBERS_BRONZE(rates.getProperty("rate_three_numbers_bronze").toInt()),
        THREE_NUMBERS_SILVER(rates.getProperty("rate_three_numbers_silver").toInt()),
        THREE_NUMBERS_GOLD(rates.getProperty("rate_three_numbers_gold").toInt())
    }
}