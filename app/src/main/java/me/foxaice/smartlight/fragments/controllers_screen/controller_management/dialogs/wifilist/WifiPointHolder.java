package me.foxaice.smartlight.fragments.controllers_screen.controller_management.dialogs.wifilist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.foxaice.smartlight.R;

class WifiPointHolder extends RecyclerView.ViewHolder {
    final ImageView signalImage;
    final TextView SSIDText;
    final TextView BSSIDText;

    WifiPointHolder(View view) {
        super(view);
        signalImage = (ImageView) view.findViewById(R.id.fragment_controller_management_dialog_image_wifi_signal);
        SSIDText = (TextView) view.findViewById(R.id.fragment_controller_management_dialog_text_wifi_SSID);
        BSSIDText = (TextView) view.findViewById(R.id.fragment_controller_management_dialog_text_wifi_BSSID);
    }

    void setVisibility(int visibility) {
        itemView.setVisibility(visibility);
        signalImage.setVisibility(visibility);
        SSIDText.setVisibility(visibility);
        BSSIDText.setVisibility(visibility);
    }
}