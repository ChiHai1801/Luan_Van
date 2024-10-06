package com.example.luanvan2324;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luanvan2324.databinding.ActivityHomeBinding;
import com.example.luanvan2324.ml.Model1012Jupyter;
import com.google.android.material.navigation.NavigationView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class Activity_Home extends AppCompatActivity {

    ActivityHomeBinding binding;
    Uri uri;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ListView listView;
    ArrayList<Items_Toolbar> arrayList;
    MyAdapter_Toolbar adapterToolbar;
    private Uri imageUri;
    String username;
    int imageSize = 256;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        anhXa();
        actionToolBar();
        actionMenu();
        myCamera();

        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home) {
                Intent intent = new Intent(Activity_Home.this, Activity_Home.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.check) {
                Intent intent = new Intent(Activity_Home.this, Activity_Detail.class);
                if (uri != null) {
                    intent.putExtra("img", uri);
                } else if (imageUri != null) {
                    intent.putExtra("imageUri", imageUri.toString());
                }
                intent.putExtra("name", username);
                startActivity(intent);
            }
            return true;
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        binding.chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDetil();
            }
        });

    }



    private void myCamera() {
        activityResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            private boolean fales;

            @Override
            public void onActivityResult(ActivityResult result ) {
                Bundle extras = result.getData().getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                WeakReference<Bitmap> result1 = new WeakReference<>(Bitmap.createScaledBitmap(imageBitmap,
                        imageBitmap.getHeight(), imageBitmap.getWidth(), fales).copy(
                        Bitmap.Config.RGB_565, true));

                Bitmap bm = result1.get();
                imageUri = saveImage(bm, Activity_Home.this);
                binding.ivMain.setImageURI(imageUri);

                int dimension = Math.min(imageBitmap.getWidth(), imageBitmap.getHeight());
                imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, dimension, dimension);
                binding.ivMain.setImageBitmap(imageBitmap);

                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, imageSize, imageSize, false);
                classifyImage(imageBitmap);
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
    }

    public void classifyImage(Bitmap image){
        try {
            Model1012Jupyter model = Model1012Jupyter.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for(int i = 0; i < imageSize; i ++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model1012Jupyter.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }


            String[] classes = {"Acalypha Lanceolata (Tai Tượng Thon)", "Acanthus Ebracteatus Vahl (Ô Rô)",
                                "Ageratum Conyzoides (Cỏ Hôi)", "Aloe Vera (Lô Hội)", "Alpinia Galanga (Riềng nếp)",
                                "Amaranthus Tricolor (Dền Canh)", "Amaranthus Viridis (Dền cơm)",
                                "Andrographis Paniculata (Xuyên Tâm Liên)", "Angelica Dahurica (Bạch Chỉ)",
                                "Annona Squamosa (Na)", "Argemone Mexicana (Gai Cua)", "Artemisia Vulgaris (Ngải Cứu)",
                                "Artocarpus Heterophyllus Lam (Mít)", "Azadirachta Indica Juss (Sầu Đâu)",
                                "Basella Alba (Mồng Tơi)", "Brassica Juncea (Cải Xanh)", "Không thấy thực vật trong ảnh",
                                "Cardiospermum Halicacabum (Tam Phỏng)", "Carica Papaya (Đu Đủ)",
                                "Carissa Carandas (Xirô)", "Celastrus Orbiculatus Thunb (Dây Gối Tròn)",
                                "Không thấy thực vật trong ảnh",
                                "Citrus Aurantifolia (Chanh Ta)", "Cleome Chelidonii (Màn Ri Tía)",
                                "Cleome Gynandra (Màn Màn)", "Clitoria Ternatea (Đậu Biếc)",
                                "Colocasia Antiquorum Schott (Khoai Sọ)", "Commelina Benghalensis (Thài Lài Lông)",
                                "Corchorus Olitorius (Đay Quả Dài)", "Coriandrum Sativum (Rau Mùi)",
                                "Crinum Latifolium (Trinh Nữ Hoàng Cung)", "Không thấy thực vật trong ảnh",
                                "Epiphyllum Oxypetalum (Lá Quỳnh)",
                                "Euphorbia Hirta (Vú Sữa Đất)", "Không thấy thực vật trong ảnh",
                                "Không thấy thực vật trong ảnh", "Ficus Auriculata Lour (Vả)",
                                "Ficus Religiosa (Đề)", "Graptophyllum Pictum (Lá Ngọc Diệp)",
                                "Hibiscus Rosa Sinensis (Râm Bụt)", "Ipomoea Aquatica (Rau Muống)",
                                "Ixora Coccinea (Bông Trang Đỏ)", "Jasminum Sambac (Nhài)",
                                "Jatropha Podagrica (Sen Lục Bình)", "Không thấy thực vật trong ảnh",
                                "Không thấy thực vật trong ảnh", "Không thấy thực vật trong ảnh",
                                "Lycopersicon Esculentum Mill (Cà Chua)",
                                "Mangifera Indica (Xoài)", "Manilkara Zapota (Hồng Xiêm)",
                                "Mentha Spicata (Bạc Hà Lục)", "Morinda Citrifolia (Nhàu)",
                                "Muntingia Calabura (Mật Sâm)", "Nerium Oleander (Trúc Đào)",
                                "Ocimum Basilicum (Húng Quế)", "Ocimum Sanctum (Hương Nhu Tía)",
                                "Passiflora Foetida (Lạc Tiên)", "Phaseolus Vulgaris (Đậu Tây)",
                                "Phyllanthus Amarus Schum (Chó Đẻ Răng Cưa)", "Physalis Angulata (Tầm Bóp)",
                                "Piper Betle (Trầu Không)", "Piper Nigrum (Tiêu)",
                                "Piper Sarmentosum (Lá Lốt)", "Plectranthus Amboinicus (Húng Chanh)",
                                "Psidium Guajava (Ổi)", "Punica Granatum (Lựu)", "Không thấy thực vật trong ảnh",
                                "Santalum Album (Đàn Hương Trắng)", "Sida Acuta Burm (Chổi Đực)",
                                "Sida Cordifolia (Ké Đồng Tiền)", "Stachytarpheta Jamaicensis (Đuôi Chuột)",
                                "Struchium Sparganophorum (Cỏ Lá Xoài)", "Syzygium Cumini (Vối Rừng)",
                                "Syzygium Jambos (Roi)", "Tamarindus Indica (Me)",
                                "Tridax Procumbens (Cúc Mui)", "Không thấy thực vật trong ảnh",
                                "Vernonia Cinerea (Bạch Đầu Ông)", "Không thấy thực vật trong ảnh"
            };

            username = classes[maxPos].toString();
            binding.tenKq.setText(username);
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.ivMain.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);

                uri = data.getData();
                binding.ivMain.setImageURI(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout imagesLayout = dialog.findViewById(R.id.layoutImages);
        LinearLayout cameraLayout = dialog.findViewById(R.id.layoutCamera);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        imagesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });

        cameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(takePictureIntent);

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void showBottomDetil() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomdetail);

        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerview);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this ));

        List<Items_Detail> items = new ArrayList<Items_Detail>();

        items.add(new Items_Detail("1. Hãy chắc chắn rằng bạn đang tập trung vào thực vật."));
        items.add(new Items_Detail("2. Sử dụng đèn flash nếu không đủ ánh sáng."));
        items.add(new Items_Detail("3. Nếu bạn đang xác định loại cây từ lá, hãy đảm bảo thêm nhiều lá vào ảnh."));
        items.add(new Items_Detail("4. Tập trung vào các đối tượng riêng lẻ."));
        items.add(new Items_Detail("5. Tránh các vật thể không phải thực vật và tập trung vào một loại cây duy nhất."));
        recyclerView.setAdapter(new MyAdapter_Detail(this, items));

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void actionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void anhXa() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        listView = (ListView) findViewById(R.id.ltView);

    }

    private void actionMenu() {
        arrayList = new ArrayList<>();
        arrayList.add(new Items_Toolbar("Home", R.drawable.ic_home));
        arrayList.add(new Items_Toolbar("Check Images", R.drawable.ic_baseline_whatshot_24));
        arrayList.add(new Items_Toolbar("Upload Images", R.drawable.image_24px));
        arrayList.add(new Items_Toolbar("My Camera", R.drawable.photo_camera_24px));

        adapterToolbar = new MyAdapter_Toolbar(this, R.layout.items_toolbar, arrayList);
        listView.setAdapter(adapterToolbar);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click here
                switch (position) {
                    case 0:
                        Intent intent = new Intent(Activity_Home.this, Activity_Home.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent_img = new Intent(Activity_Home.this, Activity_Detail.class);
                        if (uri != null) {
                            intent_img.putExtra("img", uri);
                        } else if (imageUri != null) {
                            intent_img.putExtra("imageUri", imageUri.toString());
                        }
                        intent_img.putExtra("name", username);
                        startActivity(intent_img);
                        break;
                    case 2:
                        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(cameraIntent, 1);
                        break;
                    case 3:
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        activityResultLauncher.launch(takePictureIntent);
                        break;
                }
            }
        });
    }
}