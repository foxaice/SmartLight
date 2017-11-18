package me.foxaice.smartlight.fragments.controllers_screen.controllers_list.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.utils.TextUtils;
import me.foxaice.smartlight.utils.Validator;

class ControllerListAdapter extends ArrayAdapter<String> {
    private IControllerListView mView;
    private List<String> mItemAddress;
    private int mResourceId;

    ControllerListAdapter(@NonNull IControllerListView view, @NonNull List<String> objects) {
        super(view.getContext(), R.layout.item_controllers_list, objects);
        mView = view;
        mResourceId = R.layout.item_controllers_list;
        mItemAddress = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater;
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            inflater = (LayoutInflater) mView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResourceId, parent, false);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.item_controllers_list_text_name);
            holder.addressTextView = (TextView) convertView.findViewById(R.id.item_controllers_list_text_address);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String[] listItems = mItemAddress.get(position).split(",");
        if (listItems.length >= 2) {
            holder.ipAddress = Validator.isIpAddress(listItems[0]) ? listItems[0] : null;
            holder.macAddress = Validator.isMacAddress(listItems[1]) ? listItems[1] : null;
            holder.macAddressWithColons = TextUtils.addColonsToMacAddress(holder.macAddress);
            holder.nameController = mView.getControllerNameByMACAddress(holder.macAddress);

            holder.nameTextView.setText(holder.nameController);
            holder.addressTextView.setText(holder.macAddressWithColons);
        }
        return convertView;
    }

    class ViewHolder {
        String ipAddress;
        String macAddressWithColons;
        String nameController;
        String macAddress;
        TextView nameTextView;
        TextView addressTextView;
    }
}
