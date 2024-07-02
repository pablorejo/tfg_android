package com.example.moluscapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.moluscapp.data.ResponseImage;
import com.example.moluscapp.data.ResponseMoreImages;

import android.Manifest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private boolean modoGaleria = false;
    private boolean modoCamara = false;
    private Button btnFoto, btnGaleria, btnConfirmar, btnNuevo;
    private static MainActivity instance;
    private RequestQueue requestQueue;
    private ArrayList<Imagen> imagenes = new ArrayList<Imagen>();

    private LinearLayout linearLayout;

    private Server server = new Server();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        linearLayout = findViewById(R.id.linear_layout);
        linearLayout.removeAllViews();

        btnFoto = findViewById(R.id.btnFoto);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        btnGaleria= findViewById(R.id.btnGaleria);
        btnNuevo = findViewById(R.id.btnNuevo);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }


        btnNuevo.setOnClickListener(v -> {
            this.imagenes = new ArrayList<Imagen>();
            updateImages();
        });
        btnFoto.setOnClickListener(view -> {
            modoCamara = true;
            dispatchTakePictureIntent();
        });

        btnConfirmar.setOnClickListener(v -> {
            confirmar();
        });

        btnGaleria.setOnClickListener(v -> {
            modoGaleria = true;
            openGalleryForMultipleImages();
        });
    }

    public static synchronized MainActivity getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }
    private Uri photoURI;
    private String currentPhotoPath;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Crear el archivo donde se guardará la foto
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error al crear el archivo
                Log.e("CameraError", "Error al crear archivo de imagen", ex);
            }
            // Continuar solo si el archivo fue creado correctamente
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Crear un nombre de archivo único para la imagen
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Guardar un archivo: el camino para usarlo con ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void updateImages(){
        linearLayout.removeAllViews();
        if (imagenes.size() == 1){
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(imagenes.get(0).imagen);
            imageView.setMaxHeight(500);
            linearLayout.addView(imageView);
        } else if (imagenes.size() > 1) {
            for (int k = 0; k<imagenes.size(); k++){
                linearLayout.addView(crearImageItem(imagenes.get(k)));
            }
        }
    }

    private View crearImageItem(Imagen imagen){
        View newLayout = LayoutInflater.from(this).inflate(R.layout.image_item, linearLayout, false);
        ImageView imageView = newLayout.findViewById(R.id.imageView);
        imageView.setImageBitmap(imagen.imagen);
        CheckBox checkBox = newLayout.findViewById(R.id.checkBox);
        checkBox.setOnClickListener(v -> {
            imagen.checked = checkBox.isChecked();
        });

        return  newLayout;
    }

    private void confirmar(){
        ArrayList<Bitmap> imagenes_cheked = new ArrayList<>();
        for (Imagen imagen: imagenes){
            if (imagen.checked){
                imagenes_cheked.add(imagen.imagen);
            }
        }

        if (imagenes_cheked.size() > 1) {
            Toast.makeText(MainActivity.this, "Clasificando Imágenes", Toast.LENGTH_SHORT).show();
            server.clasificarImagenes(imagenes_cheked, new ServerCallback() {
                @Override
                public void onResponse(Object object) {
                    ResponseImage imageResponse = (ResponseImage) object;
                    Intent intent = new Intent(getApplicationContext(), ClasificationOneImage.class);
                    saveImageResponseToFile(imageResponse);
                    startActivity(intent);
                }
            });
        }else if (imagenes_cheked.size() == 1){
            Toast.makeText(MainActivity.this, "Clasificando Imágenes", Toast.LENGTH_SHORT).show();
            server.clasificarImagen(imagenes_cheked.get(0), new ServerCallback() {
                @Override
                public void onResponse(Object object) {
                    ResponseMoreImages responseMoreImages = (ResponseMoreImages) object;
                    Intent intent = new Intent(getApplicationContext(), ClasificationMoreImages.class);
                    saveImageResponseToFile(responseMoreImages);
                    startActivity(intent);
                }
            });
        }
    }
    private static final int PICK_IMAGE_MULTIPLE = 1;

    private void openGalleryForMultipleImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  // O puedes usar ACTION_PICK
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE_MULTIPLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (modoCamara){
            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            if (imageBitmap != null){
                Imagen imagen = new Imagen(imageBitmap, true);
                this.imagenes.add(imagen);
                updateImages();
            }
            modoCamara = false;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null ) {
            if (modoGaleria){
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        // Procesar cada URI como se necesite
                        loadImage(imageUri);
                    }
                } else if (data.getData() != null) {
                    Uri singleImageUri = data.getData();
                    // Procesar un solo URI si solo se seleccionó una imagen
                    loadImage(singleImageUri);
                }
                modoGaleria = false;
            }
        }

    }

    private void loadImage(Uri imageUri) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        // Utilizar Glide o Picasso para cargar la imagen
        Glide.with(this).load(imageUri).into(imageView);

        Glide.with(this)
            .asBitmap() // Indica a Glide que cargue la imagen como Bitmap.
            .load(imageUri)
            .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    // Manejar la falla de carga
                    Log.e("GlideError", "Failed to load image");
                }

                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                    // Aquí puedes usar el Bitmap, por ejemplo, guardarlo o manipularlo.
                    Imagen imagen = new Imagen(resource,true);
                    imagenes.add(imagen);
                    updateImages();
                }
            });
    }


    private class Imagen{
        Bitmap imagen;
        Boolean checked;

        public Imagen(Bitmap imagen, Boolean checked){
            this.imagen = imagen;
            this.checked = checked;
        }
    }

    private void saveImageResponseToFile(Object object) {
        try {
            FileOutputStream fileOutputStream = openFileOutput("imageResponseFile", MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}