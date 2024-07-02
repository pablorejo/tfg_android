package com.example.moluscapp;

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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ClasificationMoreImages extends AppCompatActivity {
    TextView clase,orden,familia,genero,especie1,especie2,especie3,especie4,especie5;
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

    private void setImageIndex(int index){
        ImageResponse imageResponse = responseMoreImages.responseImages.get(index).image;
        image.setImageBitmap(imageResponse.getImagesBitmaps().get(index));

        // Ruta taxonómica
        clase = findViewById(R.id.clase);
        clase.setText(imageResponse.taxonomic_rank_top1.clase);

        orden = findViewById(R.id.orden);
        orden.setText(imageResponse.taxonomic_rank_top1.orden);

        familia = findViewById(R.id.familia);
        familia.setText(imageResponse.taxonomic_rank_top1.familia);

        genero = findViewById(R.id.genero);
        genero.setText(imageResponse.taxonomic_rank_top1.genero);

        especie1 = findViewById(R.id.especie);
        especie1.setText(imageResponse.taxonomic_rank_top1.especie);

        especie2 = findViewById(R.id.especie2);
        especie2.setText(imageResponse.taxonomic_rank_top2.especie);

        especie3 = findViewById(R.id.especie3);
        especie3.setText(imageResponse.taxonomic_rank_top3.especie);

        especie4 = findViewById(R.id.especie4);
        especie4.setText(imageResponse.taxonomic_rank_top4.especie);

        especie5 = findViewById(R.id.especie5);
        especie5.setText(imageResponse.taxonomic_rank_top5.especie);
    }
}