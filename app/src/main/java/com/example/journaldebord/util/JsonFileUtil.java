package com.example.journaldebord.util;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import com.example.journaldebord.user.User;
import com.example.journaldebord.exceptions.LoginException;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JsonFileUtil {
    private static final String LOG = "JsonFileUtil";


    /**
     * Add the users given to the json file
     * @param usersToAdd the users to add
     * @throws LoginException if unable to add the users
     */
    public static void addUsers(List<User> usersToAdd, OutputStream out) throws LoginException, IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        writer.setIndent("  ");
        if(writeUser(writer,usersToAdd)){
            Log.i(LOG,"User was successfully added");
        }else{
            Log.w(LOG ,"Unable to add the user.");
            throw new LoginException("Unable to add user, please try again.");
        }
        writer.close();
    }

    /**
     * Read the json file and re-add the user right after, as JsonReader removes the informations otherwise
     * @param filename the file to read
     * @param infos the map to stock
     * @throws IOException if unable to access the map
     * @throws LoginException if unable to read the user informations
     */
    public static  Map<UUID, User> readFile(String filename, Map<UUID,User> infos) throws IOException, LoginException{
        try {
            InputStream in = new FileInputStream(filename);
            infos = JsonFileUtil.readExistingJson(in);
            OutputStream out = new FileOutputStream(filename);
            JsonFileUtil.addUsers(new ArrayList<>(infos.values()),out);
        }catch(EOFException eof){
            Log.i(LOG, "File is empty, starting a new object now.");
        }
        return infos;
    }

    /**
     * Read the entire Json to fill the map
     * @param in the InputStream of the file
     * @return a Map filled (or empty when no user) with user
     * @throws IOException if unable to access the file
     */
    private static Map<UUID,User> readExistingJson(InputStream in) throws IOException {
        Map<UUID,User> infos = new HashMap<>();
        try(JsonReader reader = new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            fillMap(reader,infos);
            return infos;
        }
    }

    /**
     * Fill a Map with user Informations <UUID, User>
     * @param reader the reader that reads the file
     * @param infos the Map to be filled
     */
    private static void fillMap(JsonReader reader, Map<UUID,User> infos){
        User user;
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                user = readUser(reader);
                infos.put(user.getId(), user);
            }
            reader.endArray();
        }catch(IOException io){
            Log.w(LOG, "Could not fill the map, the file is probably empty", io);
        }
    }

    /**
     * Read the user informations inside the JsonFile
     * @param reader the Reader that reads the file
     * @return a User filled with the id, the username and the pass
     * @throws IOException if unable to access the file
     */
    private static User readUser(JsonReader reader) throws IOException {
        String username = "";
        UUID id = null;
        String password = "";

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch(name){
                case "name":
                    username = reader.nextString();
                    break;
                case "id":
                    id = UUID.fromString(reader.nextString());
                    break;
                case "password":
                     password = reader.nextString();
                     break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return new User(id,username, password);
    }

    /**
     * Add the user to the json file
     * @param writer the JsonWriter that writes into the file
     * @param usersToAdd list of the Users we want to add to the json
     * @return a boolean to inform whether or not the user has been added
     */
    private static boolean writeUser(JsonWriter writer, List<User> usersToAdd){
        try {
            writer.beginArray();
            for(User toBeAdded : usersToAdd){
                writer.beginObject();
                writer.name("id").value(toBeAdded.getId().toString());
                writer.name("name").value(toBeAdded.getName());
                writer.name("password").value(toBeAdded.getPassword());
                writer.endObject();
            }
            writer.endArray();
            return true;
        }catch(IOException io){
            Log.w(LOG ,"Unable to add the user, IOException occured" + io.getMessage());
            return false;
        }
    }


}
