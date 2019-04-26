package com.camera;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private static final int REQUEST_CODE = 1;
    private Button selectMultiImg, moveNext;
    private boolean isMultipleImg = false;
    private RecyclerView imageRecycler;
    private List<ImageUriModal> uriList;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        //        initialize the id's
        inItId();
//        initialize the context
        context = SecondActivity.this;
        //        listener for selecting  the multiple image form gallery
        selectMultiImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMultipleImg = true;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                //intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        //        listener for move on to next page
        moveNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, InternetImageLoadActivity.class);
                startActivity(intent);
            }
        });
        //        set the recycler for multiple image
        imageRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    private void inItId() {
        selectMultiImg = findViewById(R.id.btn_select_multiple_images);
        imageRecycler = findViewById(R.id.recycle_images);
        moveNext = findViewById(R.id.btn_move_next);
    }
//    get the callback from startActivityForResult

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (isMultipleImg) {
                if (data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        uriList = new ArrayList<>();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ImageUriModal imageUriModal = new ImageUriModal(clipData.getItemAt(i).getUri());
                            uriList.add(imageUriModal);
                        }
                        ImageRecyclerAdapter imageRecyclerAdapter = new ImageRecyclerAdapter(uriList, context);
                        imageRecycler.setAdapter(imageRecyclerAdapter);
//                    Uri uri = clipData.getItemAt(0).getUri();
//                    load the image with glide
//                    Glide.with(this).load(clipData.getItemAt(0).getUri())
//                            .centerCrop().placeholder(R.drawable.loader).into(camImg);
//                    // camImg.setImageURI(clipData.getItemAt(0).getUri());
//                    Glide.with(this).load(clipData.getItemAt(1).getUri()).into(galleryFetchImg);
                        // galleryFetchImg.setImageURI(clipData.getItemAt(1).getUri());
                        isMultipleImg = false;

                    }
                }
                //  Log.i(TAG, "onActivityResult:isMultipleImg if " + data);
            }
            //  Log.i(TAG, "getData: "+data.getData());
        } else {
            Log.i(TAG, "onActivityResult: else " + requestCode);
        }
    }
}
