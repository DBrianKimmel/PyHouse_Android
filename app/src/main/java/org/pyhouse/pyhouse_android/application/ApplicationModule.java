package org.pyhouse.pyhouse_android.application;
/*
 * Created by briank on 5/27/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

//import com.squareup.sqlbrite.BriteDatabase;
//import com.squareup.sqlbrite.SqlBrite;
//import com.tbruyelle.rxpermissions.RxPermissions;

//import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

/**
 * Created by fedepaol on 28/06/15.
 */
@Module
public class ApplicationModule {

    private android.app.Application mApp;
    public Context mContext = mApp.getApplicationContext();

    public ApplicationModule(android.app.Application app) {
        mApp = app;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(mApp);
    }

    @Provides
    @Singleton
    MqttClient provideMqttClient(Gson gson) {
        return new MqttClient(mContext);
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mContext;
    }

    @Provides
    @Singleton
    WebSocketClient provideWebSocketClient() {
        return new WebSocketClient();
    }

    @Provides
    @Singleton
    Gson provideGSon() {
        return new Gson();
    }
}
