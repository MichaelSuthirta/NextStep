package com.example.nextstep.models;

public class ExtraPosts extends Post{
    private String title, organization, startDate, end, categoryName;

    public ExtraPosts(String userId, String title, String organization, String startDate, String end) {
        super(userId);
        this.title = title;
        this.organization = organization;
        this.startDate = startDate;
        this.end = end;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
