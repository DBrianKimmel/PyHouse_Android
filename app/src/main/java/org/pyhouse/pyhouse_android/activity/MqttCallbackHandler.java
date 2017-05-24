/*
 * Created by briank on 5/21/17.
 */
package org.pyhouse.pyhouse_android.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import org.pyhouse.pyhouse_android.R;
import org.pyhouse.pyhouse_android.internal.MqttConnectionCollection;


/**
 * Handles call backs from the MQTT Client
 */
class MqttCallbackHandler implements MqttCallback {

    /**
     * {@link Context} for the application used to format and import external strings
     **/
    private final Context context;
    /**
     * Client handle to reference the connection that this handler is attached to
     **/
    private final String clientHandle;

    private static final String TAG = "MqttCallbackHandler";
    private static final String activityClass = "org.eclipse.paho.android.sample.activity.MainActivity";

    /**
     * Creates an <code>MqttCallbackHandler</code> object
     *
     * @param context      The application's context
     * @param clientHandle The handle to a {@link MqttConnection} object
     */
    public MqttCallbackHandler(Context context, String clientHandle) {
        this.context = context;
        this.clientHandle = clientHandle;
    }

    /**
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
     */
    @Override
    public void connectionLost(Throwable cause) {
        if (cause != null) {
            Log.d(TAG, "MqttConnection Lost: " + cause.getMessage());
            MqttConnection c = MqttConnectionCollection.getInstance(context).getConnection(clientHandle);
            c.addAction("MqttConnection Lost");
            c.changeConnectionStatus(MqttConnection.ConnectionStatus.DISCONNECTED);

            String message = context.getString(R.string.connection_lost, c.getId(), c.getHostName());

            //build intent
            Intent intent = new Intent();
            intent.setClassName(context, activityClass);
            intent.putExtra("handle", clientHandle);

            //notify the user
            Notify.notifcation(context, message, intent, R.string.notifyTitle_connectionLost);
        }
    }

    /**
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String, org.eclipse.paho.client.mqttv3.MqttMessage)
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        //Get connection object associated with this object
        MqttConnection c = MqttConnectionCollection.getInstance(context).getConnection(clientHandle);
        c.messageArrived(topic, message);
        //get the string from strings.xml and format
        String messageString = context.getString(R.string.messageRecieved, new String(message.getPayload()), topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained());

        Log.i(TAG, messageString);

        //update client history
        c.addAction(messageString);

    }

    /**
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Do nothing
    }

}