package com.nhrnjic.heatingcontroller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.nhrnjic.heatingcontroller.adapter.SetpointListAdapter;
import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import java.util.Arrays;
import java.util.List;

public class SetpointListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpoint_list);

        ListView listView = findViewById(R.id.setpoint_list);

        List<DbSetpoint> setpoints = Arrays.asList(
                new DbSetpoint(1, 10, 0, 45),
                new DbSetpoint(1, 12, 30, 55),
                new DbSetpoint(1, 14, 0, 15),
                new DbSetpoint(1, 17, 40, 25),
                new DbSetpoint(1, 20, 40, 20));

        SetpointListAdapter adapter = new SetpointListAdapter(setpoints, this);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose day of week");

        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        builder.setItems(daysOfWeek, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // horse
                    case 1: // cow
                    case 2: // camel
                    case 3: // sheep
                    case 4: // goat
                }
            }
        });

        final AlertDialog dialog = builder.create();

        Button dayPickerButton = findViewById(R.id.setpoint_day_picker_btn);
        dayPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }
}
