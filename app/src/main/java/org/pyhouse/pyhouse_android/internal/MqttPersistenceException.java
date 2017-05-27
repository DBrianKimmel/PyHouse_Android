/*
 * Created by briank on 5/21/17.
 */
package org.pyhouse.pyhouse_android.internal;


/*
 * MqttPersistence Exception, defines an error with persisting a {@link mqttConnection}
 * fails.
 * Example operations are {@link MqttPersistence#persistConnection(MqttConnection)} and {@link MqttPersistence#restoreConnections(android.content.Context)};
 * these operations throw this exception to indicate unexpected results occurred when performing actions on the database.
 *
 */
public class MqttPersistenceException extends Exception {

    /**
     * Creates a persistence exception with the given error message
     * @param message The error message to display
     */
    public MqttPersistenceException(String message) {
        super(message);
    }

    /** Serialisation ID**/
    private static final long serialVersionUID = 5326458803268855071L;
}

// ### END DBK
