package com.example.moluscapp;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.moluscapp.data.ImageResponse;
import com.example.moluscapp.data.ResponseImage;
import com.example.moluscapp.data.ResponseMoreImages;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
public class Server {
    String DOMINIO = "192.168.33.25/user";
    String URL = "http://" + DOMINIO;

    Gson gson = new Gson();


    // Método para convertir un Bitmap a una cadena Base64
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Comprime el Bitmap a formato JPEG y lo escribe en el ByteArrayOutputStream
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        // Obtiene los bytes del ByteArrayOutputStream
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        // Convierte los bytes a una cadena Base64
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Método para clasificar imágenes
    public void clasificarImagenes(ArrayList<Bitmap> imagenes, ServerCallback callback) {
        String url = this.URL + "/taxonomic_routes";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ResponseImage imageResponse = gson.fromJson(response, ResponseImage.class);
                callback.onResponse(imageResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Unknown error";
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    try {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(responseBody);
                        errorMessage = jsonObject.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callback.onResponse(errorMessage);
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONArray jsonArray = new JSONArray();
                for (Bitmap bitmap : imagenes) {
                    String base64Image = bitmapToBase64(bitmap);
                    jsonArray.put(base64Image);
                }
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("images", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Añadir headers si es necesario
                return headers;
            }
        };

        // Configurar el timeout para la solicitud
        int socketTimeout = 30000; // 30 segundos (30000 milisegundos)
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        RequestQueue requestQueue = MainActivity.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    // Método para clasificar imágenes
    public void clasificarImagen(Bitmap imagen, ServerCallback callback) {
        String url = this.URL + "/taxonomic_route";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Type listType = new TypeToken<ArrayList<ImageResponse>>(){}.getType();
                ResponseMoreImages responseMoreImages = gson.fromJson(response, ResponseMoreImages.class);
                callback.onResponse(responseMoreImages);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Unknown error";
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    try {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(responseBody);
                        errorMessage = jsonObject.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callback.onResponse(errorMessage);
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject jsonBody = new JSONObject();
                String base64Image = bitmapToBase64(imagen);
                try {
                    jsonBody.put("image", base64Image);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Añadir headers si es necesario
                return headers;
            }
        };

        // Configurar el timeout para la solicitud
        int socketTimeout = 30000; // 30 segundos (30000 milisegundos)
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        RequestQueue requestQueue = MainActivity.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }
}
