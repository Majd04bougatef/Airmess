package test;

import models.Users;

public class Session {
    private static Session instance;
    private Users currentUser;
    private boolean isLoggedIn;

    private Session() {
        this.isLoggedIn = false;
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public Users getCurrentUser() {
        return currentUser;
    }

    public int getId_U() {
        return currentUser != null ? currentUser.getId_U() : 0;
    }

    public String getEmail() {
        return currentUser != null ? currentUser.getEmail() : null;
    }

    public String getRoleUser() {
        return currentUser != null ? currentUser.getRoleUser() : null;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void login(Users user) {
        this.currentUser = user;
        this.isLoggedIn = true;
    }

    public void logout() {
        this.currentUser = null;
        this.isLoggedIn = false;
    }

    public void checkLogin() throws SecurityException {
        if (!isLoggedIn) {
            throw new SecurityException("Utilisateur non connecté");
        }
    }
}