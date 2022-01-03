package nl.nickkoepr.tictactoe.commands.botcommand

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

interface BotCommand {
    val name: String
    val description: String
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent, args: List<String>)
}