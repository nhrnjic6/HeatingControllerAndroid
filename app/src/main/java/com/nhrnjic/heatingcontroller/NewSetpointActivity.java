package com.nhrnjic.heatingcontroller;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;
import com.nhrnjic.heatingcontroller.exception.FieldNotSetException;
import com.nhrnjic.heatingcontroller.exception.MaxSetpointSizeException;
import com.nhrnjic.heatingcontroller.service.HeatingControlService;

import org.joda.time.DateTime;

public class NewSetpointActivity extends AppCompatActivity {
    private DbSetpoint mSetpoint;

    private HeatingControlService heatingControlService;

    private FrameLayout mProgressWheelParent;

    private Button mDayButton;
    private Button mTimeButton;
    private Button mSaveButton;

    private SeekBar mTempBar;
    private MaterialCardView mRepeatConfigParent;

    private CheckBox mWorkDayCb;
    private CheckBox mWeekendCb;

    private TextView mTempProgress;
    private TextView mDayError;
    private TextView mTempError;
    private TextView mRepeatError;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_setpoint);

        heatingControlService = new HeatingControlService();

        mProgressWheelParent = findViewById(R.id.progress_wheel_parent);

        mDayButton = findViewById(R.id.new_setpoint_day);
        mTimeButton = findViewById(R.id.new_setpoint_time);
        mSaveButton = findViewById(R.id.new_setpoint_save);

        mTempBar = findViewById(R.id.new_setpoint_temperature);
        mRepeatConfigParent = findViewById(R.id.repeat_config_parent);

        mWorkDayCb = findViewById(R.id.new_setpoint_cb_workday);
        mWeekendCb = findViewById(R.id.new_setpoint_cb_weekend);

        mTempProgress = findViewById(R.id.new_setpoint_temperature_progress);
        mDayError = findViewById(R.id.day_error_msg);
        mTempError = findViewById(R.id.temperature_error_msg);
        mRepeatError = findViewById(R.id.repeat_error_msg);

        setupUIWithData();

        // Setup day picker dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose day of week");

        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        builder.setItems(daysOfWeek, (dialog, which) -> {
            mDayButton.setText(daysOfWeek[which]);
            mSetpoint.setDay(which + 1);
        });

        final AlertDialog dialog = builder.create();
        mDayButton.setOnClickListener(v -> dialog.show());

        // Setup time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    mSetpoint.setHour(hourOfDay);
                    mSetpoint.setMinute(minute);
                    DateTime now = DateTime.now();
                    mTimeButton.setText(mSetpoint.getTimeText());
                }, 0, 0, true);

        mTimeButton.setOnClickListener(v -> timePickerDialog.show());

        // Setup progress bar
        mTempBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTempProgress.setText("Set temperature: " + progress + " \u2103");
                mSetpoint.setTemperature((double) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSaveButton.setOnClickListener(v -> {
            mProgressWheelParent.setVisibility(View.VISIBLE);
            mTempError.setVisibility(View.GONE);
            mDayError.setVisibility(View.GONE);
            mRepeatError.setVisibility(View.GONE);

            try {
                if(mSetpoint.getId() == null){
                    heatingControlService.saveNewSetpoint(
                            mSetpoint, mWorkDayCb.isChecked(), mWeekendCb.isChecked(), systemStatus -> {
                                runOnUiThread(() -> {
                                    Intent intent = new Intent(NewSetpointActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                });
                            });
                }else{
                    heatingControlService.updateSetpoint(
                            mSetpoint, systemStatus -> {
                                runOnUiThread(() -> {
                                    Intent intent = new Intent(NewSetpointActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                });
                            });
                }
            } catch (FieldNotSetException e) {
                mProgressWheelParent.setVisibility(View.GONE);
                if(e.getField().equals("Temperature")){
                    mTempError.setText(e.getMessage());
                    mTempError.setVisibility(View.VISIBLE);
                }else{
                    mDayError.setText(e.getMessage());
                    mDayError.setVisibility(View.VISIBLE);
                }
            } catch (MaxSetpointSizeException e) {
                mProgressWheelParent.setVisibility(View.GONE);
                if(e.isRepeat()){
                    mRepeatError.setText(e.getMessage());
                    mRepeatError.setVisibility(View.VISIBLE);
                }else {
                    mDayError.setText(e.getMessage());
                    mDayError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupUIWithData(){
        DbSetpoint setpoint = (DbSetpoint) getIntent()
                .getSerializableExtra(SetpointListActivity.EDIT_SETPOINT_INDEX_KEY);

        if(setpoint != null){
            mSetpoint = setpoint;
            mDayButton.setText(mSetpoint.dayToString());
            mTimeButton.setText(mSetpoint.getTimeText());
            mTempBar.setProgress(mSetpoint.getTemperature().intValue());
            mTempBar.refreshDrawableState();
            mRepeatConfigParent.setVisibility(View.GONE);
            mTempProgress.setText("Set temperature: " + mSetpoint.getTemperature().intValue() + " \u2103");
        }else{
            mSetpoint = new DbSetpoint();
        }
    }
}
