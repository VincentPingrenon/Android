package com.example.journaldebord.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;

import static android.net.Uri.fromFile;

public class EditJournal extends AppCompatActivity implements Upload_Photo.OnItemSelectedListener {
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 2;
    private final String LOG = "EditJournal";
    public String currentFilePath;
    private String idUser;
    private Upload_Photo fragment;
    private Uri imageUri = null;
    private int imageTextId;
    private int imageParentId;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journal);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        idUser = intent.getStringExtra("idConnecte");
        String idJournal = intent.getStringExtra("journalUUID");
        XMLDefi xmlDefi = (XMLDefi) intent.getExtras().get("defi");
        TextView defiName = findViewById(R.id.textView14);
        defiName.setText(xmlDefi.getName());
        TextView date = findViewById(R.id.textView13);
        date.setText(xmlDefi.getBeginDate());
        Button modify = findViewById(R.id.button4);
        Collections.sort(xmlDefi.getSelectors());
        generateInput(xmlDefi.getSelectors());
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 90;
                for (Selectors selector : xmlDefi.getSelectors()) {
                    if (selector instanceof BooleanSelector) {
                        selector.setValue(((Switch) findViewById(index)).isChecked());
                    } else if (selector instanceof DateSelector) {
                        selector.setValue(((EditText) findViewById(index)).getText().toString());
                    } else if (selector instanceof HourSelector) {
                        String hour = ((EditText) findViewById(index)).getText().toString();
                        try {
                            Date date = new SimpleDateFormat("HH:mm:ss").parse(hour);
                            selector.setValue(date.getTime());
                        } catch (ParseException e) {
                            Log.w(LOG, "Unable to parse the hour back to a long, the value will be unchanged");
                        }
                    } else if (selector instanceof ImageSelector) {
                        selector.setValue(currentFilePath);
                    } else if (selector instanceof IntegerSelector) {
                        selector.setValue(Integer.valueOf(((EditText) findViewById(index)).getText().toString()));
                    } else if (selector instanceof SatisfactionSelector) {
                        selector.setValue(((SeekBar) findViewById(index)).getProgress());
                    } else if (selector instanceof TextSelector) {
                        selector.setValue(((EditText) findViewById(index)).getText().toString());

                    }
                    index++;
                }
                String editedXML = xmlDefi.generateXML();
                createXMLFile(editedXML, idUser, idJournal);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }

    private void generateInput(List<Selectors> selectorsList) {
        final ConstraintLayout layout = findViewById(R.id.constraintLayout);
        boolean parentIsImage = false;
        int index = 90;
        int parentId = R.id.textView14;
        for (Selectors selector : selectorsList) {
            if (selector instanceof BooleanSelector) {

                TextView text = new TextView(this);
                text.setId(index * 2);
                text.setText(selector.getName());
                layout.addView(text);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(text.getId(), ConstraintSet.LEFT, R.id.constraintLayout, ConstraintSet.LEFT, 50);
                constraintSet.connect(text.getId(), ConstraintSet.TOP, parentId == R.id.textView14 ? parentId : parentId * 2, ConstraintSet.TOP, 250);
                constraintSet.applyTo(layout);

                Switch switchView = new Switch(this);
                switchView.setChecked(((BooleanSelector) selector).getValue());
                switchView.setId(index);
                layout.addView(switchView);
                constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(switchView.getId(), ConstraintSet.LEFT, text.getId(), ConstraintSet.LEFT, 300);
                constraintSet.connect(switchView.getId(), ConstraintSet.TOP, parentIsImage ? parentId - 1 : parentId, ConstraintSet.TOP, parentIsImage ? 500 : 250);
                constraintSet.applyTo(layout);
                parentIsImage = false;
            } else if (selector instanceof DateSelector) {
                TextView text = new TextView(this);
                text.setId(index * 2);
                layout.addView(text);
                text.setText(selector.getName());
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(text.getId(), ConstraintSet.LEFT, R.id.constraintLayout, ConstraintSet.LEFT, 50);
                constraintSet.connect(text.getId(), ConstraintSet.TOP, parentId == R.id.textView14 ? parentId : parentId * 2, ConstraintSet.TOP, 250);
                constraintSet.applyTo(layout);

                EditText datePicker = new EditText(this);
                datePicker.setText(((DateSelector) selector).getValue());
                datePicker.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
                datePicker.setId(index);
                layout.addView(datePicker);
                constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(datePicker.getId(), ConstraintSet.LEFT, text.getId(), ConstraintSet.LEFT, 300);
                constraintSet.connect(datePicker.getId(), ConstraintSet.TOP, parentIsImage ? parentId - 1 : parentId, ConstraintSet.TOP, parentIsImage ? 500 : 250);
                constraintSet.applyTo(layout);
                parentIsImage = false;
            } else if (selector instanceof HourSelector) {
                TextView text = new TextView(this);
                text.setId(index * 2);
                layout.addView(text);
                text.setText(selector.getName());
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(text.getId(), ConstraintSet.LEFT, R.id.constraintLayout, ConstraintSet.LEFT, 50);
                constraintSet.connect(text.getId(), ConstraintSet.TOP, parentId == R.id.textView14 ? parentId : parentId * 2, ConstraintSet.TOP, 250);
                constraintSet.applyTo(layout);

                EditText hourPicker = new EditText(this);
                Date date = new Date(((HourSelector) selector).getValue());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                hourPicker.setText(simpleDateFormat.format(date));
                hourPicker.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
                hourPicker.setId(index);
                layout.addView(hourPicker);
                constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(hourPicker.getId(), ConstraintSet.LEFT, text.getId(), ConstraintSet.LEFT, 300);
                constraintSet.connect(hourPicker.getId(), ConstraintSet.TOP, parentIsImage ? parentId - 1 : parentId, ConstraintSet.TOP, parentIsImage ? 500 : 250);
                constraintSet.applyTo(layout);
                parentIsImage = false;
            } else if (selector instanceof ImageSelector) {
                TextView text = new TextView(this);
                text.setId(index * 2);
                imageTextId = index * 2;
                layout.addView(text);
                text.setText(selector.getName());
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(text.getId(), ConstraintSet.LEFT, R.id.constraintLayout, ConstraintSet.LEFT, 50);
                constraintSet.connect(text.getId(), ConstraintSet.TOP, parentId == R.id.textView14 ? parentId : parentId * 2, ConstraintSet.TOP, 250);
                constraintSet.applyTo(layout);

                parentIsImage = true;
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragment = new Upload_Photo();
                fragmentTransaction.add(R.id.frameLayout, fragment);
                fragmentTransaction.commit();
                imageParentId = parentId;
                if (!selector.getValue().equals("null") && selector.getValue() != "") {
                    imageUri = fromFile(new File(((ImageSelector) selector).getValue()));
                }

            } else if (selector instanceof IntegerSelector) {
                TextView text = new TextView(this);
                text.setId(index * 2);
                text.setText(selector.getName());
                layout.addView(text);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(text.getId(), ConstraintSet.LEFT, R.id.constraintLayout, ConstraintSet.LEFT, 50);
                constraintSet.connect(text.getId(), ConstraintSet.TOP, parentId == R.id.textView14 ? parentId : parentId * 2, ConstraintSet.TOP, 250);
                constraintSet.applyTo(layout);

                EditText number = new EditText(this);
                number.setInputType(InputType.TYPE_CLASS_NUMBER);
                number.setText(((IntegerSelector) selector).getValue().toString());
                number.setId(index);
                layout.addView(number);
                constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(number.getId(), ConstraintSet.LEFT, text.getId(), ConstraintSet.LEFT, 300);
                constraintSet.connect(number.getId(), ConstraintSet.TOP, parentIsImage ? parentId - 1 : parentId, ConstraintSet.TOP, parentIsImage ? 500 : 250);
                constraintSet.applyTo(layout);
                parentIsImage = false;
            } else if (selector instanceof SatisfactionSelector) {
                TextView text = new TextView(this);
                text.setId(index * 2);
                layout.addView(text);
                text.setText(selector.getName());
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(text.getId(), ConstraintSet.LEFT, R.id.constraintLayout, ConstraintSet.LEFT, 50);
                constraintSet.connect(text.getId(), ConstraintSet.TOP, parentId == R.id.textView14 ? parentId : parentId * 2, ConstraintSet.TOP, 250);
                constraintSet.applyTo(layout);

                SeekBar satisfaction = new SeekBar(this);
                ConstraintLayout.LayoutParams test = new ConstraintLayout.LayoutParams(500, 50);
                satisfaction.setLayoutParams(test);
                satisfaction.setProgress(((SatisfactionSelector) selector).getValue());
                satisfaction.setId(index);
                satisfaction.setMax(100);
                layout.addView(satisfaction);
                constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(satisfaction.getId(), ConstraintSet.LEFT, text.getId(), ConstraintSet.LEFT, 300);
                constraintSet.connect(satisfaction.getId(), ConstraintSet.TOP, parentIsImage ? parentId - 1 : parentId, ConstraintSet.TOP, parentIsImage ? 500 : 250);
                constraintSet.applyTo(layout);
                parentIsImage = false;
            } else if (selector instanceof TextSelector) {
                TextView text = new TextView(this);
                text.setId(index * 2);
                text.setText(selector.getName());
                layout.addView(text);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(text.getId(), ConstraintSet.LEFT, R.id.constraintLayout, ConstraintSet.LEFT, 50);
                constraintSet.connect(text.getId(), ConstraintSet.TOP, parentId == R.id.textView14 ? parentId : parentId * 2, ConstraintSet.TOP, 250);
                constraintSet.applyTo(layout);

                EditText texte = new EditText(this);
                texte.setText(((TextSelector) selector).getValue());
                texte.setId(index);
                layout.addView(texte);
                constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(texte.getId(), ConstraintSet.LEFT, text.getId(), ConstraintSet.LEFT, 300);
                constraintSet.connect(texte.getId(), ConstraintSet.TOP, parentIsImage ? parentId - 1 : parentId, ConstraintSet.TOP, parentIsImage ? 500 : 250);
                constraintSet.applyTo(layout);
                parentIsImage = false;
            }

            parentId = index;
            index++;
        }
    }

    @Override
    public void onViewLoaded() {
        if (imageUri != null) {
            try {
                ImageView imageView = findViewById(R.id.imageViewProfilePic);
                Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Bitmap imageScaled = Bitmap.createScaledBitmap(image, 300, 300, false);
                imageView.setImageBitmap(imageScaled);

                final ConstraintLayout layout = findViewById(R.id.constraintLayout);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(imageView.getId(), ConstraintSet.LEFT, imageTextId, ConstraintSet.LEFT, 500);
                constraintSet.connect(imageView.getId(), ConstraintSet.TOP, imageParentId, ConstraintSet.TOP, 250);
                constraintSet.applyTo(layout);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRssItemSelected() {
        selectImage();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = findViewById(R.id.imageViewProfilePic);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(currentFilePath)));
                    ImageView image = findViewById(R.id.imageViewProfilePic);
                    image.setImageBitmap(imageBitmap);
                    //Glide.with(this).load(imageBitmap).apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.profile_pic_place_holder)).into(imageViewProfilePic);
                } catch (IOException io) {
                    Log.w(LOG, "Unable to get the photo from the camera, please try again");
                }
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    ImageView image = findViewById(R.id.imageViewProfilePic);
                    Bitmap saved = saveImage(bitmap);
                    image.setImageBitmap(saved);
                    //Glide.with(this).load(saved).apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.profile_pic_place_holder)).into(imageViewProfilePic);

                } catch (IOException io) {
                    Log.w(LOG, "Unable to get the photo from gallery, please try again");
                }

            }
        }
    }

    /**
     * Create file with current timestamp name
     *
     * @return a File
     * @throws IOException if unable to create the file
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.FRANCE).format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getFilesDir();
        File pictureFolder = new File(storageDir.getPath() + "/Pictures");
        if (!pictureFolder.exists()) {
            pictureFolder.mkdir();
        }
        File image = File.createTempFile(mFileName, ".jpg", pictureFolder);
        currentFilePath = image.getAbsolutePath();
        return image;
    }

    private Bitmap saveImage(Bitmap bitmap) throws IOException {
        File imageFile = createImageFile();
        FileOutputStream out = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        Uri androidURI = fromFile(imageFile);
        return MediaStore.Images.Media.getBitmap(getContentResolver(), androidURI);
    }

    public void createXMLFile(String xml, String idUser, String idJournal) {
        File folderData = new File(getFilesDir() + "/UserData/");
        File folderUser = new File(folderData.getPath() + "/" + idUser);
        File xmlFile = new File(folderUser.getPath() + "/" + idJournal);
        try {
            xmlFile.delete();
            xmlFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(xmlFile));
            writer.write(xml);
            writer.close();
        } catch (IOException io) {
            Log.w(LOG, "Unable to generate file please try again");
        }
    }


    /**
     * Alert dialog for capture or select from gallery
     */
    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                requestStoragePermission(true);
            } else if (items[item].equals("Choose from Library")) {
                requestStoragePermission(false);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.journaldebord.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }


        }
    }


    /**
     * Select image fro gallery
     */
    private void dispatchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }

    /**
     * Requesting multiple permissions (storage and camera) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission(boolean isCamera) {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent();
                            } else {
                                dispatchGalleryIntent();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }


    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}

