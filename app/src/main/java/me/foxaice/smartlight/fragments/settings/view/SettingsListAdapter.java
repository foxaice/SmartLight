package me.foxaice.smartlight.fragments.settings.view;

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

public class SettingsListAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final String[] mNamesItems;
    private final int[] mIdImages;

    public SettingsListAdapter(Context context, String[] namesItems, int[] idImages) {
        super(context, R.layout.item_settings_list, namesItems);
        mContext = context;
        mNamesItems = Arrays.copyOf(namesItems, namesItems.length);
        mIdImages = Arrays.copyOf(idImages, idImages.length);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        SettingsViewHolder holder;

        if (convertView == null) {
            holder = new SettingsViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_settings_list, parent, false);
            holder.icon = (ImageView) convertView.findViewById(R.id.item_settings_list_icon);
            holder.text = (TextView) convertView.findViewById(R.id.item_settings_list_text);
            convertView.setTag(holder);
        } else {
            holder = (SettingsViewHolder) convertView.getTag();
        }
        holder.icon.setImageResource(mIdImages[position]);
        holder.text.setText(mNamesItems[position]);

        return convertView;
    }

    private static class SettingsViewHolder {
        ImageView icon;
        TextView text;
    }
}
