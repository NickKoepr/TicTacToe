package nl.nickkoepr.tictactoe.listeners

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import nl.nickkoepr.tictactoe.game.GameManager
import nl.nickkoepr.tictactoe.game.gamerequest.GameRequestManager
import nl.nickkoepr.tictactoe.game.objects.Player
import nl.nickkoepr.tictactoe.game.objects.Position
import nl.nickkoepr.tictactoe.logger.Logger
import nl.nickkoepr.tictactoe.utils.GameUtil
import java.lang.NumberFormatException

class ButtonClickListener : ListenerAdapter() {

    override fun onButtonClick(event: ButtonClickEvent) {
        Logger.debug("Fired ButtonClickListener")
        val user = event.user
        val userId = user.id
        val message = event.message!!
        val messageId = message.id
        val buttonId = event.componentId
        if (user.isBot) return

        val requests = GameRequestManager.getRequestsFromGetter(user.id)
        //Get all the requests that a player received, and accept or decline the one
        //that is equal to the message from the event.
        //If the request is empty, the player didn't recieved any requests.

        if (requests.isNotEmpty()) {
            requests.forEach {
                if (it.messageId == messageId) {
                    if (GameUtil.buttonToRequestChoise(buttonId)) {
                        GameRequestManager.acceptRequest(message)
                    } else {
                        GameRequestManager.declineRequest(message, true)
                    }
                }
            }

            //If a player has a game, check if the request is a request button. If it is a request button,
            //get the game and compare it to the message id from this message. If the message id matches,
            //a player declined or accepted a rematch request.
        } else if (GameManager.hasGame(userId)) {
            if (GameUtil.isRequestButton(buttonId)) {
                val game = if (GameManager.checkPlayersTurn(userId)) {
                    GameManager.getGame(userId)!!
                } else {
                    GameManager.getGameFromUser(userId)!!
                }
                if (game.message == messageId) {
                    if (game.finished) {
                        GameManager.playerChoosedRematch(
                            Player(user.name, Position.X, user.id),
                            game,
                            GameUtil.buttonToRequestChoise(buttonId),
                            message
                        )
                    }
                }
                //If the button is not a request button, check if it is the players turn that reacted to this message.
                //If it is, get the game and compare it to the message id. If the message id equals to this message,
                //get the location that the player choosed, and check if the player has been taken. If it hasnt,
                //set the location to the location that the player picked.
            } else {
                if (GameManager.checkPlayersTurn(userId)) {
                    val game = GameManager.getGame(userId)!!
                    if (game.message == messageId) {
                        if (!game.finished) {
                            val location = try {
                                buttonId.toInt()
                            } catch (e: NumberFormatException) {
                                0
                            }
                            val game = GameManager.getGame(userId)!!
                            if (!GameManager.placeIsTaken(location, game)) {
                                GameManager.playerChoosed(game, location, message)
                            }
                        }
                    }
                }
            }
        }
        event.deferEdit().queue()
    }
}
