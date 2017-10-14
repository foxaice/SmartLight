package me.foxaice.smartlight.activities.redefinition_zones_screen.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

import me.foxaice.smartlight.R;

class RedefinitionZonesListAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final String[] mNamesItems;
    private final int[] mIdImages;

    RedefinitionZonesListAdapter(Context context, String[] namesItems, int[] idImages) {
        super(context, R.layout.item_settings_list, namesItems);
        mContext = context;
        mNamesItems = Arrays.copyOf(namesItems, namesItems.length);
        mIdImages = Arrays.copyOf(idImages, idImages.length);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_zone_connection, parent, false);
            holder.root = (ViewGroup) convertView.findViewById(R.id.item_zone_connection_root);
            holder.icon = (ImageView) convertView.findViewById(R.id.item_zone_connection_icon);
            holder.text = (TextView) convertView.findViewById(R.id.item_zone_connection_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Drawable background = ContextCompat.getDrawable(getContext(),
                position == 0 ? R.drawable.selector_button_background_without_frame_red : R.drawable.selector_button_background_without_frame_green);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            holder.root.setBackgroundDrawable(background);
        } else {
            holder.root.setBackground(background);
        }
        holder.icon.setImageResource(mIdImages[position]);
        holder.text.setText(mNamesItems[position]);

        return convertView;
    }

    private static class ViewHolder {
        ViewGroup root;
        ImageView icon;
        TextView text;
    }
}
