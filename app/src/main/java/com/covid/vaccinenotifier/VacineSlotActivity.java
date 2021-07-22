package com.covid.vaccinenotifier;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.covid.vaccinenotifier.databinding.ActivityVacineSlotBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VacineSlotActivity extends AppCompatActivity {

    ActivityVacineSlotBinding binding;


    private List<String> availSlotsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVacineSlotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ArrayList<UserParams2> userArrayList = new ArrayList<>();
        availSlotsList = new ArrayList<>();

        if (savedInstanceState != null) {
            if (savedInstanceState.getStringArray("availSlotsArray") != null) {
                String[] availSlotsArray = savedInstanceState.getStringArray("availSlotsArray");
                availSlotsList.clear();
                availSlotsList.addAll(Arrays.asList(availSlotsArray));

            }
        }

        onNewIntent(getIntent());


        for (int i = 0; i < availSlotsList.size(); i++) {

            String slotInfo = availSlotsList.get(i).toString();


            String[] arr = slotInfo.split("  ");
            String str_centerID, str_ageLimit, str_vaccine_name, str_cost, str_center_name, str_center_address, str_timming;

            str_centerID = arr[0];
            str_ageLimit = arr[2] + " +";
            str_vaccine_name = arr[1];
            str_cost = arr[3];
            str_center_name = arr[4];
            str_center_address = arr[5];
            str_timming = arr[6];


            UserParams2 up2 = new UserParams2(str_centerID, str_ageLimit, str_vaccine_name,
                    str_cost, str_center_name, str_center_address, str_timming);
            userArrayList.add(up2);


        }

        ListAdapter listAdapter = new ListAdapter(VacineSlotActivity.this, userArrayList);
        binding.listView.setAdapter(listAdapter);


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("availSlotsArray") && extras.get("availSlotsArray") instanceof Object[]) {
                Object[] availSlotsArray = (Object[]) extras.get("availSlotsArray");
                if (availSlotsArray.length > 0) {
                    availSlotsList.clear();
                    for (int i = 0; i < availSlotsArray.length; i++) {
                        String slotInfo = availSlotsArray[i].toString();
                        availSlotsList.add(slotInfo);
                    }


                } else {
                    Toast.makeText(this, "onNewIntent() availSlotsArray is empty", Toast.LENGTH_SHORT).show();
                }

                getApplicationContext().stopService(new Intent(getApplicationContext(), NotifierService.class));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}