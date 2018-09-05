package com.gcorso.academy.objects;

import java.util.List;

public class Lesson {
    private int id;
    private String title;
    private List<String> sections;
    private int result;
    private int courseId;
    private int numberOfSections;

    public Lesson(int id, String title, List<String> sections, int result, int courseId) {
        this.id = id;
        this.title = title;
        this.sections = sections;
        this.result = result;
        this.courseId = courseId;
    }

    public Lesson(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Lesson(int id, String title, int result, int numberOfSections) {
        this.id = id;
        this.title = title;
        this.result = result;
        this.numberOfSections = numberOfSections;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSections() {
        return sections;
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getNumberOfSections() {
        return numberOfSections;
    }

    public void setNumberOfSections(int numberOfSections) {
        this.numberOfSections = numberOfSections;
    }
}
