package com.pinit.pinitmobile.tasks;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinit.pinitmobile.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractAsyncTask<TSend, TReceive> extends AsyncTask<Void, Void, ResponseEntity<TReceive>> {

    protected static final String TAG = AbstractAsyncTask.class.getName();
    protected FragmentActivity activity;
    protected TSend objectToSend;
    protected String url = "";
    protected ProgressBar progressBar;
    protected AsyncTaskCallback requester;
    private HttpMethod method;
    private Class aClass;

    public AbstractAsyncTask(ProgressBar progressBar, FragmentActivity activity, TSend objectToSend, AsyncTaskCallback
            requester, HttpMethod method, Class<TReceive> aClass) {
        this.progressBar = progressBar;
        this.activity = activity;
        this.objectToSend = objectToSend;
        this.requester = requester;
        this.method = method;
        this.aClass = aClass;
    }

    @Override
    protected void onPreExecute() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected ResponseEntity<TReceive> doInBackground(Void... params) {
        Log.d(TAG, "started "+url);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<TSend> entity = new HttpEntity<>(objectToSend, headers);
        ResponseEntity<TReceive> response;
        try {
            response = restTemplate.exchange(url, method, entity, aClass);
        } catch (HttpClientErrorException e) {
            Log.d(TAG, e.getMessage());
            response = new ResponseEntity<>(e.getStatusCode());
        }
        return response;
    }

    @Override
    protected void onPostExecute(ResponseEntity<TReceive> responseEntity) {
        int code = -1;
        if (responseEntity != null) {
            code = responseEntity.getStatusCode().value();
            switch (responseEntity.getStatusCode()) {
                case OK: {
                    doOnSuccess(responseEntity);
                    break;
                }
                case FORBIDDEN: {
                    Toast.makeText(activity, R.string.token_expired, Toast.LENGTH_SHORT).show();
                    break;
                }
                default: {
                    handleStatusCodes(responseEntity.getStatusCode());
                }
            }
        } else {
            Toast.makeText(activity, R.string.connection_error, Toast.LENGTH_SHORT).show();
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        notifyRequester(code);
        Log.d(TAG, "finished " + url);
    }

    protected abstract void doOnSuccess(ResponseEntity responseEntity);

    protected abstract void notifyRequester(int code);

    protected void handleStatusCodes(HttpStatus httpStatus) {
        Toast.makeText(activity, activity.getString(R.string.error_service_unavalible), Toast.LENGTH_LONG).show();
    }
}
