package com.nashlincoln.blink.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.content.Syncro;
import com.nashlincoln.blink.model.Device;

/**
 * Created by nash on 11/10/14.
 */
public class EditNameDialogFragment extends DialogFragment {

    private String mName;
    private long mId;
    private Device mDevice;
    private EditText nameView;

    public static DialogFragment newInstance(Long id) {
        EditNameDialogFragment fragment = new EditNameDialogFragment();
        Bundle args = new Bundle();
        args.putLong(BlinkApp.EXTRA_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(BlinkApp.EXTRA_ID)) {
            mId = getArguments().getLong(BlinkApp.EXTRA_ID);
            mDevice = BlinkApp.getDaoSession().getDeviceDao().load(mId);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        nameView = new EditText(getActivity());
        if (mDevice != null) {
            nameView.setText(mDevice.getName());
        }
        builder.setTitle("Name")
                .setView(nameView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameView.getText().toString();
                        if (mDevice != null && !TextUtils.isEmpty(name)) {
                            mDevice.setName(name, true);
                            Syncro.getInstance().syncDevices();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }
}
