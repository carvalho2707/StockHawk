package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.tvStockName)
    TextView mStockName;
    @BindView(R.id.tvCurrency)
    TextView mStockCurrency;
    @BindView(R.id.tvExchange)
    TextView mStockExchange;
    @BindView(R.id.chartStockHistory)
    TextView mChartHistory;
    @BindView(R.id.tvPrice)
    TextView mPrice;

    private static final int ID_DETAIL_LOADER = 353;

    private String symbol;

    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat dollarFormat;
    private DecimalFormat percentageFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        symbol = intent.getStringExtra(Intent.EXTRA_TEXT);

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {
            case ID_DETAIL_LOADER:
                Uri uri = Contract.Quote.makeUriForStock(symbol);
                return new CursorLoader(this, uri, Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}), null, null, Contract.Quote.COLUMN_HISTORY + " DESC");
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }

        String stockExchange = data.getString(Contract.Quote.POSITION_EXCHANGE);
        String currency = data.getString(Contract.Quote.POSITION_CURRENCY);
        String history = data.getString(Contract.Quote.POSITION_HISTORY);
        float price = data.getFloat(Contract.Quote.POSITION_PRICE);

        if (PrefUtils.getDisplayMode(this)
                .equals(this.getString(R.string.pref_display_mode_absolute_key))) {
            //TODO
        } else {
            //TODO
        }

        mStockName.setText(symbol);
        mStockCurrency.setText(currency);
        mStockExchange.setText(stockExchange);
        mChartHistory.setText(history);
        mPrice.setText(price + " " + currency);
        /*generateChartData(history);*/


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

   /* public void generateChartData(String history) {
        List<Entry> entries = new ArrayList<Entry>();
        String[] historyArray = history.split("\\n");
        for (int i = 0; i < 20; i++) {
       *//* for (String historyValue : historyArray) {*//*
            String[] row = historyArray[i].split(", ");
            long timestamp = Long.valueOf(row[0]);
            float valueY = Float.valueOf(row[1]);
            entries.add(new Entry(timestamp, valueY));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }*/
}
