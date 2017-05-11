package com.mcssoft.racemeetings2.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.database.DatabaseOperations;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.interfaces.IDeleteDialog;
import com.mcssoft.racemeetings2.utility.Resources;

public class DeleteDialog extends DialogFragment
        implements DialogInterface.OnClickListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setIcon(R.drawable.ic_action_warning)
              .setTitle(Resources.getInstance().getString(R.string.title_dialog_delete))
              .setMessage(Resources.getInstance().getString(R.string.delete_dialog_text))
              .setView(R.layout.dialog_delete)
              .setPositiveButton(R.string.button_ok_text, this)
              .setNegativeButton(R.string.button_cancel_text, this);
        return dialog.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which) {
            case Dialog.BUTTON_POSITIVE:
                doDelete();
                break;
        }
    }

    private void doDelete() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_delete, null);
        String tag = ((RadioButton) view.findViewById(((RadioGroup)
                view.findViewById(R.id.id_rg_delete_dialog)).getCheckedRadioButtonId()))
                .getTag().toString();

        DatabaseOperations dbOper = new DatabaseOperations(getActivity());

        int retVal = -1;
        switch (tag) {
            case "rb_delete_all":
                dbOper.deleteAllFromTable(SchemaConstants.MEETINGS_TABLE);
                dbOper.deleteAllFromTable(SchemaConstants.RACES_TABLE);
                retVal = Resources.getInstance().getInteger(R.integer.rb_delete_all);
                ((IDeleteDialog) getActivity()).iDeleteDialog(retVal);
                break;
            case "rb_delete_prev":
                // TBA
                break;
        }
    }
}
