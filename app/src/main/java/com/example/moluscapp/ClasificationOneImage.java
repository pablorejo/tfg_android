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
import com.example.moluscapp.data.TaxonomicRoute;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class ClasificationOneImage extends AppCompatActivity {
    ImageView image;
    TextView clase,orden,familia,genero,especie,topName;
    TextView confianza_clase,confianza_orden,confianza_familia,confianza_genero,confianza_especie;

    LinearLayout layoutBtnImagenes;
    Button btnSiguiente,btnAnterior;
    Button btnSiguienteTop,btnAnteriorTop;

    ArrayList<Bitmap> bitMapsImages;
    ImageResponse imageResponse;
    int indexImage = 0;
    int indexTop = 1;
    int lastIndex;

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
                indexTop = lastIndex;
            }
            changeTop(indexTop);
        });

        btnSiguienteTop = findViewById(R.id.btnSiguienteTop);
        btnSiguienteTop.setOnClickListener(view -> {
            indexTop ++;
            if (indexTop > lastIndex){
                indexTop = 1;
            }
            changeTop(indexTop);
        });

        imageResponse = responseImage.image;
        lastIndex = imageResponse.getLastIndex();
        bitMapsImages = imageResponse.getImagesBitmaps();

        this.image.setImageBitmap(bitMapsImages.get(0));
        if(imageResponse.getImagesBitmaps().size() <= 1){
            layoutBtnImagenes.setVisibility(View.GONE);
        }

        setButons();
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

    private void changeTop(int indexTop) {
        // Establecer el nombre del top
        topName.setText("TOP " + indexTop);

        // Obtener la ruta taxonómica para el índice dado
        TaxonomicRoute route = imageResponse.getTaxonomicRoutePerIndex(indexTop);

        // Establecer los valores en los TextViews
        setTextIfNotNull(clase, route.clase);
        setConfIfNotNull(confianza_clase,route.conf_clase);

        setTextIfNotNull(orden, route.orden);
        setConfIfNotNull(confianza_orden,route.conf_orden);

        setTextIfNotNull(familia, route.familia);
        setConfIfNotNull(confianza_familia,route.conf_familia);

        setTextIfNotNull(genero, route.genero);
        setConfIfNotNull(confianza_genero,route.conf_genero);

        setTextIfNotNull(especie, route.especie);
        setConfIfNotNull(confianza_especie,route.conf_especie);
    }

    private void setButons(){
        topName = findViewById(R.id.topName);
        clase = findViewById(R.id.clase);
        orden = findViewById(R.id.orden);
        familia = findViewById(R.id.familia);
        genero = findViewById(R.id.genero);
        especie = findViewById(R.id.especie);

        confianza_clase = findViewById(R.id.confianza_clase);
        confianza_orden = findViewById(R.id.confianza_orden);
        confianza_familia = findViewById(R.id.confianza_familia);
        confianza_genero = findViewById(R.id.confianza_genero);
        confianza_especie = findViewById(R.id.confianza_especie);
    }

    // Función auxiliar para establecer el texto del TextView si el valor no es null
    private void setTextIfNotNull(TextView textView, String text) {
        if (text != null) {
            textView.setText(text);
        }else{
            textView.setText("NoData");
        }
    }
    private void setConfIfNotNull(TextView textView, Float conf){
        if (conf != null){
            textView.setText(String.format("%.2f", conf));
        }else{
            textView.setText("");
        }
    }
}