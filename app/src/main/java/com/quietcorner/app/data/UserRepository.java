package com.quietcorner.app.data;

import android.content.Context;
import android.content.SharedPreferences;

public class UserRepository {

    private static final String PREF = "user_prefs";
    private static final String KEY_NAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private final SharedPreferences prefs;

    public UserRepository(Context ctx) {
        this.prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    // Сохранение пользователя
    public void saveUser(User user) {
        prefs.edit()
                .putString(KEY_NAME, user.getUsername())
                .putString(KEY_EMAIL, user.getEmail())
                .putString(KEY_PASSWORD, user.getPassword())
                .apply();
    }

    // Чтение пользователя
    public User getUser() {
        String name = prefs.getString(KEY_NAME, null);
        String email = prefs.getString(KEY_EMAIL, null);
        String password = prefs.getString(KEY_PASSWORD, null);

        if (name == null || email == null || password == null)
            return null;

        return new User(name, email, password);
    }

    // Проверка логина
    public boolean login(String email, String password) {
        User user = getUser();
        if (user == null) return false;

        return user.getEmail().equals(email) &&
                user.getPassword().equals(password);
    }

    // Выход
    public void logout() {
        prefs.edit().clear().apply();
    }

    // Проверка если пользователь авторизован
    public boolean isLoggedIn() {
        return getUser() != null;
    }
}
