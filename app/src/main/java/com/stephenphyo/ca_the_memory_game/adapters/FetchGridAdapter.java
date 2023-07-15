package com.stephenphyo.ca_the_memory_game.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stephenphyo.ca_the_memory_game.R;

import java.util.ArrayList;
import java.util.List;

public class FetchGridAdapter extends ArrayAdapter<String> {

    // Attributes
    protected List<String> imageURLs;
    private int rowHeight;

    // Constructor
    public FetchGridAdapter(Context ctx, List<String> imageURLs, int rowHeight) {
        super(ctx, 0, imageURLs);
        this.imageURLs = imageURLs;
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

        String filename = imageURLs.get(pos);

        Bitmap bitmap;
        if (filename.startsWith("drawable:")) {
            int resId = ctx.getResources().getIdentifier(filename.substring("drawable:".length()), "drawable", ctx.getPackageName());
            bitmap = BitmapFactory.decodeResource(ctx.getResources(), resId);
            imageView.setImageBitmap(bitmap);
        }
        else {
            String imageUrl = imageURLs.get(pos);
            Picasso.get().load(imageUrl).into(imageView);
        }

        return view;
    }

    public void updateData(int pos, String newData) {
        List<String> updatedList = new ArrayList<>(imageURLs);
        updatedList.set(pos, newData);
        this.imageURLs = updatedList;
        notifyDataSetChanged();
    }

    public void updateDataList(List<String> newDataList) {
        this.imageURLs = newDataList;
        notifyDataSetChanged();
    }
}
