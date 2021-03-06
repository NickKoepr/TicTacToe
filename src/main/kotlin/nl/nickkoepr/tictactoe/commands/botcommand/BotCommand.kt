package nl.nickkoepr.tictactoe.commands.botcommand

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface BotCommand {
    val name: String
    val description: String
    fun slashCommandEvent(event: SlashCommandInteractionEvent)
}