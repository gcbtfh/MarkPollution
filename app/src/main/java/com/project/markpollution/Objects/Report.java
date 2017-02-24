package com.project.markpollution.Objects;

/**
 * Created by Hung on 07-Nov-16.
 */

public class Report {
    private String id_report;
    private String id_user;

    public Report() {
    }

    public Report(String id_report, String id_user) {
        this.id_report = id_report;
        this.id_user = id_user;
    }

    public String getId_report() {
        return id_report;
    }

    public void setId_report(String id_report) {
        this.id_report = id_report;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }
}
