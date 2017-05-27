/*
 * Created by briank on 5/21/17.
 */
package org.pyhouse.pyhouse_android.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.pyhouse.pyhouse_android.R;
import org.pyhouse.pyhouse_android.application.MainActivity;
import org.pyhouse.pyhouse_android.internal.MqttConnectionCollection;

import java.util.Map;


public class MqttConnectionFragment extends Fragment {
    private MqttConnection mqttConnection;
    private FragmentTabHost mTabHost;
    private Switch connectSwitch;

    public MqttConnectionFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map<String, MqttConnection> connections = MqttConnectionCollection.getInstance(this.getActivity())
                .getConnections();
        mqttConnection = connections.get(this.getArguments().getString(ActivityConstants.CONNECTION_KEY));
        boolean connected = this.getArguments().getBoolean(ActivityConstants.CONNECTED, false);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_connection, container, false);


        Bundle bundle = new Bundle();
        bundle.putString(ActivityConstants.CONNECTION_KEY, mqttConnection.handle());

        // Initialise the tab-host
        mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
        // Add a tab to the tabHost
        mTabHost.addTab(mTabHost.newTabSpec("History").setIndicator("History"), MqttHistoryFragment.class, bundle);
        mTabHost.addTab(mTabHost.newTabSpec("Publish").setIndicator("Publish"), MqttPublishFragment.class, bundle);
        mTabHost.addTab(mTabHost.newTabSpec("Subscribe").setIndicator("Subscribe"), MqttSubscriptionFragment.class, bundle);
        return rootView;

    }

    private void changeConnectedState(boolean state) {
        mTabHost.getTabWidget().getChildTabViewAt(1).setEnabled(state);
        mTabHost.getTabWidget().getChildTabViewAt(2).setEnabled(state);
        connectSwitch.setChecked(state);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_connection, menu);


        connectSwitch = (Switch) menu.findItem(R.id.connect_switch).getActionView().findViewById(R.id.switchForActionBar);

        connectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((MainActivity) getActivity()).connect(mqttConnection);
                    changeConnectedState(true);
                } else {
                    ((MainActivity) getActivity()).disconnect(mqttConnection);
                    changeConnectedState(false);
                }
            }
        });
        changeConnectedState(mqttConnection.isConnected());
        super.onCreateOptionsMenu(menu, inflater);
    }

}
