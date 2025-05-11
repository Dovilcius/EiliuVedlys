package com.example.eiliuvedlys;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class NuotraukuAdapter extends BaseAdapter {

    private Context context;
    private int[] nuotraukos;
    private int selectedResId;

    public NuotraukuAdapter(Context context, int[] nuotraukos, int selectedResId) {
        this.context = context;
        this.nuotraukos = nuotraukos;
        this.selectedResId = selectedResId;
    }

    public void setSelectedResId(int resId) {
        this.selectedResId = resId;
        notifyDataSetChanged(); // atnaujina vaizdą
    }

    @Override
    public int getCount() {
        return nuotraukos.length;
    }

    @Override
    public Object getItem(int position) {
        return nuotraukos[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            int size = (int) (80 * context.getResources().getDisplayMetrics().density);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(nuotraukos[position]);

        // remelis pasirinktos nuotraukos
        if (nuotraukos[position] == selectedResId) {
            GradientDrawable border = new GradientDrawable();
            border.setStroke(24, Color.parseColor("#9F87A9"));
            border.setColor(Color.TRANSPARENT);
            imageView.setBackground(border);
        } else {
            imageView.setBackground(null);
        }

        return imageView;
    }
}