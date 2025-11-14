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

    private ImageView placeImage;
    private TextView placeName, placeDescription;
    private Button btnShowOnMap, btnAddToFavorites;
    private Place place;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_details, container, false);

        placeImage = view.findViewById(R.id.place_image);
        placeName = view.findViewById(R.id.place_name);
        placeDescription = view.findViewById(R.id.place_description);
        btnShowOnMap = view.findViewById(R.id.btn_show_on_map);
        btnAddToFavorites = view.findViewById(R.id.btnAddToFavorites);

        // Загружаем данные о месте
        if (getArguments() != null) {
            place = (Place) getArguments().getSerializable("place");

            if (place != null) {
                placeName.setText(place.getName());
                placeDescription.setText(place.getDescription());

                int imageRes = R.drawable.placeholder;
                switch (place.getCategory()) {
                    case "library":
                        imageRes = R.drawable.library;
                        break;
                    case "cafe":
                        imageRes = R.drawable.coffee2;
                        break;
                    case "coworking":
                        imageRes = R.drawable.coworking;
                        break;
                }
                placeImage.setImageResource(imageRes);
            }
        }

        btnShowOnMap.setOnClickListener(v -> {
            if (place != null && getActivity() != null) {
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
            }
        });

        btnAddToFavorites.setOnClickListener(v -> {
            if (place != null) {
                SharedPreferences prefs = requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(place.getName(), new Gson().toJson(place));
                editor.apply();

                Toast.makeText(requireContext(), "Добавлено в избранное ⭐", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
