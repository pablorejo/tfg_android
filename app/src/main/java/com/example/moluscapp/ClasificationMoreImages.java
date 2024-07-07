package com.example.moluscapp;

import android.hardware.camera2.CameraExtensionSession;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moluscapp.data.ImageResponse;
import com.example.moluscapp.data.ResponseImage;
import com.example.moluscapp.data.ResponseMoreImages;
import com.example.moluscapp.data.TaxonomicRoute;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ClasificationMoreImages extends AppCompatActivity {
    ImageView image;

    ResponseMoreImages responseMoreImages;
    int indexResponseImage = 0;

    Button btnSiguiente,btnAnterior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clasification_more_images);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.image = findViewById(R.id.image_recive);

        responseMoreImages = loadImageResponseFromFile();
        setImageIndex(indexResponseImage);

        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(view -> {
            indexResponseImage ++;
            if (indexResponseImage == responseMoreImages.responseImages.size()){
                indexResponseImage = 0;
            }
            setImageIndex(indexResponseImage);
        });

        btnAnterior = findViewById(R.id.btnAnterior);
        btnAnterior.setOnClickListener(view -> {
            indexResponseImage --;
            if (indexResponseImage < 0){
                indexResponseImage = responseMoreImages.responseImages.size()-1;
            }
            setImageIndex(indexResponseImage);
        });
    }

    private ResponseMoreImages loadImageResponseFromFile() {
        ResponseMoreImages imageResponse = null;
        try {
            FileInputStream fileInputStream = openFileInput("imageResponseFile");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            imageResponse = (ResponseMoreImages) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return imageResponse;
    }

    private void setImageIndex(int index) {
        ImageResponse imageResponse = responseMoreImages.responseImages.get(index).image;

        // Establecer la imagen
        image.setImageBitmap(imageResponse.getImagesBitmaps().get(index));

        // Función auxiliar para establecer el texto del TextView si el valor no es null
        setTextIfNotNull(R.id.clase, imageResponse.taxonomic_rank_top1.clase);
        setConfIfNotNull(R.id.confianza_clase, imageResponse.taxonomic_rank_top1.conf_clase);

        setTextIfNotNull(R.id.orden, imageResponse.taxonomic_rank_top1.orden);
        setConfIfNotNull(R.id.confianza_orden, imageResponse.taxonomic_rank_top1.conf_orden);

        setTextIfNotNull(R.id.familia, imageResponse.taxonomic_rank_top1.familia);
        setConfIfNotNull(R.id.confianza_familia, imageResponse.taxonomic_rank_top1.conf_familia);

        setTextIfNotNull(R.id.genero, imageResponse.taxonomic_rank_top1.genero);
        setConfIfNotNull(R.id.confianza_genero, imageResponse.taxonomic_rank_top1.conf_genero);

        setTextIfNotNull(R.id.especie, imageResponse.taxonomic_rank_top1.especie);
        setConfIfNotNull(R.id.confianza_especie, imageResponse.taxonomic_rank_top1.conf_especie);

        setTextIfNotNull(R.id.especie2, imageResponse.taxonomic_rank_top2.especie);
        setConfIfNotNull(R.id.confianza_especie2, imageResponse.taxonomic_rank_top2.conf_especie);

        setTextIfNotNull(R.id.especie3, imageResponse.taxonomic_rank_top3.especie);
        setConfIfNotNull(R.id.confianza_especie3, imageResponse.taxonomic_rank_top3.conf_especie);

        setTextIfNotNull(R.id.especie4, imageResponse.taxonomic_rank_top4.especie);
        setConfIfNotNull(R.id.confianza_especie4, imageResponse.taxonomic_rank_top4.conf_especie);

        setTextIfNotNull(R.id.especie5, imageResponse.taxonomic_rank_top5.especie);
        setConfIfNotNull(R.id.confianza_especie5, imageResponse.taxonomic_rank_top5.conf_especie);
    }

    // Función auxiliar para establecer el texto del TextView si el valor no es null
    private void setTextIfNotNull(int textViewId, String text) {
        if (text != null) {
            TextView textView = findViewById(textViewId);
            textView.setText(text);
        }
    }

    private void setConfIfNotNull(int textViewId, Float confInt) {
        if (confInt != null){
            TextView textView = findViewById(textViewId);
            textView.setText(String.format("%.2f", confInt));
        }
    }
}