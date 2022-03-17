package nl.nickkoepr.tictactoe.listeners

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import nl.nickkoepr.tictactoe.commands.botcommand.CommandManager
import nl.nickkoepr.tictactoe.utils.BotUtil
import nl.nickkoepr.tictactoe.utils.MessageUtil

class SlashCommandListener : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val commandName = event.name
        event.deferReply().queue()
        if (!event.channelType.isThread) {
            if (BotUtil.hasAllPermissions(event.guild!!, event.textChannel, interactionHook = event.hook)) {
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
        } else {
            event.hook.sendMessageEmbeds(
                MessageUtil.errorMessage(
                    "The bot does not work in threads!",
                    "Please use a TextChannel instead of a thread."
                )
            ).queue()
        }
    }
}
