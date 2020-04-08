package com.nhrnjic.heatingcontroller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;
import com.nhrnjic.heatingcontroller.model.SystemStatus;
import com.nhrnjic.heatingcontroller.repository.SetpointRepository;
import com.nhrnjic.heatingcontroller.service.HeatingControlService;
import com.nhrnjic.heatingcontroller.service.MqttService;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity {
    private HeatingControlService heatingControlService = new HeatingControlService();
    private SetpointRepository setpointRepository = SetpointRepository.getInstance();
    private MqttService mqttService;

    private FrameLayout mProgressWheelParent;

    private TextView mTempText;
    private TextView mStatusUpdateAt;
    private TextView mActiveSetpointLabel;
    private TextView mActiveSetpoint;

    private Button defaultModeButton;
    private Button onModeButton;
    private Button offModeButton;

    private Drawable defaultButtonDrawable;
    private Drawable onButtonDrawable;
    private Drawable offButtonDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mqttService = MqttService.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressWheelParent = findViewById(R.id.progress_wheel_parent);

        mStatusUpdateAt = findViewById(R.id.tv_status_update_at);
        mTempText = findViewById(R.id.temperature);
        mActiveSetpointLabel = findViewById(R.id.active_setpoint_label);
        mActiveSetpoint = findViewById(R.id.active_setpoint);

        defaultModeButton = findViewById(R.id.btn_default_mode);
        defaultButtonDrawable = defaultModeButton.getBackground();
        onModeButton = findViewById(R.id.btn_on_mode);
        onButtonDrawable = onModeButton.getBackground();
        offModeButton = findViewById(R.id.btn_off_mode);
        offButtonDrawable = offModeButton.getBackground();

        updateSystemStatus();

        try {
            mqttService.getSystemStatus(systemStatus -> {
                setpointRepository.setSystemStatus(systemStatus);
                updateSystemStatus();
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        offModeButton.setOnClickListener(v -> {
            mProgressWheelParent.setVisibility(View.VISIBLE);
            heatingControlService.changeRulesMode(
                    0,
                    systemStatus -> updateSystemStatus());
        });

        onModeButton.setOnClickListener(v -> {
            mProgressWheelParent.setVisibility(View.VISIBLE);
            heatingControlService.changeRulesMode(
                    1,
                    systemStatus -> updateSystemStatus());
        });

        defaultModeButton.setOnClickListener(v -> {
            mProgressWheelParent.setVisibility(View.VISIBLE);
            heatingControlService.changeRulesMode(
                    2,
                    systemStatus -> updateSystemStatus());
        });

        ValueLineChart mCubicValueLineChart = findViewById(R.id.cubiclinechart);

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        series.addPoint(new ValueLinePoint("Jan", 2.4f));
        series.addPoint(new ValueLinePoint("Feb", 3.4f));
        series.addPoint(new ValueLinePoint("Mar", .4f));
        series.addPoint(new ValueLinePoint("Apr", 1.2f));
        series.addPoint(new ValueLinePoint("Mai", 2.6f));
        series.addPoint(new ValueLinePoint("Jun", 1.0f));
        series.addPoint(new ValueLinePoint("Jul", 3.5f));
        series.addPoint(new ValueLinePoint("Aug", 2.4f));
        series.addPoint(new ValueLinePoint("Sep", 2.4f));
        series.addPoint(new ValueLinePoint("Oct", 3.4f));
        series.addPoint(new ValueLinePoint("Nov", .4f));
        series.addPoint(new ValueLinePoint("Dec", 1.3f));

        mCubicValueLineChart.addSeries(series);
        mCubicValueLineChart.startAnimation();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateSystemStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setpoints:
                Intent intent = new Intent(MainActivity.this, SetpointListActivity.class);
                startActivity(intent);
                return true;
        }

        return true;
    }

    private void setActiveButton(int heaterMode){
        switch (heaterMode){
            case 0:
                offModeButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                offModeButton.setTextColor(Color.parseColor("#EFECEC"));

                onModeButton.setBackground(onButtonDrawable);
                onModeButton.setTextColor(Color.parseColor("#E3171616"));

                defaultModeButton.setBackground(defaultButtonDrawable);
                defaultModeButton.setTextColor(Color.parseColor("#E3171616"));
                return;

            case 1:
                onModeButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                onModeButton.setTextColor(Color.parseColor("#EFECEC"));

                defaultModeButton.setBackground(defaultButtonDrawable);
                defaultModeButton.setTextColor(Color.parseColor("#E3171616"));

                offModeButton.setBackground(offButtonDrawable);
                offModeButton.setTextColor(Color.parseColor("#E3171616"));
                return;
            case 2:
                defaultModeButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                defaultModeButton.setTextColor(Color.parseColor("#EFECEC"));

                onModeButton.setBackground(onButtonDrawable);
                onModeButton.setTextColor(Color.parseColor("#E3171616"));

                offModeButton.setBackground(offButtonDrawable);
                offModeButton.setTextColor(Color.parseColor("#E3171616"));
                return;
        }
    }

    private void updateSystemStatus(){
        SystemStatus status = setpointRepository.getSystemStatus();

        runOnUiThread(() -> {
            mTempText.setText(status.getTemperatureRounded() + "\u2103");
            mStatusUpdateAt.setText("Updated at:" + status.formattedUpdatedAt());
            setActiveButton(status.getRulesMode());
            updateActiveSetpointUI(status);
            mProgressWheelParent.setVisibility(View.GONE);
        });
    }

    private void updateActiveSetpointUI(SystemStatus systemStatus){
        if(systemStatus.getRules().isEmpty()){
            mActiveSetpointLabel.setText("Upcoming setpoint");
            mActiveSetpoint.setText("No upcoming setpoints");
            return;
        }

        if(systemStatus.getId() == -1){
            mActiveSetpointLabel.setText("Upcoming setpoint");
            DbSetpoint upcomingSetpoint = setpointRepository.findNextSetpoint();
            if(upcomingSetpoint == null){
                mActiveSetpoint.setText("No upcoming setpoints");
            }else{
                mActiveSetpoint.setText(upcomingSetpoint.dayToString() + " " + upcomingSetpoint.getTimeText() + " with max temperature at " + upcomingSetpoint.getTemperatureText());
            }
            return;
        }

        DbSetpoint activeSetpoint = setpointRepository.getSetpointById(systemStatus.getId());
        mActiveSetpointLabel.setText("Active setpoint");
        mActiveSetpoint.setText(activeSetpoint.dayToString() + " " + activeSetpoint.getTimeText() + " with max temperature at " + activeSetpoint.getTemperatureText());
    }
}
