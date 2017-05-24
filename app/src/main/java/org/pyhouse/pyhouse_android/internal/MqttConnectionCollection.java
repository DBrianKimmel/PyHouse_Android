/*
 * Created by briank on 5/21/17.
 */
package org.pyhouse.pyhouse_android.internal;

import android.content.Context;

import org.pyhouse.pyhouse_android.activity.MqttConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <code>MqttConnectionCollection</code> is a singleton class which stores all the connection objects
 * in one central place so they can be passed between activities using a client handle.
 */
public class MqttConnectionCollection {

    /**
     * Singleton instance of <code>MqttConnectionCollection</code>
     **/
    private static MqttConnectionCollection instance = null;

    /**
     * List of {@link MqttConnection} object
     **/
    private HashMap<String, MqttConnection> connections = null;

    /**
     * {@link Persistence} object used to save, delete and restore connections
     **/
    private Persistence persistence = null;

    /**
     * Create a MqttConnectionCollection object
     *
     * @param context Applications context
     */
    private MqttConnectionCollection(Context context) {
        connections = new HashMap<String, MqttConnection>();

        // If there is state, attempt to restore it
        persistence = new Persistence(context);
        try {
            List<MqttConnection> mqttConnectionList = persistence.restoreConnections(context);
            for (MqttConnection mqttConnection : mqttConnectionList) {
                System.out.println("MqttConnection was persisted.." + mqttConnection.handle());
                connections.put(mqttConnection.handle(), mqttConnection);
            }
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns an already initialised instance of <code>MqttConnectionCollection</code>, if MqttConnectionCollection has yet to be created, it will
     * create and return that instance.
     *
     * @param context The applications context used to create the <code>MqttConnectionCollection</code> object if it is not already initialised
     * @return <code>MqttConnectionCollection</code> instance
     */
    public synchronized static MqttConnectionCollection getInstance(Context context) {
        if (instance == null) {
            instance = new MqttConnectionCollection(context);
        }
        return instance;
    }

    /**
     * Finds and returns a {@link MqttConnection} object that the given client handle points to
     *
     * @param handle The handle to the {@link MqttConnection} to return
     * @return a connection associated with the client handle, <code>null</code> if one is not found
     */
    public MqttConnection getConnection(String handle) {
        return connections.get(handle);
    }

    /**
     * Adds a {@link MqttConnection} object to the collection of connections associated with this object
     *
     * @param mqttConnection {@link MqttConnection} to add
     */
    public void addConnection(MqttConnection mqttConnection) {
        connections.put(mqttConnection.handle(), mqttConnection);
        try {
            persistence.persistConnection(mqttConnection);
        } catch (PersistenceException e) {
            // @todo Handle this error more appropriately
            //error persisting well lets just swallow this
            e.printStackTrace();
        }

    }

// --Commented out by Inspection START (12/10/2016, 10:21):
//    /**
//     * Create a fully initialised <code>MqttAndroidClient</code> for the parameters given
//     * @param context The Applications context
//     * @param serverURI The ServerURI to connect to
//     * @param clientId The clientId for this client
//     * @return new instance of MqttAndroidClient
//     */
//    public MqttAndroidClient createClient(Context context, String serverURI, String clientId){
//        return new MqttAndroidClient(context, serverURI, clientId);
//    }
// --Commented out by Inspection STOP (12/10/2016, 10:21)

    /**
     * Get all the connections associated with this <code>MqttConnectionCollection</code> object.
     *
     * @return <code>Map</code> of connections
     */
    public Map<String, MqttConnection> getConnections() {
        return connections;
    }

    /**
     * Removes a mqttConnection from the map of connections
     *
     * @param mqttConnection mqttConnection to be removed
     */
    public void removeConnection(MqttConnection mqttConnection) {
        connections.remove(mqttConnection.handle());
        persistence.deleteConnection(mqttConnection);
    }


    /**
     * Updates an existing mqttConnection within the map of
     * connections as well as in the persisted model
     *
     * @param mqttConnection mqttConnection to be updated.
     */
    public void updateConnection(MqttConnection mqttConnection) {
        connections.put(mqttConnection.handle(), mqttConnection);
        persistence.updateConnection(mqttConnection);
    }


}