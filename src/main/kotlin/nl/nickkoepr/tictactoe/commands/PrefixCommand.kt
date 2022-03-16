//package nl.nickkoepr.tictactoe.commands
//
//import net.dv8tion.jda.api.Permission
//import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
//import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
//import nl.nickkoepr.tictactoe.commands.botcommand.BotCommand
//import nl.nickkoepr.tictactoe.database.AnalyticsData
//import nl.nickkoepr.tictactoe.database.DatabaseManager
//import nl.nickkoepr.tictactoe.logger.Logger
//import nl.nickkoepr.tictactoe.utils.BotUtil
//import nl.nickkoepr.tictactoe.utils.MessageUtil
//
//class PrefixCommand(override val name: String, override val description: String) : BotCommand {
//
//    override fun slashCommandEvent(event: SlashCommandEvent, args: List<String>) {
//        Logger.debug("Fired the prefix command")
//        DatabaseManager.updateAnalytics(AnalyticsData.TOTALPREFIXCOMMAND)
//        DatabaseManager.updateAnalytics(AnalyticsData.TOTALCOMMANDS)
//
//        //val handler = BotUtil.getUnknownMessageHandler(event.message)
//        val guildId = event.guild?.id?.toLong()!!
//        val channel = event.channel
//
//        if (event.member?.hasPermission(Permission.MANAGE_SERVER) == true) {
//            if (args.size == 2) {
//                if (DatabaseManager.hasPrefix(guildId)) DatabaseManager.removePrefix(guildId)
//                event.hook.sendMessageEmbeds(
//                    MessageUtil.successMessage(
//                        "Reset prefix",
//                        "The prefix has been reset to `${BotUtil.standardPrefix}`!"
//                    )
//                ).queue()
//            } else {
//                val char = args[2].toCharArray()
//                if (char.size == 1) {
//                    if (char[0] != BotUtil.standardPrefix) {
//                        DatabaseManager.setPrefix(guildId, char[0])
//                    } else {
//                        DatabaseManager.removePrefix(guildId)
//                    }
//                    event.hook.sendMessageEmbeds(
//                        MessageUtil.successMessage(
//                            "Updated prefix",
//                            "Updated the server prefix to `${char[0]}`!"
//                        )
//                    ).queue()
//                } else {
//                    channel.sendMessage(
//                        MessageUtil.errorMessage(
//                            "Please add one character",
//                            "The prefix can only be one character!"
//                        )
//                    ).queue(null, handler)
//                }
//            }
//        } else {
//            channel.sendMessage(
//                MessageUtil.errorMessage(
//                    "No permission",
//                    "You need the permission `MANAGE_SERVER` to do this."
//                )
//            ).queue(null, handler)
//        }
//    }
//}
