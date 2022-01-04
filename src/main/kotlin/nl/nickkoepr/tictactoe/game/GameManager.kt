package nl.nickkoepr.tictactoe.game

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import nl.nickkoepr.tictactoe.color.Colors
import nl.nickkoepr.tictactoe.database.AnalyticsData
import nl.nickkoepr.tictactoe.database.DatabaseManager
import nl.nickkoepr.tictactoe.game.gamerequest.GameRequest
import nl.nickkoepr.tictactoe.game.input.InputType
import nl.nickkoepr.tictactoe.game.objects.Board
import nl.nickkoepr.tictactoe.game.objects.GameInstance
import nl.nickkoepr.tictactoe.game.objects.Player
import nl.nickkoepr.tictactoe.game.objects.Position
import nl.nickkoepr.tictactoe.logger.Logger
import nl.nickkoepr.tictactoe.utils.*

object GameManager {

    //The key is the id of the player who is allowed to make a next move in the current round. If it is the other
    //player's turn, the key will be the other players' id.
    private val games: HashMap<String, GameInstance> = hashMapOf()
    private val playersWithGames: MutableList<String> = mutableListOf()
    private val acceptedRematch: MutableList<String> = mutableListOf()

    fun startGame(p1: Player, p2: Player, message: Message) {
        if (BotUtil.hasAllPermissions(message)) {
            if (!playersWithGames.contains(p1.userId) && !playersWithGames.contains(p2.userId)) {
                val game = GameInstance(
                    p1,
                    p2,
                    Board(),
                    Position.X,
                    message.id,
                    false,
                    message.channel.id,
                    System.currentTimeMillis()
                )
                val handler = BotUtil.getUnknownMessageHandler(message)

                //Add the game to the list of games, with the player who gets to make the first move.
                games[p1.userId] = game
                //Add both players to a list of players with a game.
                playersWithGames.addAll(listOf(p1.userId, p2.userId))

                message.editMessage(getBoardEmbed(game)).setActionRows(
                    BoardUtil.renderBoard(game.board)
                ).queue(null, handler)

                DatabaseManager.updateAnalytics(AnalyticsData.TOTALGAMESPLAYED)
                Logger.debug("Started a new game")
            }
        }
    }

    fun stopGame(game: GameInstance) {
        //Remove the players from the players with games list.
        playersWithGames.remove(game.p1.userId)
        playersWithGames.remove(game.p2.userId)

        //Remove the game from the list.
        games.remove(getTurnToPlayer(game).userId)
        Logger.debug("Stopped a game")
    }

    private fun finishGame(game: GameInstance, message: Message) {
        if (BotUtil.hasAllPermissions(message)) {
            game.finished = true
            changeEmbedByRematch(game = game, firstEmbed = true, message = message)
            Logger.debug("Game is finished")
        }
    }

    private fun startNewGame(game: GameInstance, message: Message) {
        stopGame(game)
        startGame(game.p2, game.p1, message)
        Logger.debug("Started a new game")
    }

    fun setLocation(game: GameInstance, location: Int, message: Message) {
        if (BotUtil.hasAllPermissions(message)) {
            val player = PlayerUtil.playerToUser(game, game.turn)

            val inputType = when (game.turn) {
                Position.O -> InputType.O
                Position.X -> InputType.X
            }

            game.board = BoardUtil.updateBoard(location, inputType, game.board)

            //Check if the game is over.
            if (!checkIfPlayerWon(game) && !checkIfDraw(game)) {
                game.turn = when (game.turn) {
                    Position.X -> Position.O
                    Position.O -> Position.X
                }
                val newUser = PlayerUtil.playerToUser(game, game.turn)

                games.remove(player.userId)
                games[newUser.userId] = game

                updateLastActivity(game)

                message.editMessage(getBoardEmbed(game)).setActionRows(BoardUtil.renderBoard(game.board))
                    .queue(null, BotUtil.getUnknownMessageHandler(message))

                Logger.debug("Player made a decision")
            } else {
                finishGame(game, message)
            }
        }
    }

    fun playerRematchChoice(user: Player, game: GameInstance, input: Boolean, msg: Message) {
        if (BotUtil.hasAllPermissions(msg)) {
            if (input) {
                //When a user accepted a rematch, change the embed and check
                // if the other player has also accepted the rematch.
                acceptedRematch.add(user.userId)
                changeEmbedByRematch(game, user, true, message = msg)

                if (acceptedRematch.contains(game.p1.userId) && acceptedRematch.contains(game.p2.userId)) {
                    acceptedRematch.remove(game.p1.userId)
                    acceptedRematch.remove(game.p2.userId)
                    startNewGame(game, msg)
                }
            } else {
                changeEmbedByRematch(game, user, false, message = msg)
                stopGame(game)
                acceptedRematch.remove(game.p1.userId)
                acceptedRematch.remove(game.p2.userId)
            }
            updateLastActivity(game)
        }
    }


    fun gameStopRequest(game: GameInstance, userId: String) {
        BotUtil.jda.getTextChannelById(game.channelId)?.retrieveMessageById(game.message)?.queue({ message ->
            if (BotUtil.hasAllPermissions(message)) {
                val handler = BotUtil.getUnknownMessageHandler(message)
                val embed = getBoardEmbed(game)
                val changedEmbed = EmbedBuilder()

                changedEmbed.setTitle(embed.title)
                changedEmbed.setColor(ColorUtil.get(Colors.WINNING))

                var description = embed.description
                description += "\n\n**__${
                    if (game.p1.userId == userId) game.p2.name else game.p1.name
                } has won!__**" +
                        "\n\n*${if (game.p1.userId == userId) game.p1.name else game.p2.name} has stopped the game.*"

                changedEmbed.setDescription(description)
                message.editMessage(changedEmbed.build()).setActionRows().queue(null, handler)
                stopGame(game)
                Logger.debug("A game is stopped")
            }
        }, BotUtil.getUnknownMessageHandler())
    }

    fun stopInactiveGame(game: GameInstance) {
        BotUtil.jda.getTextChannelById(game.channelId)?.retrieveMessageById(game.message)?.queue({
            if (BotUtil.hasAllPermissions(it)) {
                val handler = BotUtil.getUnknownMessageHandler(it)
                val embed = MessageUtil.errorMessage(
                    "Stopped game due to inactivity",
                    "This game is stopped due to inactivity for a long time."
                )
                it.editMessage(embed).setActionRows().queue(null, handler)
            }
        }, BotUtil.getUnknownMessageHandler())
        stopGame(game)
        if (acceptedRematch.contains(game.p1.userId)) acceptedRematch.remove(game.p1.userId)
        if (acceptedRematch.contains(game.p2.userId)) acceptedRematch.remove(game.p2.userId)

        Logger.debug("An inactive game is stopped")
    }


    private fun changeEmbedByRematch(
        game: GameInstance,
        user: Player? = null,
        acceptedOrDeclined: Boolean = true,
        firstEmbed: Boolean = false,
        message: Message
    ) {

        val embed = getBoardEmbed(game)
        val changedEmbed = EmbedBuilder()
        val handler = BotUtil.getUnknownMessageHandler(message)

        changedEmbed.setColor(embed.color)
        changedEmbed.setTitle(embed.title)
        var description = embed.description
        val emote = if (acceptedOrDeclined) ":white_check_mark:" else ":x:"

        description += "\n\n***Rematch?***\n"

        //Check if it is the first embed. If it is, no player has voted for a rematch,
        // so it will show nothing behind the names of the players.
        if (!firstEmbed) {
            if (game.p1.userId == user?.userId) {
                //If the player who made the choice is player 1, set his choice in the embed.
                description += "*${game.p1.name}: $emote*\n"
                //It will also add the choice of player 2 if that player already made a choice.
                description += if (!acceptedRematch.contains(game.p2.userId)) {
                    "*${game.p2.name}:*\n"
                } else {
                    "*${game.p2.name}: :white_check_mark:*\n"
                }
            }

            //This is the same as for player 1, instead for player 2.
            if (game.p2.userId == user?.userId) {
                description += if (!acceptedRematch.contains(game.p1.userId)) {
                    "*${game.p1.name}:*\n"
                } else {
                    "*${game.p1.name}: :white_check_mark:*\n"
                }
                description += "*${game.p2.name}: $emote*\n"
            }
        } else {
            description += "*${game.p1.name}:*\n"
            description += "*${game.p2.name}:*\n"
        }

        description += "\n*:white_check_mark: - Accept*\n"
        description += "*:x: - Decline*\n"

        val actionRows: MutableList<ActionRow> = mutableListOf()

        //If a user declined a rematch, and the ActionRow will clear everything.
        if (!acceptedOrDeclined) {
            description += "\n*${user?.name} declined a rematch. Thanks for playing!*"

        } else {
            //Gets the latest board, that will also be added to the ActionRows.
            actionRows.addAll(BoardUtil.renderBoard(game.board).toMutableList())
            //Adds a 'Accept' and 'Decline' Button under the current board.
            actionRows.add(
                ActionRow.of(
                    Button.success("accept", "Accept"),
                    Button.danger("decline", "Decline")
                )
            )
        }
        changedEmbed.setDescription(description)
        message.editMessage(changedEmbed.build()).setActionRows(actionRows).queue(null, handler)
    }

    fun sendDeclineGameRequestMessage(gameRequest: GameRequest, message: Message, showPlayerName: Boolean) {
        if (BotUtil.hasAllPermissions(message)) {
            val handler = BotUtil.getUnknownMessageHandler(message)
            val embed = MessageUtil.errorMessage(
                "TicTacToe request",
                "*${
                    if (showPlayerName) gameRequest.getter.name + " declined the request." else 
                        "This request is declined due to another game that started."
                }*"
            )
            message.editMessage(embed).setActionRows().queue(null, handler)
        }
    }

    fun sendCancelGameRequestMessage(gameRequest: GameRequest) {
        BotUtil.jda.getTextChannelById(gameRequest.channelId)?.retrieveMessageById(gameRequest.messageId)
            ?.queue({ message ->
                if (BotUtil.hasAllPermissions(message)) {
                    val handler = BotUtil.getUnknownMessageHandler(message)
                    val embed = MessageUtil.errorMessage(
                        "TicTacToe request",
                        "*${
                            gameRequest.sender.name
                        } cancelled the game request.*"
                    )

                    message.editMessage(embed).setActionRows().queue(null, handler)
                }
            }, BotUtil.getUnknownMessageHandler())
    }

    private fun getBoardEmbed(game: GameInstance): MessageEmbed {
        val embed = EmbedBuilder()
        embed.setColor(ColorUtil.get(Colors.STANDARD))
        embed.setTitle("TicTacToe")
        var description = ""

        description += ":regional_indicator_x: = ${game.p1.name}\n"
        description += ":regional_indicator_o: = ${game.p2.name}\n\n"

        if (checkIfPlayerWon(game)) {
            description += "**__${
                getPlayerThatWon(game)?.name
            } has won!__**"
            embed.setColor(ColorUtil.get(Colors.WINNING))
        } else if (hasFullBoard(game)) {
            description += "**It's a draw!**"
        } else {
            description += "*${
                getTurnToPlayer(game).name
            }'s turn.*"
        }

        embed.setDescription(description)

        return embed.build()
    }

    fun getGamesList(): List<GameInstance> {
        return games.values.toList()
    }

    private fun updateLastActivity(game: GameInstance) {
        game.lastActivity = System.currentTimeMillis()
    }

    fun hasPlayerChosenRematch(p: String): Boolean {
        return acceptedRematch.contains(p)
    }

    fun hasGame(p: String): Boolean {
        return playersWithGames.contains(p)
    }

    fun checkPlayersTurn(p: String): Boolean {
        return games.containsKey(p)
    }

    fun getGame(p: String): GameInstance? {
        return games[p]
    }

    fun placeIsTaken(place: Int, game: GameInstance): Boolean {
        return (game.board.layout[place] != InputType.NOTHING)
    }

    private fun getPlayerThatWon(game: GameInstance): Player? {
        val playerWon = BoardUtil.checkIfWon(game.board)
        if (playerWon != null) {
            return PlayerUtil.playerToUser(game, playerWon)
        }
        return null
    }

    private fun checkIfPlayerWon(game: GameInstance): Boolean {
        return (BoardUtil.checkIfWon(game.board) != null)
    }

    private fun checkIfDraw(game: GameInstance): Boolean {
        return BoardUtil.checkFullBoard(game.board)
    }

    private fun hasFullBoard(game: GameInstance): Boolean {
        return BoardUtil.checkFullBoard(game.board)
    }

    //Get the game from the player wo is allowed to make a next move.
    private fun getTurnToPlayer(game: GameInstance): Player {
        return PlayerUtil.playerToUser(game, game.turn)
    }

    //Get the game from the other player.
    fun getGameFromUser(userId: String): GameInstance? {
        for (game in games) {
            if (game.value.p1.userId == userId || game.value.p2.userId == userId) {
                return game.value
            }
        }
        return null
    }

    fun getGameFromMessage(messageId: String): GameInstance? {
        for (game in games) {
            if (game.value.message == messageId) {
                return game.value
            }
        }
        return null
    }

    fun getActiveGamesSize(): Int {
        return games.size
    }
}

