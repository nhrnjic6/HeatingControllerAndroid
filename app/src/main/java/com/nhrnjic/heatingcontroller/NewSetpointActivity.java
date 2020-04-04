package com.nhrnjic.heatingcontroller;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;
import com.nhrnjic.heatingcontroller.service.HeatingControlService;

import org.joda.time.DateTime;

public class NewSetpointActivity extends AppCompatActivity {
    private DbSetpoint setpoint;
    private HeatingControlService heatingControlService;

    private Button mDayButton;
    private Button mTimeButton;
    private Button mSaveButton;

    private SeekBar mTempBar;

    private CheckBox mWorkDayCb;
    private CheckBox mWeekendCb;

    private TextView mTempProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_setpoint);

        setpoint = new DbSetpoint();
        heatingControlService = new HeatingControlService();

        mDayButton = findViewById(R.id.new_setpoint_day);
        mTimeButton = findViewById(R.id.new_setpoint_time);
        mSaveButton = findViewById(R.id.new_setpoint_save);

        mTempBar = findViewById(R.id.new_setpoint_temperature);

        mWorkDayCb = findViewById(R.id.new_setpoint_cb_workday);
        mWeekendCb = findViewById(R.id.new_setpoint_cb_weekend);

        mTempProgress = findViewById(R.id.new_setpoint_temperature_progress);

        // Setup day picker dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose day of week");

        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        builder.setItems(daysOfWeek, (dialog, which) -> {
            mDayButton.setText(daysOfWeek[which]);
            setpoint.setDay(which + 1);
        });

        final AlertDialog dialog = builder.create();
        mDayButton.setOnClickListener(v -> dialog.show());

        // Setup time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    setpoint.setHour(hourOfDay);
                    setpoint.setMinute(minute);
                    DateTime now = DateTime.now();
                    mTimeButton.setText(setpoint.getTimeText());
                }, 0, 0, true);

        mTimeButton.setOnClickListener(v -> timePickerDialog.show());

        // Setup progress bar
        mTempBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTempProgress.setText("Set temperature: " + progress + " \u2103");
                setpoint.setTemperature(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSaveButton.setOnClickListener(v -> heatingControlService.saveNewSetpoint(
                setpoint, mWorkDayCb.isChecked(), mWeekendCb.isChecked(), systemStatus -> {
            // react to addition of new setpoint
                    Intent intent = new Intent(NewSetpointActivity.this, MainActivity.class);
                    startActivity(intent);
        }));
    }
}
