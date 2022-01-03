package nl.nickkoepr.tictactoe.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import nl.nickkoepr.tictactoe.color.Colors
import nl.nickkoepr.tictactoe.commands.botcommand.BotCommand
import nl.nickkoepr.tictactoe.commands.botcommand.CommandManager
import nl.nickkoepr.tictactoe.database.AnalyticsData
import nl.nickkoepr.tictactoe.database.DatabaseManager
import nl.nickkoepr.tictactoe.logger.Logger
import nl.nickkoepr.tictactoe.utils.BotUtil
import nl.nickkoepr.tictactoe.utils.ColorUtil

class HelpCommand(override val name: String, override val description: String) : BotCommand {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent, args: List<String>) {
        Logger.debug("Fired the Help command")
        DatabaseManager.updateAnalytics(AnalyticsData.TOTALHELPCOMMANDS)
        DatabaseManager.updateAnalytics(AnalyticsData.TOTALCOMMANDS)

        val handler = BotUtil.getUnknownMessageHandler(event.message)
        val embed = EmbedBuilder()
        val guildId = event.guild.id.toLong()

        val prefix = if (DatabaseManager.hasPrefix(guildId)) {
            DatabaseManager.getPrefix(guildId)
        } else {
            BotUtil.standardPrefix
        }
        embed.setColor(ColorUtil.get(Colors.STANDARD))
        embed.setTitle("TicTacToe help")
        embed.addField(
            "__**!tictactoe @username**__",
            "**Tag a Discord user to start a match of TicTacToe!**",
            false
        )
        CommandManager.commands.forEach {
            embed.addField(
                "${prefix}tictactoe ${it.value.name}",
                "*${it.value.description}*",
                true
            )
        }
        event.channel.sendMessage(embed.build()).queue(null, handler)
    }
}
