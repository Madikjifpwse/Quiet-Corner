package com.quietcorner.app.data;

import android.content.Context;
import android.content.SharedPreferences;

public class UserRepository {
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private final Context context;

    public UserRepository(Context context) {
        this.context = context;
    }

    // ✅ Регистрация
    public void registerUser(String username, String email, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .apply();
    }

    // ✅ Логин
    public boolean login(String email, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedEmail = prefs.getString(KEY_EMAIL, null);
        String savedPassword = prefs.getString(KEY_PASSWORD, null);

        // Если совпадает — авторизация успешна
        return savedEmail != null && savedEmail.equals(email)
                && savedPassword != null && savedPassword.equals(password);
    }

    // ✅ Получение текущего пользователя
    public User getCurrentUser() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String username = prefs.getString(KEY_USERNAME, null);
        String email = prefs.getString(KEY_EMAIL, null);
        String password = prefs.getString(KEY_PASSWORD, null);

        if (username == null || email == null) return null;
        return new User(username, email, password);
    }

    // ✅ Выход (очистка данных)
    public void logout() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
