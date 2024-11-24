package com.example.electricbillapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button btnSaveBill = findViewById(R.id.btnSaveBill);
        Button btnViewAll = findViewById(R.id.btnViewAll);
        Button btnViewSpecific = findViewById(R.id.btnViewSpecific);

        btnSaveBill.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        btnViewAll.setOnClickListener(view -> {
            Intent intent = new Intent(this, BillHistoryActivity.class);
            startActivity(intent);
        });

        btnViewSpecific.setOnClickListener(view -> {
            Intent intent = new Intent(this, BillHistoryActivity.class);
            intent.putExtra("mode", "specific");
            startActivity(intent);


        });
    }
} 