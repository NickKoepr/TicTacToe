package nl.nickkoepr.tictactoe.commands

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import nl.nickkoepr.tictactoe.commands.botcommand.BotCommand
import nl.nickkoepr.tictactoe.database.AnalyticsData
import nl.nickkoepr.tictactoe.database.DatabaseManager
import nl.nickkoepr.tictactoe.logger.Logger
import nl.nickkoepr.tictactoe.utils.BotUtil
import nl.nickkoepr.tictactoe.utils.MessageUtil

class PrefixCommand(override val name: String, override val description: String) : BotCommand {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent, args: List<String>) {
        Logger.debug("Fired the prefix command")
        DatabaseManager.updateAnalytics(AnalyticsData.TOTALPREFIXCOMMAND)
        DatabaseManager.updateAnalytics(AnalyticsData.TOTALCOMMANDS)

        val handler = BotUtil.getUnknownMessageHandler(event.message)
        val guildId = event.guild.id.toLong()
        val channel = event.channel

        if (event.member?.hasPermission(Permission.MANAGE_SERVER) == true) {
            if (args.size == 2) {
                if (DatabaseManager.hasPrefix(guildId)) DatabaseManager.removePrefix(guildId)
                channel.sendMessage(
                    MessageUtil.successMessage(
                        "Changed prefix",
                        "The prefix is been reset to `${BotUtil.standardPrefix}`!"
                    )
                ).queue(null, handler)
            } else {
                val char = args[2].toCharArray()
                if (char.size == 1) {
                    if (char[0] != '.') {
                        DatabaseManager.setPrefix(guildId, char[0])
                    } else {
                        DatabaseManager.removePrefix(guildId)
                    }
                    channel.sendMessage(
                        MessageUtil.successMessage(
                            "Updated prefix",
                            "Updated the server prefix to `${char[0]}`!"
                        )
                    ).queue(null, handler)
                } else {
                    channel.sendMessage(
                        MessageUtil.errorMessage(
                            "Please add one character",
                            "The prefix can only be one character!"
                        )
                    ).queue(null, handler)
                }
            }
        } else {
            channel.sendMessage(
                MessageUtil.errorMessage(
                    "No permission",
                    "You don't have permission to do this."
                )
            ).queue(null, handler)
        }
    }

}