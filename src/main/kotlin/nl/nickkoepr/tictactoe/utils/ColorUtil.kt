package nl.nickkoepr.tictactoe.utils

import nl.nickkoepr.tictactoe.color.Colors
import java.awt.Color

object ColorUtil {

    fun get(color: Colors): Color {
        return color.color
    }
}