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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        View btnLogout = view.findViewById(R.id.btnLogout);

        UserRepository repo = new UserRepository(requireContext());
        User user = repo.getUser();

        if (user != null) {
            tvName.setText(user.getUsername());
            tvEmail.setText(user.getEmail());
        }

        btnLogout.setOnClickListener(v -> {
            repo.logout();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .commit();
        });

        return view;
    }
}

