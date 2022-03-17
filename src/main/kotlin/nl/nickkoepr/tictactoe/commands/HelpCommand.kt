package nl.nickkoepr.tictactoe.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import nl.nickkoepr.tictactoe.color.Colors
import nl.nickkoepr.tictactoe.commands.botcommand.BotCommand
import nl.nickkoepr.tictactoe.commands.botcommand.CommandManager
import nl.nickkoepr.tictactoe.database.AnalyticsData
import nl.nickkoepr.tictactoe.database.DatabaseManager
import nl.nickkoepr.tictactoe.logger.Logger
import nl.nickkoepr.tictactoe.utils.BotUtil
import nl.nickkoepr.tictactoe.utils.ColorUtil

class HelpCommand(override val name: String, override val description: String) : BotCommand {

    override fun slashCommandEvent(event: SlashCommandInteractionEvent) {
        Logger.debug("Fired the Help command")
        DatabaseManager.updateAnalytics(AnalyticsData.TOTALHELPCOMMANDS)
        DatabaseManager.updateAnalytics(AnalyticsData.TOTALCOMMANDS)

        val handler = BotUtil.getUnknownMessageHandler()
        val embed = EmbedBuilder()
//        val guildId = event.guild?.id?.toLong()!!
//        val prefix = if (DatabaseManager.hasPrefix(guildId)) {
//            DatabaseManager.getPrefix(guildId)
//        } else {
//            BotUtil.standardPrefix
//        }

        embed.setColor(ColorUtil.get(Colors.STANDARD))
        embed.setTitle("TicTacToe help")
        embed.addField(
            "__**/start @username**__",
            "**Play a match of tic tac toe!**",
            false
        )
        CommandManager.commands.forEach {
            if (it.key != "start") {
                embed.addField(
                    "/${it.value.name}",
                    "*${it.value.description}*",
                    true
                )
            }
        }
        event.hook.sendMessageEmbeds(embed.build()).queue(null, handler)
    }
}
