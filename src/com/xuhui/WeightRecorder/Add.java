package com.xuhui.WeightRecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaox_000 on 2016-07-05.
 */
public class Add extends Activity {
    TextView tv_time;
    EditText et_weight;
    EditText et_ps;
    TextView Operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);
        tv_time = (TextView) findViewById(R.id.tv_time);
        et_weight = (EditText) findViewById(R.id.et_weight);
        Operation = (TextView) findViewById(R.id.Operation);
        et_ps = (EditText) findViewById(R.id.et_ps);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        tv_time.setText(formatter.format(curDate));
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
            String time = tv_time.getText().toString();
            double weight = Double.parseDouble(et_weight.getText().toString());
            String ps = et_ps.getText().toString();
            if (ps.isEmpty()) {
                ps = "无";
            }
            Intent data = new Intent();
            data.putExtra("Time", time);
            data.putExtra("Weight", weight);
            data.putExtra("PS", ps);
            setResult(1, data);
            this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Cancel(View view) {
        this.finish();
    }
}
