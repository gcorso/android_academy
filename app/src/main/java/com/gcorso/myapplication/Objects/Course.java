package com.gcorso.myapplication.Objects;

import java.util.List;

public class Course {
    private int courseid;
    private String title;
    private List<Lesson> lessons;

    public Course(int courseid, String title) {
        this.courseid = courseid;
        this.title = title;
    }

    public Course(int courseid, String title, List<Lesson> lessons) {
        this.courseid = courseid;
        this.title = title;
        this.lessons = lessons;
    }

    public int getCourseid() {
        return courseid;
    }

    public void setCourseid(int courseid) {
        this.courseid = courseid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

}
