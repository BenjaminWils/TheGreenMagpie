package com.ig2i.thegreenmagpie;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryActivity extends Activity {
    private final int LIMIT = 30;
    private ObjectPreference objectPreference;
    private User currentUser;
    private TableLayout tableLayout;

    private List<Transaction> makeTransactionsList(String serverData){
        List<Transaction> transactionsList = new ArrayList<Transaction>();
        try{
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
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return transactionsList;
    }

    private void fillTransactionsListView(List<Transaction> transactionList){
        TableRow header = new TableRow(this);
        header.setMinimumWidth(tableLayout.getWidth());
        header.setBackgroundColor(getResources().getColor(R.color.grey));

        TextView header1 = new TextView(this);
        header1.setText("Date");
        header1.setTextColor(getResources().getColor(R.color.black));
        header1.setPadding(5, 5, 40, 5);

        TextView header2 = new TextView(this);
        header2.setText("Amount");
        header2.setTextColor(getResources().getColor(R.color.black));
        header2.setPadding(5, 5, 40, 5);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            header1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            header2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        header.addView(header1);
        header.addView(header2);
        tableLayout.addView(header);

        int alternativeColor = R.color.very_light_green;

        for(Transaction transaction : transactionList){
            TableRow row = new TableRow(this);
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
            if(transaction.getType() == Operation.SPENDING){ //inverse pour vendeur
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
            amountTextView.setText(prefix+" $"+String.valueOf(transaction.getAmount()));

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

        objectPreference = (ObjectPreference) this.getApplication();
        ComplexPreferences complexPreferences = objectPreference.getComplexPreference();

        currentUser = complexPreferences.getObject("user", User.class);

        Loader.start(this);
        new GetHistory(new GetHistory.AsyncResponse() {
            @Override
            public void GetHistoryIsFinished(String output) {
                Log.d("history", output);
                List<Transaction> transactionsList = makeTransactionsList(output);
                fillTransactionsListView(transactionsList);
                Loader.end();
            }
        }).execute(currentUser.getEmail(), String.valueOf(LIMIT));
    }
}
