package nl.nickkoepr.tictactoe.game.gamerequest

import nl.nickkoepr.tictactoe.game.objects.Player

data class GameRequest(val sender: Player, val getter: Player, val messageId: String, val channelId: String, var lastActivity: Long)
