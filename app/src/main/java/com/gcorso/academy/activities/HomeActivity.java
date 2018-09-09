/*
 *  Copyright (c) 2018 Gabriele Corso
 *
 *  Distributed under the MIT software license, see the accompanying
 *  file LICENSE or http://www.opensource.org/licenses/mit-license.php.
 */

package com.gcorso.academy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.gcorso.academy.adapters.CoursesListAdapter;
import com.gcorso.academy.persistence.LessonsLDH;
import com.gcorso.academy.objects.Course;
import com.gcorso.academy.R;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    CoursesListAdapter coursesListAdapter;
    LessonsLDH lessonsLDH;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {

                case R.id.navigation_explore:

                    return true;
                case R.id.navigation_chat:
                    intent = new Intent(HomeActivity.this, ChatActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_tools:
                    intent = new Intent(HomeActivity.this, ToolsActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_profile:
                    intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        lessonsLDH = LessonsLDH.getInstance(this);
        List<Course> courses = lessonsLDH.getCourses();


        coursesListAdapter = new CoursesListAdapter(this, courses);
        ListView listCourses = findViewById(R.id.courses_list);
        listCourses.setAdapter(coursesListAdapter);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Explore");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);

        /*
        //DUMP courses and lessons
        Log.d("", "COURSE DUMP");

        for (Course c : courses) {
            System.out.println(String.format("<course name='%s' id='%d'>%n", c.getTitle(), c.getId()));
            for (Lesson lesson : c.getLessons()) {
                int lessonId = lesson.getId();
                int nSections = lesson.getNumberOfSections();
                System.out.println(String.format("\t<lesson name='%s' id='%d'>%n", lesson.getTitle(), lessonId));
                for(int i = 0;i< nSections;i++) {

                    //Log.d("", String.format("\t\t<section number='%d' id='%d'>%n", i, lessonsLDH.getSection(lessonId, i).getLessonId()));
                    Section s = lessonsLDH.getSection(lessonId, i);

                    if (s != null) {
                        System.out.printf("\t\t<section number='%d' id='%d'>%n", i, s.getLessonId());

                        System.out.println("\t\t\t<title>");
                        System.out.println(s.getTitle());
                        System.out.println("\t\t\t</title>");
                        System.out.println("\t\t\t<text>");
                        System.out.println(s.getText());
                        System.out.println("\t\t\t</text>");

                        System.out.println("\t\t</section>");
                    }
                }
                System.out.println("\t</lesson>");

            }
            System.out.println("</course>");

        }
*/
    }

}
