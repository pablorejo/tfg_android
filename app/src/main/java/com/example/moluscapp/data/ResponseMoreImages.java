package com.example.moluscapp.data;

import java.io.Serializable;
import java.util.ArrayList;

public class ResponseMoreImages implements Serializable {
    public ArrayList<ResponseImage> responseImages;
    public String status;
    public String message;
}
