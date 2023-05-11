package com.example.qrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//import androidx.activity.result.ActivityResultLauncher;
//import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {

    String sheetID = "1kvXWZgqF1vZu60n7RWYtt9VqIQXAYDktDmCgZozO9dw";
    String apikey = "AIzaSyA1YKatN9RK7zlV-XUpgOlKNAKHh0PdJso";

    String strRegno;
    String strName;
    String strPic;
    String strh_status;
    String strt_status;

    ImageView imgView;
    Button btnScan;
    Button LogOut;
    JSONArray jsonArray;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();

        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setCameraId(0);
        scanOptions.setPrompt("Scan the BUS PASS or HOSTEL PASS");
        scanOptions.setOrientationLocked(true);
        scanOptions.setBeepEnabled(true);
        scanOptions.setCaptureActivity(CaptureActivity.class);
//        barLauncher.launch(scanOptions);
        btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(View->{
            barLauncher.launch(scanOptions);
        });
        LogOut = findViewById(R.id.btnLogOut);
        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        LogOut.setOnClickListener(View->{
            FirebaseAuth.getInstance().signOut();
            startActivity(i);
        });
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result->{
        if(result.getContents() != null)
        {
            String s = result.getContents().substring(7);


            String url = "https://sheets.googleapis.com/v4/spreadsheets/"+sheetID+"/values/StudentList?key="+apikey;

            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        jsonArray = response.getJSONArray("values");

                    } catch (JSONException e) {
                    }

                    IntStream.range(1, jsonArray.length())
                            .forEach(i -> {
                                try {
                                    JSONArray json = jsonArray.getJSONArray(i);
                                    strRegno = json.getString(1);
                                    if(Objects.equals(strRegno, s)){
                                        strName = json.getString(0);
                                        strPic = json.getString(2);
                                        strh_status = json.getString(3);
                                        strt_status = json.getString(4);

                                        TextView name = findViewById(R.id.textView2);
                                        name.setText(strName);
                                        TextView regno = findViewById(R.id.textView3);
                                        regno.setText(strRegno);
                                        imgView = findViewById(R.id.imageView3);
                                        Glide.with(getApplicationContext()).load(strPic).into(imgView);
                                        TextView hostel = findViewById(R.id.HostelStatus);
                                        hostel.setText(strh_status);
                                        TextView trans = findViewById(R.id.TransportStatus);
                                        trans.setText(strt_status);
                                    }

                                } catch (Exception e) {
                                }
                            });
                    if(strName==null){
                        
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(jsonObjectRequest);
        }


    });
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        super.onBackPressed();
    }


}