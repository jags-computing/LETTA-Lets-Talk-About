package com.letta.entities;

public class Group {
    private String groupname; 
    private String description;
    private int ownerid;
    private int groupID;


    Group(){}

    public Group(String groupname, String description, int ownerid){
        this.setGroupname(groupname);
        this.setDescription(description);
        this.ownerid = ownerid;
    }

    public Group(String groupname, String description, int ownerid, int groupID){
        this.setGroupname(groupname);
        this.setDescription(description);
        this.ownerid = ownerid;
        this.groupID = groupID;
    }

    public String getGroupname(){
        return this.groupname;
    }
    public void setGroupname(String groupname){
        this.groupname = groupname;
    }

    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public int getGroupID() {
        return groupID;
    }

    public int getOwnerid(){
        return this.ownerid;
    }
    
}
