package com.nhrnjic.heatingcontroller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nhrnjic.heatingcontroller.adapter.SetpointListAdapter;
import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;
import com.nhrnjic.heatingcontroller.repository.SetpointRepository;

import org.joda.time.DateTime;

import java.util.List;

public class SetpointListActivity extends AppCompatActivity {
    public static final String EDIT_SETPOINT_INDEX_KEY = "EDIT_SETPOINT_INDEX";

    private SetpointRepository setpointRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpoint_list);

        setpointRepository = SetpointRepository.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar_list);
        setSupportActionBar(toolbar);

        Button dayPickerButton = findViewById(R.id.setpoint_day_picker_btn);
        ListView listView = findViewById(R.id.setpoint_list);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            DbSetpoint setpoint = setpointRepository.getSetpoints().get(position);
            Intent intent = new Intent(SetpointListActivity.this, NewSetpointActivity.class);
            intent.putExtra(EDIT_SETPOINT_INDEX_KEY, setpoint);
            startActivity(intent);
        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setpoint_new:
                Intent intent = new Intent(SetpointListActivity.this, NewSetpointActivity.class);
                startActivity(intent);
                return true;
        }

        return true;
    }
}
