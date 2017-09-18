package com.trendyfy.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.trendyfy.R;

public class DeliveryCompleteActivity extends AppCompatActivity implements View.OnClickListener {


    private Activity mActivity;

    private static String TAG = DeliveryCompleteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_complete);
        mActivity = DeliveryCompleteActivity.this;
        init();
    }


    private void init() {

        Button btn_continue_shopping = (Button) findViewById(R.id.btn_continue_shopping);
        btn_continue_shopping.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_continue_shopping:
                finish();
                Intent intent = new Intent(mActivity, HomeActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(mActivity, HomeActivity.class);
        startActivity(intent);
    }
}
