package com.example.joao;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Joao on 09/09/2015.
 */
public class MyArrayAdapter extends ArrayAdapter<String>{

    private final Context context;
    private final String[] valuesRow;
    private final String[] valuesFacility;

    public MyArrayAdapter(Context context, String[] valuesRow, String[] valuesFacility) {
        super(context, android.R.layout.simple_list_item_1, valuesRow);
        this.context = context;
        this.valuesRow = valuesRow;
        this.valuesFacility = valuesFacility;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView textView = (TextView) rowView.findViewById(android.R.id.text1);
        textView.setText(valuesRow[position]);
        // Change the icon for Windows and iPhone
        String s = valuesFacility[position];

        if(s.equals("0")){
            textView.setBackgroundColor(Color.RED);
        }else if(s.equals("1")){
            textView.setBackgroundColor(Color.YELLOW);
        }else{
            textView.setBackgroundColor(Color.GREEN);
        }
        return rowView;
    }
}
