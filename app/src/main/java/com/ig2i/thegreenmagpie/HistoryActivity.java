package com.ig2i.thegreenmagpie;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ig2i.thegreenmagpie.buyer.BuyerConnectionActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryActivity extends Activity {
    private final int LIMIT = 30;
    private TableLayout tableLayout;
    private TextView returnTextView;
    private ImageView returnImg;
    private String email;

    private List<Transaction> makeTransactionsList(String serverData){
        List<Transaction> transactionsList = new ArrayList<Transaction>();
        if (serverData != null && !serverData.equals("null")) {
            try {
                JSONArray jsonArray = new JSONArray(serverData);
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject row = jsonArray.getJSONObject(index);
                    transactionsList.add(new Transaction(
                            Operation.valueOf(row.getString("operation")),
                            row.getDouble("amount"),
                            row.getString("date"),
                            row.getString("clientEmail"),
                            row.getString("sellerEmail")
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return transactionsList;
    }

    private void fillTransactionsListView(List<Transaction> transactionList){
        TableRow header = new TableRow(this);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);

        TableLayout headerTable = new TableLayout(this);

        header.setMinimumWidth(container.getWidth());
        header.setBackgroundColor(getResources().getColor(R.color.grey));

        TextView header1 = new TextView(this);
        header1.setText("Date");
        header1.setTextColor(getResources().getColor(R.color.black));
        header1.setPadding(5, 5, 5, 5);
        header1.setWidth(tableLayout.getWidth() / 2);

        TextView header2 = new TextView(this);
        header2.setText("Amount");
        header2.setTextColor(getResources().getColor(R.color.black));
        header2.setPadding(5, 5, 5, 5);
        header2.setWidth(tableLayout.getWidth() / 2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            header1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            header2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        header.addView(header1);
        header.addView(header2);
        headerTable.addView(header);
        container.addView(headerTable, 0);

        int alternativeColor = R.color.very_light_green;

        for(final Transaction transaction : transactionList){
            TableRow row = new TableRow(this);
            row.setPadding(20,20,20,20);
            row.setBackgroundColor(getResources().getColor(alternativeColor));
            alternativeColor = alternativeColor == R.color.very_light_green ?
                    R.color.whole_interface_background : R.color.very_light_green;

            TextView dateTextView = new TextView(this);
            dateTextView.setWidth(tableLayout.getWidth() / 2);

            TextView amountTextView = new TextView(this);
            amountTextView.setWidth(tableLayout.getWidth() / 2);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                amountTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }

            String prefix;
            Operation conditionForRedColor;
            if(("seller").equals(email)){
                conditionForRedColor = Operation.REFUND;
            }
            else{
                conditionForRedColor = Operation.SPENDING;
            }
            if(transaction.getType() == conditionForRedColor){ //inverse pour vendeur
                dateTextView.setTextColor(getResources().getColor(R.color.red));
                amountTextView.setTextColor(getResources().getColor(R.color.red));
                prefix = "-";
            }
            else{
                dateTextView.setTextColor(getResources().getColor(R.color.green));
                amountTextView.setTextColor(getResources().getColor(R.color.green));
                prefix = "+";
            }
            dateTextView.setText(transaction.getDate());
            amountTextView.setText(prefix + " $" + String.valueOf(transaction.getAmount()));

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), TransactionDetailsActivity.class);
                    intent.putExtra("transaction", transaction);
                    startActivity(intent);
                }
            });

            row.addView(dateTextView);
            row.addView(amountTextView);
            tableLayout.addView(row);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        tableLayout = (TableLayout) findViewById(R.id.tableLayout);

        returnTextView = (TextView) findViewById(R.id.historyActivityReturnTextView);
        returnImg = (ImageView) findViewById(R.id.returnView);

        returnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        returnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        email = this.getIntent().getStringExtra("email");

        Loader.start(this);
        new GetHistory(new GetHistory.AsyncResponse() {
            @Override
            public void GetHistoryIsFinished(String output) {
                Log.d("history", output);
                List<Transaction> transactionsList = makeTransactionsList(output);
                fillTransactionsListView(transactionsList);
                Loader.end();
            }
        }).execute(email, String.valueOf(LIMIT));
    }
}
