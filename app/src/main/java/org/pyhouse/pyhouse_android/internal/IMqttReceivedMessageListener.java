/*
 * Created by briank on 5/21/17.
 */
package org.pyhouse.pyhouse_android.internal;

import org.pyhouse.pyhouse_android.model.MqttReceivedMessageData;


public interface IMqttReceivedMessageListener {

    void onMessageReceived(MqttReceivedMessageData message);
}