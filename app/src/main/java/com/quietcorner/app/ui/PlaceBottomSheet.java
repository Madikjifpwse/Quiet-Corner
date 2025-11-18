package com.quietcorner.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.quietcorner.app.Place;
import com.quietcorner.app.R;

public class PlaceBottomSheet extends BottomSheetDialogFragment {

    private Place place;

    public static PlaceBottomSheet newInstance(Place place) {
        PlaceBottomSheet sheet = new PlaceBottomSheet();
        Bundle b = new Bundle();
        b.putSerializable("place", place);
        sheet.setArguments(b);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_place, container, false);

        if (getArguments() != null)
            place = (Place) getArguments().getSerializable("place");

        TextView tvName = view.findViewById(R.id.tvPlaceName);
        TextView tvCategory = view.findViewById(R.id.tvPlaceCategory);
        TextView tvDescription = view.findViewById(R.id.tvPlaceDescription);
        TextView tvInfo = view.findViewById(R.id.tvPlaceInfo);

        tvName.setText(place.getName());
        tvCategory.setText(place.getCategory());
        tvDescription.setText(place.getDescription());

        tvInfo.setText(
                "Wi-Fi: " + (place.isWifi() ? "Есть" : "Нет") +
                        "\nРозетки: " + (place.isSockets() ? "Есть" : "Нет") +
                        "\nСтоимость: " + place.getCost() +
                        "\nУровень шума: " + place.getNoiseLevel()
        );


        return view;
    }
}
