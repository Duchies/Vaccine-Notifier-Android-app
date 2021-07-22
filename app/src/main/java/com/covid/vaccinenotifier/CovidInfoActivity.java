package com.covid.vaccinenotifier;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CovidInfoActivity extends AppCompatActivity {
    private RequestQueue requestQueue;

    private TextView infected, acive, recoverd, death, lastUpdateTime, caseTested;

    final String TAG = "MainActivity";

 //   ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.covid_info);
       // progressBar = findViewById(R.id.progressBar);
        infected = findViewById(R.id.text_view_infected);
        acive = findViewById(R.id.text_view_Active);
        recoverd = findViewById(R.id.text_view_recoverd);
        death = findViewById(R.id.text_view_death);
        lastUpdateTime = findViewById(R.id.textView2);
  //      caseTested = findViewById(R.id.text_view_sample_tested);

        requestQueue = Volley.newRequestQueue(this);


        String url = "https://api.covid19india.org/data.json";


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONArray jsonArray = response.getJSONArray("statewise");

                            Loop:
                            for (int i = 0; i < 1; i++) {
                                JSONObject employee = jsonArray.getJSONObject(i);


                                String _lastupdatedtime = employee.getString("lastupdatedtime").trim();
                                String _death = employee.getString("deaths").trim();
                                String _recovered = employee.getString("recovered").trim();
                                String _active = employee.getString("active").trim();
                                String _infected = employee.getString("confirmed").trim();


                                infected.append(_infected);
                                acive.append(_active);
                                recoverd.append(_recovered);
                                death.append(_death);

                                String temp_time = formatDate(_lastupdatedtime, 0);

                                lastUpdateTime.append("Updated on "+temp_time);


                            }
//                            JSONArray jsonArray2 = response.getJSONArray("tested");
//                            for (int i = jsonArray2.length() - 1; i > jsonArray2.length() - 2; i--) {
//                                JSONObject employee2 = jsonArray2.getJSONObject(i);
//
//                                String sampletest = employee2.getString("totalsamplestested".trim());
//                                caseTested.append(sampletest);
//                            }

                       //     progressBar.setVisibility(View.INVISIBLE);


                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "data load problem");

                Toast.makeText(getApplicationContext(), "Network Issue", Toast.LENGTH_SHORT).show();

            }
        });

        requestQueue.add(request);
    }

    private String formatDate(String date, int testCase) {

        Date mDate = null;
        String dateFormat;
        try {
            mDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).parse(date);
            if (testCase == 0) {
                dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(mDate);
                return dateFormat;
            } else if (testCase == 1) {
                dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US).format(mDate);
                return dateFormat;
            } else if (testCase == 2) {
                dateFormat = new SimpleDateFormat("hh:mm a", Locale.US).format(mDate);
                return dateFormat;
            } else {
                Log.d(TAG + "error", "Wrong input! Choose from 0 to 2");
                return "Error";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }


//    public void goToNotificationActivityMethod(View view) {
//        Intent send = new Intent(MainActivity.this, vacination_notification.class);
//        startActivity(send);
//    }
}