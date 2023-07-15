package com.stephenphyo.ca_the_memory_game.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stephenphyo.ca_the_memory_game.R;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter<String> {

    // Attributes
    private final Context ctx;
    protected List<String> dataList;

    // Constructor
    public ListAdapter(Context ctx, List<String> dataList) {
        super(ctx, 0, dataList);
        this.ctx = ctx;
        this.dataList = dataList;
    }

    // Methods
    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        Context ctx = getContext();

        if (view == null) {
            view = LayoutInflater.from(ctx)
                    .inflate(R.layout.list_row, parent, false);
        }

        TextView textTimeElapsed = view.findViewById(R.id.text_time_elapsed);
        textTimeElapsed.setText(dataList.get(pos));

        return view;
    }

    public void updateData(int pos, String newData) {
        List<String> updatedList = new ArrayList<>(dataList);
        updatedList.set(pos, newData);
        this.dataList = updatedList;
        notifyDataSetChanged();
    }
}
