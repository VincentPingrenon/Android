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

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Class that handles the Inscription part
 * @author Vincent Pingrenon
 */
public class Inscription extends AppCompatActivity {
    private static final String LOG = "Inscription";
    private boolean finished = false;

    /**
     * Method by default
     * Manage mainly the Register button
     * @param savedInstanceState default arg
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = getFilesDir() + "/loginInfos.json";
                try {
                    File file = new File(filename);
                    if(!file.exists()){
                        if(!file.createNewFile()){
                            throw new LoginException("Unable to create the login file please try again.");
                        }
                    }
                    Map<UUID, User> infos = new HashMap<>();
                    try {
                        infos = JsonFileUtil.readFile(filename, infos);
                    }catch(EOFException eof){
                        Log.i(LOG, "File is empty, starting a new object now.");
                    }
                    OutputStream out = new FileOutputStream(filename);
                    storeLoginInfos(infos,out);
                    finished = true;
                    Inscription.this.finish();
                }catch(LoginException se){
                    Log.w(LOG , "" + se.getMessage());
                    Toast.makeText(Inscription.this, se.getMessage(), Toast.LENGTH_SHORT).show();
                } catch(FileNotFoundException f){
                    Log.w(LOG, "Could not find file : " + f.getMessage());
                } catch (IOException e){
                    Log.w(LOG, "Unable to access the file : "+ e.getMessage());
                }

            }
        });
    }

    /**é
     * Method that store the user login infos inside the json, so that we can get it later.
     * @param out the OutputStream of the file
     * @throws LoginException if the password matches.
     * @throws IOException if unable to access the file
     */
    private void storeLoginInfos(Map<UUID, User> infos, OutputStream out) throws LoginException, IOException{
        UUID id = UUID.randomUUID();
        String login = ((EditText)findViewById(R.id.loginUser)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();
        if(password.equals(((EditText)findViewById(R.id.passwordMatch)).getText().toString())) {
            password = Base64.encodeToString(password.getBytes(),Base64.DEFAULT);
            User toBeAdded = new User(id,login,password);
            List<User> usersToAdd = new ArrayList<>(infos.values());
            for(User u : infos.values()){
                if(u.getName().equals(toBeAdded.getName())){
                    Log.i(LOG, "Username taken, asking for another try");
                    JsonFileUtil.addUsers(usersToAdd, out);
                    throw new LoginException("This username already exist, please try again with another name.");
                }
            }
            usersToAdd.add(toBeAdded);
            JsonFileUtil.addUsers(usersToAdd, out);
            Toast.makeText(Inscription.this, "User "+ toBeAdded.getName() +" was successfully added.", Toast.LENGTH_SHORT).show();
        }else{
            throw new LoginException("Passwords do not match, please change!");
        }
    }


}
