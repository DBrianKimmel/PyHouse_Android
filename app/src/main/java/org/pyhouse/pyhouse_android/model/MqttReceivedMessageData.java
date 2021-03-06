package org.pyhouse.pyhouse_android.model;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Date;

/*
 * Created by briank on 5/20/17.
 */


public class MqttReceivedMessageData {

    public MqttReceivedMessageData(String topic, MqttMessage message) {
        this.topic = topic;
        this.message = message;
        this.timestamp = new Date();
    }

    private final String topic;
    private final MqttMessage message;
    private final Date timestamp;

    public String getTopic() {
        return topic;
    }

    public MqttMessage getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "MqttReceivedMessageData{" +
                "topic='" + topic + '\'' +
                ", message=" + message +
                ", timestamp=" + timestamp +
                '}';
    }
}