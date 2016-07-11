package com.arifacar.reports.constant;

public enum ReportBaseEntity {

    REPORT_ID("REPORT_ID");

    private final String reportBaseEntity;

    ReportBaseEntity(String reportBaseEntity) {
        this.reportBaseEntity = reportBaseEntity;
    }

    @Override
    public String toString() {
        return reportBaseEntity;
    }


}
