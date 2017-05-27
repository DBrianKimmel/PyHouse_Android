/*
 * Created by briank on 5/21/17.
 */
package org.pyhouse.pyhouse_android.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.pyhouse.pyhouse_android.R;
import org.pyhouse.pyhouse_android.application.MainActivity;
import org.pyhouse.pyhouse_android.internal.MqttConnectionCollection;

import java.util.Map;


public class ManageConnectionFragment extends Fragment {
    private MqttConnection mqttConnection;
    private Map<String, MqttConnection> connections;
    private String connectionKey;


    public ManageConnectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connections = MqttConnectionCollection.getInstance(this.getActivity())
                .getConnections();
        connectionKey = this.getArguments().getString(ActivityConstants.CONNECTION_KEY);
        mqttConnection = connections.get(connectionKey);
        setHasOptionsMenu(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage, container, false);
        final String name = mqttConnection.getId() + "@" + mqttConnection.getHostName() + ":" + mqttConnection.getPort();
        TextView label = (TextView) rootView.findViewById(R.id.connection_id_text);
        label.setText(name);

        Button deleteButton = (Button) rootView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Deleting MqttConnection: " + name + ".");
                connections.remove(connectionKey);
                MqttConnectionCollection.getInstance(getActivity()).removeConnection(mqttConnection);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, new HomeFragment());
                fragmentTransaction.commit();
                ((MainActivity) getActivity()).removeConnectionRow(mqttConnection);
            }
        });

        Button editButton = (Button) rootView.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Editing MqttConnection: " + name + ".");
                MqttEditConnectionFragment mqttEditConnectionFragment = new MqttEditConnectionFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ActivityConstants.CONNECTION_KEY, mqttConnection.handle());
                mqttEditConnectionFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, mqttEditConnectionFragment);
                fragmentTransaction.commit();
            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

}