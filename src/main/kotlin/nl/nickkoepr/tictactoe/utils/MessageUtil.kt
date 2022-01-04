package nl.nickkoepr.tictactoe.utils

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import nl.nickkoepr.tictactoe.color.Colors

object MessageUtil {

    fun errorMessage(title: String, message: String): MessageEmbed {
        val embed = EmbedBuilder()
        embed.setColor(ColorUtil.get(Colors.ERROR))
        embed.setTitle(title)
        embed.setDescription(message)
        return embed.build()
    }

    fun successMessage(title: String, message: String): MessageEmbed {
        val embed = EmbedBuilder()
        embed.setColor(ColorUtil.get(Colors.SUCCESS))
        embed.setTitle(title)
        embed.setDescription(message)
        return embed.build()
    }
}