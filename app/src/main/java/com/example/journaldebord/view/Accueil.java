package com.example.journaldebord.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.journaldebord.R;

public class Accueil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void connexion(View view) {
        Intent intent = new Intent(Accueil.this, Connexion.class);
        startActivity(intent);

    }
    public void inscription(View view) {
        Intent intent = new Intent(this, Inscription.class);
        startActivity(intent);
    }
}
