package nl.nickkoepr.tictactoe.utils

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.exceptions.ErrorHandler
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.requests.ErrorResponse
import nl.nickkoepr.tictactoe.database.AnalyticsData
import nl.nickkoepr.tictactoe.database.DatabaseManager
import nl.nickkoepr.tictactoe.game.GameManager
import nl.nickkoepr.tictactoe.game.gamerequest.GameRequestManager
import java.time.Duration

object BotUtil {
    lateinit var jda: JDA
    var turnOnDebugger = false
    var timeStartedMilliseconds: Long = 0
    const val standardPrefix = '.'

    //Check if a message is unknown. If it is, it will stop a game or decline a game request.
    fun getUnknownMessageHandler(message: Message? = null): ErrorHandler {
        return ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE) {
            if (message != null) {
                val game = GameManager.getGameFromMessage(message.id)
                if (game != null) {
                    GameManager.stopGame(game)
                }
                val request = GameRequestManager.getRequestFromMessage(message.id)
                if (request != null) {
                    GameRequestManager.declineRequest(message, true)
                }
            }
        }
    }

    //Check if the bot had all the permissions that are essential to make the bot work.
    fun hasAllPermissions(
        guild: Guild,
        textChannel: GuildChannel,
        message: Message? = null,
        interactionHook: InteractionHook? = null
    ): Boolean {
        val permissionsList = listOf(
            Permission.VIEW_CHANNEL,
            Permission.MESSAGE_SEND,
            Permission.MESSAGE_EMBED_LINKS,
            Permission.MESSAGE_HISTORY
        )
        val neededPermissions = mutableListOf<Permission>()

        for (permission in permissionsList) {
            if (!guild.selfMember.hasPermission(permission) ||
                !guild.selfMember.hasPermission(textChannel, permission)
            ) {
                neededPermissions.add(permission)
            }
        }

        if (neededPermissions.isNotEmpty()) {
            var perms = ""
            neededPermissions.forEach { perms += "$it\n" }
            val handler = getUnknownMessageHandler(message)

            if (guild.selfMember.hasPermission(Permission.MESSAGE_SEND) &&
                guild.selfMember.hasPermission(textChannel, Permission.MESSAGE_SEND)
            ) {
                if (guild.selfMember.hasPermission(Permission.MESSAGE_EMBED_LINKS) &&
                    guild.selfMember.hasPermission(textChannel, Permission.MESSAGE_EMBED_LINKS)
                ) {
                    val errorMessage = MessageUtil.errorMessage(
                        "Missing permission(s)!", "**I don't have the permission(s)** `$perms`** " +
                                "Please give these permissions, otherwise I can't work.**"
                    )
                    if (interactionHook == null) {
                        message?.channel?.sendMessageEmbeds(
                            errorMessage
                        )?.queue(null, handler)
                    } else {
                        interactionHook.sendMessageEmbeds(
                            errorMessage
                        ).queue(null, handler)
                    }
                } else {
                    val errorMessage = "**I don't have the permission(s)** `$perms`** \n" +
                            "Please give these permissions, otherwise I can't work.**"
                    if (interactionHook == null) {
                        message?.channel?.sendMessage(
                            errorMessage
                        )?.queue(null, handler)
                    } else {
                        interactionHook.sendMessage(
                            errorMessage
                        ).queue(null, handler)
                    }
                }
            } else {
                interactionHook?.sendMessage(
                    "**I don't have the permission(s)** `$perms`** \n" +
                            "Please give these permissions, otherwise I can't work.**"
                )?.queue(null, handler)
            }
            return false
        }
        return true
    }


    private fun getUptime(): String {
        val difference = System.currentTimeMillis() - timeStartedMilliseconds
        val duration = Duration.ofMillis(difference)
        return "${duration.toDays()} day(s), " +
                "${duration.toHours() % 24} hour(s), " +
                "${duration.toMinutes() % 60} minute(s) and " +
                "${duration.seconds % 60} second(s)."
    }

    fun getStats(): String {
        return "TicTacToe Discord bot stats: \n\n" +
                "Bot version: v${getVersion()}\n" +
                "Gateway ping: ${jda.gatewayPing}ms\n" +
                "Bot uptime: ${getUptime()}\n" +
                "Active games: ${GameManager.getActiveGamesSize()}\n" +
                "Active requests: ${GameRequestManager.getRequestsSize()}\n" +
                "Current server count: ${jda.guilds.size}\n" +
                "total commands sent: ${DatabaseManager.getAnalyticsData(AnalyticsData.TOTALCOMMANDS)}\n" +
                "   - total start commands sent: ${DatabaseManager.getAnalyticsData(AnalyticsData.TOTALSTARTCOMMANDS)}\n" +
                "   - total help commands sent: ${DatabaseManager.getAnalyticsData(AnalyticsData.TOTALHELPCOMMANDS)}\n" +
                "   - total stop commands sent: ${DatabaseManager.getAnalyticsData(AnalyticsData.TOTALSTOPCOMMANDS)}\n" +
                "Total games played: ${DatabaseManager.getAnalyticsData(AnalyticsData.TOTALGAMESPLAYED)}"
    }

    fun stopBot() {
        jda.shutdown()
        DatabaseManager.disconnect()
    }

    fun getVersion(): String {
        return "1.0.0"
    }
}