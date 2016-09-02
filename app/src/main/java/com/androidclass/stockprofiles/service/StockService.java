package com.androidclass.stockprofiles.service;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by jsingh on 8/10/16.
 */
public class StockService {

    private static HashMap<String,Stock> stockList = new HashMap<String,Stock>();
    private static boolean initComplete = false;
    private Context mCtx = null;
    private final static String ASSET_FILE = "stocks.txt";
    private final static String TAG = StockService.class.getSimpleName();



    public StockService(Context ctx) {
        mCtx = ctx;


        if (!initComplete)  readStocksFromAssetFile();

    }


    public Stock getStock(String symbol) {
        return stockList.get(symbol);

    }

    private void readStocksFromAssetFile() {

        AssetManager assetManager = mCtx.getAssets();
        BufferedReader reader = null;

        try {

            reader = new BufferedReader(new InputStreamReader(
                    assetManager.open(ASSET_FILE), "UTF-8"));

            String line;
            stockList.clear();

            Log.d(TAG, "Reading : " + ASSET_FILE);

            while ((line = reader.readLine()) != null) {

                Log.d(TAG, "Line : " + line);
                String[] separated = line.split(":");
                if (separated.length == 8) {
                    try {
                        String sym = separated[0];
                        String comp = separated[1];
                        Double price = Double.parseDouble(separated[2]);
                        Double change = Double.parseDouble(separated[3]);
                        Double high = Double.parseDouble(separated[4]);
                        Double low = Double.parseDouble(separated[5]);
                        Long vol = Long.parseLong(separated[6]);
                        String info = separated[7];


                        Stock st = new Stock(sym,comp,price,change, high, low, vol);
                        st.setInfo(info);




                        stockList.put(st.getSymbol(), st);

                    }
                    catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                else {
                    Log.d(TAG, "could not create stock: " + separated.length);
                }

            }
            initComplete = true;

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