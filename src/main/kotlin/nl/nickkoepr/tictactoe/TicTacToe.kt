package nl.nickkoepr.tictactoe

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import nl.nickkoepr.tictactoe.commands.HelpCommand
import nl.nickkoepr.tictactoe.commands.PrefixCommand
import nl.nickkoepr.tictactoe.commands.StopCommand
import nl.nickkoepr.tictactoe.commands.botcommand.CommandManager
import nl.nickkoepr.tictactoe.commands.console.ConsoleThread
import nl.nickkoepr.tictactoe.database.DatabaseManager
import nl.nickkoepr.tictactoe.game.timer.Timer
import nl.nickkoepr.tictactoe.listeners.ButtonClickListener
import nl.nickkoepr.tictactoe.listeners.GuildLeaveListener
import nl.nickkoepr.tictactoe.listeners.MessageListener
import nl.nickkoepr.tictactoe.utils.BotUtil
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files

private lateinit var token: String

fun main() {

    println(
        "TicTacToe Discord bot.\n" +
                "Version ${BotUtil.getVersion()}"
    )

    val tokenFile = File("token.txt")
    if (!tokenFile.exists()) {
        println(
            "TicTacToe Discord bot \n\n" +
                    "To use this Discord bot, you have to provide a Discord bot token. " +
                    "You can paste your Discord bot token here:"
        )
        val input = readln()
        if (tokenFile.createNewFile()) {
            println("Created the token file!")
        } else {
            println("Error while creating the token.txt file. Please try again!")
            return
        }
        Files.write(tokenFile.toPath(), input.toByteArray())
    }
    token = tokenFile.inputStream().readBytes().toString(Charset.defaultCharset())

    val jdaBuilder = JDABuilder.createDefault(token)
    jdaBuilder.setActivity(Activity.playing("TicTacToe!"))
    jdaBuilder.addEventListeners(MessageListener(), GuildLeaveListener(), ButtonClickListener())

    //Register the commands.
    CommandManager.commands["help"] = HelpCommand("help", "description")
    CommandManager.commands["stop"] = StopCommand("stop", "stop")
    CommandManager.commands["prefix"] = PrefixCommand("prefix", "prefix command")

    //Set the current system milliseconds when the bot starts.
    BotUtil.timeStartedMilliseconds = System.currentTimeMillis()

    //Create a console tread for listening to commands from the console.
    val consoleThread = ConsoleThread()
    consoleThread.isDaemon = true
    consoleThread.start()

    BotUtil.jda = jdaBuilder.build()

    DatabaseManager.connect()

    DatabaseManager.checkTable()
    DatabaseManager.checkAnalytics()

    //Create and start the timer.
    Timer()
}