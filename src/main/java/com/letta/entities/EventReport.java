package com.letta.entities;


public class EventReport {

    private int eventid;
    private String reportReason;
    private int owner_id;

    EventReport(){}

    public EventReport(int eventid, String reportReason, int owner_id){
        this.eventid = eventid;
        this.setReportReason(reportReason);
        this.owner_id = owner_id;
    }

    public int getEventId(){
        return this.eventid;
    }
    public int getOwnerId(){
        return this.owner_id;
    }

    public String getReportReason(){
        return this.reportReason;
    }

    public void setReportReason(String reportReason){
        this.reportReason = reportReason;
    }
}
