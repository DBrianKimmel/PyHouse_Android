/*
 * Created by briank on 5/21/17.
 *
 */
package org.pyhouse.pyhouse_android.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pyhouse.pyhouse_android.R;
import org.pyhouse.pyhouse_android.adapter.NavigationDrawerAdapter;
import org.pyhouse.pyhouse_android.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class FragmentDrawer extends Fragment {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter mAdapter;
    private View containerView;
    private FragmentDrawerListener drawerListener;
    private final List<NavDrawerItem> data = new ArrayList<NavDrawerItem>();

    public FragmentDrawer() {
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public void addConnection(MqttConnection mqttConnection) {
        System.out.println("Adding new MqttConnection:  " + mqttConnection.getId());
        NavDrawerItem navItem = new NavDrawerItem(mqttConnection);
        data.add(navItem);
        mAdapter.notifyDataSetChanged();
    }

    public void updateConnection(MqttConnection mqttConnection) {
        System.out.println("Updating MqttConnection: " + mqttConnection.getId());
        Iterator<NavDrawerItem> iterator = data.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            NavDrawerItem item = iterator.next();
            if (item.getHandle().equals(mqttConnection.handle())) {
                item = new NavDrawerItem(mqttConnection);
                data.set(index, item);
                break;
            }
            index++;
        }
        mAdapter.notifyDataSetChanged();
    }

    public void removeConnection(MqttConnection mqttConnection) {
        System.out.println("Removing mqttConnection from drawer: " + mqttConnection.getId());
        Iterator<NavDrawerItem> iterator = data.iterator();
        while (iterator.hasNext()) {
            NavDrawerItem item = iterator.next();
            if (item.getHandle().equals(mqttConnection.handle())) {
                iterator.remove();
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public void clearConnections() {
        data.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    private List<NavDrawerItem> getData() {
        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);

        TextView addConnectionTextView = (TextView) layout.findViewById(R.id.action_add_connection);
        addConnectionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerListener.onAddConnectionSelected();
                mDrawerLayout.closeDrawer(containerView);
            }
        });
        TextView helpTextView = (TextView) layout.findViewById(R.id.action_help);
        helpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerListener.onHelpSelected();
                mDrawerLayout.closeDrawer(containerView);
            }
        });
        mAdapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {

            @Override
            public void onClick(int position) {
                drawerListener.onDrawerItemSelected(position);
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(int position) {
                System.out.println("I want to delete: " + position);
                drawerListener.onDrawerItemLongSelected(position);
                mDrawerLayout.closeDrawer(containerView);
            }
        }));
        return layout;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public interface ClickListener {
        void onClick(int position);

        void onLongClick(int position);
    }




    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private final GestureDetector gestureDetector;
        private final ClickListener clickListener;

        public RecyclerTouchListener(
                Context context,
                final RecyclerView recyclerView,
                final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(
                RecyclerView rv,
                MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(int position);

        void onDrawerItemLongSelected(int position);

        void onAddConnectionSelected();

        void onHelpSelected();
    }
}