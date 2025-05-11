package com.example.eiliuvedlys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class SiuluActivity extends BaseActivity {

    private Button buttonMegztinis, buttonUzklotas;
    private LinearLayout layoutRankovesIlgis, layoutRankovesPlotis;
    private TextView textRezultatas, labelApimtis;
    private EditText inputIlgis, inputApimtis, inputRankoveIlgis, inputRankovePlotis;
    private EditText inputAkiuSkaicius, inputEiliuSkaicius, inputSiuloIlgis;
    private ImageButton btnSkaiciuoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View childView = getLayoutInflater().inflate(R.layout.activity_siulu, null);
        contentLayout.addView(childView);

        titleText.setText("MEZGUŽĖ");
        subTitleText.setText("Siūlų skaičiuoklė");
        setActiveMenuButton(btnSiulu);

        buttonMegztinis = findViewById(R.id.buttonMegztinis);
        buttonUzklotas = findViewById(R.id.buttonUzklotas);
        layoutRankovesIlgis = findViewById(R.id.layoutRankovesIlgis);
        layoutRankovesPlotis = findViewById(R.id.layoutRankovesPlotis);
        textRezultatas = findViewById(R.id.rezultatas);
        labelApimtis = findViewById(R.id.labelApimtis);

        inputIlgis = findViewById(R.id.inputIlgis);
        inputApimtis = findViewById(R.id.inputApimtis);
        inputRankoveIlgis = findViewById(R.id.inputRankoveIlgis);
        inputRankovePlotis = findViewById(R.id.inputRankovePlotis);
        inputAkiuSkaicius = findViewById(R.id.inputAkiuSkaicius);
        inputEiliuSkaicius = findViewById(R.id.inputEiliuSkaicius);
        inputSiuloIlgis = findViewById(R.id.inputSiuloIlgis);
        btnSkaiciuoti = findViewById(R.id.btnSkaiciuoti);

        buttonMegztinis.setOnClickListener(v -> pasirinktiMegztini());
        buttonUzklotas.setOnClickListener(v -> pasirinktiUzklota());
        btnSkaiciuoti.setOnClickListener(v -> skaiciuotiSiuluKiekis());

        pasirinktiMegztini();

        nustatytiMygtukusSuPlusuMinusu(R.id.btnPlusIlgis, R.id.btnMinusIlgis, inputIlgis);
        nustatytiMygtukusSuPlusuMinusu(R.id.btnPlusApimtis, R.id.btnMinusApimtis, inputApimtis);
        nustatytiMygtukusSuPlusuMinusu(R.id.btnPlusRankoveIlgis, R.id.btnMinusRankoveIlgis, inputRankoveIlgis);
        nustatytiMygtukusSuPlusuMinusu(R.id.btnPlusRankovePlotis, R.id.btnMinusRankovePlotis, inputRankovePlotis);
        nustatytiMygtukusSuPlusuMinusu(R.id.btnPlusAkiu, R.id.btnMinusAkiu, inputAkiuSkaicius);
        nustatytiMygtukusSuPlusuMinusu(R.id.btnPlusEiliu, R.id.btnMinusEiliu, inputEiliuSkaicius);
        nustatytiMygtukusSuPlusuMinusu(R.id.btnPlusSiuloIlgis, R.id.btnMinusSiuloIlgis, inputSiuloIlgis);
    }

    private void pasirinktiMegztini() {
        buttonMegztinis.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green));
        buttonMegztinis.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        buttonUzklotas.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
        buttonUzklotas.setTextColor(ContextCompat.getColor(this, android.R.color.black));

        layoutRankovesIlgis.setVisibility(View.VISIBLE);
        layoutRankovesPlotis.setVisibility(View.VISIBLE);
        labelApimtis.setText("Apimtis (cm)");

        isvalytiVisusLaukus();
    }

    private void pasirinktiUzklota() {
        buttonUzklotas.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green));
        buttonUzklotas.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        buttonMegztinis.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
        buttonMegztinis.setTextColor(ContextCompat.getColor(this, android.R.color.black));

        layoutRankovesIlgis.setVisibility(View.GONE);
        layoutRankovesPlotis.setVisibility(View.GONE);
        labelApimtis.setText("Plotis (cm)");

        isvalytiVisusLaukus();
    }

    private void isvalytiVisusLaukus() {
        inputIlgis.setText("0");
        inputApimtis.setText("0");
        inputRankoveIlgis.setText("0");
        inputRankovePlotis.setText("0");
        inputAkiuSkaicius.setText("0");
        inputEiliuSkaicius.setText("0");
        inputSiuloIlgis.setText("0");
        textRezultatas.setText("Jums reikės: ? kamuoliukų");
    }

    private void skaiciuotiSiuluKiekis() {
        try {
            int ilgis = Integer.parseInt(inputIlgis.getText().toString());
            int apimtis = Integer.parseInt(inputApimtis.getText().toString());
            int rankoveIlgis = layoutRankovesIlgis.getVisibility() == View.VISIBLE ? Integer.parseInt(inputRankoveIlgis.getText().toString()) : 0;
            int rankovePlotis = layoutRankovesPlotis.getVisibility() == View.VISIBLE ? Integer.parseInt(inputRankovePlotis.getText().toString()) : 0;
            int akiuSkaicius = Integer.parseInt(inputAkiuSkaicius.getText().toString());
            int eiliuSkaicius = Integer.parseInt(inputEiliuSkaicius.getText().toString());
            int siuloIlgis = Integer.parseInt(inputSiuloIlgis.getText().toString()); // metrais kamuoliuke

            if (ilgis <= 0 || apimtis <= 0 || akiuSkaicius <= 0 || eiliuSkaicius <= 0 || siuloIlgis <= 0 ||
                    (layoutRankovesIlgis.getVisibility() == View.VISIBLE && (rankoveIlgis <= 0 || rankovePlotis <= 0))) {
                textRezultatas.setText("Visi skaičiai turi būti didesni už nulį!");
                return;
            }

            // Tankis cm²: kiek akių viename kvadratiniame centimetre
            double tankis = (akiuSkaicius / 10.0) * (eiliuSkaicius / 10.0);

            // Pagrindinis plotas
            double plotas = ilgis * apimtis;

            // Jei megztinis, pridėti rankoves
            if (layoutRankovesIlgis.getVisibility() == View.VISIBLE) {
                plotas += 2 * (rankoveIlgis * rankovePlotis);
            }

            // Kiek akių iš viso
            double bendrasAkiuKiekis = tankis * plotas;

            // Tarkime 1 akis sunaudoja 1 cm siūlo → viso sunaudota (cm), paverčiam į metrus
            double sunaudojimoIlgisMetrais = bendrasAkiuKiekis / 100.0;

            // Kiek kamuoliukų reikia
            int kiekKamuoliuku = (int) Math.ceil(sunaudojimoIlgisMetrais / siuloIlgis);

            textRezultatas.setText("Jums reikės: ~ " + kiekKamuoliuku + " kamuoliukų");
        } catch (Exception e) {
            textRezultatas.setText("Įveskite visus duomenis!");
        }
    }


    private void nustatytiMygtukusSuPlusuMinusu(int plusId, int minusId, EditText laukas) {
        ImageButton btnPlus = findViewById(plusId);
        ImageButton btnMinus = findViewById(minusId);

        btnPlus.setOnClickListener(v -> {
            int skaicius = Integer.parseInt(laukas.getText().toString());
            laukas.setText(String.valueOf(++skaicius));
        });

        btnMinus.setOnClickListener(v -> {
            int skaicius = Integer.parseInt(laukas.getText().toString());
            if (skaicius > 0) skaicius--;
            laukas.setText(String.valueOf(skaicius));
        });
    }
}