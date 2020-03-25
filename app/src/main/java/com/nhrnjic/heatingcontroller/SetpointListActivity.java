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
import com.nhrnjic.heatingcontroller.repository.SetpointRepository;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;

public class SetpointListActivity extends AppCompatActivity {
    private SetpointRepository setpointRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpoint_list);

        Button dayPickerButton = findViewById(R.id.setpoint_day_picker_btn);
        ListView listView = findViewById(R.id.setpoint_list);

        setpointRepository = SetpointRepository.getInstance();

        List<DbSetpoint> setpoints = setpointRepository.getSetpoints(
                DateTime.now().getDayOfWeek()
        );

        SetpointListAdapter adapter = new SetpointListAdapter(setpoints, this);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose day of week");

        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        builder.setItems(daysOfWeek, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dayPickerButton.setText(daysOfWeek[which]);
                List<DbSetpoint> setpoints = setpointRepository.getSetpoints(which + 1);
                adapter.setSetpoints(setpoints);
                adapter.notifyDataSetChanged();
            }
        });

        final AlertDialog dialog = builder.create();

        dayPickerButton.setText(daysOfWeek[DateTime.now().getDayOfWeek() - 1]);
        dayPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }
}
