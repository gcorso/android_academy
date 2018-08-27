package com.gcorso.academy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gcorso.academy.Explore.HomeActivity;
import com.gcorso.academy.Explore.LessonsLDH;
import com.gcorso.academy.Objects.FitDoughnut;
import com.gcorso.academy.Objects.Level;
import com.gcorso.academy.Tools.ToolsActivity;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    public static final String SCORE_NAME = "CyberPower";


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {

                case R.id.navigation_explore:
                    intent = new Intent(ProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_chat:
                    intent = new Intent(ProfileActivity.this, ChatActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_tools:
                    intent = new Intent(ProfileActivity.this, ToolsActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_profile:

                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Profilo");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_profile);

        LessonsLDH lessonsLDH = new LessonsLDH(this);
        Level level = lessonsLDH.getLevel();

        // set up the overall level
        FitDoughnut doughnut = (FitDoughnut) findViewById(R.id.doughnuttot);
        doughnut.animateSetPercent((float) level.getPerctot());
        TextView tvperctot = findViewById(R.id.tvpercentage);
        String p = Integer.toString(level.getPerctot())+ "%";
        tvperctot.setText(p);
        TextView tvLev = findViewById(R.id.tvlevel);
        tvLev.setText(level.getLiv());
        TextView tvProg = findViewById(R.id.tvprogress);
        String prog = Integer.toString(level.getProg()) + " / " + Integer.toString(level.getTot());
        tvProg.setText(prog);

        FitDoughnut[] dcourses = {findViewById(R.id.doughnut1), findViewById(R.id.doughnut2), findViewById(R.id.doughnut3),
                findViewById(R.id.doughnut4), findViewById(R.id.doughnut5), findViewById(R.id.doughnut6)};
        TextView[] tvperccourses = new TextView[]{findViewById(R.id.tvpercentage1), findViewById(R.id.tvpercentage2), findViewById(R.id.tvpercentage3),
                findViewById(R.id.tvpercentage4), findViewById(R.id.tvpercentage5), findViewById(R.id.tvpercentage6)};

        // TextViews for the titles
        List<String> coursesTitles = lessonsLDH.getCoursesNames();
        TextView[] tvtitles = new TextView[]{findViewById(R.id.tvtitle1), findViewById(R.id.tvtitle2), findViewById(R.id.tvtitle3),
                findViewById(R.id.tvtitle4), findViewById(R.id.tvtitle5), findViewById(R.id.tvtitle6)};

        for(int i = 0; i<coursesTitles.size(); i++){
            dcourses[i].animateSetPercent((float) level.getPerccourses()[i]);
            String t = level.getPerccourses()[i] + "%";
            tvperccourses[i].setText(t);
            tvtitles[i].setText(coursesTitles.get(i));
        }


    }
}
