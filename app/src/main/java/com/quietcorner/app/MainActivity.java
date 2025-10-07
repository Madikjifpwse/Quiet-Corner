package com.quietcorner.app;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.util.GeoPoint;

public class MainActivity extends AppCompatActivity {

    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Настройка конфигурации OSMDroid
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_main);
        map = findViewById(R.id.map);

        // Тип карты (по умолчанию — стандартная OSM)
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Разрешаем масштабирование и перемещение
        map.setMultiTouchControls(true);

        // Устанавливаем стартовую позицию (например, Алматы)
        GeoPoint startPoint = new GeoPoint(43.238949, 76.889709);
        map.getController().setZoom(13.0);
        map.getController().setCenter(startPoint);
        // Кнопка "Центрировать карту"
        Button btnCenter = findViewById(R.id.btnCenter);
        btnCenter.setOnClickListener(v -> {
            map.getController().setZoom(13.0);
            map.getController().animateTo(startPoint);
        });

        // Добавляем тихие места
        java.util.List<Place> places = java.util.Arrays.asList(
                new Place("Central Library", 43.2385, 76.9097, "Quiet place to read and study."),
                new Place("Coffee & Peace", 43.2379, 76.8901, "Small cozy café with calm music."),
                new Place("Botanical Garden", 43.2223, 76.9282, "Nice outdoor space for focus."),
                new Place("SDU Learning Zone", 43.2077, 76.6680, "Comfortable student study area.")
        );

        // Отображаем их на карте
        for (Place p : places) {
            org.osmdroid.views.overlay.Marker marker = new org.osmdroid.views.overlay.Marker(map);
            marker.setPosition(new org.osmdroid.util.GeoPoint(p.getLatitude(), p.getLongitude()));
            marker.setTitle(p.getName());
            marker.setSubDescription(p.getDescription());

            // Добавляем обработчик клика по маркеру
            marker.setOnMarkerClickListener((m, mapView) -> {
                androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                dialog.setTitle(p.getName());
                dialog.setMessage(p.getDescription());
                dialog.setPositiveButton("OK", null);
                dialog.show();
                return true;
            });

            map.getOverlays().add(marker);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume(); // Важно для корректного отображения карты
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}
