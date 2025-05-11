package com.example.eiliuvedlys;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProjektoDetalusActivity extends BaseActivity {

    private EditText inputPavadinimas, editPastaba, inputPriminimoEile, inputPriminimoTekstas;
    private TextView textEiliuInfo;
    private ImageView imageProjektoFoto;
    private Button btnPlusEile, btnMinusEile, btnIssaugoti, btnGrizti, btnPridetiPriminima;
    private ListView listPriminimai;

    private int projektasId = -1;
    private int dabartineEile = 0;

    private String nuotraukosUriTekstas = null;
    private int imageResId = R.drawable.projektu;

    private ProjektuDatabaseHelper dbHelper;
    private Projektas projektas;

    private List<Priminimas> priminimuSarasas = new ArrayList<>();
    private ArrayAdapter<String> priminimuAdapter;

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        String savedPath = saugotiVaizdaISaugiaVieta(imageUri);
                        if (savedPath != null) {
                            nuotraukosUriTekstas = savedPath;
                            imageProjektoFoto.setImageURI(Uri.parse(savedPath));
                            imageResId = 0;
                        } else {
                            Toast.makeText(this, "Nepavyko išsaugoti nuotraukos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View childView = getLayoutInflater().inflate(R.layout.activity_projekto_detalus, null);
        contentLayout.addView(childView);

        titleText.setText("Eilių vedlys");
        subTitleText.setText("Projekto detalės");

        dbHelper = new ProjektuDatabaseHelper(this);

        inputPavadinimas = findViewById(R.id.inputPavadinimas);
        editPastaba = findViewById(R.id.editPastaba);
        textEiliuInfo = findViewById(R.id.textEiliuInfo);
        imageProjektoFoto = findViewById(R.id.imageProjektoFoto);
        btnPlusEile = findViewById(R.id.btnPlusEile);
        btnMinusEile = findViewById(R.id.btnMinusEile);
        btnIssaugoti = findViewById(R.id.btnIssaugoti);
        btnGrizti = findViewById(R.id.btnGrizti);
        btnPridetiPriminima = findViewById(R.id.btnPridetiPriminima);
        listPriminimai = findViewById(R.id.listPriminimai);
        inputPriminimoEile = findViewById(R.id.inputPriminimoEile);
        inputPriminimoTekstas = findViewById(R.id.inputPriminimoTekstas);

        priminimuAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listPriminimai.setAdapter(priminimuAdapter);

        projektasId = getIntent().getIntExtra("projekto_id", -1);

        if (projektasId != -1) {
            projektas = dbHelper.gautiProjekta(projektasId);
            if (projektas != null) {
                inputPavadinimas.setText(projektas.getPavadinimas());
                editPastaba.setText(projektas.getPastaba());
                dabartineEile = projektas.getEile();
                textEiliuInfo.setText(String.valueOf(dabartineEile));

                nuotraukosUriTekstas = projektas.getImageUri();
                if (nuotraukosUriTekstas != null) {
                    imageProjektoFoto.setImageURI(Uri.parse(nuotraukosUriTekstas));
                } else {
                    imageResId = projektas.getImageResId();
                    imageProjektoFoto.setImageResource(imageResId);
                }

                priminimuSarasas = dbHelper.gautiPriminimusProjekto(projektas.getId());
                atnaujintiPriminimuVaizda();
            } else {
                Toast.makeText(this, "Projektas nerastas.", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        btnPlusEile.setOnClickListener(v -> atnaujintiEile(dabartineEile + 1));
        btnMinusEile.setOnClickListener(v -> {
            if (dabartineEile > 0) atnaujintiEile(dabartineEile - 1);
        });

        btnIssaugoti.setOnClickListener(v -> issaugotiProjekta());
        btnGrizti.setOnClickListener(v -> finish());
        imageProjektoFoto.setOnClickListener(v -> rodytiPasirinkimoDialoga());

        btnPridetiPriminima.setOnClickListener(v -> {
            String eilStr = inputPriminimoEile.getText().toString().trim();
            String tekstas = inputPriminimoTekstas.getText().toString().trim();
            if (!eilStr.isEmpty() && !tekstas.isEmpty()) {
                Priminimas p = new Priminimas();
                p.setProjektoId(projektas.getId());
                p.setEile(Integer.parseInt(eilStr));
                p.setTekstas(tekstas);
                dbHelper.pridetiPriminima(p);
                priminimuSarasas.add(p);
                atnaujintiPriminimuVaizda();
                inputPriminimoEile.setText("");
                inputPriminimoTekstas.setText("");
            }
        });
        listPriminimai.setOnItemLongClickListener((parent, view, position, id) -> {
            Priminimas p = priminimuSarasas.get(position);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Ištrinti priminimą")
                    .setMessage("Ar tikrai?")
                    .setPositiveButton("Taip", (d, w) -> {
                        dbHelper.istrintiPriminima(p.getId());
                        priminimuSarasas.remove(position);
                        atnaujintiPriminimuVaizda();
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
    }
    private void rodytiPasirinkimoDialoga() {
        String[] pasirinkimai = {"Pasirinkti iš galerijos", "Pasirinkti iš esamų nuotraukų"};

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Pasirinkite nuotrauką")
                .setItems(pasirinkimai, (dialogInterface, which) -> {
                    if (which == 0) {
                        rodytiLeidimoPaaiskinima();
                    } else {
                        rodytiNuotraukuPasirinkima();
                    }
                })
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(this, R.color.dialog_background)));
        });

        dialog.show();
    }
    private void rodytiNuotraukuPasirinkima() {
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

        NuotraukuAdapter adapter = new NuotraukuAdapter(this, nuotraukos, imageResId);
        gridView.setAdapter(adapter);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            imageResId = nuotraukos[position];
            nuotraukosUriTekstas = null;
            imageProjektoFoto.setImageResource(imageResId);
            adapter.setSelectedResId(imageResId);
            dialog.dismiss();
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
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 101);
            } else {
                atidarytiGalerija();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            } else {
                atidarytiGalerija();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            atidarytiGalerija();
        } else {
            Toast.makeText(this, "Reikalingas leidimas pasiekti nuotraukas", Toast.LENGTH_SHORT).show();
        }
    }

    private void atidarytiGalerija() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void atnaujintiEile(int naujaEile) {
        dabartineEile = naujaEile;
        textEiliuInfo.setText(String.valueOf(dabartineEile));
    }

    private void issaugotiProjekta() {
        String pavadinimas = inputPavadinimas.getText().toString().trim();
        String pastaba = editPastaba.getText().toString().trim();

        if (pavadinimas.isEmpty()) {
            Toast.makeText(this, "Įveskite pavadinimą", Toast.LENGTH_SHORT).show();
            return;
        }

        projektas.setPavadinimas(pavadinimas);
        projektas.setEile(dabartineEile);
        projektas.setPastaba(pastaba);
        projektas.setImageUri(nuotraukosUriTekstas);
        projektas.setImageResId(imageResId);

        dbHelper.atnaujintiProjekta(projektas);
        Toast.makeText(this, "Išsaugota", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void atnaujintiPriminimuVaizda() {
        Collections.sort(priminimuSarasas, Comparator.comparingInt(Priminimas::getEile));
        priminimuAdapter.clear();
        for (Priminimas p : priminimuSarasas) {
            priminimuAdapter.add("Eilė: " + p.getEile() + " — " + p.getTekstas());
        }
        priminimuAdapter.notifyDataSetChanged();
        nustatytiListViewAuksti(listPriminimai);
    }

    private void nustatytiListViewAuksti(ListView listView) {
        ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
        if (adapter == null) return;

        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private String saugotiVaizdaISaugiaVieta(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream inputStream = resolver.openInputStream(uri);
            if (inputStream == null) return null;

            File dir = new File(getFilesDir(), "nuotraukos");
            if (!dir.exists()) dir.mkdirs();

            String fileName = "nuotrauka_" + System.currentTimeMillis() + ".jpg";
            File file = new File(dir, fileName);

            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return Uri.fromFile(file).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}