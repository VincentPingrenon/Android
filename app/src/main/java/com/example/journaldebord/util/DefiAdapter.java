package com.example.journaldebord.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.journaldebord.R;

import java.util.List;


public class DefiAdapter extends ArrayAdapter<XMLDefi> {

    public DefiAdapter(@NonNull Context context, @NonNull List<XMLDefi> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        XMLDefi defi = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_defi, parent, false);
        }
        // Lookup view for data population
        TextView nomDefi = convertView.findViewById(R.id.textView12);
        TextView dateDefi = convertView.findViewById(R.id.textView15);
        // Populate the data into the template view using the data object
        nomDefi.setText(defi.getName());
        dateDefi.setText(defi.getBeginDate());

        // Return the completed view to render on screen
        return convertView;
    }

}
