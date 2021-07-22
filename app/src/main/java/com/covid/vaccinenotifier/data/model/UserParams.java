package com.covid.vaccinenotifier.data.model;

public class UserParams {
    String selectedAge;
    String selectedPinCode;
    String selectedCost;
    String selectedVaccineType;

    public String getSelectedAge() {
        return selectedAge;
    }

    public void setSelectedAge(String selectedAge) {
        this.selectedAge = selectedAge;
    }

    public String getSelectedPinCode() {
        return selectedPinCode;
    }

    public void setSelectedPinCode(String selectedPinCode) {
        this.selectedPinCode = selectedPinCode;
    }

    public String getSelectedCost() {
        return selectedCost;
    }

    public void setSelectedCost(String selectedCost) {
        this.selectedCost = selectedCost;
    }

    public String getSelectedVaccineType() {
        return selectedVaccineType;
    }

    public void setSelectedVaccineType(String selectedVaccineType) {
        this.selectedVaccineType = selectedVaccineType;
    }
}
