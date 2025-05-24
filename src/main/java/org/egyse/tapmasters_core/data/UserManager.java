package org.egyse.tapmasters_core.data;

import org.egyse.tapmasters_core.models.User;
import org.egyse.tapmasters_core.models.Upgrade;
import org.egyse.tapmasters_core.models.Booster;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.lang.reflect.Type;

public class UserManager implements DataManager {

    private final File dbFile;
    private Connection connection;
    private final Gson gson = new Gson();

    public UserManager(File dataFolder) {
        this.dbFile = new File(dataFolder, "database");
        dbFile.getParentFile().mkdirs();
        connect();
    }

    public void connect() {
        try {
            if (connection != null && !connection.isClosed()) return;

            // Explicitly load H2 driver
            Class.forName("org.h2.Driver");

            String url = "jdbc:h2:file:" + dbFile.getAbsolutePath() + ";MODE=MySQL;DB_CLOSE_DELAY=-1";
            connection = DriverManager.getConnection(url, "sa", "");
            System.out.println("Created database at: " + dbFile.getAbsolutePath());
            initializeDatabase();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    private void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users(" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "name VARCHAR(64), " +
                    "click DOUBLE, " +
                    "rawclick DOUBLE, " +
                    "money DOUBLE, " +
                    "gem DOUBLE, " +
                    "token DOUBLE, " +
                    "prestige DOUBLE, " +
                    "prestigepoint DOUBLE, " +
                    "upgrades TEXT, " +
                    "boosters TEXT)");

            // Create global_data table for single booster
            stmt.execute("CREATE TABLE IF NOT EXISTS global_data (" +
                    "id INT PRIMARY KEY, " +
                    "booster TEXT)");

            ResultSet rs = stmt.executeQuery(
                    "SELECT booster FROM global_data WHERE id = 1"
            );

            if (!rs.next()) {
                stmt.executeUpdate(
                        "INSERT INTO global_data (id, booster) VALUES (1, NULL)"
                );
            }

            print:
            System.out.println("Created database tables");
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public void createUser(User user) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO users VALUES (?,?,?,?,?,?,?,?,?,?,?)")) {

            stmt.setString(1, user.getUuid().toString());
            stmt.setString(2, user.getName());
            stmt.setDouble(3, user.getClick());
            stmt.setDouble(4, user.getRawClick());
            stmt.setDouble(5, user.getMoney());
            stmt.setDouble(6, user.getGem());
            stmt.setDouble(7, user.getToken());
            stmt.setDouble(8, user.getPrestige());
            stmt.setDouble(9, user.getPrestigePoint());
            stmt.setString(10, gson.toJson(user.getUpgrades()));
            stmt.setString(11, gson.toJson(user.getBoosters()));
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUser(String uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM users WHERE uuid = ?")) {

            stmt.setString(1, uuid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String upgradesJson = rs.getString("upgrades");
                    String boostersJson = rs.getString("boosters");

                    Type upgradeListType = new TypeToken<List<Upgrade>>(){}.getType();
                    List<Upgrade> upgrades = upgradesJson != null ?
                            gson.fromJson(upgradesJson, upgradeListType) : new ArrayList<>();

                    Type boosterListType = new TypeToken<List<Booster>>(){}.getType();
                    List<Booster> boosters = boostersJson != null ?
                            gson.fromJson(boostersJson, boosterListType) : new ArrayList<>();

                    return new User(
                            UUID.fromString(rs.getString("uuid")),
                            rs.getString("name"),
                            rs.getDouble("click"),
                            rs.getDouble("rawclick"),
                            rs.getDouble("money"),
                            rs.getDouble("gem"),
                            rs.getDouble("token"),
                            rs.getDouble("prestige"),
                            rs.getDouble("prestigepoint"),
                            upgrades,
                            boosters
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                String upgradesJson = rs.getString("upgrades");
                String boostersJson = rs.getString("boosters");

                Type upgradeListType = new TypeToken<List<Upgrade>>(){}.getType();
                List<Upgrade> upgrades = upgradesJson != null ?
                        gson.fromJson(upgradesJson, upgradeListType) : new ArrayList<>();

                Type boosterListType = new TypeToken<List<Booster>>(){}.getType();
                List<Booster> boosters = boostersJson != null ?
                        gson.fromJson(boostersJson, boosterListType) : new ArrayList<>();

                users.add(new User(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("name"),
                        rs.getDouble("click"),
                        rs.getDouble("rawclick"),
                        rs.getDouble("money"),
                        rs.getDouble("gem"),
                        rs.getDouble("token"),
                        rs.getDouble("prestige"),
                        rs.getDouble("prestigepoint"),
                        upgrades,
                        boosters
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void updateUser(User user) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE users SET name=?, click=?, rawclick=?, money=?, gem=?, token=?, prestige=?, prestigepoint=?, upgrades=?, boosters=? WHERE uuid=?")) {

            stmt.setString(1, user.getName());
            stmt.setDouble(2, user.getClick());
            stmt.setDouble(3, user.getRawClick());
            stmt.setDouble(4, user.getMoney());
            stmt.setDouble(5, user.getGem());
            stmt.setDouble(6, user.getToken());
            stmt.setDouble(7, user.getPrestige());
            stmt.setDouble(8, user.getPrestigePoint());
            stmt.setString(9, gson.toJson(user.getUpgrades()));
            stmt.setString(10, gson.toJson(user.getBoosters()));
            stmt.setString(11, user.getUuid().toString());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Global Booster Management
    public Booster getGlobalBooster() {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT booster FROM global_data WHERE id = 1")) {
            System.out.println("getting global booster from database");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String boosterJson = rs.getString("booster");
                    //System.out.println(boosterJson);
                    if (boosterJson == null)
                    {
                        //System.out.println("returned null");
                        return null;
                    }
                    //System.out.println("returning global booster");
                    return gson.fromJson(boosterJson, Booster.class);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("failed getting global booster");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setGlobalBooster(Booster booster) {
        if (booster == null) return;
        System.out.println("saving global booster");
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE global_data SET booster = ? WHERE id = 1")) {

            //System.out.println("setting string");
            stmt.setString(1, gson.toJson(booster));
            //System.out.println("executing update");
            stmt.executeUpdate();
        } catch (SQLException e) {
            //System.out.println("failed setting global booster");
            e.printStackTrace();
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT booster FROM global_data WHERE id = 1")) {

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String boosterJson = rs.getString("booster");
                    System.out.println(boosterJson);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //System.out.println("failed getting global booster");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Existing methods (deleteUser, disconnect) remain unchanged
    public void deleteUser(UUID uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM users WHERE uuid = ?")) {

            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from H2 database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}