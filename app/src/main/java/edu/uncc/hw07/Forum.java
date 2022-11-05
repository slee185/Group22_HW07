// Homework Assignment 07
// Group22_HW07
// Stephanie Lee Karp & Ken Stanley

package edu.uncc.hw07;

import java.io.Serializable;
import com.google.firebase.Timestamp;
import java.util.UUID;

public class Forum implements Serializable {
    private String forum_id = UUID.randomUUID().toString();
    private String user_id;
    private String user_name;
    private String forum_title;
    private String forum_description;
    private Timestamp created_at;

    public Forum() {}

    public Forum(String user_id, String user_name, String forum_title, String forum_description) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.forum_title = forum_title;
        this.forum_description = forum_description;
        this.created_at = Timestamp.now();
    }

    public String getForum_id() {
        return forum_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getForum_title() {
        return forum_title;
    }

    public String getForum_description() {
        return forum_description;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public Forum setForum_id(String forum_id) {
        this.forum_id = forum_id;
        return this;
    }

    public Forum setUser_id(String user_id) {
        this.user_id = user_id;
        return this;
    }

    public Forum setUser_name(String user_name) {
        this.user_name = user_name;
        return this;
    }

    public Forum setForum_title(String forum_title) {
        this.forum_title = forum_title;
        return this;
    }

    public Forum setForum_description(String forum_description) {
        this.forum_description = forum_description;
        return this;
    }

    public Forum setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
        return this;
    }

    @Override
    public String toString() {
        return "Forum{" +
                "forum_id='" + forum_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                ", forum_title='" + forum_title + '\'' +
                ", forum_description='" + forum_description + '\'' +
                ", created_at=" + created_at +
                '}';
    }
}
