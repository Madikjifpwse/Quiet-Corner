package com.quietcorner.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quietcorner.app.Place;
import com.quietcorner.app.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {

    private MapView map;
    private List<Place> allPlaces = new ArrayList<>();
    private final Random random = new Random();

    // ✅ обработчик результата фильтра
    private final ActivityResultLauncher<Intent> filterLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    boolean wifi = result.getData().getBooleanExtra("wifi", false);
                    boolean sockets = result.getData().getBooleanExtra("sockets", false);
                    boolean free = result.getData().getBooleanExtra("free", false);
                    String noise = result.getData().getStringExtra("noise");

                    List<Place> filtered = new ArrayList<>();
                    for (Place p : allPlaces) {
                        if ((wifi && !p.isWifi()) ||
                                (sockets && !p.isSockets()) ||
                                (free && !"free".equalsIgnoreCase(p.getCost())) ||
                                (noise != null && !noise.isEmpty() && !noise.equalsIgnoreCase(p.getNoiseLevel()))) {
                            continue;
                        }
                        filtered.add(p);
                    }

                    showPlaces(filtered);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(43.238949, 76.889709);
        map.getController().setZoom(13.0);
        map.getController().setCenter(startPoint);

        FloatingActionButton btnFilter = view.findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), com.quietcorner.app.FilterActivity.class);
            filterLauncher.launch(intent);
        });

        loadPlaces();
        showPlaces(allPlaces);

        return view;
    }

    private void loadPlaces() {
        String json = loadJSONFromAsset("quite_places.json");
        if (json == null) return;

        try {
            JSONArray rootArray = new JSONArray(json);
            JSONObject rootObject = rootArray.getJSONObject(0);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Place>>() {}.getType();

            if (rootObject.has("library"))
                allPlaces.addAll(gson.fromJson(rootObject.getJSONArray("library").toString(), listType));
            if (rootObject.has("cafe"))
                allPlaces.addAll(gson.fromJson(rootObject.getJSONArray("cafe").toString(), listType));
            if (rootObject.has("coworking"))
                allPlaces.addAll(gson.fromJson(rootObject.getJSONArray("coworking").toString(), listType));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPlaces(List<Place> places) {
        map.getOverlays().clear();

        for (Place place : places) {
            Marker marker = new Marker(map);
            marker.setPosition(new GeoPoint(place.getLatitude(), place.getLongitude()));
            marker.setTitle(place.getName());
            marker.setSubDescription(place.getDescription());
            marker.setIcon(requireContext().getDrawable(getImageForCategory(place.getCategory())));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            marker.setOnMarkerClickListener((m, mapView) -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle(place.getName())
                        .setMessage(place.getDescription() +
                                "\n\nWi-Fi: " + (place.isWifi() ? "✅" : "❌") +
                                "\nSockets: " + (place.isSockets() ? "✅" : "❌") +
                                "\nCost: " + place.getCost())
                        .setPositiveButton("OK", null)
                        .show();
                return true;
            });

            map.getOverlays().add(marker);
        }

        map.invalidate();
    }

    private int getImageForCategory(String category) {
        if (category == null) return R.drawable.placeholder;

        switch (category.toLowerCase()) {
            case "library":
                int[] libs = {R.drawable.library, R.drawable.library2};
                return libs[random.nextInt(libs.length)];
            case "cafe":
                int[] cafes = {R.drawable.coffee2};
                return cafes[random.nextInt(cafes.length)];
            case "coworking":
                int[] co = {R.drawable.coworking, R.drawable.coworking2};
                return co[random.nextInt(co.length)];
            default:
                return R.drawable.placeholder;
        }
    }

    private String loadJSONFromAsset(String filename) {
        try (InputStream is = requireContext().getAssets().open(filename)) {
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}
