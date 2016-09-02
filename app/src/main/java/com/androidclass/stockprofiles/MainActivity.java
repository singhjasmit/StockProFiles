package com.androidclass.stockprofiles;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidclass.stockprofiles.service.Stock;
import com.androidclass.stockprofiles.service.StockService;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final static String INT_FILE = "stocks_out_internal.csv";
    final static String TAG = MainActivity.class.getSimpleName();
    StockService ss;
    EditText edSymbol, edQty;
    Button btnAdd;
    TextView txTotalValue;
    ImageView ivSetting;
    ListView myListView;
    MyCustomAdapter myAdapter;
    private ArrayList<StockHolding> userStocks = new ArrayList<StockHolding>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        edSymbol = (EditText) findViewById(R.id.txt_symbol);
        edQty = (EditText) findViewById(R.id.txt_qty);
        btnAdd = (Button) findViewById(R.id.add_button);
        ivSetting = (ImageView) findViewById(R.id.setting);
        myListView = (ListView) findViewById(R.id.lv);

        readFileFromInternalStorage();

        // Create the adapter to convert the array to views
        myAdapter = new MyCustomAdapter(this, userStocks);
        myListView.setAdapter(myAdapter);

        // React to user clicks on item
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long id) {


                StockHolding sh = myAdapter.getItem(position);
                showDetail(sh);

            }
        });


        ivSetting.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             showSettings();

                                         }
                                     }
        );


        btnAdd.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          String sym = edSymbol.getText().toString();
                                          String qtyStr = edQty.getText().toString();
                                          int qty = 1;


                                          if ((sym == null) || sym.isEmpty()) {
                                              Toast.makeText(getBaseContext(), "Stock Symbol is required", Toast.LENGTH_SHORT).show();
                                          } else {
                                              Stock st = ss.getStock(sym.toUpperCase());
                                              if (st != null) {

                                                  if ((qtyStr != null)) {
                                                      try {
                                                          qty = Integer.parseInt(qtyStr);
                                                      } catch (Exception e) {
                                                          qty = -1;
                                                      }
                                                      if (qty < 1) qty = 1;
                                                  }

                                                  if (!updateUserHolding(sym.toUpperCase(), qty)) {
                                                      StockHolding sh = new StockHolding(sym.toUpperCase(), qty);
                                                      userStocks.add(sh);
                                                  }


                                                  updateRows();

                                              } else {
                                                  Toast.makeText(getBaseContext(), "Stock Symbol is not available", Toast.LENGTH_SHORT).show();

                                              }
                                          }

                                      }
                                  }

        );
        ss = new StockService(getBaseContext());

        updateRows();


    }




    private boolean updateUserHolding(String symbol, int qty) {

        boolean updated = false;
        for (StockHolding s : userStocks) {
            if (s.getSymbol().equals(symbol)) {
                s.setQty(s.getQty() + qty);
                updated = true;
                break;
            }
        }
        return updated;
    }


    private void updateRows() {
        writeFileToInternalStorage();

        myAdapter.notifyDataSetChanged();

    }


    private void showDetail(StockHolding sh) {

        Intent i = new Intent(this, StockDetailActivity.class);
        startActivity(i);


    }

    private void showSettings() {

        Intent i = new Intent(this, SettingActivity.class);
        startActivity(i);

    }


    @Override
    protected void onPause() {
        super.onPause();
        writeFileToInternalStorage();
    }

    private void writeFileToInternalStorage() {

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(INT_FILE, Context.MODE_PRIVATE);
            Log.d(TAG, "writing to internal storage");


            StringBuilder sb = new StringBuilder();

            for (StockHolding sh : userStocks) {
                sb.append(sh.getSymbol()).append(":")
                        .append(sh.getQty()).append("\n");
            }
            outputStream.write(sb.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.d(TAG, "Exception e" + e.toString());
            e.printStackTrace();
        }
    }


    private void readFileFromInternalStorage() {


        BufferedReader reader = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;


        try {


            fis = openFileInput(INT_FILE);
            isr = new InputStreamReader(fis);
            reader = new BufferedReader(isr);

            String line;
            userStocks.clear();

            Log.d(TAG, "Reading : " + INT_FILE);

            while ((line = reader.readLine()) != null) {

                Log.d(TAG, "Line : " + line);
                String[] separated = line.split(":");
                if (separated.length == 2) {
                    try {

                        String sym = separated[0];
                        int qty = Integer.parseInt(separated[1]);
                        StockHolding sh = new StockHolding(sym, qty);
                        userStocks.add(sh);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());

                    }
                } else {
                    Log.d(TAG, "could not create stock: " + separated.length);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}




