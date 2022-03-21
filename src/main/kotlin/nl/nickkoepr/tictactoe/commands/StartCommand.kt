package nl.nickkoepr.tictactoe.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import nl.nickkoepr.tictactoe.color.Colors
import nl.nickkoepr.tictactoe.commands.botcommand.BotCommand
import nl.nickkoepr.tictactoe.database.AnalyticsData
import nl.nickkoepr.tictactoe.database.DatabaseManager
import nl.nickkoepr.tictactoe.game.GameManager
import nl.nickkoepr.tictactoe.game.gamerequest.GameRequestManager
import nl.nickkoepr.tictactoe.game.objects.Player
import nl.nickkoepr.tictactoe.game.objects.Position
import nl.nickkoepr.tictactoe.utils.BotUtil
import nl.nickkoepr.tictactoe.utils.ColorUtil
import nl.nickkoepr.tictactoe.utils.MessageUtil

class StartCommand(override val name: String, override val description: String) : BotCommand {
    override fun slashCommandEvent(event: SlashCommandInteractionEvent) {

        DatabaseManager.updateAnalytics(AnalyticsData.TOTALSTARTCOMMANDS)
        DatabaseManager.updateAnalytics(AnalyticsData.TOTALCOMMANDS)

        val taggedUser = event.getOption("opponent")?.asUser!!
        val user = event.user
        val handler = BotUtil.getUnknownMessageHandler()

        if (user.id == taggedUser.id) {
            event.hook.sendMessageEmbeds(
                MessageUtil.errorMessage(
                    "Invalid user",
                    "You cannot play tic tac toe with yourself!"
                )
            ).queue(null, handler)
            return
        }
        if (taggedUser.isBot) {
            event.hook.sendMessageEmbeds(
                MessageUtil.errorMessage(
                    "Invalid user",
                    "You cannot play TicTacToe with a bot!"
                )
            ).queue(null, handler)
            return
        }
        if (GameManager.hasGame(taggedUser.id)) {
            event.hook.sendMessageEmbeds(
                MessageUtil.errorMessage(
                    "This user is already in a game!",
                    "The user ${taggedUser.name} is already in a game."
                )
            ).queue(null, handler)
            return
        }
        if (GameRequestManager.hasSendRequest(user.id)) {
            event.hook.sendMessageEmbeds(
                MessageUtil.errorMessage(
                    "You already send a request!",
                    "Type `/stop` to stop your current request."
                )
            ).queue(null, handler)
            return
        }

        if (GameManager.hasGame(user.id)) {
            event.hook.sendMessageEmbeds(
                MessageUtil.errorMessage(
                    "You are already in a game!",
                    "You are already in a game. Use `/stop` to stop your current game."
                )
            ).queue(null, handler)
            return
        }

        val embed = EmbedBuilder()
        embed.setTitle("TicTacToe request")
        embed.setColor(ColorUtil.get(Colors.STANDARD))
        embed.setDescription(
            "${taggedUser.name}, ${user.name} requested a match of TicTacToe!"
        )
        event.hook.sendMessageEmbeds(embed.build()).addActionRow(
            Button.success("accept", "Accept"),
            Button.danger("decline", "Decline")
        ).queue({
            GameRequestManager.createRequest(
                Player(user.name, Position.X, user.id),
                Player(taggedUser.name, Position.O, taggedUser.id),
                it.id,
                event.channel.id
            )
        }, handler)

    }
}
