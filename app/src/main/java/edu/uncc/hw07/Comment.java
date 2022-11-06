// Homework Assignment 07
// Group22_HW07
// Stephanie Lee Karp & Ken Stanley

package edu.uncc.hw07;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.UUID;

public class Comment implements Serializable {
    private String comment_id = UUID.randomUUID().toString();
    private String user_id;
    private String user_name;
    private String comment_text;
    private Timestamp created_at;

    public Comment(){}

    public Comment(String user_id, String user_name, String comment_text) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.comment_text = comment_text;
        this.created_at = Timestamp.now();
    }

    public String getComment_id() {
        return comment_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getComment_text() {
        return comment_text;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment_id='" + comment_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                ", comment_text='" + comment_text + '\'' +
                ", created_at=" + created_at +
                '}';
    }
}
