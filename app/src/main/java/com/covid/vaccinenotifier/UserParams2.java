package com.covid.vaccinenotifier;

public class UserParams2 {
    public UserParams2(String centerID, String ageLimit, String vaccineName, String cost, String centerName, String centerAddress, String timming) {
        this.centerID = centerID;
        this.ageLimit = ageLimit;
        this.vaccineName = vaccineName;
        this.cost = cost;
        this.centerName = centerName;
        this.centerAddress = centerAddress;
        this.timming = timming;
    }

    String centerID,ageLimit,vaccineName,cost,centerName,centerAddress,timming;
}
