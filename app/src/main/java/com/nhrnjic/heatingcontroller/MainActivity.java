package com.nhrnjic.heatingcontroller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;
import com.nhrnjic.heatingcontroller.model.SystemStatus;
import com.nhrnjic.heatingcontroller.repository.SetpointRepository;
import com.nhrnjic.heatingcontroller.service.HeatingControlService;
import com.nhrnjic.heatingcontroller.service.MqttService;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
                    systemStatus -> updateSystemStatus(),
                    this::failedConnectionActivity);
        });

        onModeButton.setOnClickListener(v -> {
            mProgressWheelParent.setVisibility(View.VISIBLE);
            heatingControlService.changeRulesMode(
                    1,
                    systemStatus -> updateSystemStatus(),
                    this::failedConnectionActivity);
        });

        defaultModeButton.setOnClickListener(v -> {
            mProgressWheelParent.setVisibility(View.VISIBLE);
            heatingControlService.changeRulesMode(
                    2,
                    systemStatus -> updateSystemStatus(),
                    this::failedConnectionActivity);
        });

        LineChart chart = findViewById(R.id.line_chart);
        // enable touch gestures
        chart.setTouchEnabled(true);
        chart.setScaleYEnabled(false);

        MPPointF center = chart.getViewPortHandler().getContentCenter();
        chart.zoom(3f, 0f, center.x,center.y);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        List<Integer> swapedTemperature = new ArrayList<>();
        List<Integer> avgTemperature = getAvgTemperature(setpointRepository.getSystemStatus().getTempList());
        int offset = getSwapedTemperature(avgTemperature, swapedTemperature, DateTime.now());

        List<Entry> entries = new ArrayList<>();
        for(int i = 0; i < swapedTemperature.size(); i++){
            entries.add(new Entry(i, swapedTemperature.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Numbers");

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return getActualIndex((int) value, offset, swapedTemperature.size()) + "";
            }
        });

        chart.getAxisRight().setEnabled(false);

        LineData data = new LineData(dataSet);

        chart.setData(data);
        chart.invalidate();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateSystemStatus();
    }

    @Override
    public void onBackPressed() {
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

    private int getSwapedTemperature(
            List<Integer> temperature,
            List<Integer> swaped,
            DateTime now){
        int hourNow = now.getHourOfDay();
        int offset = (temperature.size() - 1) - hourNow;

        List<Integer> today = temperature.subList(0, hourNow + 1);
        List<Integer> yesterday = temperature.subList(hourNow + 1, temperature.size());

        swaped.addAll(yesterday);
        swaped.addAll(today);

        return offset;
    }

    private int getActualIndex(int newIndex, int offset, int size){
        int actualIndex = newIndex - offset;

        if(actualIndex < 0){
            actualIndex += size;
        }

        return actualIndex;
    }

    public List<Integer> getAvgTemperature(List<Integer> temperature){
        int sum = 0;

        List<Integer> avgTemperature = new ArrayList<>();

        for(int i = 0; i < temperature.size(); i++){
            sum += temperature.get(i);

            if((i + 1) % 4 == 0){
                avgTemperature.add(sum / 4);
                sum = 0;
            }
        }

        return avgTemperature;
    }

    private void failedConnectionActivity(){
        Intent intent = new Intent(MainActivity.this, ReloadActivity.class);
        startActivity(intent);
        finish();
    }
}
