package helpers

import CasinoLib.exceptions.ForbiddenException
import CasinoLib.services.Auth
import com.google.gson.Gson
import exceptions.WrongContentTypeException
import model.Bet

object RequestProcess {
    fun validateContentType(contentType: String) {
        if (!contentType.contains("application/json")) throw WrongContentTypeException(contentType)
    }

    fun validateApikey(apikey: String) {
        when(Auth.getUserPrivilege(apikey).level) {
            !in Environment.players -> throw ForbiddenException()
        }
    }

    fun bodyToBet(requestBody: String): Bet {
        return Gson().fromJson(requestBody, Bet::class.java)
    }
}