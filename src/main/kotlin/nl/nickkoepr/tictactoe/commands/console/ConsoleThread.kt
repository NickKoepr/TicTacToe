package nl.nickkoepr.tictactoe.commands.console

import nl.nickkoepr.tictactoe.utils.BotUtil
import java.util.*

class ConsoleThread : Thread() {
    override fun run() {
        val input = Scanner(System.`in`)
        while (input.hasNextLine()) {

            when (input.nextLine().lowercase()) {
                "help" -> println(
                    "TicTacToe console commands\n\n" +
                            "stats - Get different stats from the bot.\n" +
                            "stop - Stop the bot peacefully. \n" +
                            "debug - Turn debug messages on or off."
                )
                "stats" -> println(BotUtil.getStats())
                "stop" -> {
                    println("Stopping bot...")
                    BotUtil.stopBot()
                }
                "debug" -> {
                    BotUtil.turnOnDebugger = !BotUtil.turnOnDebugger
                    println("The debugger is now ${if (BotUtil.turnOnDebugger) "on" else "off"}!")
                }
                else -> println("Unknown command. Please type 'help' to get a list with commands.")
            }
        }
    }
}