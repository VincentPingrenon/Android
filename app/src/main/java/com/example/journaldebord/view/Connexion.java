package com.example.journaldebord.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.journaldebord.R;
import com.example.journaldebord.exceptions.LoginException;
import com.example.journaldebord.user.User;
import com.example.journaldebord.util.JsonFileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Connexion extends AppCompatActivity {
    private static final String LOG = "Connexion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        Button login = findViewById(R.id.connection);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = getFilesDir() + "/loginInfos.json";
                try {
                    File file = new File(filename);
                    if(!file.exists()){
                        throw new LoginException("No file with connexion information, please register a user first");
                    }
                    Map<UUID, User> infos = new HashMap<>();
                    infos = JsonFileUtil.readFile(filename,infos);
                    String username = ((EditText)findViewById(R.id.loginusername)).getText().toString();
                    String password = Base64.encodeToString((((EditText)findViewById(R.id.loginpassword)).getText().toString()).getBytes(),Base64.DEFAULT);
                    boolean connected = false;
                    for(User u : infos.values()){
                        if(u.getName().equals(username) && u.getPassword().equals(password)){
                            Toast.makeText(Connexion.this, "Now connected as "+ u.getName(), Toast.LENGTH_SHORT).show();
                            connected=true;
                        }
                    }
                    if(!connected){
                        Toast.makeText(Connexion.this, "Unable to find user "+ username + ". Please register before connecting", Toast.LENGTH_SHORT).show();
                    }else{
                        setContentView(R.layout.activity_main);
                    }
                }catch(LoginException se){
                    Log.w(LOG , ""+ se.getMessage());
                    Toast.makeText(Connexion.this, se.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException e){
                    Log.w(LOG, "Unable to access the file : "+ e.getMessage());
                }
            }
        });
    }


}

