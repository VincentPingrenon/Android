package com.example.journaldebord.view;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.example.journaldebord.R;

import java.util.UUID;

public class Journaux extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journaux);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newJournal = new Intent(Journaux.this, Journal_Creation.class);
                newJournal.putExtra("id", UUID.randomUUID());
                String test = intent.getStringExtra("idConnecte");
                newJournal.putExtra("idConnecte",test);
                startActivity(newJournal);
            }
        });
    }

}
