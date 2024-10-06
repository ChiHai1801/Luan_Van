package com.example.luanvan2324;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luanvan2324.databinding.ActivityHomeBinding;
import com.example.luanvan2324.databinding.ActivityPyBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.WeakHashMap;

public class PyActivity extends AppCompatActivity {

    ActivityPyBinding binding;
    private Uri imageUri;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivPickShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(takePictureIntent);
            }
        });

        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(takePictureIntent);
            }
        });


        activityResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            private boolean fales;

            @Override
            public void onActivityResult(ActivityResult result) {
                Bundle extras = result.getData().getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                WeakReference<Bitmap> result1 = new WeakReference<>(Bitmap.createScaledBitmap(imageBitmap,
                        imageBitmap.getHeight(), imageBitmap.getWidth(), fales).copy(
                                Bitmap.Config.RGB_565, true));

                Bitmap bm = result1.get();
                imageUri = saveImage(bm, PyActivity.this);
                binding.ivPickShow.setImageURI(imageUri);
                binding.tvUri.setText(""+imageUri);
            }

            private Uri saveImage(Bitmap image, Context context) {

                File imageFolder = new File(context.getCacheDir(), "images");
                Uri uri = null;
                try{
                    imageFolder.mkdir();
                    File file = new File(imageFolder, "captured_image.jpg");
                    FileOutputStream stream = new FileOutputStream(file);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    stream.flush();
                    stream.close();
                    uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.example.luanvan2324"+".provider", file);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return uri;
            }
        });


        binding.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PyActivity.this, Activity_demo.class);
                intent.putExtra("imageUri", imageUri.toString());
                startActivity(intent);
            }
        });
    }

}