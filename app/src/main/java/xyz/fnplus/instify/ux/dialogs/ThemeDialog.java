package xyz.fnplus.instify.ux.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import xyz.fnplus.instify.R;
import xyz.fnplus.instify.app.AppController;
import xyz.fnplus.instify.app.Themes;
import xyz.fnplus.instify.ux.MainActivity;

/**
 * Created by Abhish3k on 13/01/2017.
 */

public class ThemeDialog extends DialogFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        final View layout = inflater.inflate(R.layout.view_dialog_theme, container, false);
        layout.findViewById(R.id.blue_theme).setOnClickListener(this);
        layout.findViewById(R.id.indigo_theme).setOnClickListener(this);
        layout.findViewById(R.id.green_theme).setOnClickListener(this);
        layout.findViewById(R.id.red_theme).setOnClickListener(this);
        layout.findViewById(R.id.blue_grey_theme).setOnClickListener(this);
        layout.findViewById(R.id.black_theme).setOnClickListener(this);
        layout.findViewById(R.id.purple_theme).setOnClickListener(this);
        layout.findViewById(R.id.orange_theme).setOnClickListener(this);
        layout.findViewById(R.id.pink_theme).setOnClickListener(this);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onClick(View v) {

        Themes theme;
        switch (v.getId()) {
            case R.id.blue_theme:
                theme = Themes.Blue;
                break;
            case R.id.indigo_theme:
                theme = Themes.Indigo;
                break;
            case R.id.green_theme:
                theme = Themes.Green;
                break;
            case R.id.red_theme:
                theme = Themes.Red;
                break;
            case R.id.blue_grey_theme:
                theme = Themes.BlueGrey;
                break;
            case R.id.black_theme:
                theme = Themes.Black;
                break;

            case R.id.orange_theme:
                theme = Themes.Orange;
                break;

            case R.id.purple_theme:
                theme = Themes.Purple;
                break;
            case R.id.pink_theme:
                theme = Themes.Pink;
                break;
            default:
                theme = Themes.Blue;
                break;
        }

        AppController.getInstance().getPrefManager().setCurrentTheme(theme);
        startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finish();
    }
}
