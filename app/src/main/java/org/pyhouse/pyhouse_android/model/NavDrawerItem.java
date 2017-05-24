/*
 * Created by briank on 5/21/17.
 */
package org.pyhouse.pyhouse_android.model;

import org.pyhouse.pyhouse_android.activity.Connection;


public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private String handle;

    public NavDrawerItem(Connection connection){
        this.title = connection.getId();
        this.handle = connection.handle();
    }

    public String getTitle(){
        return title;
    }

    public String getHandle() {
        return handle;
    }

}