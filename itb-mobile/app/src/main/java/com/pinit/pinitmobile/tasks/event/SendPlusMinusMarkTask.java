package com.pinit.pinitmobile.tasks.event;


import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.model.Event;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class SendPlusMinusMarkTask extends AbstractAsyncTask<Void, Integer> {

    public static final int TASK_ID_PLUS = 18;
    public static final int TASK_ID_MINUS = 19;
    private Event event;
    private int mark;

    public SendPlusMinusMarkTask(ProgressBar progressBar, FragmentActivity activity, AsyncTaskCallback requester, Event event,
                                 int mark) {
        super(progressBar, activity, null, requester, HttpMethod.PUT, Integer.class);
        this.event = event;
        this.mark = mark;
        url = Globals.SERVER_URL + Globals.PUT_PLUS_MINUS_URL_1 + String.valueOf(event.getEventId()) + "/" + Globals
                .PUT_PLUS_MINUS_URL_2 + String.valueOf(mark) + "/" + UserData.getToken().getToken();
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        event.setMark(mark);
        event.markEvent(mark);
        App.getEventsDao().update(event);
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) if (mark == 1) requester.afterExecute(TASK_ID_PLUS, code);
        else requester.afterExecute(TASK_ID_MINUS, code);
    }
}
