package com.gcorso.myapplication.Tools;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcorso.myapplication.ChatActivity;
import com.gcorso.myapplication.Explore.HomeActivity;
import com.gcorso.myapplication.ProfileActivity;
import com.gcorso.myapplication.R;

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
        textviewTitle.setText("Strumenti");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_tools);

        RelativeLayout itemData = findViewById(R.id.itemdata);
        itemData.setOnClickListener(itemVuotoListener);
        RelativeLayout itemAntivirus = findViewById(R.id.itemantivirus);
        itemAntivirus.setOnClickListener(itemVuotoListener);
        RelativeLayout itemVpn = findViewById(R.id.itemvpn);
        itemVpn.setOnClickListener(itemVuotoListener);

        RelativeLayout itemVault = findViewById(R.id.itemvault);
        itemVault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = ToolsActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.password_dialog, null);
                final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(ToolsActivity.this).setView(dialogView).show();

                final EditText input = dialogView.findViewById(R.id.fieldpassword);

                Button btOpen = dialogView.findViewById(R.id.btOpen);
                btOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String passwordstr = input.getText().toString();

                        if(passwordstr.length()>0){

                            while(passwordstr.length()<16){
                                passwordstr = passwordstr + " ";
                            }
                            Intent openReading = new Intent (ToolsActivity.this, PasswordVaultActivity.class);
                            openReading.putExtra("password", passwordstr);
                            startActivity(openReading);
                            dialog.dismiss();
                        }


                    }
                });
                Button btCancel = dialogView.findViewById(R.id.btCancel);
                btCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }
        });





    }

    View.OnClickListener itemVuotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(ToolsActivity.this, "Ci dispiace questa funzione non è ancora disponibile. Prova invece la password vault.", Toast.LENGTH_LONG).show();
        }
    };
}
