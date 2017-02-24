package me.foxaice.smartlight.fragments.modes.music_mode;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.fragments.modes.ModeBaseView;
import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;
import me.foxaice.smartlight.fragments.modes.music_mode.view.IMusicModeView;

public class MusicModeFragment extends ModeBaseView implements IMusicModeView {
    public static final String TAG = "MUSIC_MODE_FRAGMENT";


    @SuppressWarnings("deprecation")
    public static Spanned getColorModeSpannedFromResources(Context context, @IMusicInfo.ColorMode int name) {
        int stringId;
        switch (name) {
            case IMusicInfo.ColorMode.RGBM:
                stringId = R.string.RGBM;
                break;
            case IMusicInfo.ColorMode.GBRY:
                stringId = R.string.GBRY;
                break;
            case IMusicInfo.ColorMode.BRGC:
                stringId = R.string.BRGC;
                break;
            case IMusicInfo.ColorMode.RBGY:
                stringId = R.string.RBGY;
                break;
            case IMusicInfo.ColorMode.GRBC:
                stringId = R.string.GRBC;
                break;
            case IMusicInfo.ColorMode.BGRM:
                stringId = R.string.BGRM;
                break;
            default:
                throw new IllegalArgumentException("Wrong Color Mode ModeName!");
        }
        String html = context.getString(stringId);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }


    @Override
    public void onChangedControllerSettings() {

    }

    @Override
    public void drawWaveFormView(double[] data, String color, double max, @IMusicInfo.ViewType int viewType) {

    }

    @Override
    public void setCurrentVolumeText(double value) {

    }

    @Override
    public void setFrequencyText(int value) {

    }

    @Override
    public void setMaxVolumeText(int value) {

    }

    @Override
    public void setMinVolumeText(int value) {

    }

    @Override
    public void setColorModeText(@IMusicInfo.ColorMode int colorMode) {

    }

    @Override
    public void setWaveFormVisible(boolean visible) {

    }

    @Override
    public String[] getBytesColorsFromResources() {
        return new String[0];
    }
}
