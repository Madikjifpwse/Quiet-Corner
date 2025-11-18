package com.quietcorner.app.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.quietcorner.app.Place;
import com.quietcorner.app.R;

public class PlaceDetailsFragment extends Fragment {

    private Place place;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_place_details, container, false);

        ImageView iv = v.findViewById(R.id.place_image);
        TextView tvName = v.findViewById(R.id.place_name);
        TextView tvDesc = v.findViewById(R.id.place_description);
        Button btnMap = v.findViewById(R.id.btn_show_on_map);
        Button btnFav = v.findViewById(R.id.btnAddToFavorites);

        if (getArguments() != null) {
            place = (Place) getArguments().getSerializable("place");
        }

        if (place != null) {
            tvName.setText(place.getName());
            tvDesc.setText(place.getDescription());

            int imageRes = R.drawable.placeholder;
            if (place.getCategory() != null) {
                switch (place.getCategory().toLowerCase()) {
                    case "library": imageRes = R.drawable.library; break;
                    case "cafe": imageRes = R.drawable.coffee2; break;
                    case "coworking": imageRes = R.drawable.coworking; break;
                }
            }
            iv.setImageResource(imageRes);
        }

        btnMap.setOnClickListener(view -> {
            if (place == null) return;
            Bundle args = new Bundle();
            args.putDouble("latitude", place.getLatitude());
            args.putDouble("longitude", place.getLongitude());
            args.putString("name", place.getName());

            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mapFragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnFav.setOnClickListener(view -> {
            if (place == null) return;
            SharedPreferences prefs = requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);
            prefs.edit().putString(place.getName(), new Gson().toJson(place)).apply();
            Toast.makeText(requireContext(), "Добавлено в избранное ⭐", Toast.LENGTH_SHORT).show();
        });

        return v;
    }
}
