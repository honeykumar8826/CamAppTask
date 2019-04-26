package com.camera;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "TAG";
    private final String[] permissionList = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ImageView camImg, galleryFetchImg;
   // private CircleImageView camImg, galleryFetchImg;
    private Button capImg, storeImg, openGallery, moveNextScreen;
    private String currentPath;
    private boolean isCapture = false;
    private boolean isStoreImg = false;
    private boolean isOpenGallery = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initialize the id's
        inItId();
//        check the permission is allowed or not
        checkPermission();
//        listener for cam button
        capImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCapture = true;
                captureImage();
            }
        });
//        listener for storing image button
        storeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStoreImg = true;
                storeImage();
            }
        });
//        listener for open the gallery and get image
        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpenGallery = true;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        //        listener for open the gallery and get image
        moveNextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this ,SecondActivity.class);
                startActivity(intent);
            }
        });
    }

    private void captureImage() {
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (camIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.i(TAG, "captureImage: photo file12" + photoFile);

            } catch (IOException e) {
                // Error occurred while creating the File
                Log.i(TAG, "inside catch file create " + e);
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri fileUri = FileProvider.getUriForFile(this, "com.camera.fileProvider", photoFile);
                camIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(camIntent, REQUEST_CODE);
            }
            Log.i(TAG, "onClick: " + camIntent.resolveActivity(getPackageManager()));

        }
    }

    private void storeImage() {
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (camIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile;
            photoFile = createImageFileForGallery();
            Log.i(TAG, "inside storeImage: photo file12 -- " + photoFile);

            if (photoFile != null) {
                Uri fileUri = FileProvider.getUriForFile(this, "com.camera.fileProvider", photoFile);
                camIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(camIntent, REQUEST_CODE);
            }
            Log.i(TAG, "onClick: " + camIntent.resolveActivity(getPackageManager()));

        }
    }

    private void inItId() {
        camImg = findViewById(R.id.cam);
        capImg = findViewById(R.id.btn_capture);
        storeImg = findViewById(R.id.btn_capture_gallery);
        openGallery = findViewById(R.id.btn_open_gallery);
        galleryFetchImg = findViewById(R.id.gallery_selected_image);
        moveNextScreen = findViewById(R.id.btn_move_next_);
    }

//    get the callback from startActivityForResult

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (isCapture) {
                Bitmap getBitmap = BitmapFactory.decodeFile(currentPath);
                camImg.setImageBitmap(getBitmap);
                isCapture = false;
            } else if (isOpenGallery) {
                if(data.getData()!=null)
                {
                    Uri imageUri = data.getData();
                    /** we can directly set the image  by their uri and another way
                     * is use the content resolver to fetch or set the image */
                    // camImg.setImageURI(imageUri);
                    //method for setting the image from the gallery
                    setImageFromGallery(imageUri);
                    isOpenGallery = false;
                }
            } else if (isStoreImg) {
                Bitmap getBitmap = BitmapFactory.decodeFile(currentPath);
                camImg.setImageBitmap(getBitmap);
                isStoreImg = false;
            }
            //  Log.i(TAG, "getData: "+data.getData());
        } else {
            Log.i(TAG, "onActivityResult: else " + requestCode);
        }
    }

    private void setImageFromGallery(Uri imgUri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(imgUri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        camImg.setImageBitmap(BitmapFactory.decodeFile(picturePath));
    }

    //    create image file
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = "JPEG" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File img = File.createTempFile(fileName, ".jpg", storageDir);
        //store the current path of the image for later use
        currentPath = img.getAbsolutePath();
        Log.i(TAG, "createImageFile: " + currentPath);
        return img;
    }

    //    create image file for storing in gallery
    private File createImageFileForGallery() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = "JPEG" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File img = null;
        try {
            img = File.createTempFile(fileName, ".jpg", storageDir);
            currentPath = img.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        File img = new File(storageDir, fileName + ".jpg");
        //store the current path of the image for later use
        Log.i(TAG, "createImageFileForGallery: " + currentPath);
        return img;
    }

    //    for saving the image inside gallery
/*    private void galleryAddPic() {
        Log.i(TAG, "galleryAddPic: ");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        Log.i(TAG, "galleryAddPic: ");
        this.sendBroadcast(mediaScanIntent);
    }*/
    /*--------------------------------permission related code--------------------------------*/
    private void checkPermission() {
//        int count = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(MainActivity.this, permissionList)) {
//            Log.i(TAG, "checkPermission: " + count++);
                ActivityCompat.requestPermissions(MainActivity.this, permissionList, 10);
            } else {
                Toast.makeText(this, " Permission  granted ", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "permission automatically granted", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasPermissions(Context context, String... permissions) {
        int count = 0;
        if (context != null && permissions != null) {
            Log.i(TAG, "hasPermissions: " + permissions.length);
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    count++;
                    Log.i(TAG, "hasPermissions: " + count);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            Log.i(TAG, "onRequestPermissionsResult: " + permissions);
            if (grantResults[0] == -1) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Log.i(TAG, "shouldShowRequestPermissionRationale:");
                    showMessageOkCancel("External Storage permission is required to access location",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    // Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "External Storage Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showMessageOkCancel(String permissionDetail, DialogInterface.OnClickListener onClickListener) {

        new AlertDialog.Builder(this).setMessage(permissionDetail)
                .setPositiveButton("Ok", onClickListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    /*-----------------------end of permission code--------------------------------*/
}
