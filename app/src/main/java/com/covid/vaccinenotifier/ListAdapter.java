package com.covid.vaccinenotifier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<UserParams2> {

    public ListAdapter(Context context, ArrayList<UserParams2> userArrayList){

       super(context,R.layout.activity_home,userArrayList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        UserParams2  user2 = getItem(position);

        if (convertView == null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_home,parent,false);

        }

        TextView centerID = convertView.findViewById(R.id.tv_centerID);
        TextView ageLimit = convertView.findViewById(R.id.tv_age_limit);
        TextView vaccineName = convertView.findViewById(R.id.tv_vaccine_name);
        TextView cost = convertView.findViewById(R.id.tv_cost_type);
        TextView centerName = convertView.findViewById(R.id.tv_center_name);
        TextView centerAddress = convertView.findViewById(R.id.tv_center_addresss);
        TextView timming = convertView.findViewById(R.id.tv_center_timing);

        centerID.setText(user2.centerID);
        ageLimit.setText(user2.ageLimit);
        vaccineName.setText(user2.vaccineName);
        cost.setText(user2.cost);
        centerName.setText(user2.centerName);
        centerAddress.setText(user2.centerAddress);
        timming.setText(user2.timming);

        return convertView;
    }
}
