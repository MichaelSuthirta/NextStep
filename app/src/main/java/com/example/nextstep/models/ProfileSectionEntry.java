package com.example.nextstep.models;

public class ProfileSectionEntry {
    private int id;
    private int sectionId;
    private String companyName;
    private String role;
    private String startDate;
    private String endDate;
    private boolean isCurrent;

    public ProfileSectionEntry(int id, int sectionId, String companyName, String role,
                               String startDate, String endDate, boolean isCurrent) {
        this.id = id;
        this.sectionId = sectionId;
        this.companyName = companyName;
        this.role = role;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCurrent = isCurrent;
    }

    public ProfileSectionEntry(int sectionId, String companyName, String role,
                               String startDate, String endDate, boolean isCurrent) {
        this(0, sectionId, companyName, role, startDate, endDate, isCurrent);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSectionId() {
        return sectionId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getRole() {
        return role;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean isCurrent() {
        return isCurrent;
    }
}
