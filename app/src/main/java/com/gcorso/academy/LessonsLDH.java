package com.gcorso.academy.Explore;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gcorso.academy.Objects.Course;
import com.gcorso.academy.Objects.Lesson;
import com.gcorso.academy.Objects.Level;
import com.gcorso.academy.Objects.Section;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class LessonsLDH {

    public static final String[] levels = {"Beginner", "Amateur", "Expert", "Professional", "Master"};
    public static final int[] boundaries = {0, 100, 250, 450, 700, 1500};

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "lessons.db";
    private static final String TABLE_NAME_LESSONS = "lesson";
    private static final String TABLE_NAME_COURSES = "course";

    private static final String LESSON_COLUMN_ID = "_id";
    private static final String LESSON_COLUMN_TITLE = "title";
    private static final String LESSON_COLUMN_COURSEID = "courseid";
    private static final String[] LESSON_COLUMN_SECTIONS = {"section0", "section1", "section2", "section3", "section4", "section5", "section6", "section7", "section8", "section9"};
    private static final String LESSON_COLUMN_QUESTIONS = "questions";
    private static final String LESSON_COLUMN_RESULT = "result";
    private static final String LESSON_COLUMN_NSECTIONS = "nsections";

    private static final String COURSE_COLUMN_ID = "_id";
    private static final String COURSE_COLUMN_TITLE = "title";

    private LessonsDBOpenHelper openHelper;
    private SQLiteDatabase database;

    // in order not to have more that one instance at any time, this class is developed as a singleton
    private static LessonsLDH instance = null;

    private LessonsLDH(Context context){
        openHelper = new LessonsDBOpenHelper(context);
        database = openHelper.getReadableDatabase();
    }

    public static LessonsLDH getInstance(Context context){
        if(instance == null){
            instance = new LessonsLDH(context); // called just the first time the user opens the app
        }
        return instance;
    }

    public LessonsDBOpenHelper getOpenHelper (Context context){
        openHelper = new LessonsDBOpenHelper(context);
        return openHelper;
    }

    public SQLiteDatabase getWritableDatabase (Context context){
        openHelper = new LessonsDBOpenHelper(context);
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    public List<String> getCoursesNames(){
        openHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT " + COURSE_COLUMN_TITLE + " FROM " + TABLE_NAME_COURSES + " ORDER BY " + COURSE_COLUMN_ID,
                null
        );

        List<String> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String str = cursor.getString(0);
            list.add(str);
            cursor.moveToNext();
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    public List<Course> getCourses(){
        openHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT " + COURSE_COLUMN_ID + ", " + COURSE_COLUMN_TITLE + " FROM " + TABLE_NAME_COURSES,
                null
        );

        List<Course> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Course course = new Course(cursor.getInt(0), cursor.getString(1));
            List<Lesson> lessons = new ArrayList<>();
            Cursor curles = database.rawQuery( "SELECT _id, title, result, nsections FROM lesson WHERE courseid = " + Integer.toString(course.getCourseid()), null);
            curles.moveToFirst();
            while (!curles.isAfterLast()){
                Lesson lesson = new Lesson(curles.getInt(0), curles.getString(1), curles.getInt(2),curles.getInt(3));
                lessons.add(lesson);
                curles.moveToNext();
            }
            if (curles != null && !curles.isClosed()) {
                curles.close();
            }
            course.setLessons(lessons);
            list.add(course);
            cursor.moveToNext();
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    public Section getSection(int lessonid, int sectionn){
        String sql = "SELECT title, nsections, " + LESSON_COLUMN_SECTIONS[sectionn] + " FROM lesson WHERE _id = " + Integer.toString(lessonid);
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();

        String lessontitle = cursor.getString(0);
        int lessonsections = cursor.getInt(1);
        String sec = cursor.getString(2);
        String title = sec.split("<<-->>")[0];
        String text = sec.split("<<-->>")[1];
        cursor.close();

        return new Section(lessonid, sectionn, title, text, lessontitle, lessonsections);

    }

    public String getQuestion(int lessonid){
        String sql = "SELECT questions FROM lesson WHERE _id = " + Integer.toString(lessonid);
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();

        String questions = cursor.getString(0);
        cursor.close();
        return questions;

    }

    public String getLessonTitle(int lessonid){
        String sql = "SELECT title FROM lesson WHERE _id = " + Integer.toString(lessonid);
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        String title = cursor.getString(0);
        cursor.close();
        return title;

    }

    public Level getLevel(){
        String sql = "SELECT SUM(result) FROM lesson";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        int totq = cursor.getInt(0) * 5;
        cursor.close();
        int liv = 0;
        for(int i = 4; i>=0; i--){
            if(totq>=boundaries[i]){
                liv = i;
                break;
            }
        }

        int prog = totq-boundaries[liv];
        int tot = boundaries[liv+1] - boundaries[liv];
        int perc = prog*100/tot;
        int[] perccourses = {0,0,0,0,0,0};
        String sql2 = "SELECT SUM(result), COUNT(_id), courseid FROM lesson WHERE courseid = 1 UNION " +
                "SELECT SUM(result), COUNT(_id), courseid FROM lesson WHERE courseid = 2 UNION " +
                "SELECT SUM(result), COUNT(_id), courseid FROM lesson WHERE courseid = 3 UNION " +
                "SELECT SUM(result), COUNT(_id), courseid FROM lesson WHERE courseid = 4 UNION " +
                "SELECT SUM(result), COUNT(_id), courseid FROM lesson WHERE courseid = 5 UNION " +
                "SELECT SUM(result), COUNT(_id), courseid FROM lesson WHERE courseid = 6";
        cursor = database.rawQuery(sql2, null);
        cursor.moveToFirst();

        for(int i = 0; i<6; i++){
            perccourses[cursor.getInt(2)-1] = cursor.getInt(0) * 10 / cursor.getInt(1);
            cursor.moveToNext();
        }
        cursor.close();

        return new Level(perc, levels[liv], prog, tot, perccourses);
    }

    public int updateResult(int lessonid, int nuovo){
        String sql = "SELECT result FROM lesson WHERE _id = " + Integer.toString(lessonid);
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();

        int vecchio = cursor.getInt(0);
        cursor.close();

        if(nuovo > vecchio){
            String update = "UPDATE lesson SET result = " + Integer.toString(nuovo) + " WHERE _id = " + Integer.toString(lessonid);
            database.execSQL(update);
            return nuovo-vecchio;
        }
        return 0;
    }

    // class that interfaces the connection between the LDH and the database
    private class LessonsDBOpenHelper extends SQLiteOpenHelper {

        Context context;

        public LessonsDBOpenHelper(Context context) {
          super(context, DATABASE_NAME, null, DATABASE_VERSION);
          this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            AssetManager assetManager = context.getAssets();

            InputStream input;
            try {
                input = assetManager.open("lessonsdb.sql");

                int size = input.available();
                byte[] buffer = new byte[size];
                input.read(buffer);
                input.close();
                String SQL_CREATE_TABLE_BOOK = new String(buffer);

                String commands[] = SQL_CREATE_TABLE_BOOK.split(";");

                for(String cmd : commands){
                    if(cmd.length()>4 && (!cmd.toLowerCase().contains("commit"))){
                        System.out.println(cmd);
                        db.execSQL(cmd);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COURSES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LESSONS);

            onCreate(db);
        }
    }
}
