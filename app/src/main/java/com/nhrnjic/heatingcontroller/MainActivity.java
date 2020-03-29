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
import android.widget.Button;
import android.widget.TextView;

import com.nhrnjic.heatingcontroller.database.SetpointDatabaseService;
import com.nhrnjic.heatingcontroller.repository.SetpointRepository;
import com.nhrnjic.heatingcontroller.service.HeatingControlService;
import com.nhrnjic.heatingcontroller.service.MqttService;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {
    private SetpointDatabaseService setpointDatabaseService = new SetpointDatabaseService();
    private HeatingControlService heatingControlService = new HeatingControlService(setpointDatabaseService);
    private SetpointRepository setpointRepository = SetpointRepository.getInstance();

    private MqttService mqttService;

    private TextView mTempText;
    private TextView mStatusUpdateAt;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mqttService = MqttService.getInstance(this);
        mStatusUpdateAt = findViewById(R.id.tv_status_update_at);
        mTempText = findViewById(R.id.temperature);
        defaultModeButton = findViewById(R.id.btn_default_mode);
        defaultButtonDrawable = defaultModeButton.getBackground();
        onModeButton = findViewById(R.id.btn_on_mode);
        onButtonDrawable = onModeButton.getBackground();
        offModeButton = findViewById(R.id.btn_off_mode);
        offButtonDrawable = offModeButton.getBackground();

//        mTempText.setText(roundTemperature(setpointRepository.getTemperature()) + "\u2103");
//        mStatusUpdateAt.setText("Updated at:" + formatTime(setpointRepository.getUpdatedAt()));
//
//
//        try {
//            mqttService.getSystemStatus(systemStatus -> {
//                runOnUiThread(() -> {
//                    mTempText.setText(systemStatus.getTemperatureRounded() + "\u2103");
//                    mStatusUpdateAt.setText("Updated at:" + systemStatus.formattedUpdatedAt());
//                });
//            });
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }

        defaultModeButton.setOnClickListener(v -> {
            heatingControlService.sendCurrentRules(2);
            defaultModeButton.setBackgroundColor(Color.parseColor("#4CAF50"));
            defaultModeButton.setTextColor(Color.parseColor("#EFECEC"));

            onModeButton.setBackground(onButtonDrawable);
            onModeButton.setTextColor(Color.parseColor("#E3171616"));

            offModeButton.setBackground(offButtonDrawable);
            offModeButton.setTextColor(Color.parseColor("#E3171616"));
        });

        onModeButton.setOnClickListener(v -> {
            heatingControlService.sendCurrentRules(1);
            onModeButton.setBackgroundColor(Color.parseColor("#4CAF50"));
            onModeButton.setTextColor(Color.parseColor("#EFECEC"));

            defaultModeButton.setBackground(defaultButtonDrawable);
            defaultModeButton.setTextColor(Color.parseColor("#E3171616"));

            offModeButton.setBackground(offButtonDrawable);
            offModeButton.setTextColor(Color.parseColor("#E3171616"));
        });

        offModeButton.setOnClickListener(v -> {
            heatingControlService.sendCurrentRules(0);
            offModeButton.setBackgroundColor(Color.parseColor("#4CAF50"));
            offModeButton.setTextColor(Color.parseColor("#EFECEC"));

            onModeButton.setBackground(onButtonDrawable);
            onModeButton.setTextColor(Color.parseColor("#E3171616"));

            defaultModeButton.setBackground(defaultButtonDrawable);
            defaultModeButton.setTextColor(Color.parseColor("#E3171616"));
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

    private String roundTemperature(BigDecimal temperature) {
        temperature = temperature.setScale(2 , BigDecimal.ROUND_HALF_UP);
        return temperature.toString();
    }

    private String formatTime(long milis){
        return new DateTime(milis * 1000, DateTimeZone.UTC)
                .toString("HH:mm:ss");
    }
}
