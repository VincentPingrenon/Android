package com.example.journaldebord.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.journaldebord.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Upload_Photo extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 2;
    private static final String LOG = "Upload";
    String currentFilePath;
    @BindView(R.id.imageViewProfilePic)
    ImageView imageViewProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadphoto);
        ButterKnife.bind(this);
    }

    /**
     * Alert dialog for capture or select from galley
     */
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Upload_Photo.this);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView= findViewById(R.id.imageViewProfilePic);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(currentFilePath)));
                    Glide.with(Upload_Photo.this).load(imageBitmap).apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.profile_pic_place_holder)).into(imageViewProfilePic);
                }catch(IOException io){
                    Log.w(LOG,"Unable to get the photo from the camera, please try again");
                }
                } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    Bitmap saved = saveImage(bitmap);
                    Glide.with(Upload_Photo.this).load(saved).apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.profile_pic_place_holder)).into(imageViewProfilePic);

                }catch(IOException io){
                    Log.w(LOG,"Unable to get the photo from gallery, please try again");
                }

            }
        }
    }

    private Bitmap saveImage(Bitmap bitmap) throws IOException{
        File imageFile = createImageFile();
        FileOutputStream out = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
        Uri androidURI = Uri.fromFile(imageFile);
        return  MediaStore.Images.Media.getBitmap(this.getContentResolver(),androidURI);
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
        if(!pictureFolder.exists()){
            pictureFolder.mkdir();
        }
        File image =  File.createTempFile(mFileName, ".jpg", pictureFolder);
        currentFilePath = image.getAbsolutePath();
        return image;
    }


    @OnClick(R.id.imageViewProfilePic)
    public void onViewClicked() {
        selectImage();
    }
}