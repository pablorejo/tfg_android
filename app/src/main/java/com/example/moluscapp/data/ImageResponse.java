package com.example.moluscapp.data;


import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;


import androidx.annotation.Nullable;

import java.util.ArrayList;
public class ImageResponse implements Serializable {

    public ArrayList<String> images; // This can be a String or an ArrayList<String>
    public TaxonomicRoute taxonomic_rank_top1;
    public TaxonomicRoute taxonomic_rank_top2;
    public TaxonomicRoute taxonomic_rank_top3;
    public TaxonomicRoute taxonomic_rank_top4;
    public TaxonomicRoute taxonomic_rank_top5;

    public ImageResponse(ArrayList<String> images, TaxonomicRoute taxonomic_rank_top1, TaxonomicRoute taxonomic_rank_top2, TaxonomicRoute taxonomic_rank_top3, TaxonomicRoute taxonomic_rank_top4, TaxonomicRoute taxonomic_rank_top5) {
        this.images = images;
        this.taxonomic_rank_top1 = taxonomic_rank_top1;
        this.taxonomic_rank_top2 = taxonomic_rank_top2;
        this.taxonomic_rank_top3 = taxonomic_rank_top3;
        this.taxonomic_rank_top4 = taxonomic_rank_top4;
        this.taxonomic_rank_top5 = taxonomic_rank_top5;
    }


    public ArrayList<Bitmap> getImagesBitmaps() {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        for (String base64Image : images) {
            try {
                // Clean the base64 string
                base64Image = base64Image.trim();

                // Decode the base64 string
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                bitmaps.add(decodedByte);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                // Optionally, add a placeholder image or handle the error as needed
            }
        }

        return bitmaps;
    }

    public TaxonomicRoute getTaxonomicRoutePerIndex(int indexTop){
        switch (indexTop){

            case 1:
                return this.taxonomic_rank_top1;
            case 2:
                return this.taxonomic_rank_top2;
            case 3:
                return this.taxonomic_rank_top3;
            case 4:
                return this.taxonomic_rank_top4;
            case 5:
                return this.taxonomic_rank_top5;
            default:
                return null;
        }
    }

    public Integer getLastIndex() {
        if (this.taxonomic_rank_top5 != null) return 5;
        if (this.taxonomic_rank_top4 != null) return 4;
        if (this.taxonomic_rank_top3 != null) return 3;
        if (this.taxonomic_rank_top2 != null) return 2;
        if (this.taxonomic_rank_top1 != null) return 1;
        return 0;
    }
}
