package com.arifacar.reports.constant;

public enum ReportEntity {

    COUNTRY("COUNTRY"),
    CITY("CITY"),
    DISTRICT("DISTRIC");

    private final String reportBaseEntity;

    ReportEntity(String reportBaseEntity) {
        this.reportBaseEntity = reportBaseEntity;
    }

    @Override
    public String toString() {
        return reportBaseEntity;
    }


}
