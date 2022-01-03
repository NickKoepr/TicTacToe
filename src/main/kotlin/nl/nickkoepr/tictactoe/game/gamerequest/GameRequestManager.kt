package nl.nickkoepr.tictactoe.game.gamerequest

import net.dv8tion.jda.api.entities.Message
import nl.nickkoepr.tictactoe.game.GameManager
import nl.nickkoepr.tictactoe.game.objects.Player
import nl.nickkoepr.tictactoe.logger.Logger
import nl.nickkoepr.tictactoe.utils.BotUtil
import nl.nickkoepr.tictactoe.utils.MessageUtil


object GameRequestManager {
    private val requestList: MutableList<GameRequest> = mutableListOf()

    fun createRequest(u1: Player, u2: Player, message: String, channelId: String) {
        requestList.add(GameRequest(u1, u2, message, channelId, System.currentTimeMillis()))
    }

    fun acceptRequest(message: Message) {
        val gameRequest = getRequestFromMessage(message.id)!!
        GameManager.startGame(gameRequest.getter, gameRequest.sender, message)
        requestList.remove(gameRequest)
        declineOtherRequests(gameRequest.getter.userId)
        Logger.debug("Request got accepted")
    }

    fun declineRequest(message: Message, showPlayerName: Boolean) {
        val gameRequest = getRequestFromMessage(message.id)!!
        GameManager.sendDeclineGameRequestMessage(gameRequest, message, showPlayerName)
        requestList.remove(gameRequest)
        Logger.debug("Request got declined")
    }

    private fun declineOtherRequests(user: String) {
        val gameRequests: MutableList<GameRequest> = mutableListOf()
        gameRequests.addAll(getRequestsFromGetter(user))
        gameRequests.addAll(getRequestsFromSender(user))
        if (gameRequests.isNotEmpty()) {
            gameRequests.forEach {
                BotUtil.jda.getTextChannelById(it.channelId)?.retrieveMessageById(it.messageId)?.queue({ message ->
                    declineRequest(message, false)
                }, BotUtil.getUnknownMessageHandler())
            }
            Logger.debug("Other requests got declined")
        }
    }

    fun stopInactiveRequest(gameRequest: GameRequest) {
        BotUtil.jda.getTextChannelById(gameRequest.channelId)?.retrieveMessageById(gameRequest.messageId)?.queue({
            if (BotUtil.hasAllPermissions(it)) {
                val handler = BotUtil.getUnknownMessageHandler(it)
                val embed = MessageUtil.errorMessage(
                    "Request cancelled due to inactivity",
                    "This request is cancelled due to inactivity for a long time."
                )
                it.editMessage(embed).setActionRows().queue(null, handler)
            }
        }, BotUtil.getUnknownMessageHandler())
        requestList.remove(gameRequest)
        Logger.debug("Inactive request is cancelled")
    }

    fun getAllRequests(): List<GameRequest> {
        return requestList.toList()
    }

    fun cancelRequest(id: String) {
        val gameRequest = getRequestFromSender(id)!!
        GameManager.sendCancelGameRequestMessage(
            gameRequest
        )
        requestList.remove(gameRequest)
    }

    fun hasSendRequest(id: String): Boolean {
        requestList.forEach { if (it.sender.userId == id) return true }
        return false
    }

    fun getRequestFromMessage(message: String): GameRequest? {
        requestList.forEach { if (it.messageId == message) return it }
        return null
    }

    private fun getRequestFromSender(user: String): GameRequest? {
        requestList.forEach { if (it.sender.userId == user) return it }
        return null
    }

    fun getRequestsFromSender(user: String): List<GameRequest> {
        val gameRequests: MutableList<GameRequest> = mutableListOf()
        requestList.forEach { if (it.sender.userId == user) gameRequests.add(it) }
        return gameRequests
    }

    fun getRequestsFromGetter(user: String): List<GameRequest> {
        val gameRequests: MutableList<GameRequest> = mutableListOf()
        requestList.forEach { if (it.getter.userId == user) gameRequests.add(it) }
        return gameRequests
    }

    fun getRequestsSize(): Int {
        return requestList.size
    }
}