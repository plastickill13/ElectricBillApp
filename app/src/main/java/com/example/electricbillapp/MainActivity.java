package com.example.electricbillapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etUserName, etConsumption;
    private TextView tvTotalBill;
    private Button btnCalculate;

    private static final String INSERT_URL = "http://192.168.1.22/electricbilldb/insert_bill.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUserName = findViewById(R.id.etUserName);
        etConsumption = findViewById(R.id.etConsumption);
        tvTotalBill = findViewById(R.id.tvTotalBill);
        btnCalculate = findViewById(R.id.btnCalculate);

        btnCalculate.setOnClickListener(view -> {
            String userName = etUserName.getText().toString();
            String consumptionStr = etConsumption.getText().toString();

            if (userName.isEmpty() || consumptionStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double consumption = Double.parseDouble(consumptionStr);
            double ratePerKWh = 0.12;
            double totalBill = consumption * ratePerKWh;

            tvTotalBill.setText("Total Bill: ₱" + String.format("%.2f", totalBill));

            saveBillToDatabase(userName, consumption, totalBill);
        });


    }

    private void saveBillToDatabase(String userName, double consumption, double totalBill) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, INSERT_URL,
                response -> {
                    Toast.makeText(this, "Bill saved successfully", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(this, "Error saving bill: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_name", userName);
                params.put("consumption", String.valueOf(consumption));
                params.put("total_bill", String.valueOf(totalBill));
                params.put("bill_date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}
