package webservice

import CasinoLib.exceptions.ForbiddenException
import CasinoLib.exceptions.WrongApikeyProvidedException
import CasinoLib.helpers.Exceptions
import CasinoLib.model.Message
import CasinoLib.services.CasinoLibrary
import exceptions.InvalidRequestBodyException
import exceptions.WrongContentTypeException
import helpers.Environment
import helpers.RequestProcess
import model.Reward
import org.springframework.boot.SpringApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

class WebServiceApplication {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            CasinoLibrary.init("src/main/resources/casinolib.properties")
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
            //TODO after new version of CasinoLib make sub/add balance opers
            return ResponseEntity(Reward("xxx", 0), HttpStatus.ACCEPTED)
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