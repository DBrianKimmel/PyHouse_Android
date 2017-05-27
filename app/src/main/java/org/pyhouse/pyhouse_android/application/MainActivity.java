/*
 *  The purpose of this application has several parts.
 *    1.  Configure the house data.
 *    2.  Configure the computer data.
 *    3.  Control the house
 *    ...
 *
 *
 *
 *  To configure the house server.
 *
 *  There may be one or more servers available to the app.
 */
package org.pyhouse.pyhouse_android.application;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.pyhouse.pyhouse_android.R;
import org.pyhouse.pyhouse_android.activity.MqttActionListener;
import org.pyhouse.pyhouse_android.activity.ActivityConstants;
import org.pyhouse.pyhouse_android.activity.MqttConnection;
import org.pyhouse.pyhouse_android.activity.MqttConnectionFragment;
import org.pyhouse.pyhouse_android.activity.FragmentDrawer;
import org.pyhouse.pyhouse_android.activity.HomeFragment;
import org.pyhouse.pyhouse_android.activity.MqttTraceCallback;
import org.pyhouse.pyhouse_android.internal.MqttConnectionCollection;
import org.pyhouse.pyhouse_android.model.MqttConnectionModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        // implements FragmentDrawer.FragmentDrawerListener {
    private static final String TAG = "MainActivity         :";
    //private final ChangeListener changeListener = new ChangeListener();
    private FragmentDrawer drawerFragment;
    private final MainActivity mainActivity = this;
    private ArrayList<String> connectionMap;
    static final String LOGIN_INTENT = "org.pyhouse.pyhouse_android.app.myapp.LoginActivity_2";
    public static final String EXTRA_MESSAGE = "org.pyhouse.pyhouse_android.MESSAGE";
    private Button start;
    private TextView output;
    private OkHttpClient client;

    /*
     * We just created the main UI Thread
     *
     * Create MQTT client
     * Create Websocket client
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "Enter onCreate");

        // view
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // FAB
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {

         //   @Override
            //public void onClick(View view) {
            //    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //            .setAction("Action", null).show();
            //}
        //});


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        //drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        //drawerFragment.setDrawerListener(this);

        // Fire up MQTT
        Context context = this.getApplicationContext();
        new MqttClient(context);
        client = new OkHttpClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(TAG, "Enter onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(TAG, "Enter onRestart");
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    public void removeConnectionRow(MqttConnection mqttConnection){
        drawerFragment.removeConnection(mqttConnection);
        populateConnectionList();
    }

    private void populateConnectionList(){
        // Clear drawerFragment
        drawerFragment.clearConnections();

        // get all the available connections
        Map<String, MqttConnection> connections = MqttConnectionCollection.getInstance(this)
                .getConnections();
        int connectionIndex = 0;
        connectionMap = new ArrayList<String>();

        Iterator connectionIterator = connections.entrySet().iterator();
        while (connectionIterator.hasNext()){
            Map.Entry pair = (Map.Entry) connectionIterator.next();
            drawerFragment.addConnection((MqttConnection) pair.getValue());
            connectionMap.add((String) pair.getKey());
            ++connectionIndex;
        }

        if(connectionMap.size() == 0){
            displayView(-1);
        } else {
            displayView(0);
        }
    }

    private void displayView(int position){
        if(position == -1){
            displayFragment(new HomeFragment(), "Home");
        } else {
            Fragment fragment  = new MqttConnectionFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ActivityConstants.CONNECTION_KEY, connectionMap.get(position));
            fragment.setArguments(bundle);
            Map<String, MqttConnection> connections = MqttConnectionCollection.getInstance(this)
                    .getConnections();
            MqttConnection mqttConnection = connections.get(connectionMap.get(position));
            String title = mqttConnection.getId();
            displayFragment(fragment, title);
        }
    }

    private void displayFragment(Fragment fragment, String title){
        if (fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // Set Toolbar Title
            getSupportActionBar().setTitle(title);


        }
    }

    public void updateAndConnect(MqttConnectionModel model){
        Map<String, MqttConnection> connections = MqttConnectionCollection.getInstance(this)
                .getConnections();

        Log.i(TAG, "Updating connection: " + connections.keySet().toString());
        try {
            MqttConnection mqttConnection = connections.get(model.getClientHandle());
            // First disconnect the current instance of this mqttConnection
            if(mqttConnection.isConnected()){
                mqttConnection.changeConnectionStatus(MqttConnection.ConnectionStatus.DISCONNECTING);
                mqttConnection.getClient().disconnect();
            }
            // Update the mqttConnection.
            mqttConnection.updateConnection(model.getClientId(), model.getServerHostName(), model.getServerPort(), model.isTlsConnection());
            mqttConnection.changeConnectionStatus(MqttConnection.ConnectionStatus.CONNECTING);

            String[] actionArgs = new String[1];
            actionArgs[0] = model.getClientId();
            final MqttActionListener callback = new MqttActionListener(this,
                    MqttActionListener.Action.CONNECT, mqttConnection, actionArgs);
            mqttConnection.getClient().setCallback(new MqttCallbackHandler(this, model.getClientHandle()));

            mqttConnection.getClient().setTraceCallback(new MqttTraceCallback());
            MqttConnectOptions connOpts = optionsFromModel(model);
            mqttConnection.addConnectionOptions(connOpts);
            MqttConnectionCollection.getInstance(this).updateConnection(mqttConnection);
            drawerFragment.updateConnection(mqttConnection);

            mqttConnection.getClient().connect(connOpts, null, callback);
            Fragment fragment  = new MqttConnectionFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ActivityConstants.CONNECTION_KEY, mqttConnection.handle());
            fragment.setArguments(bundle);
            String title = mqttConnection.getId();
            displayFragment(fragment, title);


        } catch (MqttException ex){
            Log.e(TAG, "Exception occurred updating connection: " + connections.keySet().toString() + " : " + ex.getMessage());
        }
    }

    /**
     * Takes a {@link MqttConnectionModel} and uses it to connect
     * and then persist.
     * @param model - The connection Model
     */
    public void persistAndConnect(MqttConnectionModel model){
        Log.i(TAG, "Persisting new mqttConnection:" + model.getClientHandle());
        MqttConnection mqttConnection = MqttConnection.createConnection(model.getClientHandle(),model.getClientId(),model.getServerHostName(),model.getServerPort(),this,model.isTlsConnection());
        //mqttConnection.registerChangeListener(changeListener);
        mqttConnection.changeConnectionStatus(MqttConnection.ConnectionStatus.CONNECTING);


        String[] actionArgs = new String[1];
        actionArgs[0] = model.getClientId();
        final MqttActionListener callback = new MqttActionListener(this,
                MqttActionListener.Action.CONNECT, mqttConnection, actionArgs);
        mqttConnection.getClient().setCallback(new MqttCallbackHandler(this, model.getClientHandle()));



        mqttConnection.getClient().setTraceCallback(new MqttTraceCallback());

        MqttConnectOptions connOpts = optionsFromModel(model);

        mqttConnection.addConnectionOptions(connOpts);
        MqttConnectionCollection.getInstance(this).addConnection(mqttConnection);
        connectionMap.add(model.getClientHandle());
        drawerFragment.addConnection(mqttConnection);

        try {
            mqttConnection.getClient().connect(connOpts, null, callback);
            Fragment fragment  = new MqttConnectionFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ActivityConstants.CONNECTION_KEY, mqttConnection.handle());
            bundle.putBoolean(ActivityConstants.CONNECTED, true);
            fragment.setArguments(bundle);
            String title = mqttConnection.getId();
            displayFragment(fragment, title);

        }
        catch (MqttException e) {
            Log.e(TAG, "MqttException occurred", e);
        }

    }













    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
     * DBK - test
     */
    //@Override
    //public boolean FragmentDrawerListener(MenuItem item) {
    //    int id = item.getItemId();
    //    return true;
    //}




    /**
     * Set the optional to set the fields in the MQTT connect packet.
     *
     * @param model is the model data
     * @return MqttConnectionOptions filled in
     */
    private MqttConnectOptions optionsFromModel(MqttConnectionModel model){

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(model.isCleanSession());
        connOpts.setConnectionTimeout(model.getTimeout());
        connOpts.setKeepAliveInterval(model.getKeepAlive());
        if(!model.getUsername().equals(ActivityConstants.empty)){
            connOpts.setUserName(model.getUsername());
        }
        if(!model.getPassword().equals(ActivityConstants.empty)){
            connOpts.setPassword(model.getPassword().toCharArray());
        }
        if(!model.getLwtTopic().equals(ActivityConstants.empty) && !model.getLwtMessage().equals(ActivityConstants.empty)){
            connOpts.setWill(model.getLwtTopic(), model.getLwtMessage().getBytes(), model.getLwtQos(), model.isLwtRetain());
        }
        return connOpts;
    }


    /**
     * The main connect packet for mqtt.
     * ]
     * @param mqttConnection is the data for the connect packet
     */
    public void connect(MqttConnection mqttConnection) {
        String[] actionArgs = new String[1];
        actionArgs[0] = mqttConnection.getId();
        final MqttActionListener callback = new MqttActionListener(this,
                MqttActionListener.Action.CONNECT, mqttConnection, actionArgs);
        mqttConnection.getClient().setCallback(new MqttCallbackHandler(this, mqttConnection.handle()));
        try {
            mqttConnection.getClient().connect(mqttConnection.getConnectionOptions(), null, callback);
        }
        catch (MqttException e) {
            Log.e(TAG, "MqttException occurred", e);
        }
    }

    public void disconnect(MqttConnection mqttConnection){
        try {
            mqttConnection.getClient().disconnect();
        } catch( MqttException ex){
            Log.e(TAG, "Exception occurred during disconnect: " + ex.getMessage());
        }
    }

    public void publish(MqttConnection mqttConnection, String topic, String message, int qos, boolean retain){
        try {
            String[] actionArgs = new String[2];
            actionArgs[0] = message;
            actionArgs[1] = topic;
            final MqttActionListener callback = new MqttActionListener(this,
                    MqttActionListener.Action.PUBLISH, mqttConnection, actionArgs);
            mqttConnection.getClient().publish(topic, message.getBytes(), qos, retain, null, callback);
        } catch( MqttException ex){
            Log.e(TAG, "Exception occurred during publish: " + ex.getMessage());
        }
    }


    private void makeRequestWithOkHttp(String url) {
        OkHttpClient client = new OkHttpClient();   // 1
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();  // 2

        client.newCall(request).enqueue(new okhttp3.Callback() { // 3
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                    throws IOException {
                final String result = response.body().string();  // 4

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //try {
                            //downloadComplete(Util.retrieveRepositoriesFromResponse(result));  // 5
                        //} catch (JSONException e) {
                        //    e.printStackTrace();
                        //}
                    }
                });
            }
        });
    }

}

// END DBK

