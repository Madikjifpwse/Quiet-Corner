package com.quietcorner.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class FilterActivity extends AppCompatActivity {

    private CheckBox switchWifi, switchSockets, switchFree;
    private RadioGroup groupNoise;
    private Button btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        switchWifi = findViewById(R.id.switch_wifi);
        switchSockets = findViewById(R.id.switch_sockets);
        switchFree = findViewById(R.id.switch_free);
        groupNoise = findViewById(R.id.group_noise);
        btnApply = findViewById(R.id.btnApply);

        // Восстанавливаем состояние
        var prefs = getSharedPreferences("filters", MODE_PRIVATE);
        switchWifi.setChecked(prefs.getBoolean("wifi", false));
        switchSockets.setChecked(prefs.getBoolean("sockets", false));
        switchFree.setChecked(prefs.getBoolean("free", false));

        String savedNoise = prefs.getString("noise", "");
        if (savedNoise.equals("Quiet")) groupNoise.check(R.id.radio_quiet);
        else if (savedNoise.equals("Moderate")) groupNoise.check(R.id.radio_moderate);
        else if (savedNoise.equals("Loud")) groupNoise.check(R.id.radio_loud);

        // Обработка нажатия кнопки
        btnApply.setOnClickListener(v -> {
            boolean wifi = switchWifi.isChecked();
            boolean sockets = switchSockets.isChecked();
            boolean free = switchFree.isChecked();

            String noise = "";
            int checkedId = groupNoise.getCheckedRadioButtonId();
            if (checkedId == R.id.radio_quiet) noise = "Quiet";
            else if (checkedId == R.id.radio_moderate) noise = "Moderate";
            else if (checkedId == R.id.radio_loud) noise = "Loud";

            // сохраняем
            prefs.edit()
                    .putBoolean("wifi", wifi)
                    .putBoolean("sockets", sockets)
                    .putBoolean("free", free)
                    .putString("noise", noise)
                    .apply();

            // возвращаем результат
            Intent result = new Intent();
            result.putExtra("wifi", wifi);
            result.putExtra("sockets", sockets);
            result.putExtra("free", free);
            result.putExtra("noise", noise);
            setResult(RESULT_OK, result);
            finish();
        });
    }
}
