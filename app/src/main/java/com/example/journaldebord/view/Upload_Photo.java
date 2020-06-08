package com.example.journaldebord.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.journaldebord.R;


public class Upload_Photo extends Fragment {
    private OnItemSelectedListener listener;

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement Upload_Photo.OnItemSelectedListener");
        }
    }

    // Now we can fire the event when the user selects something in the fragment
    public void onSomeClick(View v) {
        listener.onRssItemSelected();
    }

    public void onViewCreated(View v) {
        listener.onViewLoaded();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_uploadphoto, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewCreated(view);
        view.findViewById(R.id.imageViewProfilePic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSomeClick(v);
            }
        });
    }

    // Define the events that the fragment will use to communicate
    public interface OnItemSelectedListener {
        void onRssItemSelected();

        void onViewLoaded();
    }
}