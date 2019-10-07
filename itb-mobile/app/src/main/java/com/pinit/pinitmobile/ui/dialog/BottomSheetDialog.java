package com.pinit.pinitmobile.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.Event;
import com.pinit.pinitmobile.model.EventType;
import com.pinit.pinitmobile.model.Group;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.tasks.event.GetPlusMinusMarkTask;
import com.pinit.pinitmobile.tasks.event.SendPlusMinusMarkTask;
import com.pinit.pinitmobile.ui.CommentsActivity;
import com.pinit.pinitmobile.util.TimeFormater;

public class BottomSheetDialog extends Dialog implements Button.OnClickListener, AsyncTaskCallback {

    private static final String TAG = BottomSheetDialog.class.getName();
    private Event event;
    private TextView positives;
    private TextView negatives;
    private FragmentActivity activity;
    private ProgressBar progressBar;

    public BottomSheetDialog(FragmentActivity activity, int themeResId, Event event) {
        super(activity, themeResId);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        setContentView(view);
        setCancelable(true);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.activity = activity;
        this.event = event;
        initTextView();
        initButtons();
    }

    private void initTextView() {
        TextView eventName = (TextView) findViewById(R.id.event_name);
        eventName.setText(event.getName());
        TextView eventAddress = (TextView) findViewById(R.id.event_address);
        eventAddress.setText(App.getLocationDao().get(event.getLocationId()).toString());
        TextView eventTime = (TextView) findViewById(R.id.event_time);
        eventTime.setText(TimeFormater.longToDateString(event.getStartDate()));
        TextView description = (TextView) findViewById(R.id.event_description);
        description.setText(event.getDescription());
        TextView eventType = (TextView) findViewById(R.id.event_type);
        EventType type = App.getEventTypesDao().get(event.getEventTypeId());
        eventType.setText(type.getEventType());
        if (event.getGroupId() > 0) {
            Group g = App.getGroupsDao().get(event.getGroupId());
            TextView eventGroup = (TextView) findViewById(R.id.event_group);
            if (g != null) {
                eventGroup.setText(g.getName());
            }
        }
        positives = (TextView) findViewById(R.id.positive_amount);
        positives.setText(String.valueOf(event.getPositivesAmount()));
        negatives = (TextView) findViewById(R.id.negative_amount);
        negatives.setText(String.valueOf(event.getNegativesAmount()));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    private void initButtons() {
        ImageButton thumbUp = (ImageButton) findViewById(R.id.thumb_up);
        ImageButton thumbDown = (ImageButton) findViewById(R.id.thumb_down);
        ImageButton comments = (ImageButton) findViewById(R.id.comments_button);
        thumbUp.setOnClickListener(this);
        thumbDown.setOnClickListener(this);
        comments.setOnClickListener(this);
        if (event.getMark() == -2) {
            GetPlusMinusMarkTask getMarkTask = new GetPlusMinusMarkTask(progressBar, activity, this, event);
            getMarkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.thumb_up:
                if (event.getMark() == 1 || event.getMark() == -1) {
                    showAlreadyMarkedInfoDialog();
                } else {
                    SendPlusMinusMarkTask sendPlusMinusMarkTaskUP = new SendPlusMinusMarkTask(progressBar, activity, this,
                            event, 1);
                    sendPlusMinusMarkTaskUP.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }
                break;
            case R.id.thumb_down:
                if (event.getMark() == 1 || event.getMark() == -1) {
                    showAlreadyMarkedInfoDialog();
                } else {
                    SendPlusMinusMarkTask sendPlusMinusMarkTaskDOWN = new SendPlusMinusMarkTask(progressBar, activity, this,
                            event, -1);
                    sendPlusMinusMarkTaskDOWN.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }
                break;
            case R.id.comments_button:
                Intent intent = new Intent(activity, CommentsActivity.class);
                intent.putExtra("eventId", event.getEventId());
                activity.startActivity(intent);
                break;
            default:
                Log.e(TAG, "onClick not implemented for this item");
        }
    }

    private void showAlreadyMarkedInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.event_already_marked).setTitle(R.string.attention);
        AlertDialog dialog = builder.create();
        dialog.show();
        int titleDividerId = App.getCtx().getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null) titleDivider.setBackgroundColor(App.getCtx().getResources().getColor(R.color.dark_green));
    }

    private void updatePositivesNegatives(int mark) {
        if (mark > 0) {
            positives.setText(String.valueOf(event.getPositivesAmount()));
        } else {
            negatives.setText(String.valueOf(event.getNegativesAmount()));
        }
    }

    @Override
    public void afterExecute(int taskId, int httpStatusCode) {
        if (taskId == SendPlusMinusMarkTask.TASK_ID_PLUS && httpStatusCode == Globals.EXECUTE_SUCCESS) {
            updatePositivesNegatives(1);
        } else if (taskId == SendPlusMinusMarkTask.TASK_ID_MINUS && httpStatusCode == Globals.EXECUTE_SUCCESS) {
            updatePositivesNegatives(-1);
        }
    }
}
