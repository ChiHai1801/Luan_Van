package com.example.luanvan2324;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.example.luanvan2324.databinding.ActivityDetailBinding;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class Activity_Detail extends AppCompatActivity {

    ActivityDetailBinding binding;
    Toolbar toolbar;


    String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;
    String DATABASE_NAME ="Database.db";

    //Khai báo ListView
    ListView lv;
    ArrayList<String> mylist;
    ArrayAdapter<String> myadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ảnh từ Camera
        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            Uri cameraImageUri = Uri.parse(imageUriString);
            binding.img.setImageURI(cameraImageUri);
        }else {
            // Ảnh lấy từ Thư viện
            Uri uri = (Uri) getIntent().getParcelableExtra("img");
            binding.img.setImageURI(uri);
        }

        String username = getIntent().getStringExtra("name");
        binding.collapsingToolbar.setTitle(username);
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setTitle(username);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Detail.this, Activity_Home.class);
                startActivity(intent);
            }
        });

        // đổi kích thước chữ khi nhận dạng và màu sắc từ tập style.xml
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedTitleStyle);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedTitleStyle);

        processCopy();
        //Mở CSDL lên để dùng
        database = openOrCreateDatabase("Database.db",MODE_PRIVATE, null);
        // Tạo ListView
        lv = findViewById(R.id.lv_ChiTiet);
        mylist = new ArrayList<>();
        myadapter = new ArrayAdapter(Activity_Detail.this, android.R.layout.simple_list_item_1, mylist);
        lv.setAdapter(myadapter);
        // Truy vấn CSDL và cập nhật hiển thị lên Listview
        Cursor c = database.query("ChiTiet", null,null,null,null,null,null);
        c.moveToFirst();
        String data ="";
        while (c.isAfterLast() == false)
        {
            data =   c.getString(2)+"\n______________________________________\n\n"
                    +c.getString(3)+"\n______________________________________\n\n"
                    +c.getString(4)+"\n______________________________________\n\n"
                    +c.getString(5)+"\n______________________________________\n\n"
                    +c.getString(6)+"\n______________________________________\n\n"
                    +c.getString(7)+"\n______________________________________\n\n"
                    +c.getString(8)+"\n______________________________________\n\n";
            // so sanh chuoi
            if(username.equals(c.getString(1))) {
            mylist.add(data);
            }
            c.moveToNext();
        }
        c.close();
        myadapter.notifyDataSetChanged();

    }


    private void processCopy() {
        //private app
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists())
        {
            try{CopyDataBaseFromAsset();
                Toast.makeText(this, "Copying sucess from Assets folder", Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getDatabasePath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
    }

    public void CopyDataBaseFromAsset() {
        // TODO Auto-generated method stub
        try {
            InputStream myInput;
            myInput = getAssets().open(DATABASE_NAME);
            // Path to the just created empty db
            String outFileName = getDatabasePath();
            // if the path doesn't exist first, create it
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();
            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);
            // transfer bytes from the inputfile to the outputfile
            // Truyền bytes dữ liệu từ input đến output
            int size = myInput.available();
            byte[] buffer = new byte[size];
            myInput.read(buffer);
            myOutput.write(buffer);
            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}