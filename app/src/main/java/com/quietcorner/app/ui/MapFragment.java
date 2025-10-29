package com.quietcorner.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.quietcorner.app.R;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapFragment extends Fragment {

    private MapView map;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        map = view.findViewById(R.id.map);

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        if (getArguments() != null) {
            double lat = getArguments().getDouble("latitude", 0);
            double lon = getArguments().getDouble("longitude", 0);
            String name = getArguments().getString("name", "Место");

            GeoPoint point = new GeoPoint(lat, lon);
            map.getController().setZoom(17.0);
            map.getController().setCenter(point);

            Marker marker = new Marker(map);
            marker.setPosition(point);
            marker.setTitle(name);
            map.getOverlays().add(marker);
        }

        return view;
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
