package exceptions

import CasinoLib.services.Logger
import helpers.Environment

class WrongContentTypeException(message: String): RuntimeException(message)
{
    init {
        Logger.log(service = Environment.service, message = "Got WrongContentTypeException: $message")
    }
}
