/*
 *  Copyright (c) 2018 Gabriele Corso
 *
 *  Distributed under the MIT software license, see the accompanying
 *  file LICENSE or http://www.opensource.org/licenses/mit-license.php.
*/

package com.gcorso.academy;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gcorso.academy.objects.Course;
import com.gcorso.academy.objects.Lesson;
import com.gcorso.academy.objects.Level;
import com.gcorso.academy.objects.Section;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.gcorso.academy.Preferences.BOUNDARIES;
import static com.gcorso.academy.Preferences.LEVELS;

public class LessonsLDH {

    private static final int DATABASE_VERSION = 55;
    private static final String DATABASE_NAME = "lessons.db";
    private static final String TABLE_NAME_LESSONS = "lesson";
    private static final String TABLE_NAME_COURSES = "course";
    private static final String SQL_CHECK = "SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name != 'android_metadata' AND name != 'sqlite_sequence';";

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

        Cursor cursor = database.rawQuery(SQL_CHECK, null);
        cursor.moveToFirst();
        int tables = cursor.getInt(0);
        cursor.close();

        if(tables < 2){
            Log.w("Database","Populating database");
            populateDatabase(context);
        }
    }

    public static LessonsLDH getInstance(Context context){
        if(instance == null){
            instance = new LessonsLDH(context); // called just the first time the user opens the app
        }
        return instance;
    }

    private void populateDatabase(Context context){
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
                if(cmd.length()>4 && (!(cmd.toLowerCase().contains("commit") && cmd.length() < 15))
                        && (!(cmd.toLowerCase().contains("begin transaction") && cmd.length() < 30))){
                    database.execSQL(cmd);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            Cursor curles = database.rawQuery( "SELECT _id, title, result, nsections FROM lesson WHERE courseid = " + Integer.toString(course.getId()), null);
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

    public Section getSection(int lessonId, int sectionN){
        String sql = "SELECT title, nsections, " + LESSON_COLUMN_SECTIONS[sectionN] + " FROM lesson WHERE _id = " + Integer.toString(lessonId);
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();

        String lessonTitle = cursor.getString(0);
        int lessonSections = cursor.getInt(1);
        String sec = cursor.getString(2);
        String title = sec.split("<<-->>")[0];
        String text = sec.split("<<-->>")[1];
        cursor.close();

        return new Section(lessonId, sectionN, title, text, lessonTitle, lessonSections);

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
        String sql = "SELECT SUM(result), COUNT(nsections), COUNT(DISTINCT courseid) FROM lesson";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        int totQ = cursor.getInt(0) * 5;
        int totalPoints = cursor.getInt(1) * 50;
        int nCourses = cursor.getInt(2);
        cursor.close();
        int liv = 0;
        for(int i = LEVELS.length-1; i>=0; i--){
            if(totQ >= BOUNDARIES[i]){
                liv = i;
                break;
            }
        }

        int prog = totQ - BOUNDARIES[liv];
        int tot;
        if(liv == LEVELS.length-1){
            tot = totalPoints;
        } else {
            tot = BOUNDARIES[liv+1] - BOUNDARIES[liv];
        }
        int perc = prog*100/tot;

        int[] percCourses = new int[nCourses];
        String sql2 = "SELECT SUM(result), COUNT(_id) FROM lesson GROUP BY courseid ORDER BY courseid";
        cursor = database.rawQuery(sql2, null);
        cursor.moveToFirst();
        int idx = 0;
        while (!cursor.isAfterLast()){
            percCourses[idx] = cursor.getInt(0) * 10 / cursor.getInt(1);
            idx++;
            cursor.moveToNext();
        }
        cursor.close();

        return new Level(perc, LEVELS[liv], prog, tot, percCourses);
    }

    public int updateResult(int lessonid, int newResult){
        String sql = "SELECT result FROM lesson WHERE _id = " + Integer.toString(lessonid);
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();

        int oldResult = cursor.getInt(0);
        cursor.close();

        if(newResult > oldResult){
            String update = "UPDATE lesson SET result = " + Integer.toString(newResult) + " WHERE _id = " + Integer.toString(lessonid);
            database.execSQL(update);
            return newResult - oldResult;
        }
        return 0;
    }

    // class that interfaces the connection between the LDH and the database
    private class LessonsDBOpenHelper extends SQLiteOpenHelper {

        public LessonsDBOpenHelper(Context context) {
          super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COURSES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LESSONS);

            onCreate(db);
        }
    }
}
