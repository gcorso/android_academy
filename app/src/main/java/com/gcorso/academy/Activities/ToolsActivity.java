/*
 *  Copyright (c) 2018 Gabriele Corso
 *
 *  Distributed under the MIT software license, see the accompanying
 *  file LICENSE or http://www.opensource.org/licenses/mit-license.php.
 */

package com.gcorso.academy.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcorso.academy.R;

public class ToolsActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {

                case R.id.navigation_explore:
                    intent = new Intent(ToolsActivity.this, HomeActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_chat:
                    intent = new Intent(ToolsActivity.this, ChatActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_tools:
                    return true;
                case R.id.navigation_profile:
                    intent = new Intent(ToolsActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);

        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Tools");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_tools);

        RelativeLayout item1 = findViewById(R.id.item1);
        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // click on item 1
            }
        });

        RelativeLayout item2 = findViewById(R.id.item2);
        item2.setOnClickListener(itemEmptyListener);
        RelativeLayout item3 = findViewById(R.id.item3);
        item3.setOnClickListener(itemEmptyListener);
        RelativeLayout item4 = findViewById(R.id.item4);
        item4.setOnClickListener(itemEmptyListener);
    }

    View.OnClickListener itemEmptyListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(ToolsActivity.this, "Sorry, this tool is not available yet.", Toast.LENGTH_LONG).show();
        }
    };
}
