package com.gcorso.myapplication.Tools;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcorso.myapplication.ChatActivity;
import com.gcorso.myapplication.Explore.HomeActivity;
import com.gcorso.myapplication.Objects.Note;
import com.gcorso.myapplication.ProfileActivity;
import com.gcorso.myapplication.R;

import java.util.List;

public class PasswordVaultActivity extends AppCompatActivity {

    private String password;
    private NotesBaseAdapter notesAdapter;
    private ListView listView;
    private List<Note> notes;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {

                case R.id.navigation_explore:
                    intent = new Intent(PasswordVaultActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.navigation_chat:
                    intent = new Intent(PasswordVaultActivity.this, ChatActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.navigation_tools:
                    intent = new Intent(PasswordVaultActivity.this, ToolsActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.navigation_profile:
                    intent = new Intent(PasswordVaultActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_vault);

        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Password Vault");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_tools);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        Intent intent = getIntent();
        password = intent.getStringExtra("password");

        final VaultLDH vaultLDH = new VaultLDH(this);
        notes = vaultLDH.getNotes(password);
        notesAdapter = new NotesBaseAdapter(notes);
        listView = findViewById(R.id.listnotes);
        listView.setAdapter(notesAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                LayoutInflater inflater = PasswordVaultActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.read_note_dialog, null);
                final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(PasswordVaultActivity.this).setView(dialogView).show();
                final TextView tvTitle = dialogView.findViewById(R.id.notetitle);
                final TextView tvText = dialogView.findViewById(R.id.notetext);
                tvTitle.setText(notes.get(pos).getTitle());
                tvText.setText(notes.get(pos).getText());

                Button btCopy = dialogView.findViewById(R.id.btCopy);
                btCopy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("message", notes.get(pos).getText());
                        if (clipboard != null) {
                            clipboard.setPrimaryClip(clip);
                        }

                        Toast.makeText(PasswordVaultActivity.this, "Note copied to clipboard", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });

                Button btDelete = dialogView.findViewById(R.id.btDelete);
                btDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vaultLDH.deleteNote(notes.get(pos).getNoteid());
                        notes = vaultLDH.getNotes(password);
                        notesAdapter.setNotesList(notes);
                        notesAdapter.notifyDataSetChanged();
                        //notesAdapter = new NotesBaseAdapter(vaultLDH.getNotes(password));
                        //listView.setAdapter(notesAdapter);
                        dialog.dismiss();
                    }
                });
            }
        });


        FloatingActionButton fab = findViewById(R.id.floatingAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = PasswordVaultActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.new_note_dialog, null);
                final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(PasswordVaultActivity.this).setView(dialogView).show();
                final EditText fieldTitle = dialogView.findViewById(R.id.fieldtitle);
                final EditText fieldText = dialogView.findViewById(R.id.fieldtext);

                Button btSave = dialogView.findViewById(R.id.btSave);
                btSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = fieldTitle.getText().toString();
                        String text = fieldText.getText().toString();

                        if (title.length()==0){
                            Toast.makeText(PasswordVaultActivity.this, "The title can't be empty!", Toast.LENGTH_SHORT).show();
                        } else if (text.length()==0){
                            Toast.makeText(PasswordVaultActivity.this, "The text can't be empty!", Toast.LENGTH_SHORT).show();
                        } else {
                            vaultLDH.addNote(password, title, text);
                            notes = vaultLDH.getNotes(password);
                            notesAdapter.setNotesList(notes);;
                            notesAdapter.notifyDataSetChanged();

                            dialog.dismiss();
                        }


                    }
                });

                Button btCancel = dialogView.findViewById(R.id.btCancel);
                btCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });




    }





}
