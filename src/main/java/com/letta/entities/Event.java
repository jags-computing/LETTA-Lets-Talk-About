package com.letta.entities;

public class Event {
    
    private String event_name;
    private String event_date;
    private int groupid;
    private String event_description;
    private String media_file;
    private int owner_id;

    Event(){}

    public Event(String event_name, String event_date, int groupid, 
        String event_description, String media_file, int owner_id){

        this.setEventName(event_name);
        this.setEventDate(event_date);
        this.setGroupId(groupid);
        this.setDescription(event_description);
        this.setMediaFile(media_file);
        this.owner_id = owner_id;
    }
    

    public String getEventName(){
        return this.event_name;
    }

    public void setEventName(String event_name){
        this.event_name = event_name;
    }

    public String getEventDate(){
        return this.event_date;
    }

    public void setEventDate(String event_date){
        this.event_date = event_date;
    }

    public int getGroupId(){
        return this.groupid;
    }
    public void setGroupId(int groupid){
        this.groupid = groupid;
    }

    public String getDescription(){
        return this.event_description;
    }

    public void setDescription(String event_description){
        this.event_description = event_description;
    }

    public String getMediaFile(){
        return this.media_file;
    }

    public void setMediaFile(String media_file){
        this.media_file = media_file;
    }

    public int getOwnerId(){
        return this.owner_id;
    }


}
