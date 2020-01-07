package helpers

import kotlin.math.floor

object RandomGen {

        fun generateRandomString(length: Int = 3): String {
            val sb = StringBuilder()
            for (i in 1..length) {
                sb.append(generateRandomSymbol())
            }
            return sb.toString()
        }

        private fun generateRandomSymbol(): String {
            return floor((10 * Math.random())).toInt().toString()
        }


}