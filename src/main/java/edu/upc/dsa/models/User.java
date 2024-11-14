package edu.upc.dsa.models;

import edu.upc.dsa.util.RandomUtils;

public class User {
     String username;
     String password; // Recuerda que normalmente se debe almacenar hasheada
     String id;
    boolean isAdmin;

    public User() {this.setId(RandomUtils.getId());
        this.isAdmin = false;}

    public User(String username, String password, boolean isAdmin) {
        this();
        if (id != null) this.setId(id);
        this.setPassword(password);
        this.setUsername(username);
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return "User [id="+id+", username=" + username + ", password=" + password +"]";
    }
}