package com.example.journaldebord.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journaldebord.R;
import com.example.journaldebord.indicateurs.BooleanSelector;
import com.example.journaldebord.indicateurs.DateSelector;
import com.example.journaldebord.indicateurs.HourSelector;
import com.example.journaldebord.indicateurs.ImageSelector;
import com.example.journaldebord.indicateurs.IntegerSelector;
import com.example.journaldebord.indicateurs.SatisfactionSelector;
import com.example.journaldebord.indicateurs.Selectors;
import com.example.journaldebord.indicateurs.TextSelector;
import com.example.journaldebord.util.XMLDefi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Journal_Creation extends AppCompatActivity {
    private String idUser;
    private int idCompo = 0;
    private final String LOG = "Journal_Creation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_creation);
        Intent intent = getIntent();
        idUser = intent.getStringExtra("idConnecte");
        Toast.makeText(this,idUser , Toast.LENGTH_SHORT).show();
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        List<String> types = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ConstraintLayout constraintLayout = findViewById(R.id.layoutCreation);
                Spinner spinner = findViewById(R.id.spinner);
                final String selectorType = spinner.getSelectedItem().toString();
                if(idCompo < 5) {
                    TextView text = new TextView(Journal_Creation.this);
                    text.setText(selectorType);
                    text.setId(idCompo++);
                    text.setTextSize(18);
                    constraintLayout.addView(text);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(text.getId(), ConstraintSet.LEFT, R.id.layoutCreation, ConstraintSet.LEFT, 75);
                    constraintSet.connect(text.getId(), ConstraintSet.TOP, R.id.spinner, ConstraintSet.TOP, idCompo == 1 ? 175 : (175 * (idCompo)));
                    constraintSet.applyTo(constraintLayout);

                    EditText position = new EditText(Journal_Creation.this);
                    position.setId(5*idCompo+1);
                    types.add(selectorType);
                    ids.add(position.getId());
                    position.setInputType(InputType.TYPE_CLASS_NUMBER);
                    position.setHint("Position souhaitée (1 à 5)");
                    position.setTextSize(12);
                    constraintLayout.addView(position);
                    constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(position.getId(),ConstraintSet.LEFT, text.getId(), ConstraintSet.LEFT, idCompo == 1 ? 375:300);
                    constraintSet.connect(position.getId(), ConstraintSet.TOP, R.id.spinner, ConstraintSet.TOP, Journal_Creation.getTopPosition(idCompo));
                    constraintSet.applyTo(constraintLayout);
                }else{
                    Toast.makeText(Journal_Creation.this,"You cannot add more than 5 Selectors", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button addDefi = findViewById(R.id.button3);
        addDefi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            List<Selectors> selectorsList = new ArrayList<>();
            String xmlGenerated = createXML(selectorsList,types,ids);
            createXMLFile(xmlGenerated, idUser, UUID.randomUUID());
            }
        });
    }

    private String createXML(List<Selectors> selectorsList, List<String>types, List<Integer>ids){
        String name = ((EditText) findViewById(R.id.editText15)).getText().toString();
        String beginDate = ((EditText) findViewById(R.id.editText14)).getText().toString();
        String endDate = ((EditText) findViewById(R.id.editText13)).getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date today = new Date();
        int position;
        int index = 0;
        for(String type: types){
            switch (type){
                case"Oui/Non":
                    position = Integer.parseInt(((EditText) findViewById(ids.get(index))).getText().toString());
                    BooleanSelector bS = new BooleanSelector(1,position,Boolean.FALSE, sdf.format(today));
                    selectorsList.add(bS);
                    break;
                case "Date":
                    position = Integer.parseInt(((EditText) findViewById(ids.get(index))).getText().toString());
                    DateSelector dS = new DateSelector(1,position,sdf.format(today), sdf.format(today));
                    selectorsList.add(dS);
                    break;
                case "Heure":
                    position = Integer.parseInt(((EditText) findViewById(ids.get(index))).getText().toString());
                    HourSelector hS = new HourSelector(1,position, System.currentTimeMillis(), sdf.format(today));
                    selectorsList.add(hS);
                    break;
                case"Image":
                    position = Integer.parseInt(((EditText) findViewById(ids.get(index))).getText().toString());
                    ImageSelector iS = new ImageSelector(1,position, null, sdf.format(today));
                    selectorsList.add(iS);
                    break;
                case "Nombre":
                    position = Integer.parseInt(((EditText) findViewById(ids.get(index))).getText().toString());
                    IntegerSelector nS = new IntegerSelector(1,position, 0, sdf.format(today));
                    selectorsList.add(nS);
                    break;
                case "Satisfaction":
                    position = Integer.parseInt(((EditText) findViewById(ids.get(index))).getText().toString());
                    SatisfactionSelector sS = new SatisfactionSelector(1,position, 0.0, sdf.format(today));
                    selectorsList.add(sS);
                    break;
                case "Texte":
                    position = Integer.parseInt(((EditText) findViewById(ids.get(index))).getText().toString());
                    TextSelector tS = new TextSelector(1,position, "", sdf.format(today));
                    selectorsList.add(tS);
                    break;
            }
            index++;
        }
        XMLDefi xmlDefi = new XMLDefi(name,beginDate,endDate,selectorsList);
        return xmlDefi.generateXML();
    }

    private static int getTopPosition(int idCompo){
        int position = 0;
        switch(idCompo){
            case 1:
                position = 150;
                break;
            case 2:
                position = 310;
                break;
            case 3:
                position = 490;
                break;
            case 4:
                position = 670;
                break;
            case 5:
                position = 840;
                break;

        }
        return position;
    }

    private void createXMLFile(String xml, String idUser, UUID idJournal){
        File folderData = new File(getFilesDir() + "/UserData/");
        if(!folderData.exists()){
            folderData.mkdir();
        }
        File folderUser = new File(folderData.getPath() +"/"+ idUser);
        if(!folderUser.exists()){
            folderUser.mkdir();
        }
        File xmlFile = new File(folderUser.getPath() +"/"+ idJournal+".xml");
        try {
            xmlFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(xmlFile));
            writer.write(xml);
            writer.close();
        } catch(IOException io){
            Log.w(LOG,"Unable to generate file please try again");
        }
    }
}
