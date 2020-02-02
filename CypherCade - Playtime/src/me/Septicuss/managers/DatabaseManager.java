package me.Septicuss.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import me.Septicuss.Files;
import me.Septicuss.Files.FileType;
import me.Septicuss.objects.PlaytimeData;

public class DatabaseManager {

	private static FileConfiguration config;
	private static boolean UUID_BASED;

	final static String table = "cyphercade_playtime";

	private static boolean ENABLED = false;

	private static Connection connection;
	private String host, database, username, password;
	private int port;

	// -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- //

	public void initialize() {

		config = new Files().getFile(FileType.CONFIG);
		UUID_BASED = config.getBoolean("settings.uuid_based");

		host = config.getString("mysql.host");
		port = config.getInt("mysql.port");
		database = config.getString("mysql.database");
		username = config.getString("mysql.username");
		password = config.getString("mysql.password");

		mysqlSetup();

		if (isEnabled()) {
			createTables();
		}
	}

	// -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- //

	public void createTables() {
		try {
			final String statementString = "CREATE TABLE IF NOT EXISTS " + table + " (USER text, PLAYTIME int)";
			PreparedStatement statement = getConnection().prepareStatement(statementString);
			statement.execute();
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
	}

	// -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- //

	public boolean playerExists(OfflinePlayer op) {
		try {
			final String statementString = "SELECT * FROM " + table + " WHERE USER " + "=?";
			PreparedStatement statement = getConnection().prepareStatement(statementString);
			statement.setString(1, (UUID_BASED ? op.getUniqueId().toString() : op.getName()));

			ResultSet results = statement.executeQuery();
			return results.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	public void createPlayer(OfflinePlayer op) {
		try {
			final String statementString = "SELECT * FROM `" + table + "`WHERE USER " + "=?";
			PreparedStatement statement = getConnection().prepareStatement(statementString);
			statement.setString(1, (UUID_BASED ? op.getName() : op.getUniqueId().toString()));

			ResultSet results = statement.executeQuery();
			results.next();

			if (!playerExists(op)) {
				final String insertString = "INSERT INTO `" + table + "`(USER,PLAYTIME) VALUE (?,?)";
				PreparedStatement insert = getConnection().prepareStatement(insertString);
				insert.setString(1, (UUID_BASED ? op.getUniqueId().toString() : op.getName()));
				insert.setLong(2, 0L);
				insert.executeUpdate();
			}
		} catch (SQLException e) {
			e.getMessage();
		}
	}

	public void updateVariable(OfflinePlayer op, long value) {

		try {
			final String statementString = "UPDATE `" + table + "` SET `PLAYTIME`=? WHERE USER =?";
			PreparedStatement statement = getConnection().prepareStatement(statementString);
			statement.setLong(1, value);
			statement.setString(2, (UUID_BASED ? op.getUniqueId().toString() : op.getName()));
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public long getVariable(OfflinePlayer op) {

		try {
			final String statementString = "SELECT * FROM `" + table + "`WHERE USER =?";
			PreparedStatement statement = getConnection().prepareStatement(statementString);
			statement.setString(1, (UUID_BASED ? op.getUniqueId().toString() : op.getName()));
			ResultSet results = statement.executeQuery();
			results.next();

			return results.getLong("PLAYTIME");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<PlaytimeData> getPlaytimeCache() {

		List<PlaytimeData> cache = new ArrayList<>();

		try {
			final String statementString = "SELECT * FROM `" + table + "`";
			PreparedStatement statement = getConnection().prepareStatement(statementString);
			ResultSet results = statement.executeQuery();

			while (results.next()) {
				final PlaytimeData data = new PlaytimeData(results.getString("USER"), results.getLong("PLAYTIME"));
				cache.add(data);
			}

		} catch (Exception e) {
			e.getMessage();
		}

		return cache;
	}

	// -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- //

	public void mysqlSetup() {

		try {

			synchronized (this) {
				if (this.getConnection() != null && !getConnection().isClosed()) {
					return;
				}

				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database,
						username, password));

				ENABLED = true;

				System.out.println("Connected");
			}

		} catch (SQLException e) {
			e.getMessage();
		} catch (ClassNotFoundException e) {
			e.getMessage();
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		DatabaseManager.connection = connection;
	}

	public boolean isEnabled() {
		return ENABLED;
	}

	// -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- //
}
