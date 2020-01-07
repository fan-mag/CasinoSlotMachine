package exceptions

import CasinoLib.services.Logger
import helpers.Environment

class InvalidRequestBodyException(message: String) : RuntimeException(message)
{
    init {
        Logger.log(service = Environment.service, message = "Got InvalidRequestBodyException: $message")
    }
}
