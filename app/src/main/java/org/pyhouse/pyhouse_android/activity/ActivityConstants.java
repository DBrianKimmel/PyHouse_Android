/*
 * Created by briank on 5/21/17.
 */
package org.pyhouse.pyhouse_android.activity;

import org.eclipse.paho.client.mqttv3.MqttMessage;


public class ActivityConstants {

    static final String TAG = "MQTT Android";

    /** Bundle key for passing a connection around by it's name **/
    public static final String CONNECTION_KEY = "CONNECTION_KEY";
    public static final String AUTO_CONNECT = "AUTO_CONNECT";
    public static final String CONNECTED = "CONNECTEd";
    public static final String LOGGING_KEY = "LOGGING_ENABLED";

  /*Default values **/
    static final int defaultQos = 0;
    static final int defaultTimeOut = 1000;
    static final int defaultKeepAlive = 10;
    static final boolean defaultSsl = false;
    static final boolean defaultRetained = false;
    static final MqttMessage defaultLastWill = null;
    static final int defaultPort = 1883;

    /** Connect Request Code */
    static final int connect = 0;
    /** Advanced Connect Request Code  **/
    static final int advancedConnect = 1;
    /** Last will Request Code  **/
    static final int lastWill = 2;
    /** Show History Request Code  **/
    static final int showHistory = 3;

  /* Bundle Keys */

    /** Server Bundle Key **/
    static final String server = "server";
    /** Port Bundle Key **/
    static final String port = "port";
    /** ClientID Bundle Key **/
    static final String clientId = "clientId";
    /** Topic Bundle Key **/
    static final String topic = "topic";
    /** History Bundle Key **/
    static final String history = "history";
    /** Message Bundle Key **/
    static final String message = "message";
    /** Retained Flag Bundle Key **/
    static final String retained = "retained";
    /** QOS Value Bundle Key **/
    static final String qos = "qos";
    /** User name Bundle Key **/
    static final String username = "username";
    /** Password Bundle Key **/
    static final String password = "password";
    /** Keep Alive value Bundle Key **/
    static final String keepalive = "keepalive";
    /** Timeout Bundle Key **/
    static final String timeout = "timeout";
    /** SSL Enabled Flag Bundle Key **/
    static final String ssl = "ssl";
    /** SSL Key File Bundle Key **/
    static final String ssl_key = "ssl_key";
    /** MqttConnectionCollection Bundle Key **/
    static final String connections = "connections";
    /** Clean Session Flag Bundle Key **/
    static final String cleanSession = "cleanSession";
    /** Action Bundle Key **/
    static final String action = "action";

  /* Property names */

    /** Property name for the history field in {@link MqttConnection} object for use with {@link java.beans.PropertyChangeEvent} **/
    static final String historyProperty = "history";

    /** Property name for the connection status field in {@link MqttConnection} object for use with {@link java.beans.PropertyChangeEvent} **/
    public static final String ConnectionStatusProperty = "connectionStatus";

  /* Useful constants*/

    /** Space String Literal **/
    static final String space = " ";
    /** Empty String for comparisons **/
    public static final String empty = new String();

}
