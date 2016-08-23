package com.xuhui.WeightRecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Created by zhaox_000 on 2016-07-06.
 */
public class BMI extends Activity {
    EditText et_height;
    TextView tv_sW;
    Button btn_bmi;
    double sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bmi);
        btn_bmi = (Button) findViewById(R.id.btn_bmi);
        Intent intent = getIntent();
        int Flag = intent.getIntExtra("Flag", 0);
        if (Flag == 1) {
            btn_bmi.setVisibility(View.VISIBLE);
        } else {
            btn_bmi.setVisibility(View.GONE);
        }
        et_height = (EditText) findViewById(R.id.et_height);
        tv_sW = (TextView) findViewById(R.id.tv_sW);
        et_height.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_height.getText().toString().isEmpty()) {
                    tv_sW.setText("");
                } else {
                    sw = Math.pow(Double.parseDouble(et_height.getText().toString()), 2) * 21;
                    DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
                    decimalFormat.applyPattern("0.00");
                    tv_sW.setText(decimalFormat.format(sw));
                }
            }
        });
    }

    public void SetTarget(View view) {
        if (tv_sW.getText().toString().isEmpty()) {
            Toast.makeText(this, "您还没有输入", Toast.LENGTH_LONG).show();
        } else {
            sw = Double.parseDouble(tv_sW.getText().toString());
            Intent data = new Intent();
            data.putExtra("Standard", sw);
            setResult(1, data);
            this.finish();
        }
    }
}
