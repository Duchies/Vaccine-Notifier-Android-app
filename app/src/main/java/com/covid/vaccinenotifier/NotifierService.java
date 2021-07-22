package com.covid.vaccinenotifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.covid.vaccinenotifier.data.model.UserParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NotifierService extends Service {

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    RequestQueue requestQueue;
    public static int SLEEPTIME = 1000*60*10;
    public static final String NOTIFICATION_CHANNEL_ID = "vaccine.slot.available.notification";
    public static final String channelName = "vaccineNotificationChannel";
    public static final int NOTIF_ID = 111001100;
    private Context ctx;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            UserParams userParams = (UserParams) msg.obj;

            //Poll server every 10 mins and print result in logs
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String date = sdf.format(new Date());

            List<String> availSlots = new ArrayList<>();
            int ageLimit = 45;
            switch(userParams.getSelectedAge()) {
                case "18+":
                    ageLimit = 18;
                    break;
                case "45+":
                    ageLimit = 45;
                    break;
                case "any":
                    ageLimit = 60;

                default:
                    break;
            }

            while(availSlots.size() < 1) {

                Log.println(Log.INFO, "VaccineNotifierService", "No slot found polling again");

                //API to get availability by district for next 7 days
                //https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id=670&date=07-05-2021
                // https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=110086&date=03-06-2021

//
                String districtCalUrl = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=" + userParams.getSelectedPinCode() + "&date=" + date;


                //   Toast.makeText(ctx,"hgjhd"+userParams.getSelectedPinCode(), Toast.LENGTH_SHORT).show();


                int finalAgeLimit = ageLimit;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, districtCalUrl, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                //Toast.makeText(getApplicationContext(), "got district calendar resp", Toast.LENGTH_SHORT).show();
                                Log.println(Log.INFO, "VaccineNotifierService", "Got response from url: "+districtCalUrl);
                                try {
                                    JSONArray centersJson = (JSONArray) response.get("centers");
                                    for(int i=0; i<centersJson.length();i++) {
                                        JSONObject centerJson = centersJson.getJSONObject(i);
                                        String centerName = centerJson.getString("name");
                                        String centerAddr = centerJson.getString("address");
                                        String centerFeeType = centerJson.getString("fee_type");
                                        String pincode = centerJson.getString("pincode");
                                        String centerID = centerJson.getString("center_id");

                                        JSONArray sessionsJson = centerJson.getJSONArray("sessions");

                                        for(int j=0;j<sessionsJson.length();j++) {
                                            JSONObject sessionJson = sessionsJson.getJSONObject(j);
                                            int availCapacity = sessionJson.getInt("available_capacity");
                                            int minAgeLimit = sessionJson.getInt("min_age_limit");
                                            String date = sessionJson.getString("date");
                                            String vaccine = sessionJson.getString("vaccine");
                                            String slots = sessionJson.getString("slots");

                                            String selectedVaccine = userParams.getSelectedVaccineType().trim();
                                            String selectedCost = userParams.getSelectedCost().trim();

                                            boolean flag2 = false;

                                            if (selectedCost.equals("Any")) {
                                                flag2 = true;
                                            }

                                            boolean flag1 = false;
                                            if (selectedVaccine.equals("Any")) {
                                                flag1 = true;
                                            }


                                            if (availCapacity > 0 && minAgeLimit <= finalAgeLimit) {

                                                // case 1 when vaccine any and cost any
                                                // case 2 vaccine any and cost have some val
                                                // case 3 vaccine some cost any
                                                // case 4 vaccine some val and cost some val

                                                String ageLimitStringValue = String.valueOf(finalAgeLimit);

                                                if(flag1 ==true && flag2 ==true) {
                                                //    availSlots.add(centerName.toUpperCase() + ";" + " Pincode=" + pincode + ";" + " Age=" + minAgeLimit + "; Capacity=" + availCapacity + "; Date=" + date);
                                                    availSlots.add(centerID+"  "+vaccine+"  "+minAgeLimit+"  "+centerFeeType+"  "+centerName+"  "+centerAddr+"  "+availCapacity);
                                                }else if(flag1 ==true&& flag2==false){

                                                    if(selectedCost.equals(centerFeeType)){
                                                        availSlots.add(centerID+"  "+vaccine+"  "+minAgeLimit+"  "+centerFeeType+"  "+centerName+"  "+centerAddr+"  "+availCapacity);
                                                        //availSlots.add(centerName.toUpperCase() + ";" + " Pincode=" + pincode + ";" + " Age=" + minAgeLimit + "; Capacity=" + availCapacity + "; Date=" + date);
                                                    }

                                                }else if(flag2==true && flag1 == false){

                                                    if(vaccine.equals(selectedVaccine)){
                                                        availSlots.add(centerID+"  "+vaccine+"  "+minAgeLimit+"  "+centerFeeType+"  "+centerName+"  "+centerAddr+"  "+availCapacity);
                                                       // availSlots.add(centerName.toUpperCase() + ";" + " Pincode=" + pincode + ";" + " Age=" + minAgeLimit + "; Capacity=" + availCapacity + "; Date=" + date);
                                                    }

                                                }else  if(flag1== false && flag2 == false){

                                                    if(vaccine.equals(selectedVaccine) && selectedCost.equals(centerFeeType)){
                                                        availSlots.add(centerID+"  "+vaccine+"  "+minAgeLimit+"  "+centerFeeType+"  "+centerName+"  "+centerAddr+"  "+availCapacity);
                                                       // availSlots.add(centerName.toUpperCase() + ";" + " Pincode=" + pincode + ";" + " Age=" + minAgeLimit + "; Capacity=" + availCapacity + "; Date=" + date);
                                                    }

                                                }



                                                 //   availSlots.add(centerID+"$"+selectedVaccine+"$"+ageLimitStringValue+"$"+selectedCost+"$"+centerName+"$"+centerAddr+"$"+slots);

                                                Log.println(Log.INFO, "VaccineNotifierService", "############ SLOT FOUND #################");
                                                Log.println(Log.INFO, "VaccineNotifierService", centerName.toUpperCase() + ":" + centerAddr + ":" + " Age=" + minAgeLimit + "; Capacity=" + availCapacity + "; Date=" + date + "; Vaccine=" + vaccine);
                                            }
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ctx, "error", Toast.LENGTH_SHORT).show();
                                    Log.e("VaccineNotifierService", "JSON Error: " + e.toString());
                                }

                                //Notify the user of available slot
                                if (availSlots.size() > 1) {
                                    Log.println(Log.INFO, "VaccineNotifierService", "Available slot found will notify user now");
                                    Intent notificationIntent = new Intent(getApplicationContext(), VacineSlotActivity.class);
                                    notificationIntent.putExtra("availSlotsArray", availSlots.toArray());
                                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(NotifierService.this, NOTIFICATION_CHANNEL_ID);
                                    Notification notification = notificationBuilder.setOngoing(true)
                                            .setSmallIcon(R.drawable.ic_vaccine_1_)
                                            .setContentTitle("Available vaccine slot found")
                                            .setContentText("For Parameter - Pincode " + userParams.getSelectedPinCode() + ", Age " + userParams.getSelectedAge() + ", Cost " + userParams.getSelectedCost())
                                            .setSubText("vaccine slot found")
                                            .setPriority(NotificationManager.IMPORTANCE_HIGH)
                                            .setCategory(Notification.CATEGORY_SERVICE)
                                            .setContentIntent(pendingIntent)
                                            .setSound(Uri.parse("android.resource://"+ctx.getPackageName()+"/"+R.raw.notification))
                                            .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                                            .setAutoCancel(true)
                                            .build();

                                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    mNotificationManager.notify(NOTIF_ID, notification);

                                    //Stop the foreground service
                                    //stopSelf(NOTIF_ID);
                                    stopForeground(false);
                                    //stopSelf();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                Log.e("VaccineNotifierService", "Volley Error: "+error.toString());
                                Log.e("Volley", error.toString());
                            }
                        }) {

                    /**
                     * Passing some request headers
                     */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        headers.put("accept", "application/json");
                        headers.put("User-Agent", "Mozilla/5.0");
                        headers.put("Accept-Language", "en_US");
                        return headers;
                    }
                };

                requestQueue.add(jsonObjectRequest);

                try {
                    Thread.sleep(SLEEPTIME);
                    Log.println(Log.INFO, "VaccineNotifierService", "Going to sleep for " + SLEEPTIME / 60000 + " mins");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("VaccineNotifierService", "Thread sleep interrupted: " + e.toString());
                }
            }
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);

        requestQueue = Volley.newRequestQueue(this);
        ctx = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;

        UserParams userParams = new UserParams();
        userParams.setSelectedPinCode(intent.getStringExtra("selectedPinCode"));
        userParams.setSelectedVaccineType(intent.getStringExtra("selectedVaccineType"));
        userParams.setSelectedCost(intent.getStringExtra("selectedCost"));
        userParams.setSelectedAge(intent.getStringExtra("selectedAge"));

        msg.obj = userParams;

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(ctx, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Checking availability of slots in every 10 minutes")
                .setContentText("Your Parameter - Pincode " + userParams.getSelectedPinCode() + ", Age " + userParams.getSelectedAge() + ", Cost " + userParams.getSelectedCost())
                .setSubText("Checking slots")
                .setSmallIcon(R.drawable.ic_vaccine_1_)
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .setSound(Uri.parse("android.resource://"+ctx.getPackageName()+"/"+R.raw.notification))
                .setAutoCancel(true)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground(notification);
        else
            startForeground(NOTIF_ID, notification);

        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_REDELIVER_INTENT;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(Notification notification){
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        chan.setLightColor(Color.BLUE);
        AudioAttributes att = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        chan.setSound(Uri.parse("android.resource://"+ctx.getPackageName()+"/"+R.raw.notification),att);
        chan.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        startForeground(NOTIF_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}