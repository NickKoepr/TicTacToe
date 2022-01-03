package nl.nickkoepr.tictactoe.database

import nl.nickkoepr.tictactoe.logger.Logger
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

class Database {

    private val useMysql = false

    private val sqlHost = ""
    private val sqlDatabase = ""
    private val sqlUser = ""
    private val sqlPassword = ""

    lateinit var database: Connection

    fun connect() {
        try {
            if (useMysql) {
                val dbProperties = Properties()
                dbProperties.setProperty("user", sqlUser)
                dbProperties.setProperty("password", sqlPassword)
                dbProperties.setProperty("useSSL", "false")
                dbProperties.setProperty("autoReconnect", "true")

                Class.forName("com.mysql.jdbc.Driver")
                database = DriverManager.getConnection(
                    "jdbc:mysql://$sqlHost/$sqlDatabase",
                    dbProperties
                )
            } else {
                val dbFile = File("tictactoe.db")
                if (!dbFile.exists()) {
                    if (!dbFile.createNewFile()) {
                        Logger.error(
                            "Error while creating the tictactoe.db database!" +
                                    "Please try again."
                        )
                    }
                }
                Class.forName("org.sqlite.JDBC")
                database = DriverManager.getConnection("jdbc:sqlite:tictactoe.db")
            }
            Logger.debug("Succesfully connected to the database")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            database.close()
            Logger.debug("Successfully closed the database connection")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun update(update: String, objects: List<Any>) {
        val preparedStatement = database.prepareStatement(update)
        var i = 1
        for (dbObject in objects) {
            preparedStatement.setObject(i, dbObject)
            i++
        }
        preparedStatement.executeUpdate()
    }

    fun query(query: String): ResultSet? {
        return try {
            database.prepareStatement(query).executeQuery()
        } catch (e: SQLException) {
            null
        }
    }
}