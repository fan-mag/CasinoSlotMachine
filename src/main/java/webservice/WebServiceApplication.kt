package webservice

import CasinoLib.exceptions.ForbiddenException
import CasinoLib.exceptions.NotEnoughMoney
import CasinoLib.exceptions.WrongApikeyProvidedException
import CasinoLib.helpers.Exceptions
import CasinoLib.model.Message
import CasinoLib.model.Operator
import CasinoLib.services.Account
import CasinoLib.services.Auth
import CasinoLib.services.CasinoLibrary
import exceptions.InvalidRequestBodyException
import exceptions.WrongContentTypeException
import helpers.Database
import helpers.Environment
import helpers.RequestProcess
import helpers.Slot
import model.Reward
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
open class WebServiceApplication {
    companion object {
        lateinit var operator: Operator

        @JvmStatic
        fun main(args: Array<String>) {
            Database()
            CasinoLibrary.init("src/main/resources/casinolib.properties")
            operator = Operator().fromJson()
            SpringApplication.run(WebServiceApplication::class.java)
        }
    }


    @PostMapping("/slot")
    fun slotMachine(@RequestBody requestBody: String,
                    @RequestHeader(name = "Content-Type", required = true) contentType: String,
                    @RequestHeader(name = "apikey", required = true) apikey: String): ResponseEntity<Any> {
        try {
            RequestProcess.validateContentType(contentType)
            RequestProcess.validateApikey(apikey)
            val bet = RequestProcess.bodyToBet(requestBody)
            val userLogin = Auth.getUserLogin(apikey)
            Account.subBalance(operator.apikey, userLogin, bet.amount)
            Account.addBalance(operator.apikey, operator.login, bet.amount)
            val slot = Slot.play()
            val winRate = Slot.getRate(slot)
            val winAmount = bet.amount * winRate.rate
            if (winRate.rate != 0) {
                Account.addBalance(operator.apikey, userLogin, winAmount)
                Account.subBalance(operator.apikey, operator.login, winAmount)
            }
            Database.Log(userLogin, slot, bet.amount - winAmount)
            return ResponseEntity(Reward(slot = slot, win = winAmount), HttpStatus.OK)
        } catch (exception: Exception) {
            when (exception) {
                is WrongContentTypeException -> return ResponseEntity(Message("Wrong Content-Type Header"), HttpStatus.BAD_REQUEST)
                is WrongApikeyProvidedException -> return ResponseEntity(Message("Wrong Apikey provided"), HttpStatus.UNAUTHORIZED)
                is InvalidRequestBodyException -> return ResponseEntity(Message("Invalid request body"), HttpStatus.BAD_REQUEST)
                is ForbiddenException -> return ResponseEntity(Message("You are not authorized to do this request"), HttpStatus.FORBIDDEN)
                is NotEnoughMoney -> return ResponseEntity(Message("You don't have enough money to play"), HttpStatus.UNPROCESSABLE_ENTITY)
                else -> Exceptions.handle(exception, Environment.service)
            }
            return ResponseEntity(Message("Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}