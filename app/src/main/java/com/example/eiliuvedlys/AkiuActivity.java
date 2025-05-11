package com.example.eiliuvedlys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class AkiuActivity extends BaseActivity {

    private Button buttonPriekis, buttonNugara, buttonRankove;
    private EditText inputApimtis, inputLaisvumas, inputTankis;
    private TextView textRezultatas, labelApimtis, infoTankisText;
    private LinearLayout layoutApimtis;
    private ImageButton infoTankis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View childView = getLayoutInflater().inflate(R.layout.activity_akiu, null);
        contentLayout.addView(childView);

        titleText.setText("MEZGUŽĖ");
        if (subTitleText != null) {
            subTitleText.setText("Akių skaičiuoklė");
        }

        setActiveMenuButton(btnAkiu);

        // Komponentai visu 3
        buttonPriekis = findViewById(R.id.buttonPriekis);
        buttonNugara = findViewById(R.id.buttonNugara);
        buttonRankove = findViewById(R.id.buttonRankove);

        inputApimtis = findViewById(R.id.inputApimtis);
        inputLaisvumas = findViewById(R.id.inputLaisvumas);
        inputTankis = findViewById(R.id.inputTankis);

        labelApimtis = findViewById(R.id.labelApimtis);
        layoutApimtis = findViewById(R.id.layoutApimtis);
        textRezultatas = findViewById(R.id.rezultatas);

        infoTankis = findViewById(R.id.infoTankis);
        infoTankisText = findViewById(R.id.infoTankisText);

        infoTankis.setOnClickListener(v -> {
            if (infoTankisText.getVisibility() == View.GONE) {
                infoTankisText.setVisibility(View.VISIBLE);
            } else {
                infoTankisText.setVisibility(View.GONE);
            }
        });

        buttonPriekis.setOnClickListener(v -> pasirinkti("Priekis"));
        buttonNugara.setOnClickListener(v -> pasirinkti("Nugara"));
        buttonRankove.setOnClickListener(v -> pasirinkti("Rankove"));

        findViewById(R.id.btnSkaiciuoti).setOnClickListener(v -> skaiciuoti());

        nustatytiMygtukusSuPlusuMinusu(R.id.btnPlusApimtis, R.id.btnMinusApimtis, inputApimtis);
        nustatytiMygtukusSuPlusuMinusu(R.id.btnPlusLaisvumas, R.id.btnMinusLaisvumas, inputLaisvumas);
        nustatytiMygtukusSuPlusuMinusu(R.id.btnPlusTankis, R.id.btnMinusTankis, inputTankis);

        pasirinkti("Priekis");
    }

    private void pasirinkti(String tipas) {
        resetButtonStyles();

        if (tipas.equals("Priekis") || tipas.equals("Nugara")) {
            labelApimtis.setText("Krūtinės apimtis (cm)");
        } else if (tipas.equals("Rankove")) {
            labelApimtis.setText("Rankos apimtis (cm)");
        }

        if (tipas.equals("Priekis")) nustatytiSpalva(buttonPriekis);
        else if (tipas.equals("Nugara")) nustatytiSpalva(buttonNugara);
        else nustatytiSpalva(buttonRankove);

        layoutApimtis.setVisibility(View.VISIBLE);

        // pakeiciam vel i 0 rezultatus
        inputApimtis.setText("0");
        inputLaisvumas.setText("0");
        inputTankis.setText("0");
        textRezultatas.setText("Rezultatas: ?");
        infoTankisText.setVisibility(View.GONE);
    }

    private void nustatytiSpalva(Button button) {
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green));
        button.setTextColor(ContextCompat.getColor(this, android.R.color.black));
    }

    private void resetButtonStyles() {
        int fonas = ContextCompat.getColor(this, R.color.purple_500);
        int tekstas = ContextCompat.getColor(this, android.R.color.black);

        buttonPriekis.setBackgroundColor(fonas);
        buttonNugara.setBackgroundColor(fonas);
        buttonRankove.setBackgroundColor(fonas);

        buttonPriekis.setTextColor(tekstas);
        buttonNugara.setTextColor(tekstas);
        buttonRankove.setTextColor(tekstas);
    }

    private void skaiciuoti() {
        try {
            String apimtisStr = inputApimtis.getText().toString();
            String laisvumasStr = inputLaisvumas.getText().toString();
            String tankisStr = inputTankis.getText().toString();

            if (apimtisStr.isEmpty() || laisvumasStr.isEmpty() || tankisStr.isEmpty()) {
                textRezultatas.setText("Įveskite visus duomenis!");
                return;
            }

            int apimtis = Integer.parseInt(apimtisStr);
            int laisvumas = Integer.parseInt(laisvumasStr);
            int tankis = Integer.parseInt(tankisStr);

            if (apimtis <= 0 || tankis <= 0 || laisvumas < 0) {
                textRezultatas.setText("Apimtis ir tankis turi būti įvesti");
                return;
            }

            int bendrasIlgis = apimtis + laisvumas;
            int akiuKiekis = (int) Math.round((double) bendrasIlgis * tankis / 10.0);
            textRezultatas.setText("Reikia: " + akiuKiekis + " akių");
        } catch (Exception e) {
            textRezultatas.setText("Įveskite skaičius teisingai!");
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