package com.quietcorner.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
    private int[] libraryImages = {R.drawable.library, R.drawable.library2, R.drawable.library3, R.drawable.library4, R.drawable.library5, R.drawable.library6, R.drawable.library7, R.drawable.library8, R.drawable.library9, R.drawable.library10};
    private int[] cafeImages = {R.drawable.coffee2, R.drawable.coffee3, R.drawable.coffee4, R.drawable.coffee5, R.drawable.coffee6, R.drawable.coffee7, R.drawable.coffee8, R.drawable.coffee9, R.drawable.coffee10, R.drawable.coffee11, R.drawable.coffee12, R.drawable.coffee13};
    private int[] coworkingImages = {R.drawable.coworking, R.drawable.coworking2, R.drawable.coworking3, R.drawable.coworking4, R.drawable.coworking5, R.drawable.coworking6, R.drawable.coworking8, R.drawable.coworking9};


    private final List<Place> allPlaces = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_places, container, false);

        RecyclerView recycler = v.findViewById(R.id.recycler_places);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadPlaces();
        PlaceAdapter adapter = new PlaceAdapter(requireContext(), allPlaces);
        recycler.setAdapter(adapter);

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
}
