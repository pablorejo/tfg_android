package com.example.moluscapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.moluscapp.data.ImageResponse;
import com.example.moluscapp.data.ResponseImage;
import com.example.moluscapp.data.ResponseMoreImages;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class ClasificationOneImage extends AppCompatActivity {
    ImageView image;
    TextView clase,orden,familia,genero,especie,topName;
    LinearLayout layoutBtnImagenes;
    Button btnSiguiente,btnAnterior;
    Button btnSiguienteTop,btnAnteriorTop;

    ArrayList<Bitmap> bitMapsImages;
    ImageResponse imageResponse;
    int indexImage = 0;
    int indexTop = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clasificacion);

        this.image = findViewById(R.id.image_recive);
        ResponseImage responseImage = loadImageResponseFromFile();

        layoutBtnImagenes = findViewById(R.id.layoutBtnImagenes);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(view -> {
            indexImage ++;
            if (indexImage == bitMapsImages.size()){
                indexImage = 0;
            }
            this.image.setImageBitmap(bitMapsImages.get(indexImage));
        });

        btnAnterior = findViewById(R.id.btnAnterior);
        btnAnterior.setOnClickListener(view -> {
            indexImage --;
            if (indexImage < 0){
                indexImage = bitMapsImages.size()-1;
            }
            this.image.setImageBitmap(bitMapsImages.get(indexImage));
        });

        btnAnteriorTop = findViewById(R.id.btnAnteriorTop);
        btnAnteriorTop.setOnClickListener(view -> {
            indexTop --;
            if (indexTop < 0){
                indexTop = 5;
            }
            changeTop(indexTop);
        });

        btnSiguienteTop = findViewById(R.id.btnSiguienteTop);
        btnSiguienteTop.setOnClickListener(view -> {
            indexTop ++;
            if (indexTop > 5){
                indexTop = 1;
            }
            changeTop(indexTop);
        });

        imageResponse = responseImage.image;
        bitMapsImages = imageResponse.getImagesBitmaps();

        this.image.setImageBitmap(bitMapsImages.get(0));
        if(imageResponse.getImagesBitmaps().size() <= 1){
            layoutBtnImagenes.setVisibility(View.GONE);
        }

        topName = findViewById(R.id.topName);
        clase = findViewById(R.id.clase);
        orden = findViewById(R.id.orden);
        familia = findViewById(R.id.familia);
        genero = findViewById(R.id.genero);
        especie = findViewById(R.id.especie);

        changeTop(indexTop);




    }

    private ResponseImage loadImageResponseFromFile() {
        ResponseImage imageResponse = null;
        try {
            FileInputStream fileInputStream = openFileInput("imageResponseFile");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            imageResponse = (ResponseImage) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return imageResponse;
    }

    private void changeTop(int indexTop){
        topName.setText("TOP  " + String.valueOf(indexTop));

        // Ruta taxonómica
        clase.setText(imageResponse.getTaxonomicRoutePerIndex(indexTop).clase);

        orden.setText(imageResponse.getTaxonomicRoutePerIndex(indexTop).orden);

        familia.setText(imageResponse.getTaxonomicRoutePerIndex(indexTop).familia);

        genero.setText(imageResponse.getTaxonomicRoutePerIndex(indexTop).genero);

        especie.setText(imageResponse.getTaxonomicRoutePerIndex(indexTop).especie);
    }
}