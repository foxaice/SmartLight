package me.foxaice.smartlight.fragments.controllers_screen.controller_management.dialogs.wifilist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.foxaice.smartlight.R;

class WifiPointsAdapter extends RecyclerView.Adapter<WifiPointHolder> {
    private List<WifiPoint> mWifiPointsList;
    private WifiListDialog mWifiListDialog;
    private Comparator<WifiPoint> mComparator = new Comparator<WifiPoint>() {
        @Override
        public int compare(WifiPoint o1, WifiPoint o2) {
            return o1.getSignal() == o2.getSignal() ? 0 : o1.getSignal() > o2.getSignal() ? 1 : -1;
        }
    };

    WifiPointsAdapter(WifiListDialog dialog) {
        mWifiPointsList = new ArrayList<>();
        mWifiListDialog = dialog;
    }

    static int getIndexDrawableBySignal(WifiPoint wifiPoint) {
        return wifiPoint.getSignal() < 0 ?
                0 : wifiPoint.getSignal() >= 0 && wifiPoint.getSignal() < 25 ?
                1 : wifiPoint.getSignal() >= 25 && wifiPoint.getSignal() < 50 ?
                2 : wifiPoint.getSignal() >= 50 && wifiPoint.getSignal() < 75 ?
                3 : 4;
    }

    @Override
    public WifiPointHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi_point, parent, false);
        return new WifiPointHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WifiPointHolder holder, int position) {
        final WifiPoint wifiPoint = mWifiPointsList.get(position);
        int indexDrawable = getIndexDrawableBySignal(wifiPoint);
        holder.signalImage.setImageDrawable(mWifiListDialog.getWifiSignalLevelDrawables()[indexDrawable]);
        holder.BSSIDText.setText(wifiPoint.getBSSID());
        holder.SSIDText.setText(wifiPoint.getSSID());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWifiListDialog.onWifiPointClick(wifiPoint);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWifiPointsList.size();
    }

    void add(WifiPoint item) {
        mWifiPointsList.add(item);
        notifyDataSetChanged();
    }

    void remove(WifiPoint item) {
        mWifiPointsList.remove(item);
        notifyDataSetChanged();
    }

    WifiPoint getWifiPointBySSID(String ssid) {
        for (WifiPoint item : mWifiPointsList) {
            if (ssid.equals(item.getSSID())) {
                return item;
            }
        }
        return null;
    }

    void fillWifiPointsList(String response) {
        String[] array = response.split("\n\r");
        for (int i = 2; i < array.length; i++) {
            String[] temp = array[i].split(",");
            if (temp.length > 4) {
                mWifiPointsList.add(new WifiPoint(temp[0], temp[1], temp[2], temp[3], temp[4]));
            }
        }
        notifyDataSetChanged();
    }

    void sortByDesc() {
        Collections.sort(mWifiPointsList, mComparator);
        Collections.reverse(mWifiPointsList);
    }
}