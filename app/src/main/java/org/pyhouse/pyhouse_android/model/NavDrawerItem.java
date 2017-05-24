/*
 * Created by briank on 5/21/17.
 */
package org.pyhouse.pyhouse_android.model;

import org.pyhouse.pyhouse_android.activity.MqttConnection;


public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private String handle;

    public NavDrawerItem(MqttConnection mqttConnection){
        this.title = mqttConnection.getId();
        this.handle = mqttConnection.handle();
    }

    public String getTitle(){
        return title;
    }

    public String getHandle() {
        return handle;
    }

}