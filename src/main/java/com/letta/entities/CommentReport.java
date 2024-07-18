package com.letta.entities;

public class CommentReport {
    
    private int comment_id;
    private int user_id;
    private String reportReason;

    CommentReport(){}

    public CommentReport(int comment_id, int user_id, String reporReason){
        this.comment_id = comment_id;
        this.user_id = user_id;
        this.reportReason = reporReason;
    }
    
    public int getCommentId(){
        return this.comment_id;
    }

    public int getUserId(){
        return this.user_id;
    }

    public String getReportReason(){
        return this.reportReason;
    }

    public void setReportReason(String reportReason){
        this.reportReason = reportReason;
    }

}
