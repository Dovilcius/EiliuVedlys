package com.example.eiliuvedlys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected LinearLayout contentLayout;
    protected TextView titleText;
    protected TextView subTitleText;
    protected ImageButton btnSekimas, btnSiulu, btnProjektai, btnAkiu, btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Susiejimas bendru komponentu
        contentLayout = findViewById(R.id.contentLayout);
        titleText = findViewById(R.id.titleText);
        subTitleText = findViewById(R.id.subTitleText);

        btnSekimas = findViewById(R.id.btnSekimas);
        btnSiulu = findViewById(R.id.btnSiulu);
        btnProjektai = findViewById(R.id.btnProjektai);
        btnAkiu = findViewById(R.id.btnAkiu);
        btnHome = findViewById(R.id.btnHome);

        // bendra meniu mygtuku elgsena
        initBottomMenu();
    }
    protected void setSubTitleText(String text) {
        if (subTitleText != null) {
            if (text == null || text.isEmpty()) {
                subTitleText.setVisibility(View.GONE);
            } else {
                subTitleText.setVisibility(View.VISIBLE);
                subTitleText.setText(text);
            }
        }
    }

    protected void initBottomMenu() {
        if (btnSekimas != null) {
            btnSekimas.setOnClickListener(v -> startActivity(new Intent(this, SekimasActivity.class)));
        }
        if (btnSiulu != null) {
            btnSiulu.setOnClickListener(v -> startActivity(new Intent(this, SiuluActivity.class)));
        }
        if (btnProjektai != null) {
            btnProjektai.setOnClickListener(v -> startActivity(new Intent(this, ProjektaiActivity.class)));
        }
        if (btnAkiu != null) {
            btnAkiu.setOnClickListener(v -> startActivity(new Intent(this, AkiuActivity.class)));
        }
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        }
    }

    /**
     * paryškinti aktyvų meniu mygtuką
     */
    protected void setActiveMenuButton(ImageButton activeButton) {
        // is naujo mygtuku spalvos
        if (btnSekimas != null) btnSekimas.setBackgroundResource(R.drawable.round_button);
        if (btnSiulu != null) btnSiulu.setBackgroundResource(R.drawable.round_button);
        if (btnProjektai != null) btnProjektai.setBackgroundResource(R.drawable.round_button);
        if (btnAkiu != null) btnAkiu.setBackgroundResource(R.drawable.round_button);
        if (btnHome != null) btnHome.setBackgroundResource(R.drawable.round_button);

        // Aktyviam mygtukui kitas fonas
        if (activeButton != null) {
            activeButton.setBackgroundResource(R.drawable.round_button_active);
        }
    }
}