package nl.nickkoepr.tictactoe.logger

import nl.nickkoepr.tictactoe.utils.BotUtil

object Logger {

    fun info(message: String) {
        println("[INFO]: $message")
    }

    fun error(message: String) {
        println("\u001B[31m[ERROR]: $message\u001B[0m")
    }

    fun debug(message: String) {
        if (BotUtil.turnOnDebugger) {
            println("[DEBUG]: $message")
        }
    }
}