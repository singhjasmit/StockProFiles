package com.androidclass.stockprofiles;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.androidclass.stockprofiles.service.Stock;
import com.androidclass.stockprofiles.service.StockService;

import java.util.ArrayList;

/**
 * Created by jsingh on 9/2/16.
 */
public class MyCustomAdapter extends ArrayAdapter<StockHolding> {


    StockService ss;
    Resources res;

    public MyCustomAdapter(Context context, ArrayList<StockHolding> stocks) {
        super(context, 0, stocks);
        ss = new StockService(context);
        res = context.getResources();


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        StockHolding sh = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.stock_row, parent, false);
            viewHolder.qty = (TextView) convertView.findViewById(R.id.row_qty);
            viewHolder.price = (TextView) convertView.findViewById(R.id.row_price);
            viewHolder.change = (TextView) convertView.findViewById(R.id.row_change);
            viewHolder.name = (TextView) convertView.findViewById(R.id.row_name);
            viewHolder.value = (TextView) convertView.findViewById(R.id.row_value);
            viewHolder.delBtn = (Button) convertView.findViewById(R.id.row_button);

            // Attach the click event handler
            viewHolder.delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    // Access the row position here to get the correct data item
                    StockHolding sh = getItem(position);
                    remove(sh);
                    notifyDataSetChanged();
                }
            });


            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object

        Stock st = ss.getStock(sh.getSymbol());



        String.format(res.getString(R.string.high), "" + Util.round(st.getHigh(), 2));
        viewHolder.qty.setText(String.format(res.getString(R.string.qty),  sh.getQty() , sh.getSymbol()));
        viewHolder.price.setText(String.format(res.getString(R.string.price),  "" + Util.round(st.getPrice(), 2)));
        viewHolder.change.setText(String.format(res.getString(R.string.change), "" + st.getChange()));

        if (st.getChange() >= 0)
            viewHolder.change.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
        else
            viewHolder.change.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        viewHolder.name.setText(st.getName());
        viewHolder.value.setText(res.getString(R.string.price, "" + Util.round(sh.qty * st.getPrice(), 2)));

        viewHolder.delBtn.setTag(position);

        // Return the completed view to render on screen
        return convertView;


    }


    // View lookup cache
    private static class ViewHolder {
        TextView qty;
        TextView price;
        TextView change;
        TextView name;
        TextView value;

        Button delBtn;

    }


}

