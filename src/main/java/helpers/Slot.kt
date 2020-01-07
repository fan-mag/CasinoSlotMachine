package helpers

object Slot {
    fun play(): String {
        return RandomGen.generateRandomString(3)
    }

    fun getRate(slot: String): Environment.Rates {
        if (slot[0] == slot[1] && slot[0] != slot[2]) return Environment.Rates.TWO_NUMBERS
        if (slot[0] == slot[2] && slot[0] != slot[1]) return Environment.Rates.TWO_NUMBERS
        if (slot[1] == slot[2] && slot[0] != slot[2]) return Environment.Rates.TWO_NUMBERS
        if (slot[0] == slot[1] && slot[0] == slot[2]) {
            return when (slot[0]) {
                '0' -> Environment.Rates.THREE_NUMBERS_BRONZE
                '5' -> Environment.Rates.THREE_NUMBERS_SILVER
                '9' -> Environment.Rates.THREE_NUMBERS_BRONZE
                '7' -> Environment.Rates.THREE_NUMBERS_GOLD
                else -> Environment.Rates.THREE_NUMBERS
            }
        }
        return Environment.Rates.LOSE
    }
}