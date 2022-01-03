package nl.nickkoepr.tictactoe.utils

import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import nl.nickkoepr.tictactoe.game.input.InputType
import nl.nickkoepr.tictactoe.game.input.WinningPattern
import nl.nickkoepr.tictactoe.game.objects.Board
import nl.nickkoepr.tictactoe.game.objects.Position

object BoardUtil {

    fun renderBoard(board: Board): List<ActionRow> {
        var actionsRows: MutableList<ActionRow> = mutableListOf()
        var buttons: MutableList<Button> = mutableListOf()
        var i = 1
        for (section in board.layout) {
            if (section != InputType.NOTHING) {
                buttons.add(Button.secondary("${i - 1}", "\u200E").withEmoji(Emoji.fromMarkdown(section.code)))
            } else {
                buttons.add(Button.secondary("${i - 1}", "\u200E").withEmoji(Emoji.fromMarkdown(section.code)))
            }
            if (i % 3 == 0) {
                actionsRows.add(ActionRow.of(buttons))
                buttons.clear()
            }
            i++
        }
        return actionsRows
    }

    fun updateBoard(location: Int, inputType: InputType, board: Board): Board {
        board.layout[location] = inputType
        return board
    }

    fun checkFullBoard(board: Board): Boolean {
        if (board.layout.contains(InputType.NOTHING)) return false
        return true
    }

    fun checkIfWon(board: Board): Position? {

        val boardLayout = board.layout

        val possibleWinningPattern = getWinningPattern()

        for (winningPattern in possibleWinningPattern) {
            if (boardLayout[winningPattern.val1] == InputType.O
                && boardLayout[winningPattern.val2] == InputType.O
                && boardLayout[winningPattern.val3] == InputType.O
            ) {
                return Position.O
            }
            if (boardLayout[winningPattern.val1] == InputType.X
                && boardLayout[winningPattern.val2] == InputType.X
                && boardLayout[winningPattern.val3] == InputType.X
            ) {
                return Position.X
            }
        }
        return null
    }

    private fun getWinningPattern(): List<WinningPattern> {
        return listOf(
            WinningPattern(0, 1, 2),
            WinningPattern(3, 4, 5),
            WinningPattern(6, 7, 8),
            WinningPattern(0, 3, 6),
            WinningPattern(1, 4, 7),
            WinningPattern(2, 5, 8),
            WinningPattern(0, 4, 8),
            WinningPattern(2, 4, 6)
        )
    }
}