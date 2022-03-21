package nl.nickkoepr.tictactoe.database

import java.sql.Connection

enum class AnalyticsData(val type: String) {
    TOTALCOMMANDS("TotalCommands"),
    TOTALSTARTCOMMANDS("TotalStartCommand"),
    TOTALHELPCOMMANDS("TotalHelpCommand"),
    TOTALSTOPCOMMANDS("TotalStopCommand"),
    TOTALGAMESPLAYED("TotalGamesPlayed")
}

object DatabaseManager {

    private lateinit var database: Connection
    private lateinit var databaseInstance: Database

    fun connect() {
        databaseInstance = Database()
        databaseInstance.connect()
        database = databaseInstance.database
    }

    fun disconnect() {
        databaseInstance.disconnect()
    }

    fun checkTable() {
        val statement = database.createStatement()

        statement.executeUpdate(
            "CREATE TABLE IF NOT EXISTS GuildPrefix  (\n" +
                    "GuildId BIGINT NOT NULL, " +
                    "Prefix VARCHAR(1) NOT NULL" +
                    ");"
        )

        statement.executeUpdate(
            "CREATE TABLE IF NOT EXISTS Analytics  (\n" +
                    "AnalyticsType VARCHAR(255) NOT NULL, " +
                    "AnalyticsValue BIGINT NOT NULL" +
                    ");"
        )
    }

    /**
     * NOTE: Because of the slash commands,the bot only needs the prefix if the server has set one
     * (to send the message that the bot is using slash commands instead of commands in a text channel).
     * The prefix classes will be fully removed when the Message Content Intent does not work anymore.
     */

    fun getPrefix(guildId: Long): Char? {
        checkTable()
        val result = databaseInstance.query("SELECT Prefix FROM GuildPrefix WHERE GuildId=$guildId;")
        if (result != null) {
            while (result.next()) {
                val char = result.getString("Prefix")
                return char.toCharArray()[0]
            }
        }
        return null
    }

    fun hasPrefix(guildId: Long): Boolean {
        checkTable()
        val result = databaseInstance.query("SELECT Prefix FROM GuildPrefix WHERE GuildId=$guildId;")
        if (result != null) {
            while (result.next()) {
                return true
            }
        }
        return false
    }

    fun checkAnalytics() {
        AnalyticsData.values().forEach {
            if (!analyticsExists(it)) {
                createAnalytics(it)
            }
        }
    }

    private fun createAnalytics(type: AnalyticsData) {
        databaseInstance.update(
            "INSERT INTO Analytics (" +
                    "AnalyticsType, AnalyticsValue)" +
                    "VALUES (?, ?);",
            listOf(type.type, 0)
        )
    }

    fun updateAnalytics(analyticsData: AnalyticsData) {
        setAnalyticsData(getAnalyticsData(analyticsData)!! + 1, analyticsData)
    }

    private fun analyticsExists(type: AnalyticsData): Boolean {
        val result = databaseInstance.query("SELECT AnalyticsValue FROM Analytics WHERE AnalyticsType='${type.type}';")
        if (result != null) {
            while (result.next()) {
                return true
            }
        }
        return false
    }

    private fun setAnalyticsData(amount: Long, type: AnalyticsData) {
        databaseInstance.update(
            "UPDATE Analytics " +
                    "SET AnalyticsValue=? " +
                    "WHERE AnalyticsType=?",
            listOf(amount, type.type)
        )
    }

    fun getAnalyticsData(type: AnalyticsData): Long? {
        val result = databaseInstance.query("SELECT AnalyticsValue FROM Analytics WHERE AnalyticsType='${type.type}';")
        if (result != null) {
            while (result.next()) {
                return result.getLong("AnalyticsValue")
            }
        }
        return null
    }
}