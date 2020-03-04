package com.nhrnjic.heatingcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nhrnjic.heatingcontroller.database.SetpointDatabaseService;
import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;
import com.nhrnjic.heatingcontroller.model.SystemStatus;
import com.nhrnjic.heatingcontroller.service.MqttService;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SetpointDatabaseService setpointDatabaseService = new SetpointDatabaseService();
    private MqttService mqttService;
    private Gson gson = new Gson();

    private ListView setpointListView;
    private Button mAddSetpointButton;
    private TextView mTempText;
    private TextView mStatusUpdateAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mqttService = MqttService.getInstance(this);
        setpointListView = findViewById(R.id.setpoint_list_view);
        mAddSetpointButton = findViewById(R.id.btn_setpoint_create);
        mTempText = findViewById(R.id.tv_temperature);
        mStatusUpdateAt = findViewById(R.id.tv_status_update_at);

        mAddSetpointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SetpointCreateActivity.class);
                startActivity(intent);
            }
        });

        List<DbSetpoint> setpoints = setpointDatabaseService.getAllSetpoints();

        setpointListView.setAdapter(new SetpointListAdapter(setpoints, this));

        mqttService.addListener(new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) {
                System.out.println("Received msg: " + new String(message.getPayload()));

                if(mTempText != null && mStatusUpdateAt != null){
                    String msg = new String(message.getPayload());
                    SystemStatus systemStatus = gson.fromJson(msg, SystemStatus.class);
                    mTempText.setText(systemStatus.getTemperature() + "\u2103");
                    mStatusUpdateAt.setText("Updated at: " + systemStatus.formattedUpdatedAt());
                }
            }
        });
    }
}
