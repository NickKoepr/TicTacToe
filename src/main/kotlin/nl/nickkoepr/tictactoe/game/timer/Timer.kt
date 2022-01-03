package nl.nickkoepr.tictactoe.game.timer

import nl.nickkoepr.tictactoe.game.GameManager
import nl.nickkoepr.tictactoe.game.gamerequest.GameRequestManager
import java.util.*
import java.util.Timer

class Timer {
    private val minutes: Long = 5

    init {
        Timer().scheduleAtFixedRate(Task(), 1000 * 60 * minutes, 1000 * 60 * minutes)
    }
}

class Task : TimerTask() {
    private val minutes = 5
    override fun run() {
        val current = System.currentTimeMillis()
        for (game in GameManager.getGamesList()) {
            if ((current - game.lastActivity) > 1000 * 60 * minutes) {
                GameManager.stopInactiveGame(game)
            }
        }
        for (request in GameRequestManager.getAllRequests()) {
            if ((current - request.lastActivity) > 1000 * 60 * minutes) {
                GameRequestManager.stopInactiveRequest(request)
            }
        }
    }
}