package com.quietcorner.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quietcorner.app.ui.adapters.PlaceAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PlacesFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlaceAdapter adapter;
    private final List<Place> allPlaces = new ArrayList<>();

    public PlacesFragment() { /* empty */ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_places, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_places);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlaceAdapter(requireContext(), allPlaces);
        recyclerView.setAdapter(adapter);

        loadPlacesFromAssets();
        // уведомляем адаптер, если данные подгрузились
        adapter.notifyDataSetChanged();
    }

    private void loadPlacesFromAssets() {
        try (InputStream is = requireContext().getAssets().open("quite_places.json")) {
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray rootArray = new JSONArray(json);
            JSONObject rootObject = rootArray.getJSONObject(0);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Place>>() {}.getType();

            if (rootObject.has("library")) {
                List<Place> libs = gson.fromJson(rootObject.getJSONArray("library").toString(), listType);
                allPlaces.addAll(libs);
            }
            if (rootObject.has("cafe")) {
                List<Place> cafes = gson.fromJson(rootObject.getJSONArray("cafe").toString(), listType);
                allPlaces.addAll(cafes);
            }
            if (rootObject.has("coworking")) {
                List<Place> co = gson.fromJson(rootObject.getJSONArray("coworking").toString(), listType);
                allPlaces.addAll(co);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
