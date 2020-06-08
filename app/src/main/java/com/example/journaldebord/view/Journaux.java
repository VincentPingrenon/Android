package com.example.journaldebord.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.journaldebord.R;
import com.example.journaldebord.util.DefiAdapter;
import com.example.journaldebord.util.XMLDefi;
import com.example.journaldebord.util.XMLReader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Journaux extends AppCompatActivity {
    private String userConnected;
    private File[] list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journaux);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        FloatingActionButton fab = findViewById(R.id.fab);
        userConnected = intent.getStringExtra("idConnecte");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newJournal = new Intent(Journaux.this, Journal_Creation.class);
                newJournal.putExtra("id", UUID.randomUUID());
                newJournal.putExtra("idConnecte", userConnected);
                startActivityForResult(newJournal, 1);
            }
        });
        File userFolder = new File(getFilesDir() + "/UserData/" + userConnected);
        if (userFolder.exists()) {
            list = userFolder.listFiles();
            reload(list);
        }
    }

    public void reload(File[] list) {
        List<File> defiFiles = new ArrayList<>(Arrays.asList(list));
        List<XMLDefi> xmlDefis = new ArrayList<>();
        for (File defiFile : defiFiles) {
            xmlDefis.add(XMLReader.readXML(defiFile));
        }
        ListView listDefi = findViewById(R.id.listDefis);
        DefiAdapter defiAdapter = new DefiAdapter(this, xmlDefis);
        listDefi.setAdapter(defiAdapter);
        listDefi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editJournal = new Intent(Journaux.this, EditJournal.class);
                editJournal.putExtra("defi", (XMLDefi) listDefi.getItemAtPosition(position));
                editJournal.putExtra("idConnecte", userConnected);
                editJournal.putExtra("journalUUID", list[position].getName());
                startActivityForResult(editJournal, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            File userFolder = new File(getFilesDir() + "/UserData/" + userConnected);
            File[] list = userFolder.listFiles();
            reload(list);
        }
    }

}
