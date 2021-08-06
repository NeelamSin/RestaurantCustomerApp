package com.eosinfotech.restaurantcustomerui.Models;

public class DuplicateModel {

    private String personName;
    private String jobProfile;

    public DuplicateModel(String personName, String jobProfile) {
        this.personName = personName;
        this.jobProfile = jobProfile;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getJobProfile() {
        return jobProfile;
    }

    public void setJobProfile(String jobProfile) {
        this.jobProfile = jobProfile;
    }
}
