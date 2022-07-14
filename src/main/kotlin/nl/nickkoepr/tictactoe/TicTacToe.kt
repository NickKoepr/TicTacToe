package nl.nickkoepr.tictactoe

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.utils.cache.CacheFlag
import nl.nickkoepr.tictactoe.commands.HelpCommand
import nl.nickkoepr.tictactoe.commands.StartCommand
import nl.nickkoepr.tictactoe.commands.StopCommand
import nl.nickkoepr.tictactoe.commands.botcommand.CommandManager
import nl.nickkoepr.tictactoe.commands.console.ConsoleThread
import nl.nickkoepr.tictactoe.database.DatabaseManager
import nl.nickkoepr.tictactoe.game.timer.Timer
import nl.nickkoepr.tictactoe.listeners.ButtonClickListener
import nl.nickkoepr.tictactoe.listeners.SlashCommandListener
import nl.nickkoepr.tictactoe.logger.Logger
import nl.nickkoepr.tictactoe.utils.BotUtil
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files

private lateinit var token: String

fun main() {

    Logger.info(
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
    val builder = JDABuilder.createDefault(token)
    builder.setActivity(Activity.playing("TicTacToe!"))
    builder.disableCache(
        CacheFlag.ACTIVITY,
        CacheFlag.VOICE_STATE,
        CacheFlag.EMOTE,
        CacheFlag.CLIENT_STATUS,
        CacheFlag.ONLINE_STATUS,
        CacheFlag.ROLE_TAGS
    )
    builder.addEventListeners(ButtonClickListener(), SlashCommandListener())

    //Register the commands.
    CommandManager.commands["start"] = StartCommand("start", "Start a game of tic tac toe!")
    CommandManager.commands["help"] = HelpCommand("help", "Gives a list with commands that you can use.")
    CommandManager.commands["stop"] = StopCommand("stop", "Cancel a request or stop a running game.")

    //Set the current system milliseconds when the bot starts.
    BotUtil.timeStartedMilliseconds = System.currentTimeMillis()

    //Create a console tread for listening to commands from the console.
    val consoleThread = ConsoleThread()
    GlobalScope.async {
        consoleThread.run()
    }

    BotUtil.jda = builder.build()
    //Register slash commands.
    BotUtil.jda.upsertCommand("start", "Start a game of tic tac toe!")
        .addOption(
            OptionType.USER, "opponent",
            "Choose a Discord member you want to play against.", true
        )
        .queue()
    BotUtil.jda.upsertCommand("help", "Gives a list with commands that you can use.").queue()
    BotUtil.jda.upsertCommand("stop", "Cancel a request or stop a running game.").queue()

    DatabaseManager.connect()

    DatabaseManager.checkTable()
    DatabaseManager.checkAnalytics()

    //Create and start the timer.
    Timer()
}