package com.quietcorner.app.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.quietcorner.app.Place;
import com.quietcorner.app.R;
import com.quietcorner.app.ui.PlaceDetailsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    private final Context context;
    private final List<Place> favorites;

    public FavoritesAdapter(Context context, List<Place> favorites) {
        this.context = context;
        this.favorites = favorites;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = favorites.get(position);
        holder.name.setText(place.getName());
        holder.rating.setText("⭐ " + place.getRating());

        int imageRes = R.drawable.placeholder;
        switch (place.getCategory()) {
            case "library":
                imageRes = R.drawable.library;
                break;
            case "cafe2":
                imageRes = R.drawable.coffee2;
                break;
            case "coworking":
                imageRes = R.drawable.coworking;
                break;
        }
        holder.image.setImageResource(imageRes);

        // 🗑 Удаление из избранного
        holder.delete.setOnClickListener(v -> {
            SharedPreferences prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
            prefs.edit().remove(place.getName()).apply();
            favorites.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Удалено из избранного", Toast.LENGTH_SHORT).show();
        });

        // ⭐ Клик по карточке — открытие деталей
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("place", place);

            Fragment fragment = new PlaceDetailsFragment();
            fragment.setArguments(bundle);

            ((FragmentActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public static List<Place> loadFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        Map<String, ?> all = prefs.getAll();
        List<Place> list = new ArrayList<>();
        Gson gson = new Gson();
        for (Object value : all.values()) {
            list.add(gson.fromJson(value.toString(), Place.class));
        }
        return list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, delete;
        TextView name, rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.fav_image);
            delete = itemView.findViewById(R.id.fav_delete);
            name = itemView.findViewById(R.id.fav_name);
            rating = itemView.findViewById(R.id.fav_rating);
        }
    }
}
