package nl.nickkoepr.tictactoe.utils

import nl.nickkoepr.tictactoe.game.objects.Player
import nl.nickkoepr.tictactoe.game.objects.GameInstance
import nl.nickkoepr.tictactoe.game.objects.Position

object PlayerUtil {

    //Returns player based on the given position.
    fun playerToUser(game: GameInstance, position: Position): Player {
        return when (position) {
            Position.X -> game.p1
            Position.O -> game.p2
        }
    }
}