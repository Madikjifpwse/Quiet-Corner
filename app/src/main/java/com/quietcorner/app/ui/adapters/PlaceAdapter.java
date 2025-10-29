package com.quietcorner.app.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.quietcorner.app.Place;
import com.quietcorner.app.R;

import java.util.List;
import java.util.Random;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private final Context context;
    private final List<Place> places;
    private final Random random = new Random();

    public PlaceAdapter(Context context, List<Place> places) {
        this.context = context;
        this.places = places;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = places.get(position);
        holder.name.setText(place.getName());
        holder.description.setText(place.getDescription());
        holder.rating.setText("⭐ " + place.getRating());


        // Показ иконок Wi-Fi и розетки
        holder.iconWifi.setVisibility(place.isWifi() ? View.VISIBLE : View.GONE);
        holder.iconSocket.setVisibility(place.isSockets() ? View.VISIBLE : View.GONE);

        // Выбор случайного изображения по категории
        int imageRes = R.drawable.placeholder;
        switch (place.getCategory()) {
            case "library":
                int[] libraries = {R.drawable.library, R.drawable.library2};
                imageRes = libraries[random.nextInt(libraries.length)];
                break;
            case "cafe":
                int[] cafes = {R.drawable.coffee, R.drawable.coffee2};
                imageRes = cafes[random.nextInt(cafes.length)];
                break;
            case "coworking":
                int[] coworkings = {R.drawable.coworking, R.drawable.coworking2, R.drawable.coworking3};
                imageRes = coworkings[random.nextInt(coworkings.length)];
                break;
        }
        holder.image.setImageResource(imageRes);

        // Эффект увеличения при нажатии
        holder.itemView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    android.view.animation.Animation scaleUp =
                            android.view.animation.AnimationUtils.loadAnimation(context, R.anim.scale_up);
                    v.startAnimation(scaleUp);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    android.view.animation.Animation scaleDown =
                            android.view.animation.AnimationUtils.loadAnimation(context, R.anim.scale_down);
                    v.startAnimation(scaleDown);
                    break;
            }
            return false; // чтобы клик всё ещё срабатывал
        });


        // 🔹 Обработчик клика по карточке
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("place", place);

            androidx.fragment.app.Fragment fragment = new com.quietcorner.app.ui.PlaceDetailsFragment();
            fragment.setArguments(bundle);

            ((androidx.fragment.app.FragmentActivity) context)
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        ImageView image, iconWifi, iconSocket;
        TextView name, description, rating;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.place_image);
            name = itemView.findViewById(R.id.tvName);
            description = itemView.findViewById(R.id.tvDescription);
            rating = itemView.findViewById(R.id.place_rating);
            iconWifi = itemView.findViewById(R.id.icon_wifi);
            iconSocket = itemView.findViewById(R.id.icon_socket);
        }
    }

}
