package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
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
    /*@BindView(R.id.chartStockHistory)
    TextView mChartHistory;*/
    @BindView(R.id.tvPrice)
    TextView mPrice;
    @BindView(R.id.ivTrending)
    ImageView mTrendingImage;
    LineChart mChartHistory;

    private static final int ID_DETAIL_LOADER = 353;

    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        symbol = intent.getStringExtra(Intent.EXTRA_TEXT);

        mChartHistory = (LineChart) findViewById(R.id.chartStockHistory);
        XAxis xaxis = mChartHistory.getXAxis();
        xaxis.setTextColor(ContextCompat.getColor(this, R.color.primaryText));
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);

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
        float rawAbsoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);

        if (rawAbsoluteChange > 0) {
            mTrendingImage.setBackgroundResource(R.drawable.ic_trending_up_black_48dp);
            mTrendingImage.setContentDescription(this.getString(R.string.trending_up_image));
        } else {
            mTrendingImage.setBackgroundResource(R.drawable.ic_trending_down_black_48dp);
            mTrendingImage.setContentDescription(this.getString(R.string.trending_down_image));
        }

        mStockName.setText(symbol);
        mStockCurrency.setText(currency);
        mStockExchange.setText(stockExchange);
/*
        mChartHistory.setText(history);
*/
        mPrice.setText(price + " " + currency);
        generateChartData(history, price);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void generateChartData(String history, float price) {
        int count = 0;
        List<Entry> entries = new ArrayList<Entry>();
        String[] historyArray = history.split("\\n");

       /* if (isRtl(this)) {
            count = historyArray.length;
            for (int i = historyArray.length - 1; i >= 0; i--) {
                String[] row = historyArray[i].split(", ");
                float valueY = Float.valueOf(row[1]);
                entries.add(new Entry(count--, valueY));
            }
            entries.add(new Entry(count, price));
        } else {*/

            for (String historyValue : historyArray) {
                String[] row = historyValue.split(", ");
                float valueY = Float.valueOf(row[1]);
                entries.add(new Entry(count++, valueY));
            }
            entries.add(new Entry(count, price));

        /*}*/
        LineDataSet dataSet = new LineDataSet(entries, symbol); // add entries to dataset
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorAccent));
        dataSet.setDrawCircles(false);
        LineData lineData = new LineData(dataSet);
        mChartHistory.setData(lineData);
        mChartHistory.invalidate(); // refresh
    }

    public boolean isRtl(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;

    }

}
