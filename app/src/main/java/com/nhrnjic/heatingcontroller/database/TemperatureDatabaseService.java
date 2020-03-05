package com.nhrnjic.heatingcontroller.database;

import com.nhrnjic.heatingcontroller.database.model.DbTemperature;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class TemperatureDatabaseService {
    public DbTemperature saveTemperature(double temperature, long updatedAt){
        Realm realm = Realm.getDefaultInstance();
        long nextNumber = 1;

        realm.beginTransaction();

        Number number = realm.where(DbTemperature.class).max("id");
        if(number != null){
            nextNumber = number.intValue() +1;
        }

        DbTemperature dbTemperature = realm.createObject(DbTemperature.class, nextNumber);
        dbTemperature.setTemperature(temperature);
        dbTemperature.setUpdatedAt(updatedAt);

        realm.commitTransaction();
        return dbTemperature;
    }

    public DbTemperature getLatestTemperature(){
        Realm realm = Realm.getDefaultInstance();
        return realm
            .where(DbTemperature.class)
            .sort("updatedAt", Sort.DESCENDING)
            .findFirst();
    }

    public List<DbTemperature> getTemperature(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<DbTemperature> realmResults = realm
                .where(DbTemperature.class)
                .sort("updatedAt", Sort.DESCENDING)
                .findAll();

        return realm.copyFromRealm(realmResults);
    }
}
