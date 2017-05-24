/*
 * Created by briank on 5/21/17.
 */
package org.pyhouse.pyhouse_android.internal;

import org.pyhouse.pyhouse_android.model.ReceivedMessage;


public interface IReceivedMessageListener {

    void onMessageReceived(ReceivedMessage message);
}