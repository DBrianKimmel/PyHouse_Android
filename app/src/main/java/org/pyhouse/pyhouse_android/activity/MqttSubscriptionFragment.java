/*
 * Created by briank on 5/21/17.
 */
package org.pyhouse.pyhouse_android.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import org.eclipse.paho.client.mqttv3.MqttException;

import org.pyhouse.pyhouse_android.R;
import org.pyhouse.pyhouse_android.components.SubscriptionListItemAdapter;
import org.pyhouse.pyhouse_android.internal.MqttConnectionCollection;
import org.pyhouse.pyhouse_android.model.MqttSubscriptionModel;

import java.util.ArrayList;
import java.util.Map;


public class MqttSubscriptionFragment extends Fragment {

    private int temp_qos_value = 0;
    // --Commented out by Inspection (12/10/2016, 10:22):ListView subscriptionListView;

    private ArrayList<MqttSubscriptionModel> mqttSubscriptionModels;

    private MqttConnection mqttConnection;

    public MqttSubscriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        String connectionHandle = bundle.getString(ActivityConstants.CONNECTION_KEY);
        Map<String, MqttConnection> connections = MqttConnectionCollection.getInstance(this.getActivity())
                .getConnections();
        mqttConnection = connections.get(connectionHandle);
        mqttSubscriptionModels = mqttConnection.getSubscriptions();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subscriptions, container, false);
        Button subscribeButton = (Button) rootView.findViewById(R.id.subscribe_button);

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        ListView subscriptionListView = (ListView) rootView.findViewById(R.id.subscription_list_view);
        SubscriptionListItemAdapter adapter = new SubscriptionListItemAdapter(this.getActivity(), mqttSubscriptionModels);

        adapter.addOnUnsubscribeListner(new SubscriptionListItemAdapter.OnUnsubscribeListner() {
            @Override
            public void onUnsubscribe(MqttSubscriptionModel mqttSubscriptionModel) {
                try {
                    mqttConnection.unsubscribe(mqttSubscriptionModel);
                    System.out.println("Unsubscribed from: " + mqttSubscriptionModel.toString());
                } catch (MqttException ex) {
                    System.out.println("Failed to unsubscribe from " + mqttSubscriptionModel.toString() + ". " + ex.getMessage());
                }
            }
        });
        subscriptionListView.setAdapter(adapter);


        // Inflate the layout for this fragment
        return rootView;
    }

    private void showInputDialog() {
        LayoutInflater layoutInflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View promptView = layoutInflater.inflate(R.layout.subscription_dialog, null);
        final EditText topicText = (EditText) promptView.findViewById(R.id.subscription_topic_edit_text);

        final Spinner qos = (Spinner) promptView.findViewById(R.id.subscription_qos_spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.qos_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        qos.setAdapter(adapter);
        qos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                temp_qos_value = Integer.parseInt(getResources().getStringArray(R.array.qos_options)[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Switch notifySwitch = (Switch) promptView.findViewById(R.id.show_notifications_switch);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext());
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(true).setPositiveButton(R.string.subscribe_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String topic = topicText.getText().toString();

                MqttSubscriptionModel mqttSubscriptionModel = new MqttSubscriptionModel(topic, temp_qos_value, mqttConnection.handle(), notifySwitch.isChecked());
                mqttSubscriptionModels.add(mqttSubscriptionModel);
                try {
                    mqttConnection.addNewSubscription(mqttSubscriptionModel);

                } catch (MqttException ex) {
                    System.out.println("MqttException whilst subscribing: " + ex.getMessage());
                }
                adapter.notifyDataSetChanged();
            }

        }).setNegativeButton(R.string.subscribe_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alert.show();
    }

}