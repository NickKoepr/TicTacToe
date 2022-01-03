package nl.nickkoepr.tictactoe.game.objects

data class GameInstance(
    val p1: Player,
    val p2: Player,
    var board: Board,
    var turn: Position,
    var message: String,
    var finished: Boolean,
    val channelId: String,
    var lastActivity: Long
)