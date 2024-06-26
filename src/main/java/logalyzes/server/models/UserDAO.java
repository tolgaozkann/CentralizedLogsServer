package logalyzes.server.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final String DB_URL = "jdbc:sqlite:Db.db";
    private static volatile UserDAO instance;

    public UserDAO() {
        createTableIfNotExists();
    }

    public static UserDAO getInstance() {
        if (instance == null) {
            synchronized (UserDAO.class) {
                if (instance == null) {
                    instance = new UserDAO();
                }
            }
        }
        return instance;
    }


    private Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database.", e);
        }
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " username TEXT NOT NULL,\n"
                + " email TEXT NOT NULL,\n"
                + " attention_levels TEXT NOT NULL\n"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating table.", e);
        }
    }

    public void insert(User user) {
        String sql = "INSERT INTO users (username, email, attention_levels) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, arrayToString(user.getAttentionLevels()));
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting data.", e);
        }
    }

    public List<User> selectAll() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setAttentionLevels(stringToArray(rs.getString("attention_levels")));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error selecting data.", e);
        }

        return users;
    }

    public List<User> findUsersByAttentionLevels(int[] attentionLevels) {
        List<User> users = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM users WHERE ");

        for (int i = 0; i < attentionLevels.length; i++) {
            if (i > 0) {
                queryBuilder.append(" OR ");
            }
            queryBuilder.append("attention_levels LIKE ?");
        }

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {

            for (int i = 0; i < attentionLevels.length; i++) {
                pstmt.setString(i + 1, "%" + attentionLevels[i] + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setAttentionLevels(stringToArray(rs.getString("attention_levels")));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error selecting users by attention levels.", e);
        }

        return users;
    }

    public User getById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        User user = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setAttentionLevels(stringToArray(rs.getString("attention_levels")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error selecting data by ID.", e);
        }

        return user;
    }

    public User getByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        User user = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setAttentionLevels(stringToArray(rs.getString("attention_levels")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error selecting data by email.", e);
        }

        return user;
    }

    public void update(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, attention_levels = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, arrayToString(user.getAttentionLevels()));
            pstmt.setInt(4, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.toString());
            throw new RuntimeException("Error updating data.", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting data.", e);
        }
    }

    private String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int num : array) {
            sb.append(num).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); // Remove the trailing comma
        }
        return sb.toString();
    }

    private int[] stringToArray(String str) {
        String[] parts = str.split(",");
        int[] array = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            array[i] = Integer.parseInt(parts[i]);
        }
        return array;
    }
}
