package com.quietcorner.app.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class HomeFragment extends Fragment {

    private MapView map;
    private final List<Place> allPlaces = new ArrayList<>();

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        // центр Алматы
        GeoPoint start = new GeoPoint(43.238949, 76.889709);
        map.getController().setZoom(13.0);
        map.getController().setCenter(start);

        // Фильтры
        FloatingActionButton btnFilter = view.findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), com.quietcorner.app.FilterActivity.class);
            filterLauncher.launch(intent);
        });

        loadPlaces();
        showPlaces(allPlaces);

        return view;
    }

    // ====== ЧТЕНИЕ JSON ======
    private void loadPlaces() {
        allPlaces.clear();
        try (InputStream is = requireContext().getAssets().open("quite_places.json")) {

            byte[] buf = new byte[is.available()];
            is.read(buf);
            String json = new String(buf, StandardCharsets.UTF_8);

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

    // ====== Кастомные иконки маркеров ======
    private Drawable getMarkerIcon(String category) {

        int icon;

        if (category == null) icon = R.drawable.ic_default_marker;
        else {
            switch (category.toLowerCase()) {
                case "library":
                    icon = R.drawable.book_open;  // ИКОНКА КНИГА
                    break;
                case "cafe":
                    icon = R.drawable.coffee;     // ИКОНКА КОФЕ
                    break;
                case "coworking":
                    icon = R.drawable.laptop;     // ИКОНКА ЛАПТОП
                    break;
                default:
                    icon = R.drawable.ic_default_marker;
                    break;
            }
        }

        return createMarkerDrawable(icon);
    }

    private Drawable createMarkerDrawable(int iconRes) {

        // Круглая подложка
        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.marker_circle);

        // Иконка категории
        Drawable icon = ContextCompat.getDrawable(requireContext(), iconRes);

        // Размеры
        int size = dpToPx(48);
        background.setBounds(0, 0, size, size);

        int iconSize = dpToPx(24);
        icon.setBounds(12, 12, 12 + iconSize, 12 + iconSize);

        return new LayerDrawable(new Drawable[]{background, icon});
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // ====== Отображение маркеров ======
    private void showPlaces(List<Place> places) {
        map.getOverlays().clear();

        for (Place p : places) {
            Marker marker = new Marker(map);

            marker.setPosition(new GeoPoint(p.getLatitude(), p.getLongitude()));
            marker.setIcon(getMarkerIcon(p.getCategory()));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            marker.setOnMarkerClickListener((m, mapView) -> {
                PlaceBottomSheet.newInstance(p)
                        .show(getParentFragmentManager(), "place_sheet");
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
