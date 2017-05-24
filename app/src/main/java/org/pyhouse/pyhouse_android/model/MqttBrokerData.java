/*
 * Created by briank on 5/19/17.
 *
 * This default data is where the initial connection will be directed.if the database is empty.
 */

package org.pyhouse.pyhouse_android.model;

import android.provider.Settings;


public class MqttBrokerData {
    public static String brokerClientID = "PyH_Android" + System.nanoTime();
    public static String brokerIpAddress = "192.168.1.10";
    public static String brokerInetType = "tcp:";
    public static String brokerUsername = "";
    public static String brokerPassword = "";
    public static int brokerPort = 1883;
}

// ### END DBK
