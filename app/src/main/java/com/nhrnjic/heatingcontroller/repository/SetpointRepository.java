package com.nhrnjic.heatingcontroller.repository;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class SetpointRepository {
    private List<DbSetpoint> setpoints;
    private static SetpointRepository setpointRepository = new SetpointRepository();

    private SetpointRepository(){
        setpoints = new ArrayList<>();
        DbSetpoint day1 = new DbSetpoint(1, 10, 30, 10);
        DbSetpoint day2 = new DbSetpoint(2, 12, 30, 20);
        DbSetpoint day3 = new DbSetpoint(3, 13, 30, 30);
        DbSetpoint day4 = new DbSetpoint(4, 14, 30, 40);
        DbSetpoint day5 = new DbSetpoint(5, 15, 30, 50);

        setpoints.add(day1);
        setpoints.add(day2);
        setpoints.add(day3);
        setpoints.add(day4);
        setpoints.add(day5);
    }

    public static SetpointRepository getInstance(){
        return setpointRepository;
    }

    public void setSetpoints(List<DbSetpoint> setpoints) {
        this.setpoints = setpoints;
    }

    public List<DbSetpoint> getSetpoints(final int day) {
        List<DbSetpoint> setpointsOfDay = new ArrayList<>();

        return setpoints.stream()
                .filter(x -> x.getDay() == day)
                .sorted()
                .collect(Collectors.toList());
    }
}
