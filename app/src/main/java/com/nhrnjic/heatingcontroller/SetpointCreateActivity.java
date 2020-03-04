package com.nhrnjic.heatingcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nhrnjic.heatingcontroller.database.SetpointDatabaseService;

public class SetpointCreateActivity extends AppCompatActivity {
    private SetpointDatabaseService setpointDatabaseService = new SetpointDatabaseService();

    private Button mSaveButton;
    private EditText mDayInput;
    private EditText mHourInput;
    private EditText mMinuteInput;
    private EditText mTemperatureInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpoint_create);

        mSaveButton = findViewById(R.id.btn_save_setpoint);
        mDayInput = findViewById(R.id.input_day);
        mHourInput = findViewById(R.id.input_hour);
        mMinuteInput = findViewById(R.id.input_minute);
        mTemperatureInput = findViewById(R.id.input_temperature);

        Toast.makeText(this, mDayInput.getText().toString(), Toast.LENGTH_LONG).show();

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setpointDatabaseService.saveSetpoint(
                        mDayInput.getText().toString(),
                        mHourInput.getText().toString(),
                        mMinuteInput.getText().toString(),
                        mTemperatureInput.getText().toString()
                );

                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
