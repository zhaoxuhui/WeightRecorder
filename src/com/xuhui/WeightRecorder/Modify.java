package com.xuhui.WeightRecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaox_000 on 2016-07-05.
 */
public class Modify extends Activity {
    EditText et_time;
    EditText et_weight;
    EditText et_ps;
    TextView Operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify);
        Operation = (TextView) findViewById(R.id.Operation);
        et_time = (EditText) findViewById(R.id.tv_time);
        et_weight = (EditText) findViewById(R.id.et_weight);
        et_ps = (EditText) findViewById(R.id.et_ps);
        Intent intent = getIntent();
        int Position = intent.getIntExtra("Position", 0);
        et_time.setText(MyActivity.weightList.get(Position).time);
        et_weight.setText(MyActivity.weightList.get(Position).weight + "");
        et_ps.setText(MyActivity.weightList.get(Position).ps);
        et_ps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_ps.getText().toString().isEmpty()) {
                    Operation.setVisibility(View.INVISIBLE);
                } else {
                    Operation.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void Clear(View view) {
        et_ps.setText("");
    }

    public void Confirm(View view) {
        try {
            String time = et_time.getText().toString();
            double weight = Double.parseDouble(et_weight.getText().toString());
            String ps = et_ps.getText().toString();
            if (ps.isEmpty()) {
                ps = "无";
            }
            Intent data = new Intent();
            data.putExtra("Time", time);
            data.putExtra("Weight", weight);
            data.putExtra("PS", ps);
            setResult(2, data);
            this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Cancel(View view) {
        this.finish();
    }
}
