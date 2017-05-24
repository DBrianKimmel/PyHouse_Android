package org.pyhouse.pyhouse_android.model;

import android.provider.Settings;

/**
 * Created by briank on 5/19/17.
 */

public class MQTTData {
    public static String brokerClientID = "PyH_Android" + System.nanoTime();
    public static String brokerIpAddress = "192.168.1.10";
    public static String brokerUsername = "";
    public static String brokerPassword = "";
    public static int brokerPort = 1883;
}
