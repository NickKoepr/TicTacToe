package nl.nickkoepr.tictactoe.listeners

import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import nl.nickkoepr.tictactoe.database.DatabaseManager
import nl.nickkoepr.tictactoe.logger.Logger

class GuildLeaveListener : ListenerAdapter() {

    override fun onGuildLeave(event: GuildLeaveEvent) {
        Logger.debug("Bot leaved a Discord server")
        DatabaseManager.removePrefix(event.guild.id.toLong())
    }
}
