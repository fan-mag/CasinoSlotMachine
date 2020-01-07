package webservice

import CasinoLib.exceptions.ForbiddenException
import CasinoLib.exceptions.WrongApikeyProvidedException
import CasinoLib.helpers.Exceptions
import CasinoLib.model.Message
import CasinoLib.model.Operator
import CasinoLib.services.Account
import CasinoLib.services.Auth
import CasinoLib.services.CasinoLibrary
import exceptions.InvalidRequestBodyException
import exceptions.WrongContentTypeException
import helpers.Environment
import helpers.RequestProcess
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
            return ResponseEntity(Reward(slot = "000", win = -bet.amount), HttpStatus.OK)
        } catch (exception: Exception) {
            when (exception) {
                is WrongContentTypeException -> return ResponseEntity(Message("Wrong Content-Type Header"), HttpStatus.BAD_REQUEST)
                is WrongApikeyProvidedException -> return ResponseEntity(Message("Wrong Apikey provided"), HttpStatus.UNAUTHORIZED)
                is InvalidRequestBodyException -> return ResponseEntity(Message("Invalid request body"), HttpStatus.BAD_REQUEST)
                is ForbiddenException -> return ResponseEntity(Message("You are not authorized to do this request"), HttpStatus.FORBIDDEN)
                else -> Exceptions.handle(exception, Environment.service)
            }
            return ResponseEntity(Message("Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}