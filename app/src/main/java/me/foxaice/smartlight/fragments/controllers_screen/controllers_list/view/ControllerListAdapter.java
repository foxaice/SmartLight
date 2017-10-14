package me.foxaice.smartlight.fragments.controllers_screen.controllers_list.view;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.utils.Validator;

class ControllerListAdapter extends ArrayAdapter<String> {
    private IControllerListView mView;
    private List<String> mItemAddress;
    private int mResourceId;

    ControllerListAdapter(@NonNull IControllerListView view, @LayoutRes int resource, @NonNull List<String> objects) {
        super(view.getContext(), resource, objects);
        mView = view;
        mResourceId = resource;
        mItemAddress = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater;
        ControllerViewHolder holder;

        if (convertView == null) {
            holder = new ControllerViewHolder();
            inflater = (LayoutInflater) mView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResourceId, parent, false);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.item_controllers_list_text_name);
            holder.addressTextView = (TextView) convertView.findViewById(R.id.item_controllers_list_text_address);
            convertView.setTag(holder);
        } else {
            holder = (ControllerViewHolder) convertView.getTag();
        }

        String[] listItems = mItemAddress.get(position).split(",");
        if (listItems.length >= 2) {
            holder.ipAddress = Validator.isIpAddress(listItems[0]) ? listItems[0] : null;
            holder.macAddress = Validator.isMacAddress(listItems[1]) ? listItems[1] : null;
            holder.macAddressWithColons = addColonsToMacAddress(holder.macAddress);
            holder.nameController = mView.getControllerNameByMACAddress(holder.macAddress);

            holder.nameTextView.setText(holder.nameController);
            holder.addressTextView.setText(holder.macAddressWithColons);
        }
        return convertView;
    }

    private String addColonsToMacAddress(String macAddress) {
        if (Validator.isMacAddress(macAddress)) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0, length = macAddress.length(); i < length; i++) {
                builder.append(macAddress.charAt(i));
                if (i % 2 != 0) builder.append(':');
            }
            builder.deleteCharAt(builder.length() - 1);
            return builder.toString();
        } else {
            return null;
        }
    }

    class ControllerViewHolder {
        String ipAddress;
        String macAddressWithColons;
        String nameController;
        String macAddress;
        TextView nameTextView;
        TextView addressTextView;
    }
}
