package com.example.electricbillapp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class BillHistoryActivity extends AppCompatActivity {

    private RecyclerView rvBillHistory;
    private EditText etSearchBill;
    private static final String GET_BILLS_URL = "http://192.168.1.22/electricbilldb/get_bills.php";
    private List<Bill> fullBillList = new ArrayList<>();
    private BillAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_history);

        rvBillHistory = findViewById(R.id.rvBillHistory);
        rvBillHistory.setLayoutManager(new LinearLayoutManager(this));
        etSearchBill = findViewById(R.id.etSearchBill);

        String mode = getIntent().getStringExtra("mode");
        if ("specific".equals(mode)) {
            etSearchBill.setVisibility(View.VISIBLE);
            etSearchBill.setHint("Enter Bill ID or Customer name");
            TextView tvInstructions = findViewById(R.id.tvInstructions);
            tvInstructions.setVisibility(View.VISIBLE);
            tvInstructions.setText("Search by: Bill ID/Customer name");
            adapter = new BillAdapter(new ArrayList<>());
            rvBillHistory.setAdapter(adapter);
        } else {
            etSearchBill.setVisibility(View.GONE);
            findViewById(R.id.tvInstructions).setVisibility(View.GONE);
            fetchBillHistory();
        }

        etSearchBill.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    try {
                        int billId = Integer.parseInt(query);
                        fetchSpecificBill(String.valueOf(billId), true);
                    } catch (NumberFormatException e) {
                        fetchSpecificBill(query, false);
                    }
                } else {
                    adapter.updateList(new ArrayList<>());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchBillHistory() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_BILLS_URL,
                response -> {
                    try {
                        JSONArray bills = new JSONArray(response);
                        fullBillList.clear();

                        for (int i = 0; i < bills.length(); i++) {
                            JSONObject billObject = bills.getJSONObject(i);
                            Bill bill = new Bill();
                            bill.id = billObject.getInt("id");
                            bill.userName = billObject.getString("user_name");
                            bill.consumption = billObject.getDouble("consumption");
                            bill.totalBill = billObject.getDouble("total_bill");
                            bill.billDate = billObject.getString("bill_date");
                            fullBillList.add(bill);
                        }

                        adapter = new BillAdapter(fullBillList);
                        rvBillHistory.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error fetching bills: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void fetchSpecificBill(String searchQuery, boolean isId) {
        String url = GET_BILLS_URL + "?" + 
                    (isId ? "id=" : "username=") + 
                    Uri.encode(searchQuery);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray bills = new JSONArray(response);
                        List<Bill> billList = new ArrayList<>();

                        for (int i = 0; i < bills.length(); i++) {
                            JSONObject billObject = bills.getJSONObject(i);
                            Bill bill = new Bill();
                            bill.id = billObject.getInt("id");
                            
                            if (isId && bill.id != Integer.parseInt(searchQuery)) {
                                continue;
                            }
                            
                            bill.userName = billObject.getString("user_name");
                            
                            if (!isId && !bill.userName.equals(searchQuery)) {
                                continue;
                            }
                            
                            bill.consumption = billObject.getDouble("consumption");
                            bill.totalBill = billObject.getDouble("total_bill");
                            bill.billDate = billObject.getString("bill_date");
                            billList.add(bill);
                        }

                        if (billList.isEmpty()) {
                            String message = isId ? 
                                "No bill found with ID: " + searchQuery :
                                "No bills found for username: " + searchQuery;
                            Toast.makeText(BillHistoryActivity.this, 
                                message, 
                                Toast.LENGTH_SHORT).show();
                        }

                        adapter.updateList(billList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        adapter.updateList(new ArrayList<>());
                        Toast.makeText(BillHistoryActivity.this, 
                            "ID did not match",
                            Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    adapter.updateList(new ArrayList<>());
                    Toast.makeText(BillHistoryActivity.this, 
                        "Error fetching bills: " + error.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

}
