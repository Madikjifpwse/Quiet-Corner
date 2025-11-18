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

import androidx.core.content.ContextCompat;
import android.graphics.drawable.Drawable;

public class MapFragment extends Fragment {

    private MapView map;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        map = v.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        if (getArguments() != null) {
            double lat = getArguments().getDouble("latitude", 0);
            double lon = getArguments().getDouble("longitude", 0);
            String name = getArguments().getString("name", "Place");

            GeoPoint p = new GeoPoint(lat, lon);
            map.getController().setZoom(17.0);
            map.getController().setCenter(p);

            Marker marker = new Marker(map);
            marker.setPosition(p);
            marker.setTitle(name);

            //Drawable d = ContextCompat.getDrawable(requireContext(), R.drawable.ic_menu_mylocation);
            //if (d != null) marker.setIcon(d);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            map.getOverlays().add(marker);
        }

        return v;
    }

    @Override
    public void onResume() { super.onResume(); if (map != null) map.onResume(); }
    @Override
    public void onPause() { if (map != null) map.onPause(); super.onPause(); }
}
