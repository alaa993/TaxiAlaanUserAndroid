package com.taxialaan.app.Activities.wallet;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.taxialaan.app.R;
import com.taxialaan.app.views.CellSettingView;

public class ActivityWalletManager extends AppCompatActivity {
    ImageView backArrow;
    CellSettingView sendMoneyCell, transactionCell, accountsChargeCell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wallet_manager);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        readView();
        functionView();
    }


    private void readView() {
        backArrow = findViewById(R.id.backArrow);
        sendMoneyCell = findViewById(R.id.sendMoneyCell);
        transactionCell = findViewById(R.id.transactionCell);
        accountsChargeCell = findViewById(R.id.accountsChargeCell);
    }

    private void functionView() {
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        sendMoneyCell.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityWalletManager.this, ActivitySendMoney.class));
            }
        });

        transactionCell.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityWalletManager.this, ActivityTransactionList.class));
            }
        });

        accountsChargeCell.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityWalletManager.this, ActivityAccountsCharge.class));
            }
        });

    }
}
