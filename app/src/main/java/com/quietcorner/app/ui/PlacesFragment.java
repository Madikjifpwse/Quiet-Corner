package com.quietcorner.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quietcorner.app.Place;
import com.quietcorner.app.R;
import com.quietcorner.app.ui.adapters.PlaceAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PlacesFragment extends Fragment {

    private List<Place> allPlaces = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        RecyclerView recycler = view.findViewById(R.id.recycler_places);

        loadPlaces();
        recycler.setAdapter(new PlaceAdapter(requireContext(), allPlaces));

        return view;
    }

    private void loadPlaces() {
        try {
            InputStream is = requireContext().getAssets().open("quite_places.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
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
}
