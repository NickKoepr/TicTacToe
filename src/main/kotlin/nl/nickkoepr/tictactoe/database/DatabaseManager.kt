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
            "CREATE TABLE IF NOT EXISTS Analytics  (\n" +
                    "AnalyticsType VARCHAR(255) NOT NULL, " +
                    "AnalyticsValue BIGINT NOT NULL" +
                    ");"
        )
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