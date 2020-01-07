package helpers

import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.Executors

open class Database {
    companion object {
        val threadQueue = Executors.newFixedThreadPool(3)

        var conn: Connection
            get() {
                if (field.isClosed) {
                    println("Connection was closed, setting new connection")
                    field = DriverManager.getConnection(Environment.databaseUrl)
                    Thread.sleep(3000)
                }
                return field
            }

        init {
            Environment()
            Class.forName(Environment.databaseDriver)
            conn = DriverManager.getConnection(Environment.databaseUrl)
        }
    }

    class Log(private var player: String, private var slot: String, private var casinoWin: Long) : Runnable {

        init {
            threadQueue.submit(this)
        }

        override fun run() {
            val query = "insert into slotmachine (player, slot, casino_win) values (?,?,?)"
            val statement = conn.prepareStatement(query)
            statement.setString(1, player)
            statement.setString(2, slot)
            statement.setLong(3, casinoWin)
            statement.execute()
        }

    }
}