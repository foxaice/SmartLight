package me.foxaice.smartlight.activities.music_mode_settings.view;

import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.foxaice.smartlight.R;

class ColorModeHolder extends RecyclerView.ViewHolder {
    final ImageView colorModeImage;
    final TextView colorModeText;
    final ViewGroup colorModeRoot;
    Spanned colorModeSpannedText;

    ColorModeHolder(View itemView) {
        super(itemView);
        colorModeImage = (ImageView) itemView.findViewById(R.id.item_color_mode_image);
        colorModeText = (TextView) itemView.findViewById(R.id.item_color_mode_text);
        colorModeRoot = (ViewGroup) itemView.findViewById(R.id.item_color_mode_root);
    }

    Spanned getColorModeSpannedText() {
        return colorModeSpannedText;
    }
}
