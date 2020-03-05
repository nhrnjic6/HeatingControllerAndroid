package com.nhrnjic.heatingcontroller.database;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SetpointDatabaseService {
    public DbSetpoint saveSetpoint(int day, int hour, int minute, double temperature){
        Realm realm = Realm.getDefaultInstance();
        long nextNumber = 1;

        realm.beginTransaction();

        Number number = realm.where(DbSetpoint.class).max("id");
        if(number != null){
            nextNumber = number.intValue() +1;
        }

        DbSetpoint dbSetpoint = realm.createObject(DbSetpoint.class, nextNumber);
        dbSetpoint.setDay(day);
        dbSetpoint.setHour(hour);
        dbSetpoint.setMinute(minute);
        dbSetpoint.setTemperature(temperature);

        realm.commitTransaction();
        return dbSetpoint;
    }

    public List<DbSetpoint> getAllSetpoints(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<DbSetpoint> realmResults = realm.where(DbSetpoint.class).findAll();
        List<DbSetpoint> setpoints = realm.copyFromRealm(realmResults);
        Collections.sort(setpoints);
        return setpoints;
    }
}
