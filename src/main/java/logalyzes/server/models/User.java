package logalyzes.server.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

public class User {
    private int id;
    private String username;
    private String email;
    private int[] attentionLevels;

    public User() {}

    public User(String username, String email, int[] attentionLevels) {
        this.username = username;
        this.email = email;
        this.attentionLevels = attentionLevels;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int[] getAttentionLevels() {
        return attentionLevels;
    }

    public void setAttentionLevels(int[] attentionLevels) {
        this.attentionLevels = attentionLevels;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", attentionLevels=" + Arrays.toString(attentionLevels) +
                '}';
    }
}
