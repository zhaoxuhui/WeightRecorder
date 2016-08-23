package com.xuhui.WeightRecorder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by zhaox_000 on 2016-07-06.
 */
public class Target extends Activity {
    EditText et_target;
    TextView Operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.target);
        Operation = (TextView) findViewById(R.id.Operation);
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        et_target = (EditText) findViewById(R.id.et_target);
        et_target.setText(sharedPreferences.getFloat("Target", 0) + "");
        et_target.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_target.getText().toString().isEmpty()) {
                    Operation.setVisibility(View.INVISIBLE);
                } else {
                    Operation.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void Confirm(View view) {
        try {
            double target = Double.parseDouble(et_target.getText().toString());
            Intent data = new Intent();
            data.putExtra("Target", target);
            setResult(3, data);
            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "设置失败", Toast.LENGTH_SHORT).show();
        }
        this.finish();
    }

    public void Cancel(View view) {
        this.finish();
    }

    public void BMI(View view) {
        Intent intent = new Intent(this, com.xuhui.WeightRecorder.BMI.class);
        intent.putExtra("Flag", 1);
        startActivityForResult(intent, 1);
    }

    public void Clear(View view) {
        et_target.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1:
                double sw = data.getDoubleExtra("Standard", 0);
                et_target.setText(sw + "");
                break;

        }
    }
}
