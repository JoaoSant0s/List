package com.example.joao.list;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.joao.sql.DbSqlAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static DbSqlAdapter myDb;
    public ArrayAdapter mAdapter;
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openDB();
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        ArrayList<String> s = myDb.getAllRows();

        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, s);
        listView.setAdapter(mAdapter);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                builder.setTitle("Nota");
                builder.setMessage((String) parent.getItemAtPosition(position));
                final int p = position;
                // Add the buttons
                builder.setPositiveButton(R.string.action_edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Edit button
                    }
                });
                builder.setNeutralButton(R.string.action_remove, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeNote(p);
                    }
                });
                builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                copyNote((String) parent.getItemAtPosition(position));
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }

    private void openDB() {
        myDb = new DbSqlAdapter(this);
        myDb.open();
    }

    private void closeDB() {
        myDb.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.

                switch (item.getItemId()) {
                    case R.id.action_new:
                        dialogCreation();
                        return true;
                    case R.id.action_clear:
                        clearDb();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dialogCreation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        builder.setTitle(R.string.action_create);
        final View view = inflater.inflate(R.layout.activity_display_message, null);
        builder.setView(view)
            // Add action buttons
            .setPositiveButton(R.string.action_create, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    createItemList(view);
                }
            })
            .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
        builder.show();
    }

    public void createItemList(View view) {
        EditText editText = (EditText) view.findViewById(R.id.new_note);
        String message = editText.getText().toString();
        myDb.insertRow(message);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void removeNote(int positionList) {
        myDb.deleteRow(positionList);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void copyNote(String value) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copy Text", value);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(MainActivity.this, R.string.action_copy , Toast.LENGTH_LONG).show();
    }

    private void clearDb() {
        myDb.clearRowset();
        startActivity(getIntent());
    }
}
