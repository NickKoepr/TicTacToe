package nl.nickkoepr.tictactoe.commands

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import nl.nickkoepr.tictactoe.commands.botcommand.BotCommand
import nl.nickkoepr.tictactoe.database.AnalyticsData
import nl.nickkoepr.tictactoe.database.DatabaseManager
import nl.nickkoepr.tictactoe.game.GameManager
import nl.nickkoepr.tictactoe.game.gamerequest.GameRequestManager
import nl.nickkoepr.tictactoe.game.objects.Player
import nl.nickkoepr.tictactoe.game.objects.Position
import nl.nickkoepr.tictactoe.logger.Logger
import nl.nickkoepr.tictactoe.utils.BotUtil
import nl.nickkoepr.tictactoe.utils.MessageUtil

class StopCommand(override val description: String, override val name: String) : BotCommand {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent, args: List<String>) {
        Logger.debug("Fired the stop command")

        DatabaseManager.updateAnalytics(AnalyticsData.TOTALSTOPCOMMANDS)
        DatabaseManager.updateAnalytics(AnalyticsData.TOTALCOMMANDS)

        val user = event.author
        val userId = user.id
        val handler = BotUtil.getUnknownMessageHandler(event.message)
        val channel = event.channel

        if (GameRequestManager.hasSendRequest(userId)) {
            GameRequestManager.cancelRequest(userId)
            channel.sendMessage(
                MessageUtil.successMessage(
                    "Your game request has been canceled",
                    "Your game request has been canceled successfully."
                )
            ).queue(null, handler)
        } else if (GameManager.hasGame(userId)) {
            val game =
                if (GameManager.checkPlayersTurn(userId)) GameManager.getGame(userId)!! else GameManager.getGameFromUser(
                    userId
                )!!
            if (!game.finished) {
                GameManager.gameStopRequest(
                    game,
                    userId
                )
                channel.sendMessage(
                    MessageUtil.successMessage(
                        "Your game is cancelled",
                        "Your cancelled your game successfully."
                    )
                ).queue(null, handler)
            } else {
                if (!GameManager.hasPlayerChosenRematch(userId)) {
                    BotUtil.jda.getTextChannelById(game.channelId)?.retrieveMessageById(game.message)?.queue({
                        GameManager.playerRematchChoice(
                            Player(user.name, Position.X, user.id),
                            game,
                            false,
                            it
                        )
                    }, handler)
                    channel.sendMessage(
                        MessageUtil.successMessage(
                            "Your game request has been declined",
                            "Your game request has been declined successfully."
                        )
                    ).queue(null, handler)
                } else {
                    channel.sendMessage(
                        MessageUtil.errorMessage(
                            "You already accepted the rematch!",
                            "You cannot cancel the rematch because you already accepted it."
                        )
                    ).queue(null, handler)
                }
            }
        } else {
            channel.sendMessage(
                MessageUtil.errorMessage(
                    "You are not playing a game or have sent a request!",
                    "You can only use the stop command when you are playing a game and if you have sent a request.",
                )
            ).queue(null, handler)
        }
    }
}