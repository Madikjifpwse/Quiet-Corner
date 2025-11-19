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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_place_details, container, false);

        ImageView img = v.findViewById(R.id.place_image);
        ImageView btnFav = v.findViewById(R.id.btnFavorite);
        TextView tvName = v.findViewById(R.id.place_name);
        TextView tvDesc = v.findViewById(R.id.place_description);
        Button btnMap = v.findViewById(R.id.btn_show_on_map);

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
            img.setImageResource(imageRes);
        }

        // -------- FAVORITES LOGIC --------
        SharedPreferences prefs = requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);
        boolean isFav = prefs.contains(place.getName());

        btnFav.setImageResource(isFav ? R.drawable.heart_filled : R.drawable.heart);

        btnFav.setOnClickListener(x -> {
            SharedPreferences.Editor ed = prefs.edit();

            if (prefs.contains(place.getName())) {
                ed.remove(place.getName());
                btnFav.setImageResource(R.drawable.heart);
            } else {
                ed.putString(place.getName(), new Gson().toJson(place));
                btnFav.setImageResource(R.drawable.heart_filled);
            }
            ed.apply();
        });

        // -------- MAP BUTTON --------
        btnMap.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putDouble("latitude", place.getLatitude());
            args.putDouble("longitude", place.getLongitude());
            args.putString("name", place.getName());

            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(args);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, mapFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return v;
    }
}
