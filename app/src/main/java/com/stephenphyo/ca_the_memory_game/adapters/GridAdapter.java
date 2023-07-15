package com.stephenphyo.ca_the_memory_game.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.stephenphyo.ca_the_memory_game.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends ArrayAdapter<String> {

    // Attributes
    protected List<String> imageFnames;
    private int rowHeight;

    // Constructor
    public GridAdapter(Context ctx, List<String> imageFnames, int rowHeight) {
        super(ctx, 0, imageFnames);
        this.imageFnames = imageFnames;
        this.rowHeight = rowHeight;
    }

    // Methods
    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        Context ctx = getContext();

        if (view == null) {
            view = LayoutInflater.from(ctx)
                    .inflate(R.layout.grid_cell, parent, false);
        }

        ImageView imageView = view.findViewById(R.id.image_grid_cell);
        ImageView imageViewOverlay = view.findViewById(R.id.image_grid_cell_overlay);

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        ViewGroup.LayoutParams layoutParamsOverlay = imageViewOverlay.getLayoutParams();

        layoutParams.height = rowHeight;
        layoutParamsOverlay.height = rowHeight;

        imageView.setLayoutParams(layoutParams);
        imageViewOverlay.setLayoutParams(layoutParamsOverlay);

        String filename = imageFnames.get(pos);

        Bitmap bitmap;
        if (filename.startsWith("drawable:")) {
            int resId = ctx.getResources().getIdentifier(filename.substring("drawable:".length()), "drawable", ctx.getPackageName());
            bitmap = BitmapFactory.decodeResource(ctx.getResources(), resId);
        }
        else {
            File dir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = new File(dir, filename);

            bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
        }
        imageView.setImageBitmap(bitmap);

        return view;
    }

    public void updateDataList(List<String> newDataList) {
        this.imageFnames = newDataList;
        notifyDataSetChanged();
    }

    public void updateData(int pos, String newData) {
        List<String> updatedList = new ArrayList<>(imageFnames);
        updatedList.set(pos, newData);
        this.imageFnames = updatedList;
        notifyDataSetChanged();
    }
}
