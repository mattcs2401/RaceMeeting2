package com.mcssoft.racemeetings2.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.utility.Preferences;
import com.mcssoft.racemeetings2.utility.Resources;

public class RaceCodesDialog extends DialogPreference {

    public RaceCodesDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkRaceCodePreference();
        setDialogLayoutResource(R.layout.dialog_race_codes);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            setRaceCodePreference();
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        radioGroup = (RadioGroup) view.findViewById(R.id.id_rg_race_codes);
        int radioButtonId = Preferences.getInstance().getDefaultSharedPreferences()
                .getInt(Resources.getInstance()
                        .getString(R.string.race_code_pref_id_key), R.integer.init_default);
        ((RadioButton) radioGroup.findViewById(radioButtonId)).setChecked(true);
    }

    /**
       Basically just a check that this custom preference exists. App may have been un-installed
       and then re-installed.
     */
    public void checkRaceCodePreference() {
        // Has to be PreferenceManager if it's the 1st time the app is run.
        if(!(PreferenceManager.getDefaultSharedPreferences(getContext())
                .contains(Resources.getInstance().getString(R.string.race_code_pref_val_key)))) {

            // If the preference doesn't exist, set the default for this preference.
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.dialog_race_codes, null);
            RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.id_rg_race_codes);
            int count = radioGroup.getChildCount();

            for(int ndx = 0; ndx < count; ndx++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(ndx);
                String text = radioButton.getText().toString();
                if(text.equals(Resources.getInstance().getString(R.string.race_code_default_val))) {
                    int radioButtonId = radioButton.getId();
                    SharedPreferences.Editor spe =
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    spe.putInt(Resources.getInstance()
                            .getString(R.string.race_code_pref_id_key), radioButtonId).apply();
                    spe.putString(Resources.getInstance()
                            .getString(R.string.race_code_pref_val_key), text).apply();
                    // no need to keep going.
                    break;
                }
            }
        }
    }

    /*
      Set the race code preference (basically a manual persist).
    */
    private void setRaceCodePreference() {
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
        String newRbText = radioButton.getText().toString();

        String oldRbText = Preferences.getInstance().getDefaultSharedPreferences()
                .getString(Resources.getInstance()
                        .getString(R.string.race_code_pref_val_key), null);

        if(oldRbText != newRbText) {
            // Only update if preference actually changed (i.e. a different one selected).
            SharedPreferences.Editor spe
                    = Preferences.getInstance().getDefaultSharedPreferences().edit();
            spe.putInt(Resources.getInstance()
                    .getString(R.string.race_code_pref_id_key), radioButtonId).apply();
            spe.putString(Resources.getInstance()
                    .getString(R.string.race_code_pref_val_key), newRbText).apply();
            notifyChanged();
        }
    }

    private RadioGroup radioGroup;
}