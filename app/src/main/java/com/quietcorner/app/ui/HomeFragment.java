package com.quietcorner.app.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

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

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {

    private MapView map;
    private final List<Place> allPlaces = new ArrayList<>();
    private final Random random = new Random();

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

    public HomeFragment() { /* empty */ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // OSMDroid конфигурация
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        map = v.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        // Центр Алматы
        GeoPoint start = new GeoPoint(43.238949, 76.889709);
        map.getController().setZoom(13.0);
        map.getController().setCenter(start);

        FloatingActionButton btnFilter = v.findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), com.quietcorner.app.FilterActivity.class);
            // Если хочешь — можно передавать текущие фильтры
            filterLauncher.launch(intent);
        });

        loadPlaces();
        showPlaces(allPlaces);

        return v;
    }

    private void loadPlaces() {
        allPlaces.clear();
        try (InputStream is = requireContext().getAssets().open("quite_places.json")) {
            byte[] buf = new byte[is.available()];
            is.read(buf);
            String json = new String(buf, StandardCharsets.UTF_8);

            JSONArray rootArray = new JSONArray(json);
            JSONObject rootObject = rootArray.getJSONObject(0);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Place>>(){}.getType();

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
    private Drawable getMarkerIcon(String category) {
        int iconRes;

        if (category == null) {
            iconRes = R.drawable.ic_default_marker;
        } else {
            switch (category.toLowerCase()) {
                case "library":
                    iconRes = R.drawable.book_open;
                    break;
                case "cafe":
                    iconRes = R.drawable.coffee;
                    break;
                case "coworking":
                    iconRes = R.drawable.briefcase;
                    break;
                default:
                    iconRes = R.drawable.ic_default_marker;
            }
        }

        Drawable drawable = requireContext().getDrawable(iconRes);

        // Масштабируем чтобы иконка всегда была маленькой
        int size = dpToPx(36); // идеально выглядит
        drawable.setBounds(0, 0, size, size);

        return drawable;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }


    private void showPlaces(List<Place> places) {
        map.getOverlays().clear();

        for (Place place : places) {
            Marker marker = new Marker(map);

            marker.setPosition(new GeoPoint(place.getLatitude(), place.getLongitude()));
            marker.setTitle(place.getName());
            marker.setSubDescription(place.getDescription());

            // ставим кастомную иконку
            marker.setIcon(getMarkerIcon(place.getCategory()));

            // правильная точка крепления
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            marker.setOnMarkerClickListener((m, mapView) -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle(place.getName())
                        .setMessage(place.getDescription())
                        .setPositiveButton("OK", null)
                        .show();
                return true;
            });

            map.getOverlays().add(marker);
        }

        map.invalidate();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        if (map != null) map.onPause();
        super.onPause();
    }
}
