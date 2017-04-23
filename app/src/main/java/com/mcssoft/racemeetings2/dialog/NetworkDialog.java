package com.mcssoft.racemeetings2.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.utility.Resources;

public class NetworkDialog extends DialogFragment
        implements DialogInterface.OnClickListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setIcon(R.drawable.ic_action_warning)
                .setTitle(Resources.getInstance().getString(R.string.title_dialog_network))
                .setMessage(getArguments().getString(Resources.getInstance()
                .getString(R.string.network_dialog_text)))
                .setPositiveButton(R.string.button_ok_text, this);
        return dialog.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which) {
            case Dialog.BUTTON_POSITIVE:
                break;
        }
    }
}
