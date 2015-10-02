package com.example.joao.list;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.joao.MyArrayAdapter;
import com.example.joao.sql.DbSqlAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    public static DbSqlAdapter myDb;
    //public ArrayAdapter<String> mAdapter;
    MyArrayAdapter mAdapter;
    public ListView listView;

    private String valueSeletion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openDB();
        Log.d("Opa", DbSqlAdapter.SQL_CREATE_DATABASE);
        setContentView(R.layout.activity_main);
        initialize();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                builder.setTitle("Nota");
                final String message = (String) parent.getItemAtPosition(position);
                builder.setMessage(message);
                final int p = position;
                // Add the buttons
                builder.setPositiveButton(R.string.action_edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogCreationOrEdit(message, p);
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

    private void initialize() {
        listView = (ListView) findViewById(R.id.listView);
        ArrayList<String[]> str = myDb.getAllRows();
        String[] str1 = new String[str.size()];
        String[] str2 = new String[str.size()];
        int cont = 0;
        for (String[] s : str) {
            str1[cont] = s[0];
            str2[cont] = s[1];
            cont++;
        }
        mAdapter = new MyArrayAdapter(this, str1, str2);
        listView.setAdapter(mAdapter);
        //mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, str2);
        //listView.setAdapter(mAdapter);
    }

    private Spinner prepareColors(View view) {
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.facility, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        return spinner;
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
                        dialogCreationOrEdit(null, -1);
                        return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dialogCreationOrEdit(final String edit, final int p) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.activity_display_message, null);
        int stringAction = -1;
        Spinner spinner = null;

        if(edit == null){
            spinner = prepareColors(view);
            stringAction = R.string.action_create;
        }else{
            ((EditText) view.findViewById(R.id.new_note)).setText(edit);
            spinner = prepareColors(view);
            stringAction = R.string.action_edit;
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //pos[0] = (String) parent.getItemAtPosition(position);
                valueSeletion = (String) parent.getItemAtPosition(position);
                Log.d("Select",valueSeletion);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        builder.setTitle(stringAction);
        builder.setView(view)
            // Add action buttons
            .setPositiveButton(stringAction, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    if (edit == null) {
                        createItemList(view, valueSeletion);
                    } else {
                        editItemList(view, p, valueSeletion);
                    }
                }
            })
            .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {}
            });
        builder.show();
    }

    private void editItemList(View view, int position, String faci) {
        EditText editText = (EditText) view.findViewById(R.id.new_note);
        String messagem = editText.getText().toString();
        myDb.updateRow(position, messagem, faci);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void createItemList(View view, String faci) {
        EditText editText = (EditText) view.findViewById(R.id.new_note);
        String message = editText.getText().toString();
        Log.d("TAG", message + " " + faci);
        myDb.insertRow(message, faci);
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
}
