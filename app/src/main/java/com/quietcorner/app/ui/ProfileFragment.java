package com.quietcorner.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.quietcorner.app.R;
import com.quietcorner.app.data.User;
import com.quietcorner.app.data.UserRepository;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        Button btnEdit = view.findViewById(R.id.btnEditProfile);
        Button btnSettings = view.findViewById(R.id.btnSettings);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        UserRepository repo = new UserRepository(requireContext());
        User user = repo.getCurrentUser();

        if (user == null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .commit();
            return view;
        }

        tvName.setText(user.getUsername());
        tvEmail.setText(user.getEmail());

        btnEdit.setOnClickListener(v ->
                tvName.setText("Редактирование профиля пока не реализовано 🙂"));

        btnSettings.setOnClickListener(v ->
                tvName.setText("Настройки в разработке ⚙️"));

        btnLogout.setOnClickListener(v -> {
            repo.logout();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .commit();
        });

        return view;
    }
}
