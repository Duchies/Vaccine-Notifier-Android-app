package com.covid.vaccinenotifier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.covid.vaccinenotifier.databinding.ActivityLoginBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

     ActivityLoginBinding binding;
    private ChipGroup chip_vaccine, chip_age, chip_cost;
    private Context ctx;
    public Boolean get_notified_flag;

    public TextInputLayout pinCode;

    String selectedAge;
    String selectedPinCode;
    String selectedCost;
    String selectedVaccineType;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ctx = this.getApplicationContext();
        pinCode = findViewById(R.id.enter_pin_code);
        chip_vaccine = findViewById(R.id.vaccine_type);
        chip_age = findViewById(R.id.age_type);
        chip_cost = findViewById(R.id.cost_type);
        get_notified_flag = false;

        final Button notify_btn = findViewById(R.id.notify);
        final Button stop_notify_btn = findViewById(R.id.stop_notify);


        notify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPinCode = pinCode.getEditText().getText().toString();

                boolean flag4 = isValidPinCode(selectedPinCode);

                if (!flag4) {
                    pinCode.setError("Error");

                } else {

                    boolean flag1 = checkAgeChip();
                    boolean flag2 = checkCostChip();
                    boolean flag3 = checkVaccineTypeChip();

                    if (true == flag2 && flag3 == true && flag1 == true) {
                        Intent serviceIntent = new Intent(ctx, NotifierService.class);
                        serviceIntent.putExtra("selectedPinCode", selectedPinCode);
                        serviceIntent.putExtra("selectedVaccineType", selectedVaccineType);
                        serviceIntent.putExtra("selectedCost", selectedCost);
                        serviceIntent.putExtra("selectedAge", selectedAge);
                        ContextCompat.startForegroundService(ctx, serviceIntent);
                        showMessage("You will be Notified");

                        get_notified_flag = true;

                    } else {
                        showMessage("Input Feild's Can't Be Empty");
                    }
                }
            }
        });

        stop_notify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (get_notified_flag) {
                    ctx.stopService(new Intent(ctx, NotifierService.class));
                    showMessage("Notification service stoped");
                } else {
                    ctx.stopService(new Intent(ctx, NotifierService.class));
                    showMessage("You Need to Start Notification Service");
                }
            }
        });
    }

    private boolean checkCostChip() {
        String msg = "";
        boolean FLAG = false;
        int chipsCount = chip_cost.getChildCount();

        int i = 0;
        while (i < chipsCount) {
            Chip chip = (Chip) chip_cost.getChildAt(i);
            if (chip.isChecked()) {
                FLAG = true;
                msg += chip.getText().toString().trim();
            }
            i++;
        }


        if (!FLAG) {

            return false;
        } else {

            selectedCost = msg;

            return true;
        }
    }

    private boolean checkVaccineTypeChip() {
        String msg = "";
        boolean FLAG = false;
        int chipsCount = chip_vaccine.getChildCount();

        int i = 0;
        while (i < chipsCount) {
            Chip chip = (Chip) chip_vaccine.getChildAt(i);
            if (chip.isChecked()) {
                FLAG = true;
                msg += chip.getText().toString().trim();
            }
            i++;
        }


        if (!FLAG) {

            return false;
        } else {
            selectedVaccineType = msg;
            return true;
        }
    }

    private boolean checkAgeChip() {
        String msg = "";
        boolean FLAG = false;
        int chipsCount = chip_age.getChildCount();

        int i = 0;
        while (i < chipsCount) {
            Chip chip = (Chip) chip_age.getChildAt(i);
            if (chip.isChecked()) {
                FLAG = true;
                msg += chip.getText().toString().trim();
            }
            i++;
        }


        if (!FLAG) {

            return false;
        } else {

            selectedAge = msg;
            return true;
        }

    }


    private boolean isValidPinCode(String pinCode) {

        // Regex to check valid pin code of India.
        String regex
                = "^[1-9]{1}[0-9]{2}\\s{0,1}[0-9]{3}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the pin code is empty
        // return false
        if (pinCode == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given pin code
        // and regular expression.
        Matcher m = p.matcher(pinCode);

        // Return if the pin code
        // matched the ReGex
        return m.matches();
    }

    public void showMessage(String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }



}