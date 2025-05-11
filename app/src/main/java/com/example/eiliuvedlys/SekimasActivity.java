package com.example.eiliuvedlys;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SekimasActivity extends BaseActivity {

    private static final String PREFS_NAME = "EiliuSekimasPrefs";
    private static final String KEY_EILE = "eiles_numeris";

    private TextView textEile;
    private Button btnMinus, btnPlus, btnAtstatyti;

    private int sekimoEile = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View childView = getLayoutInflater().inflate(R.layout.activity_sekimas, null);
        contentLayout.addView(childView);

        titleText.setText("MEZGUŽĖ");
        subTitleText.setText("Eilių sekimas");
        setActiveMenuButton(btnSekimas);

        textEile = findViewById(R.id.textEile);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAtstatyti = findViewById(R.id.btnAtstatyti);

        sekimoEile = gautiIssaugotaEile();
        atnaujintiVaizda();

        btnPlus.setOnClickListener(v -> {
            sekimoEile++;
            issaugotiEile();
        });

        btnMinus.setOnClickListener(v -> {
            if (sekimoEile > 0) {
                sekimoEile--;
                issaugotiEile();
            }
        });

        btnAtstatyti.setOnClickListener(v -> {
            sekimoEile = 0;
            issaugotiEile();
        });
    }

    private void atnaujintiVaizda() {
        textEile.setText(String.valueOf(sekimoEile));
    }

    private void issaugotiEile() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(KEY_EILE, sekimoEile).apply();
        atnaujintiVaizda();
    }

    private int gautiIssaugotaEile() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(KEY_EILE, 0); // numatyta reikšmė 0
    }
}