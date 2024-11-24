package com.example.electricbillapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {

    private List<Bill> bills;

    // URL to fetch bills
    private static final String GET_BILLS_URL = "http://192.168.1.21/electricbilldb/get_bills.php";

    public BillAdapter(List<Bill> bills) {
        this.bills = bills;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = bills.get(position);
        holder.bind(bill);
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    // Method to update the displayed list dynamically
    public void updateList(List<Bill> updatedBills) {
        this.bills = updatedBills; // Update the internal list
        notifyDataSetChanged();   // Notify the adapter to refresh the RecyclerView
    }

    // Getter for the GET_BILLS_URL
    public static String getBillsUrl() {
        return GET_BILLS_URL;
    }

    static class BillViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBillDetails;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBillDetails = itemView.findViewById(R.id.tvBillDetails);
        }

        public void bind(Bill bill) {
            tvBillDetails.setText("Name: " + bill.userName + "\n"
                    + "Consumption: " + bill.consumption + " kWh\n"
                    + "Total Bill: ₱" + bill.totalBill + "\n"
                    + "Date: " + bill.billDate + "\n");
        }
    }
}
