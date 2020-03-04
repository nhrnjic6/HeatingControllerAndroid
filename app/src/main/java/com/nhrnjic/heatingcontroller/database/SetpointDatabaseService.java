package com.nhrnjic.heatingcontroller.database;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SetpointDatabaseService {
    public DbSetpoint saveSetpoint(String day, String hour, String minute, String temperature){
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
        RealmResults<DbSetpoint> setpoints = realm.where(DbSetpoint.class).findAll();
        return realm.copyFromRealm(setpoints);
    }
}
