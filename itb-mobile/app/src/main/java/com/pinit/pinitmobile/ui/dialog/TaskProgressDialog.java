package com.pinit.pinitmobile.ui.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

import com.pinit.pinitmobile.R;

public class TaskProgressDialog {

    private TaskProgressDialog() {
    }

    public static ProgressDialog show(Context context, String title, String message) {
        ProgressDialog progressDialog = ProgressDialog.show(context, title, message, true);

        int titleDividerId = context.getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = progressDialog.findViewById(titleDividerId);
        if (titleDivider != null)
            titleDivider.setBackgroundColor(context.getResources().getColor(R.color.dark_green));
        return progressDialog;
    }

}
