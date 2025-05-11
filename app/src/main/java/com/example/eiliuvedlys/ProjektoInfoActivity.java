package com.example.eiliuvedlys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import java.io.InputStream;
import java.util.List;

public class ProjektoInfoActivity extends BaseActivity {

    private TextView textPavadinimas, textEile, textPastaba;
    private ImageView imageProjektas;
    private Button btnRedaguoti, btnGrizti, btnPlusEile, btnMinusEile;
    private ProjektuDatabaseHelper dbHelper;
    private int projektasId = -1;
    private Projektas projektas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View childView = getLayoutInflater().inflate(R.layout.activity_projekto_info, null);
        contentLayout.addView(childView);

        titleText.setText("Eilių vedlys");
        subTitleText.setText("Projekto informacija");

        textPavadinimas = findViewById(R.id.textPavadinimas);
        textEile = findViewById(R.id.textEile);
        textPastaba = findViewById(R.id.textPastaba);
        imageProjektas = findViewById(R.id.imageProjektas);
        btnRedaguoti = findViewById(R.id.btnRedaguoti);
        btnGrizti = findViewById(R.id.btnGrizti);
        btnPlusEile = findViewById(R.id.btnPlusEile);
        btnMinusEile = findViewById(R.id.btnMinusEile);

        dbHelper = new ProjektuDatabaseHelper(this);

        projektasId = getIntent().getIntExtra("projekto_id", -1);
        if (projektasId != -1) {
            ikeltiDuomenis(projektasId);
        }

        btnRedaguoti.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProjektoDetalusActivity.class);
            intent.putExtra("projekto_id", projektasId);
            startActivity(intent);
        });

        btnGrizti.setOnClickListener(v -> finish());

        btnPlusEile.setOnClickListener(v -> {
            if (projektas != null) {
                projektas.setEile(projektas.getEile() + 1);
                dbHelper.atnaujintiProjekta(projektas);
                ikeltiDuomenis(projektasId);
            }
        });

        btnMinusEile.setOnClickListener(v -> {
            if (projektas != null && projektas.getEile() > 0) {
                projektas.setEile(projektas.getEile() - 1);
                dbHelper.atnaujintiProjekta(projektas);
                ikeltiDuomenis(projektasId);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (projektasId != -1) {
            ikeltiDuomenis(projektasId);
        }
    }

    private void ikeltiDuomenis(int id) {
        projektas = dbHelper.gautiProjekta(id);
        if (projektas != null) {
            textPavadinimas.setText(projektas.getPavadinimas());
            textEile.setText(String.valueOf(projektas.getEile()));
            textPastaba.setText(projektas.getPastaba());

            String uriString = projektas.getImageUri();
            if (uriString != null) {
                Uri uri = Uri.parse(uriString);
                if (arUriPasiekiama(uri)) {
                    imageProjektas.setImageURI(uri);
                } else {
                    imageProjektas.setImageResource(projektas.getImageResId());
                }
            } else {
                imageProjektas.setImageResource(projektas.getImageResId());
            }

            List<Priminimas> priminimai = dbHelper.gautiPriminimusProjekto(projektasId);
            for (Priminimas p : priminimai) {
                if (projektas.getEile() == p.getEile()) {
                    new AlertDialog.Builder(this)
                            .setTitle("Priminimas")
                            .setMessage(p.getTekstas())
                            .setPositiveButton("Gerai", null)
                            .show();
                    dbHelper.istrintiPriminima(p.getId());
                }
            }
        } else {
            Toast.makeText(this, "Projektas nerastas.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean arUriPasiekiama(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            return inputStream != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}