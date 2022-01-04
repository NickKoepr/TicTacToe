package nl.nickkoepr.tictactoe.listeners

import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import nl.nickkoepr.tictactoe.database.DatabaseManager

class GuildLeaveListener : ListenerAdapter() {

    override fun onGuildLeave(event: GuildLeaveEvent) {
        DatabaseManager.removePrefix(event.guild.id.toLong())
    }
}
