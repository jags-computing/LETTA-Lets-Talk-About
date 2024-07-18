package com.letta.entities;

public class Topic {
    
    private String topicname;
    private int groupid;

    Topic(){}

    public Topic(String topicname, int groupid){
        this.setTopicname(topicname);
        this.groupid = groupid;
    }

    public String getTopicname(){
        return this.topicname;
    }

    public void setTopicname(String topicname){
        this.topicname = topicname;
    }

    public int getGroupId(){
        return this.groupid;
    }


}
