/*
 * Created by briank on 5/21/17.
 */

package org.pyhouse.pyhouse_android.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.pyhouse.pyhouse_android.R;
import org.pyhouse.pyhouse_android.components.MessageListItemAdapter;
import org.pyhouse.pyhouse_android.internal.MqttConnectionCollection;
import org.pyhouse.pyhouse_android.internal.IMqttReceivedMessageListener;
import org.pyhouse.pyhouse_android.model.MqttReceivedMessageData;

import java.util.ArrayList;
import java.util.Map;


public class HistoryFragment extends Fragment {

    private MessageListItemAdapter messageListAdapter;
    private ArrayList<MqttReceivedMessageData> messages;

    public HistoryFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map<String, MqttConnection> connections = MqttConnectionCollection.getInstance(this.getActivity())
                .getConnections();
        MqttConnection mqttConnection = connections.get(this.getArguments().getString(ActivityConstants.CONNECTION_KEY));
        System.out.println("History Fragment: " + mqttConnection.getId());
        setHasOptionsMenu(true);
        messages = mqttConnection.getMessages();
        mqttConnection.addReceivedMessageListner(new IMqttReceivedMessageListener() {

            @Override
            public void onMessageReceived(MqttReceivedMessageData message) {
                System.out.println("GOT A MESSAGE in history " + new String(message.getMessage().getPayload()));
                System.out.println("M: " + messages.size());
                messageListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connection_history, container, false);
        messageListAdapter = new MessageListItemAdapter(getActivity(), messages);
        ListView messageHistoryListView = (ListView) rootView.findViewById(R.id.history_list_view);
        messageHistoryListView.setAdapter(messageListAdapter);
        Button clearButton = (Button) rootView.findViewById(R.id.history_clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                messages.clear();
                messageListAdapter.notifyDataSetChanged();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }
}

// ### END DBK
