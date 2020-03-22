package com.nhrnjic.heatingcontroller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import java.util.List;

public class SetpointListAdapter extends BaseAdapter {
    private List<DbSetpoint> setpoints;
    private Context context;

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
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.setpoint_item, parent, false);
        }

        DbSetpoint setpoint = (DbSetpoint) getItem(position);

        ((TextView) convertView.findViewById(R.id.setpoint_item_day))
                .setText(setpoint.toString());
        return convertView;
    }

    public void removeSetpoint(int position){
        setpoints.remove(position);
    }
}
