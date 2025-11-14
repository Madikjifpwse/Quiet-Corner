package com.quietcorner.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.config.Configuration;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private MapView map;
    private List<Place> allPlaces = new ArrayList<>();

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

    public HomeFragment() {  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(43.238949, 76.889709);
        map.getController().setZoom(13.0);
        map.getController().setCenter(startPoint);

        FloatingActionButton btnFilter = view.findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FilterActivity.class);
            var prefs = getActivity().getSharedPreferences("filters", getActivity().MODE_PRIVATE);
            intent.putExtra("wifi", prefs.getBoolean("wifi", false));
            intent.putExtra("sockets", prefs.getBoolean("sockets", false));
            intent.putExtra("free", prefs.getBoolean("free", false));
            intent.putExtra("noise", prefs.getString("noise", ""));
            filterLauncher.launch(intent);
        });

        loadPlacesFromAssets();
        showPlaces(allPlaces);
    }

    private void loadPlacesFromAssets() {
        try (InputStream is = getContext().getAssets().open("quite_places.json")) {
            byte[] buf = new byte[is.available()];
            is.read(buf);
            String json = new String(buf, StandardCharsets.UTF_8);

            org.json.JSONArray rootArray = new org.json.JSONArray(json);
            org.json.JSONObject rootObject = rootArray.getJSONObject(0);

            com.google.gson.Gson gson = new com.google.gson.Gson();
            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<Place>>() {}.getType();

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
        for (Place p : places) {
            Marker marker = new Marker(map);
            marker.setPosition(new GeoPoint(p.getLatitude(), p.getLongitude()));
            marker.setTitle(p.getName());
            marker.setSubDescription(p.getDescription());
            marker.setOnMarkerClickListener((m, mv) -> {
                new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                        .setTitle(p.getName())
                        .setMessage(p.getDescription() + "\n\nWi-Fi: " + (p.isWifi() ? "✅":"❌"))
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
