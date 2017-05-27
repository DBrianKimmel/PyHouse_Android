/*
 * Created by briank on 5/21/17.
 */

package org.pyhouse.pyhouse_android.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.android.service.MqttAndroidClient;

import org.pyhouse.pyhouse_android.R;
import org.pyhouse.pyhouse_android.internal.MqttConnectionCollection;
import org.pyhouse.pyhouse_android.model.MqttSubscriptionModel;

import java.util.ArrayList;


/**
 * This Class handles receiving information from the
 * {@link MqttAndroidClient} and updating the {@link MqttConnection} associated with
 * the action
 */
public class MqttActionListener implements IMqttActionListener {

    private static final String TAG = "MqttActionListener";
    private static final String activityClass = "org.pyhouse.pyhouse_android.activity.MainActivity";

    /**
     * Actions that can be performed Asynchronously <strong>and</strong> associated with a {@link MqttActionListener} object
     */
    public enum Action {
        CONNECT,
        DISCONNECT,
        SUBSCRIBE,
        PUBLISH
    }

    /**
     * The {@link Action} that is associated with this instance of
     * <code>MqttActionListener</code>
     **/
    private final Action action;
    /**
     * The arguments passed to be used for formatting strings
     **/
    private final String[] additionalArgs;

    private final MqttConnection mqttConnection;
    /**
     * Handle of the {@link MqttConnection} this action was being executed on
     **/
    private final String clientHandle;
    /**
     * {@link Context} for performing various operations
     **/
    private final Context context;

    /**
     * Creates a generic action listener for actions performed form any activity
     *
     * @param context        The application context
     * @param action         The action that is being performed
     * @param mqttConnection     The mqttConnection
     * @param additionalArgs Used for as arguments for string formating
     */
    public MqttActionListener(Context context, Action action, MqttConnection mqttConnection, String... additionalArgs) {
        this.context = context;
        this.action = action;
        this.mqttConnection = mqttConnection;
        this.clientHandle = mqttConnection.handle();
        this.additionalArgs = additionalArgs;
    }

    /**
     * The action associated with this listener has been successful.
     *
     * @param asyncActionToken This argument is not used
     */
    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        switch (action) {
            case CONNECT:
                connect();
                break;
            case DISCONNECT:
                disconnect();
                break;
            case SUBSCRIBE:
                subscribe();
                break;
            case PUBLISH:
                publish();
                break;
        }

    }

    /**
     * A publish action has been successfully completed, update mqttConnection
     * object associated with the client this action belongs to, then notify the
     * user of success
     */
    private void publish() {
        MqttConnection c = MqttConnectionCollection.getInstance(context).getConnection(clientHandle);
        @SuppressLint("StringFormatMatches") String actionTaken = context.getString(R.string.toast_pub_success,
                (Object[]) additionalArgs);
        c.addAction(actionTaken);
        Notify.toast(context, actionTaken, Toast.LENGTH_SHORT);
        System.out.print("Published");
    }

    /**
     * A addNewSubscription action has been successfully completed, update the mqttConnection
     * object associated with the client this action belongs to and then notify
     * the user of success
     */
    private void subscribe() {
        MqttConnection c = MqttConnectionCollection.getInstance(context).getConnection(clientHandle);
        String actionTaken = context.getString(R.string.toast_sub_success,
                (Object[]) additionalArgs);
        c.addAction(actionTaken);
        Notify.toast(context, actionTaken, Toast.LENGTH_SHORT);
        System.out.print(actionTaken);

    }

    /**
     * A disconnection action has been successfully completed, update the
     * mqttConnection object associated with the client this action belongs to and
     * then notify the user of success.
     */
    private void disconnect() {
        MqttConnection c = MqttConnectionCollection.getInstance(context).getConnection(clientHandle);
        c.changeConnectionStatus(MqttConnection.ConnectionStatus.DISCONNECTED);
        String actionTaken = context.getString(R.string.toast_disconnected);
        c.addAction(actionTaken);
        Log.i(TAG, c.handle() + " disconnected.");
        //build intent
        Intent intent = new Intent();
        intent.setClassName(context, activityClass);
        intent.putExtra("handle", clientHandle);

    }

    /**
     * A mqttConnection action has been successfully completed, update the
     * mqttConnection object associated with the client this action belongs to and
     * then notify the user of success.
     */
    private void connect() {

        MqttConnection c = MqttConnectionCollection.getInstance(context).getConnection(clientHandle);
        c.changeConnectionStatus(MqttConnection.ConnectionStatus.CONNECTED);
        c.addAction("Client Connected");
        Log.i(TAG, c.handle() + " connected.");
        try {

            ArrayList<MqttSubscriptionModel> mqttSubscriptionModels = mqttConnection.getSubscriptions();
            for (MqttSubscriptionModel sub : mqttSubscriptionModels) {
                Log.i(TAG, "Auto-subscribing to: " + sub.getTopic() + "@ QoS: " + sub.getQos());
                mqttConnection.getClient().subscribe(sub.getTopic(), sub.getQos());
            }
        } catch (MqttException ex) {
            Log.e(TAG, "Failed to Auto-Subscribe: " + ex.getMessage());
        }

    }

    /**
     * The action associated with the object was a failure
     *
     * @param token     This argument is not used
     * @param exception The exception which indicates why the action failed
     */
    @Override
    public void onFailure(IMqttToken token, Throwable exception) {
        switch (action) {
            case CONNECT:
                connect(exception);
                break;
            case DISCONNECT:
                disconnect(exception);
                break;
            case SUBSCRIBE:
                subscribe(exception);
                break;
            case PUBLISH:
                publish(exception);
                break;
        }
    }

    /**
     * A publish action was unsuccessful, notify user and update client history
     *
     * @param exception This argument is not used
     */
    private void publish(Throwable exception) {
        MqttConnection c = MqttConnectionCollection.getInstance(context).getConnection(clientHandle);
        @SuppressLint("StringFormatMatches") String action = context.getString(R.string.toast_pub_failed,
                (Object[]) additionalArgs);
        c.addAction(action);
        Notify.toast(context, action, Toast.LENGTH_SHORT);
        System.out.print("Publish failed");
    }

    /**
     * A addNewSubscription action was unsuccessful, notify user and update client history
     *
     * @param exception This argument is not used
     */
    private void subscribe(Throwable exception) {
        MqttConnection c = MqttConnectionCollection.getInstance(context).getConnection(clientHandle);
        String action = context.getString(R.string.toast_sub_failed,
                (Object[]) additionalArgs);
        c.addAction(action);
        Notify.toast(context, action, Toast.LENGTH_SHORT);
        System.out.print(action);
    }

    /**
     * A disconnect action was unsuccessful, notify user and update client history
     *
     * @param exception This argument is not used
     */
    private void disconnect(Throwable exception) {
        MqttConnection c = MqttConnectionCollection.getInstance(context).getConnection(clientHandle);
        c.changeConnectionStatus(MqttConnection.ConnectionStatus.DISCONNECTED);
        c.addAction("Disconnect Failed - an error occured");
    }

    /**
     * A connect action was unsuccessful, notify the user and update client history
     *
     * @param exception This argument is not used
     */
    private void connect(Throwable exception) {
        MqttConnection c = MqttConnectionCollection.getInstance(context).getConnection(clientHandle);
        c.changeConnectionStatus(MqttConnection.ConnectionStatus.ERROR);
        c.addAction("Client failed to connect");
        System.out.println("Client failed to connect");
    }
}