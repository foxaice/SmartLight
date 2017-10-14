package me.foxaice.smartlight.activities.renaming_zones_screen.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

import me.foxaice.smartlight.R;

class RenamingZonesListAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final String[] mNamesItems;
    private final int[] mIdImages;
    private final String[] mDefaultNames;

    RenamingZonesListAdapter(Context context, String[] namesItems) {
        super(context, R.layout.item_settings_list, namesItems);
        mContext = context;
        mDefaultNames = Arrays.copyOfRange(context.getResources().getStringArray(R.array.zone_names), 1, 5);
        mNamesItems = namesItems;
        mIdImages = new int[]{
                R.drawable.bulb_group_1,
                R.drawable.bulb_group_2,
                R.drawable.bulb_group_3,
                R.drawable.bulb_group_4
        };
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_renaming_zone, parent, false);
            holder.icon = (ImageView) convertView.findViewById(R.id.item_renaming_zone_icon_group);
            holder.header = (TextView) convertView.findViewById(R.id.item_renaming_zone_header);
            holder.subHeader = (TextView) convertView.findViewById(R.id.item_renaming_zone_sub_header);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.icon.setImageResource(mIdImages[position]);
        holder.header.setText(mDefaultNames[position]);
        holder.subHeader.setText(mNamesItems[position]);
        return convertView;
    }

    String[] getDefaultNames() {
        return mDefaultNames;
    }

    static class ViewHolder {
        TextView header;
        TextView subHeader;
        ImageView icon;
    }
}
