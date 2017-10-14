package me.foxaice.smartlight.activities.music_mode_settings.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.fragments.modes.music_mode.view.MusicModeFragment;
import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;

class ColorModesAdapter extends RecyclerView.Adapter<ColorModeHolder> {
    private Context mContext;

    private MusicModeSettingsScreenActivity mActivity;
    private List<ColorMode> mColorModesList = Arrays.asList(
            new ColorMode(IMusicInfo.ColorMode.RGBM, R.drawable.frequency_rgbm, R.string.content_description_RGBM),
            new ColorMode(IMusicInfo.ColorMode.GBRY, R.drawable.frequency_gbry, R.string.content_description_GBRY),
            new ColorMode(IMusicInfo.ColorMode.BRGC, R.drawable.frequency_brgc, R.string.content_description_BRGC),
            new ColorMode(IMusicInfo.ColorMode.RBGY, R.drawable.frequency_rbgy, R.string.content_description_RBGY),
            new ColorMode(IMusicInfo.ColorMode.GRBC, R.drawable.frequency_grbc, R.string.content_description_GRBC),
            new ColorMode(IMusicInfo.ColorMode.BGRM, R.drawable.frequency_bgrm, R.string.content_description_BGRM)
    );

    ColorModesAdapter(MusicModeSettingsScreenActivity activity) {
        mActivity = activity;
    }

    @Override
    public ColorModeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ColorModeHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_mode, parent, false));
    }

    @Override
    public void onBindViewHolder(final ColorModeHolder holder, int position) {
        final ColorMode colorMode = mColorModesList.get(position);
        holder.colorModeImage.setImageDrawable(ContextCompat.getDrawable(mContext, colorMode.getImageID()));
        holder.colorModeImage.setContentDescription(holder.colorModeImage.getContext().getString(colorMode.getContentDescriptionID()));
        holder.colorModeSpannedText = MusicModeFragment.getColorModeSpannedFromResources(mContext, mColorModesList.get(position).getMode());
        holder.colorModeText.setText(holder.getColorModeSpannedText());
        holder.colorModeRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onColorModeClicked(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mColorModesList.size();
    }

    int getPositionOfColorMode(@IMusicInfo.ColorMode int colorMode) {
        for (ColorMode item : mColorModesList) {
            if (item.getMode() == colorMode) {
                return mColorModesList.indexOf(item);
            }
        }
        throw new IllegalArgumentException("Color Mode NOT Found!");
    }

    @IMusicInfo.ColorMode
    int getColorModeOfPosition(int position) {
        if (position < mColorModesList.size()) {
            return mColorModesList.get(position).getMode();
        }
        throw new IndexOutOfBoundsException("a Position IS Greater Than List of Color Modes!");
    }

    static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int quantity = parent.getAdapter().getItemCount();

            if (position < quantity / 2) {
                outRect.top = space;
            }
            if (position == quantity / 2 - 1 || position == quantity - 1) {
                outRect.right = space;
            }
            outRect.bottom = space;
            outRect.left = space;
        }
    }
}
