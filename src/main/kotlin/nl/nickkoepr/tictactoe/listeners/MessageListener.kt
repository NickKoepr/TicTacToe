package nl.nickkoepr.tictactoe.listeners

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import nl.nickkoepr.tictactoe.color.Colors
import nl.nickkoepr.tictactoe.database.DatabaseManager
import nl.nickkoepr.tictactoe.utils.BotUtil
import nl.nickkoepr.tictactoe.utils.ColorUtil

class MessageListener : ListenerAdapter() {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
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
            val handler = BotUtil.getUnknownMessageHandler(event.message)
            val authorId = event.author.id

            if (BotUtil.hasAllPermissions(event.message)) {

                val embedBuilder = EmbedBuilder()
                embedBuilder.setTitle("New: Slash commands!")
                embedBuilder.setDescription(
                    "The Discord bot now works with slash commands!\n" +
                            "To start a game, simply type `/start [user]`!\n" +
                            "For a list of all the commands, type `/help`.\n" +
                            "**If the commands are not showing up when you type the slash command, " +
                            "the bot has to be invited again. This can be done via this link:\n" +
                            "<https://top.gg/bot/914110118998732811>\n**"
                )
                embedBuilder.setColor(ColorUtil.get(Colors.STANDARD))
                event.channel.sendMessage(embedBuilder.build()).queue(null, handler)

//                if (args.size >= 2) {
//                    if (CommandManager.commands.containsKey(args[1])) {
//                        //Fire the onGuildMessageReceived function in the class of the command.
//                        CommandManager.commands[args[1]]?.onGuildMessageReceived(event, args)
//                    } else {
//                        if (event.message.mentionedUsers.isNotEmpty()) {
//                            val user = event.message.mentionedUsers[0]
//                            val userId = user.id
//
//                            if (user == null) {
//                                event.channel.sendMessage(
//                                    MessageUtil.errorMessage(
//                                        "User not exists", "The tagged user does not exists!"
//                                    )
//                                ).queue(null, handler)
//                                return
//                            }
//                            if (user.isBot) {
//                                event.channel.sendMessage(
//                                    MessageUtil.errorMessage(
//                                        "Invalid user",
//                                        "You cannot play TicTacToe with a bot!"
//                                    )
//                                ).queue(null, handler)
//                                return
//                            }
//                            if (userId == authorId) {
//                                event.channel.sendMessage(
//                                    MessageUtil.errorMessage(
//                                        "Invalid user",
//                                        "You cannot play TicTacToe with yourself!"
//                                    )
//                                ).queue(null, handler)
//                                return
//                            }
//                            if (GameManager.hasGame(userId)) {
//                                event.channel.sendMessage(
//                                    MessageUtil.errorMessage(
//                                        "This user is already in a game!",
//                                        "The user ${user.name} is already in a game."
//                                    )
//                                ).queue(null, handler)
//                                return
//                            }
//                            if (GameRequestManager.hasSendRequest(authorId)) {
//                                event.channel.sendMessage(
//                                    MessageUtil.errorMessage(
//                                        "You already send a request!",
//                                        "Type `${prefix}tictactoe stop` to stop your current request."
//                                    )
//                                ).queue(null, handler)
//                                return
//                            }
//                            if (GameManager.hasGame(authorId)) {
//                                event.channel.sendMessage(
//                                    MessageUtil.errorMessage(
//                                        "You are already in a game!",
//                                        "You are already in a game. Use `${prefix}tictactoe stop` to stop your current game."
//                                    )
//                                ).queue(null, handler)
//                                return
//                            }
////
//                            val embed = EmbedBuilder()
//                            embed.setTitle("TicTacToe request")
//                            embed.setColor(ColorUtil.get(Colors.STANDARD))
//                            embed.setDescription(
//                                "${user.name}, ${event.author.name} requested a match of TicTacToe!"
//                            )
//                            event.channel.sendMessage(embed.build())
//                                .setActionRows(
//                                    ActionRow.of(
//                                        Button.success("accept", "Accept"),
//                                        Button.danger("decline", "Decline")
//                                    )
//                                ).queue({
//                                    GameRequestManager.createRequest(
//                                        Player(event.author.name, Position.X, event.author.id),
//                                        Player(user.name, Position.O, user.id),
//                                        it.id,
//                                        event.channel.id
//                                    )
//                                }, handler)
//
//                        } else {
//                            val embed = EmbedBuilder()
//                            embed.setColor(ColorUtil.get(Colors.ERROR))
//                            embed.setTitle("Invalid command")
//                            embed.setDescription(
//                                "Please type `${prefix}tictactoe help` for a list with all the commands and opportunities!"
//                            )
//                            event.channel.sendMessage(embed.build()).queue(null, handler)
//                        }
//                    }
//                } else {
//                    val embed = EmbedBuilder()
//                    embed.setColor(ColorUtil.get(Colors.STANDARD))
//                    embed.setTitle("Need help?")
//                    embed.setDescription(
//                        "Please type `${prefix}tictactoe help` for a list with all the commands and opportunities!"
//                    )
//                    event.channel.sendMessage(embed.build()).queue(null, handler)
//                }
            }
        }
    }
}
