package com.nhrnjic.heatingcontroller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nhrnjic.heatingcontroller.R;
import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import java.util.List;

public class SetpointListAdapter extends BaseAdapter {
    private List<DbSetpoint> setpoints;
    private Context context;
    private int position;
    private View convertView;
    private ViewGroup parent;

    public SetpointListAdapter(List<DbSetpoint> setpoints, Context context) {
        this.setpoints = setpoints;
        this.context = context;
    }

    @Override
    public int getCount() {
        return setpoints.size();
    }

    @Override
    public Object getItem(int position) {
        return setpoints.get(position);
    }

    @Override
    public long getItemId(int position) {
        return setpoints.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.position = position;
        this.convertView = convertView;
        this.parent = parent;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.setpoint_list_item, parent, false);
        }

        DbSetpoint setpoint = (DbSetpoint) getItem(position);

        TextView timeText = convertView.findViewById(R.id.setpoint_item_time);
        timeText.setText(setpoint.getTimeText());

        TextView temperatureText = convertView.findViewById(R.id.setpoint_item_temperature);
        temperatureText.setText(setpoint.getTemperatureText());
        return convertView;
    }

    public void removeSetpoint(int position){
        setpoints.remove(position);
    }

    public void setSetpoints(List<DbSetpoint> setpoints) {
        this.setpoints = setpoints;
    }
}
