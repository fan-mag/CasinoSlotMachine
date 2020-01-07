package helpers

import java.io.FileReader
import java.util.*

class Environment {
    companion object
    {
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
        }
    }

}