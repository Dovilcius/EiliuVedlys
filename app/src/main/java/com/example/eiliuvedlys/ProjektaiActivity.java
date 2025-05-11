package com.example.eiliuvedlys;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class ProjektaiActivity extends BaseActivity {

    private GridLayout cardGrid;
    private ImageButton btnPrideti;
    private TextView textNeraProjektu;
    private ProjektuDatabaseHelper dbHelper;

    private Uri pasirinktasUri = null;
    private String nuotraukosUriTekstas = null;
    private int atsitiktineImageResId = R.drawable.projektu;

    private String laikinasPavadinimas = "";
    private String laikinaPastaba = "";

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        Uri localUri = kopijuotiVaizdaIrGautiUri(uri);
                        if (localUri != null) {
                            pasirinktasUri = localUri;
                            nuotraukosUriTekstas = localUri.toString();
                            sukurtiIrIssaugotiProjekta();
                        } else {
                            Toast.makeText(this, "Nepavyko nukopijuoti nuotraukos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View childView = getLayoutInflater().inflate(R.layout.activity_projektai, null);
        contentLayout.addView(childView);

        titleText.setText("MEZGUŽĖ");
        if (subTitleText != null) subTitleText.setText("Projektai");
        setActiveMenuButton(btnProjektai);

        cardGrid = findViewById(R.id.cardGrid);
        btnPrideti = findViewById(R.id.btnPridetiProjekta);
        textNeraProjektu = findViewById(R.id.textNeraProjektu);
        dbHelper = new ProjektuDatabaseHelper(this);

        btnPrideti.setOnClickListener(v -> rodytiPridetiDialoga());

        atnaujintiProjektuSarasa();
    }
    private void rodytiPridetiDialoga() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_naujas_projektas, null);
        EditText inputPavadinimas = dialogView.findViewById(R.id.inputPavadinimas);
        EditText inputPastaba = dialogView.findViewById(R.id.inputPastaba);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Naujas projektas")
                .setView(dialogView)
                .setPositiveButton("Tęsti", (dialogInterface, which) -> {
                    laikinasPavadinimas = inputPavadinimas.getText().toString().trim();
                    laikinaPastaba = inputPastaba.getText().toString().trim();

                    if (laikinasPavadinimas.isEmpty()) {
                        Toast.makeText(this, "Įveskite pavadinimą", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    rodytiNuotraukosPasirinkima();
                })
                .setNegativeButton("Atšaukti", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(this, android.R.color.black));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(this, android.R.color.black));

            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(this, R.color.dialog_background))
            );
        });

        dialog.show();
    }
    private void rodytiNuotraukosPasirinkima() {
        String[] pasirinkimai = {"Pasirinkti iš galerijos", "Pasirinkti iš esamų nuotraukų"};

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Pasirinkite nuotrauką")
                .setItems(pasirinkimai, (dialogInterface, which) -> {
                    if (which == 0) {
                        rodytiLeidimoPaaiskinima();
                    } else {
                        rodytiNuotraukuDialoga();
                    }
                })
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(this, R.color.dialog_background)));
        });

        dialog.show();
    }
    private void rodytiNuotraukuDialoga() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pasirinkite nuotrauką");

        View view = getLayoutInflater().inflate(R.layout.dialog_nuotrauku_galerija, null);
        GridView gridView = view.findViewById(R.id.gridNuotraukos);

        int[] nuotraukos = {
                R.drawable.salikas, R.drawable.dekutis, R.drawable.kepure,
                R.drawable.salis, R.drawable.megztinis, R.drawable.uzklotas,
                R.drawable.salikelis, R.drawable.apsiaustas,
                R.drawable.megztinis2, R.drawable.megztinis3, R.drawable.dekis
        };

        NuotraukuAdapter adapter = new NuotraukuAdapter(this, nuotraukos, atsitiktineImageResId);
        gridView.setAdapter(adapter);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            atsitiktineImageResId = nuotraukos[position];
            pasirinktasUri = null;
            nuotraukosUriTekstas = null;
            dialog.dismiss();
            sukurtiIrIssaugotiProjekta();
        });

        dialog.show();
    }

    private void rodytiLeidimoPaaiskinima() {
        new AlertDialog.Builder(this)
                .setTitle("Reikalingas leidimas")
                .setMessage("Norint pasirinkti nuotrauką iš galerijos, reikalingas leidimas pasiekti jūsų nuotraukas.")
                .setPositiveButton("Sutinku", (dialog, which) -> tikrintiLeidimaIrAtidarytiGalerija())
                .setNegativeButton("Atšaukti", null)
                .show();
    }

    private void tikrintiLeidimaIrAtidarytiGalerija() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 100);
            } else {
                atidarytiGalerija();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else {
                atidarytiGalerija();
            }
        }
    }

    private void atidarytiGalerija() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            atidarytiGalerija();
        } else {
            Toast.makeText(this, "Reikalingas leidimas pasiekti nuotraukas", Toast.LENGTH_SHORT).show();
        }
    }

    private void sukurtiIrIssaugotiProjekta() {
        Projektas projektas = new Projektas(laikinasPavadinimas, 0, laikinaPastaba);

        if (nuotraukosUriTekstas != null) {
            projektas.setImageUri(nuotraukosUriTekstas);
            projektas.setImageResId(0);
        } else {
            projektas.setImageResId(atsitiktineImageResId);
            projektas.setImageUri(null);
        }

        long id = dbHelper.pridetiProjekta(projektas);
        if (id != -1) {
            projektas.setId((int) id);
            atnaujintiProjektuSarasa();
        } else {
            Toast.makeText(this, "Nepavyko sukurti projekto", Toast.LENGTH_SHORT).show();
        }
    }

    private void atnaujintiProjektuSarasa() {
        cardGrid.removeAllViews();
        List<Projektas> projektai = dbHelper.gautiVisusProjektus();

        textNeraProjektu.setVisibility(projektai.isEmpty() ? View.VISIBLE : View.GONE);

        int columnCount = projektai.size() == 1 ? 1 : 2;
        cardGrid.setColumnCount(columnCount);

        for (Projektas projektas : projektai) {
            View kortele = getLayoutInflater().inflate(R.layout.projekto_kortele, null);
            TextView textPavadinimas = kortele.findViewById(R.id.textPavadinimas);
            ImageView imageProjektas = kortele.findViewById(R.id.imageProjektas);

            textPavadinimas.setText(projektas.getPavadinimas());

            if (projektas.getImageUri() != null) {
                Uri uri = Uri.parse(projektas.getImageUri());
                if (arUriPasiekiama(uri)) {
                    imageProjektas.setImageURI(uri);
                } else {
                    imageProjektas.setImageResource(R.drawable.projektu);
                }
            } else if (projektas.getImageResId() != 0) {
                imageProjektas.setImageResource(projektas.getImageResId());
            } else {
                imageProjektas.setImageResource(R.drawable.projektu);
            }

            kortele.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProjektoInfoActivity.class);
                intent.putExtra("projekto_id", projektas.getId());
                startActivity(intent);
            });

            kortele.setOnLongClickListener(v -> {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Ištrinti projektą")
                        .setMessage("Ar tikrai norite ištrinti šį projektą?")
                        .setPositiveButton("Taip", (d, w) -> {
                            dbHelper.istrintiProjekta(projektas.getId());
                            Toast.makeText(this, "Projektas ištrintas", Toast.LENGTH_SHORT).show();
                            atnaujintiProjektuSarasa();
                        })
                        .setNegativeButton("Ne", null)
                        .create();

                dialog.setOnShowListener(dialogInterface -> {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(ContextCompat.getColor(this, android.R.color.black));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(ContextCompat.getColor(this, android.R.color.black));
                    dialog.getWindow().setBackgroundDrawable(
                            new ColorDrawable(ContextCompat.getColor(this, R.color.dialog_background)));
                });

                dialog.show();
                return true;
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            int margin = (int) (8 * getResources().getDisplayMetrics().density);
            if (projektai.size() == 1) {
                params.width = (int) (200 * getResources().getDisplayMetrics().density);
                params.setGravity(Gravity.CENTER);
            } else {
                params.width = 0;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            }
            params.height = (int) (180 * getResources().getDisplayMetrics().density);
            params.setMargins(margin, margin, margin, margin);
            kortele.setLayoutParams(params);

            cardGrid.addView(kortele);
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

    private Uri kopijuotiVaizdaIrGautiUri(Uri originalUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(originalUri);
            if (inputStream != null) {
                String failoVardas = "nuotrauka_" + System.currentTimeMillis() + ".jpg";
                File failas = new File(getFilesDir(), failoVardas);
                FileOutputStream out = new FileOutputStream(failas);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                out.close();

                return Uri.fromFile(failas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        atnaujintiProjektuSarasa();
    }
}