/*
 * Created by briank on 5/21/17.
 */
package org.pyhouse.pyhouse_android.activity;

import android.content.Context;
import android.content.Intent;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import org.pyhouse.pyhouse_android.R;
import org.pyhouse.pyhouse_android.internal.IMqttReceivedMessageListener;
import org.pyhouse.pyhouse_android.internal.MqttPersistence;
import org.pyhouse.pyhouse_android.internal.MqttPersistenceException;
import org.pyhouse.pyhouse_android.model.MqttReceivedMessageData;
import org.pyhouse.pyhouse_android.model.MqttSubscriptionModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a {@link MqttAndroidClient} and the actions it has performed
 */
public class MqttConnection {
    private static final String TAG = "MqttConnection       :";
    private static final String activityClass = "org.pyhouse.pyhouse_android.application.MainActivity";
    private String clientHandle = null;
    private String clientId = null;
    private String host = null;
    private int port = 0;
    private ConnectionStatus status = ConnectionStatus.NONE;
    private ArrayList<String> history = null;  // The history of the MqttAndroidClient represented by this MqttConnection object
    private MqttAndroidClient client = null;  // The MqttAndroidClient instance this class represents
    private final ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();  // Collection of java.beans.PropertyChangeListener
    private Context context = null;  // The {@link Context} of the application this object is part of
    private MqttConnectOptions mqttConnectOptions;  // The MqttConnectOptions that were used to connect this client
    private boolean tlsConnection = true;  // True if this connection is secured using TLS
    private long persistenceId = -1;  // MqttPersistence id, used by MqttPersistence
    private final Map<String, MqttSubscriptionModel> subscriptions = new HashMap<String, MqttSubscriptionModel>();  // The list of this connection's subscriptions
    private final ArrayList<MqttReceivedMessageData> messageHistory = new ArrayList<MqttReceivedMessageData>();
    private final ArrayList<IMqttReceivedMessageListener> receivedMessageListeners = new ArrayList<IMqttReceivedMessageListener>();

    /**
     * MqttConnectionCollection status for  a connection
     */
    public enum ConnectionStatus {
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        DISCONNECTED,
        ERROR,
        NONE
    }

    /**
     * Creates a connection from persisted information in the database store, attempting
     * to create a {@link MqttAndroidClient} and the client handle.
     *
     * @param clientId      The id of the client
     * @param host          the server which the client is connecting to
     * @param port          the port on the server which the client will attempt to connect to
     * @param context       the application context
     * @param tlsConnection true if the connection is secured by SSL
     * @return a new instance of <code>MqttConnection</code>
     */
    public static MqttConnection createConnection(String clientHandle, String clientId, String host, int port, Context context, boolean tlsConnection) {
        String uri;
        if (tlsConnection) {
            uri = "ssl://" + host + ":" + port;
        } else {
            uri = "tcp://" + host + ":" + port;
        }
        MqttAndroidClient client = new MqttAndroidClient(context, uri, clientId);
        return new MqttConnection(clientHandle, clientId, host, port, context, client, tlsConnection);
    }

    public void updateConnection(String clientId, String host, int port, boolean tlsConnection) {
        String uri;
        if (tlsConnection) {
            uri = "ssl://" + host + ":" + port;
        } else {
            uri = "tcp://" + host + ":" + port;
        }
        this.clientId = clientId;
        this.host = host;
        this.port = port;
        this.tlsConnection = tlsConnection;
        this.client = new MqttAndroidClient(context, uri, clientId);

    }

    /**
     * Constructor
     *
     * Creates a connection object with the server information and the client
     * hand which is the reference used to pass the client around activities
     *
     * @param clientHandle  The handle to this <code>MqttConnection</code> object
     * @param clientId      The Id of the client
     * @param host          The server which the client is connecting to
     * @param port          The port on the server which the client will attempt to connect to
     * @param context       The application context
     * @param client        The MqttAndroidClient which communicates with the service for this connection
     * @param tlsConnection true if the connection is secured by SSL
     */
    private MqttConnection(
            String clientHandle,
            String clientId,
            String host,
            int port,
            Context context,
            MqttAndroidClient client,
            boolean tlsConnection) {
        //generate the client handle from its hash code
        this.clientHandle = clientHandle;
        this.clientId = clientId;
        this.host = host;
        this.port = port;
        this.context = context;
        this.client = client;
        this.tlsConnection = tlsConnection;
        history = new ArrayList<String>();
        String sb = "Client: " +
                clientId +
                " created";
        addAction(sb);
    }

    /**
     * Add an action to the history of the client
     *
     * @param action the history item to add
     */
    public void addAction(String action) {

        Object[] args = new String[1];
        DateFormat dateTimeFormatter = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        args[0] = dateTimeFormatter.format(new Date());

        String timestamp = context.getString(R.string.timestamp, args);
        history.add(action + timestamp);

        notifyListeners(new PropertyChangeEvent(this, ActivityConstants.historyProperty, null, null));
    }

    /**
     * Gets the client handle for this connection
     *
     * @return client Handle for this connection
     */
    public String handle() {
        return clientHandle;
    }

    /**
     * Determines if the client is connected
     *
     * @return is the client connected
     */
    public boolean isConnected() {
        return status == ConnectionStatus.CONNECTED;
    }

    /**
     * Changes the connection status of the client
     *
     * @param connectionStatus The connection status of this connection
     */
    public void changeConnectionStatus(ConnectionStatus connectionStatus) {
        status = connectionStatus;
        notifyListeners((new PropertyChangeEvent(this, ActivityConstants.ConnectionStatusProperty, null, null)));
    }

    /**
     * A string representing the state of the client this connection
     * object represents
     *
     * @return A string representing the state of the client
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(clientId);
        sb.append("\n ");

        switch (status) {

            case CONNECTED:
                sb.append(context.getString(R.string.connection_connected_to));
                break;
            case DISCONNECTED:
                sb.append(context.getString(R.string.connection_disconnected_from));
                break;
            case NONE:
                sb.append(context.getString(R.string.connection_unknown_status));
                break;
            case CONNECTING:
                sb.append(context.getString(R.string.connection_connecting_to));
                break;
            case DISCONNECTING:
                sb.append(context.getString(R.string.connection_disconnecting_from));
                break;
            case ERROR:
                sb.append(context.getString(R.string.connection_error_connecting_to));
        }
        sb.append(" ");
        sb.append(host);

        return sb.toString();
    }

    /**
     * Compares two connection objects for equality
     * this only takes account of the client handle
     *
     * @param o The object to compare to
     * @return true if the client handles match
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MqttConnection)) {
            return false;
        }

        MqttConnection c = (MqttConnection) o;

        return clientHandle.equals(c.clientHandle);

    }

    /**
     * Get the client Id for the client this object represents
     *
     * @return the client id for the client this object represents
     */
    public String getId() {
        return clientId;
    }

    /**
     * Get the host name of the server that this connection object is associated with
     *
     * @return the host name of the server this connection object is associated with
     */
    public String getHostName() {

        return host;
    }

    /**
     * Gets the client which communicates with the org.eclipse.paho.android.service service.
     *
     * @return the client which communicates with the org.eclipse.paho.android.service service
     */
    public MqttAndroidClient getClient() {
        return client;
    }

    /**
     * Add the connectOptions used to connect the client to the server
     *
     * @param connectOptions the connectOptions used to connect to the server
     */
    public void addConnectionOptions(MqttConnectOptions connectOptions) {
        mqttConnectOptions = connectOptions;

    }

    /**
     * Get the connectOptions used to connect this client to the server
     *
     * @return The connectOptions used to connect the client to the server
     */
    public MqttConnectOptions getConnectionOptions() {
        return mqttConnectOptions;
    }

    /**
     * Register a {@link PropertyChangeListener} to this object
     *
     * @param listener the listener to register
     */
    public void registerChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Notify {@link PropertyChangeListener} objects that the object has been updated
     *
     * @param propertyChangeEvent - The property Change event
     */
    private void notifyListeners(PropertyChangeEvent propertyChangeEvent) {
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(propertyChangeEvent);
        }
    }

    /**
     * Gets the port that this connection connects to.
     *
     * @return port that this connection connects to
     */
    public int getPort() {
        return port;
    }

    /**
     * Determines if the connection is secured using SSL, returning a C style integer value
     *
     * @return 1 if SSL secured 0 if plain text
     */
    public int isSSL() {
        return tlsConnection ? 1 : 0;
    }

    /**
     * Assign a persistence ID to this object
     *
     * @param id the persistence id to assign
     */
    public void assignPersistenceId(long id) {
        persistenceId = id;
    }

    public long persistenceId() {
        return persistenceId;
    }

    public void addNewSubscription(MqttSubscriptionModel mqttSubscriptionModel) throws MqttException {
        if (!subscriptions.containsKey(mqttSubscriptionModel.getTopic())) {
            try {
                String[] actionArgs = new String[1];
                actionArgs[0] = mqttSubscriptionModel.getTopic();
                final MqttActionListener callback = new MqttActionListener(this.context,
                        MqttActionListener.Action.SUBSCRIBE, this, actionArgs);
                this.getClient().subscribe(mqttSubscriptionModel.getTopic(), mqttSubscriptionModel.getQos(), null, callback);
                MqttPersistence mqttPersistence = new MqttPersistence(context);

                long rowId = mqttPersistence.persistSubscription(mqttSubscriptionModel);
                mqttSubscriptionModel.setPersistenceId(rowId);
                subscriptions.put(mqttSubscriptionModel.getTopic(), mqttSubscriptionModel);
            } catch (MqttPersistenceException pe) {
                throw new MqttException(pe);
            }

        }
    }


    public void unsubscribe(MqttSubscriptionModel mqttSubscriptionModel) throws MqttException {
        if (subscriptions.containsKey(mqttSubscriptionModel.getTopic())) {
            this.getClient().unsubscribe(mqttSubscriptionModel.getTopic());
            subscriptions.remove(mqttSubscriptionModel.getTopic());
            MqttPersistence mqttPersistence = new MqttPersistence(context);
            mqttPersistence.deleteSubscription(mqttSubscriptionModel);
        }

    }

    public void setSubscriptions(ArrayList<MqttSubscriptionModel> newSubs) {
        for (MqttSubscriptionModel sub : newSubs) {
            subscriptions.put(sub.getTopic(), sub);
        }
    }

    public ArrayList<MqttSubscriptionModel> getSubscriptions() {
        ArrayList<MqttSubscriptionModel> subs = new ArrayList<MqttSubscriptionModel>();
        subs.addAll(subscriptions.values());
        return subs;
    }

    public void addReceivedMessageListner(IMqttReceivedMessageListener listener) {
        receivedMessageListeners.add(listener);
    }

    public void messageArrived(String topic, MqttMessage message) {
        MqttReceivedMessageData msg = new MqttReceivedMessageData(topic, message);
        messageHistory.add(0, msg);
        if (subscriptions.containsKey(topic)) {
            subscriptions.get(topic).setLastMessage(new String(message.getPayload()));
            if (subscriptions.get(topic).isEnableNotifications()) {
                //create intent to start activity
                Intent intent = new Intent();
                intent.setClassName(context, activityClass);
                intent.putExtra("handle", clientHandle);

                //format string args
                Object[] notifyArgs = new String[3];
                notifyArgs[0] = this.getId();
                notifyArgs[1] = new String(message.getPayload());
                notifyArgs[2] = topic;

                //notify the user
                Notify.notifcation(context, context.getString(R.string.notification, notifyArgs), intent, R.string.notifyTitle);

            }
        }

        for (IMqttReceivedMessageListener listener : receivedMessageListeners) {
            listener.onMessageReceived(msg);
        }


    }

    public ArrayList<MqttReceivedMessageData> getMessages() {
        return messageHistory;
    }
}