package com.quietcorner.app.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.quietcorner.app.Place;
import com.quietcorner.app.R;
import com.quietcorner.app.ui.PlaceDetailsFragment;

import java.util.List;
import java.util.Random;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.VH> {

    private final Context context;
    private final List<Place> places;
    private final Random random = new Random();

    public PlaceAdapter(Context context, List<Place> places) {
        this.context = context;
        this.places = places;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Place p = places.get(position);
        holder.tvName.setText(p.getName());
        holder.tvDescription.setText(p.getDescription());
        holder.tvRating.setText("⭐ " + p.getRating());

        // small preview image by category (not for map icons)
        int img = R.drawable.placeholder;
        if (p.getCategory() != null) {
            switch (p.getCategory().toLowerCase()) {
                case "library": img = R.drawable.library; break;
                case "cafe": img = R.drawable.coffee2; break;
                case "coworking": img = R.drawable.coworking; break;
            }
        }
        holder.ivImage.setImageResource(img);

        holder.ivWifi.setVisibility(p.isWifi() ? View.VISIBLE : View.GONE);
        holder.ivSocket.setVisibility(p.isSockets() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            // open PlaceDetailsFragment
            Bundle args = new Bundle();
            args.putSerializable("place", p);

            PlaceDetailsFragment details = new PlaceDetailsFragment();
            details.setArguments(args);

            ((FragmentActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, details)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivImage, ivWifi, ivSocket;
        TextView tvName, tvDescription, tvRating;

        VH(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.place_image);
            ivWifi = itemView.findViewById(R.id.icon_wifi);
            ivSocket = itemView.findViewById(R.id.icon_socket);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvRating = itemView.findViewById(R.id.place_rating);
        }
    }
}
