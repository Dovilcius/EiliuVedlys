package com.example.eiliuvedlys;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    private Animation scaleUp, scaleDown;
    private LinearLayout contentLayout;
    private TextView titleText;

    private ImageButton btnSekimas, btnSiulu, btnProjektai, btnAkiu, btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // susieti su bendrais maketo elementais
        contentLayout = findViewById(R.id.contentLayout);
        titleText = findViewById(R.id.titleText);

        btnSekimas = findViewById(R.id.btnSekimas);
        btnSiulu = findViewById(R.id.btnSiulu);
        btnProjektai = findViewById(R.id.btnProjektai);
        btnAkiu = findViewById(R.id.btnAkiu);
        btnHome = findViewById(R.id.btnHome);

        // activity_main.xml į bendrą dalį
        View childView = getLayoutInflater().inflate(R.layout.activity_main, null);
        contentLayout.addView(childView);

        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        titleText.setText("MEZGUŽĖ");
        setActiveMenuButton(btnHome);


        LinearLayout korteleSekimas = findViewById(R.id.cardSekimas);
        LinearLayout korteleSiulai = findViewById(R.id.cardSiulu);
        LinearLayout korteleProjektai = findViewById(R.id.cardProjektai);
        LinearLayout korteleAkiu = findViewById(R.id.cardAkiu);

        // Kortelių paspaudimai
        nustatytiPaspaudima(korteleSekimas, SekimasActivity.class);
        nustatytiPaspaudima(korteleSiulai, SiuluActivity.class);
        nustatytiPaspaudima(korteleProjektai, ProjektaiActivity.class);
        nustatytiPaspaudima(korteleAkiu, AkiuActivity.class);

        // meniu mygtukų paspaudimai
        btnSekimas.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SekimasActivity.class)));
        btnSiulu.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SiuluActivity.class)));
        btnProjektai.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProjektaiActivity.class)));
        btnAkiu.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AkiuActivity.class)));
        btnHome.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MainActivity.class)));
    }
    private void nustatytiPaspaudima(View view, Class<?> destination) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.startAnimation(scaleUp);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.startAnimation(scaleDown);
                    v.performClick();
                    break;
            }
            return true;
        });

        view.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, destination)));
    }
}
