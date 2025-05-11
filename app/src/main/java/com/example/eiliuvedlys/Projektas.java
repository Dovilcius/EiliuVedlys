package com.example.eiliuvedlys;

public class Projektas {
    private int id;
    private String pavadinimas;
    private int eile;
    private String pastaba;
    private int imageResId;
    private String imageUri;

    public Projektas() {}

    public Projektas(String pavadinimas, int eile, String pastaba) {
        this.pavadinimas = pavadinimas;
        this.eile = eile;
        this.pastaba = pastaba;
        this.imageResId = R.drawable.projektu; // numatyta reikšmė
    }

    public Projektas(String pavadinimas, int eile, String pastaba, int imageResId) {
        this.pavadinimas = pavadinimas;
        this.eile = eile;
        this.pastaba = pastaba;
        this.imageResId = imageResId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPavadinimas() { return pavadinimas; }
    public void setPavadinimas(String pavadinimas) { this.pavadinimas = pavadinimas; }

    public int getEile() { return eile; }
    public void setEile(int eile) { this.eile = eile; }

    public String getPastaba() { return pastaba; }
    public void setPastaba(String pastaba) { this.pastaba = pastaba; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
}