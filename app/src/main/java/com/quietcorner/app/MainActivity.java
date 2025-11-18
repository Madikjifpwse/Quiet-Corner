package com.quietcorner.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.quietcorner.app.ui.HomeFragment;
import com.quietcorner.app.ui.PlacesFragment;
import com.quietcorner.app.ui.FavoritesFragment;
import com.quietcorner.app.ui.ProfileFragment;
import com.quietcorner.app.ui.RegisterFragment;
import com.quietcorner.app.data.UserRepository;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserRepository repo = new UserRepository(this);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            int id = item.getItemId();
            if (id == R.id.nav_home) selected = new HomeFragment();
            else if (id == R.id.nav_places) selected = new PlacesFragment();
            else if (id == R.id.nav_favorites) selected = new FavoritesFragment();
            else if (id == R.id.nav_profile) {

                // Если пользователь НЕ зарегистрирован → на регистрацию
                if (repo.getUser() == null) {
                    selected = new RegisterFragment();
                } else {
                    selected = new ProfileFragment();
                }
            }

            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selected)
                        .commit();
            }

            return true;
        });

        // Первое открытие приложения
        if (savedInstanceState == null) {

            if (repo.getUser() == null) {
                // Если юзера нет — регистрация
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new RegisterFragment())
                        .commit();
            } else {
                // Если уже зарегистрирован — домой
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
            }
        }
    }
}
