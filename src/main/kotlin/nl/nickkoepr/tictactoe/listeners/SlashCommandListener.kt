package nl.nickkoepr.tictactoe.listeners

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import nl.nickkoepr.tictactoe.commands.botcommand.CommandManager
import nl.nickkoepr.tictactoe.utils.MessageUtil

class SlashCommandListener : ListenerAdapter() {
    override fun onSlashCommand(event: SlashCommandEvent) {
        val commandName = event.name
        event.deferReply().queue()
        if (CommandManager.commands.containsKey(commandName)) {
            CommandManager.commands[commandName]?.slashCommandEvent(event)
        } else {
            event.hook.sendMessageEmbeds(
                MessageUtil.errorMessage(
                    "Command not found!",
                    "This command does not exists!"
                )
            ).queue()
        }
    }
}