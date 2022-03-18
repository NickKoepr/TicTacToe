package nl.nickkoepr.tictactoe.listeners

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import nl.nickkoepr.tictactoe.color.Colors
import nl.nickkoepr.tictactoe.database.DatabaseManager
import nl.nickkoepr.tictactoe.utils.BotUtil
import nl.nickkoepr.tictactoe.utils.ColorUtil

class MessageListener : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        val args = event.message.contentRaw.lowercase().split(" ")
        val guildId = event.guild.id.toLong()
        val prefix = if (DatabaseManager.hasPrefix(guildId)) {
            DatabaseManager.getPrefix(guildId)!!
        } else {
            BotUtil.standardPrefix
        }
        String
        //Check if the message starts with the prefix and 'tictactoe' or 'ttt'
        if (args[0] == "${prefix}tictactoe" || args[0] == "${prefix}ttt") {
            val handler = BotUtil.getUnknownMessageHandler()

            if (BotUtil.hasAllPermissions(event.guild, event.textChannel, message = event.message)) {

                val embedBuilder = EmbedBuilder()
                embedBuilder.setTitle("New: Slash commands!")
                embedBuilder.setDescription(
                    "**The TicTacToe Discord bot now works with slash commands!**\n\n" +
                            "**__To start a game, simply type `/start [user]`!__**\n" +
                            "**For a list of all the commands, type `/help`.**\n\n" +
                            "***__Do you not see any slash commands?" +
                            "Then the bot has to be invited again. This can be done via " +
                            "[this](https://discord.com/oauth2/authorize" +
                            "?client_id=914110118998732811" +
                            "&permissions=83968" +
                            "&scope=bot%20applications.commands) " +
                            " link.__***"
                )
                embedBuilder.setColor(ColorUtil.get(Colors.STANDARD))
                event.channel.sendMessageEmbeds(embedBuilder.build()).queue(null, handler)
            }
        }
    }
}
