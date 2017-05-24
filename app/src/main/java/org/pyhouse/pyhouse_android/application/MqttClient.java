/*
 * Created by briank on 5/19/17.
 */

package org.pyhouse.pyhouse_android.application;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import org.pyhouse.pyhouse_android.model.HouseData;

import android.support.v7.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;


public class MqttClient extends AppCompatActivity {
    static final String TAG = "MqttClient           :";
    static final String mMqttClientId = "PyH-" + System.nanoTime();
    static final String mMqttBrokerAddress = "tcp://192.168.1.10:1883";
    static String lwtTopic = "pyhouse/" + HouseData.Name + "users/last/will";

    static Context mContext;
    static MqttAndroidClient mClient;
    MqttConnectOptions mOptions;
    byte[] payload = "some payload".getBytes();


    // Constructor - create a connection.
    MqttClient(Context context) {
        Log.w(TAG, "Enter MqttClient() constructor");
        mContext = context;
        MqttConnect();
    }

    /**
     * The Mqtt protocol connect packet.
     */
    public void MqttConnect() {
        Log.w(TAG, "Enter MqttConnect");
        mOptions = new MqttConnectOptions();
        mClient = new MqttAndroidClient(mContext, mMqttBrokerAddress, mMqttClientId);
        mOptions.setWill(lwtTopic, payload , 0, false);
        // mOptions.setUserName("USERNAME");
        // mOptions.setPassword("PASSWORD".toCharArray());
        try {
            IMqttToken token = mClient.connect(mOptions);
            token.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.w(TAG, "Connected MQTT");
                    MqttSubscribe("pyhouse/");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.w(TAG, "onFailure");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void MqttPublish() {
        String topic = "foo/bar";
        String payload = "the payload";
        Log.w(TAG, "Enter MqttPublish");
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            mClient.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    public void MqttSubscribe(String pTopic) {
        Log.w(TAG, "Enter MqttSubscribe");
        int qos = 1;
        try {
            IMqttToken subToken = mClient.subscribe(pTopic, qos);
            subToken.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w(TAG, "Subscribe success");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w(TAG, "Subscribe failure");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}

// END DBK
