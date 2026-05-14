package com.testproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {

    private Integer id;
    private Integer userId;
    private String  title;
    private String  body;

    public Post() {}

    public Post(Integer userId, String title, String body) {
        this.userId = userId;
        this.title  = title;
        this.body   = body;
    }

    // ── Getter / Setter ──────────────────────────────────────────────────────

    public Integer getId()               { return id; }
    public void    setId(Integer id)     { this.id = id; }

    public Integer getUserId()                  { return userId; }
    public void    setUserId(Integer userId)    { this.userId = userId; }

    public String getTitle()                 { return title; }
    public void   setTitle(String title)     { this.title = title; }

    public String getBody()               { return body; }
    public void   setBody(String body)    { this.body = body; }

    @Override
    public String toString() {
        return "Post{id=" + id + ", userId=" + userId +
               ", title='" + title + "'}";
    }
}
