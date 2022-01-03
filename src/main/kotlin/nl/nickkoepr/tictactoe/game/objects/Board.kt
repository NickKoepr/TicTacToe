package nl.nickkoepr.tictactoe.game.objects

import nl.nickkoepr.tictactoe.game.input.InputType

class Board {

    var layout: MutableList<InputType> = mutableListOf()

    init {
        for (i in 0..8) {
            layout.add(i, InputType.NOTHING)
        }
    }
}
