package com.pinit.pinitmobile.ui;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.Comment;
import com.pinit.pinitmobile.model.Photo;
import com.pinit.pinitmobile.service.CommentService;
import com.pinit.pinitmobile.ui.dialog.TaskProgressDialog;
import com.pinit.pinitmobile.util.TimeFormater;
import com.pinit.pinitmobile.util.UserData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class CommentsActivity extends AppCompatActivity implements ServiceConnection, CommentService.CommentsClient {

    private static final String TAG = CommentsActivity.class.getName();
    private Long eventId;
    private CommentListAdapter commentsListAdapter;
    private CommentService commentService;
    private static boolean isBounded = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        eventId = getIntent().getLongExtra("eventId", -1);

        ListView commentsList = (ListView) findViewById(R.id.comments_list);
        commentsListAdapter = new CommentListAdapter(App.getCommentsDao().get(), this);
        commentsList.setAdapter(commentsListAdapter);
        progressDialog = TaskProgressDialog.show(this, getString(R.string.downloading), getString(R.string.donloading_comments));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if (!isBounded) {
            doBindService();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!isBounded) {
            doBindService();
        }
        if (commentService != null && commentService.isConnected()) {
            commentService.addCommentsClient(this);
        }
        commentsListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
//        if (isBounded)
//            doUnbindService();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (commentService.isConnected()) {
            commentService.disconnect();
            commentService.removeCommentsClient(this);
            doUnbindService();
            App.getCommentsDao().deleteAll();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_comment:
                commentService.removeCommentsClient(this);
                Intent intent = new Intent(this, AddCommentActivity.class);
                startActivity(intent);
                break;
            default:
                Log.e(TAG, "Brak akcji dla elementu menu");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected");
        commentService = ((CommentService.LocalBinder) service).getService();
        commentService.addCommentsClient(this);
        if (!commentService.isConnected()) {
            String uri = Globals.CHAT_URL + String.valueOf(eventId) + "/" + UserData.getToken().getToken();
            try {
                commentService.connectToCommentsServer(new URI(uri));
            } catch (URISyntaxException e) {
                Log.e(TAG, e.getMessage());
                commentService.disconnect();
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected");
        commentService = null;
    }

    @Override
    public void onConnected() {
        Log.d(TAG, "OnConnected");
        progressDialog.dismiss();
    }

    @Override
    public void onMessageReceived(Comment c) {
        Log.d(TAG, "Message received " + c.toString());
        refreshListView(c);
    }

    private void refreshListView(final Comment c) {
        Log.d(TAG, "refreshListView");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                App.getCommentsDao().add(c);
                commentsListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDisconnected() {
        commentService.stopSelf();
    }

    private void doUnbindService() {
        if (isBounded) {
            Log.d(TAG, "unbindService");
            unbindService(this);
            isBounded = false;
        }
    }

    private void doBindService() {
        if (!isBounded) {
            Log.d(TAG, "bindService");
            Intent intent = new Intent(this, CommentService.class);
            isBounded = bindService(intent, this, Context.BIND_AUTO_CREATE);
        }
    }

    private final class CommentListAdapter extends BaseAdapter {

        private final Context ctx;
        private List<Comment> comments;

        public CommentListAdapter(List<Comment> comments, Context ctx) {
            this.comments = comments;
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return comments.size();
        }

        @Override
        public Comment getItem(int position) {
            return comments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return comments.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(ctx).inflate(R.layout.comment_list_item, parent, false);
            }
            Comment comment = getItem(position);
            TextView author = (TextView) v.findViewById(R.id.username_text_view);
            author.setText(comment.getAuthor().getUsername());
            TextView date = (TextView) v.findViewById(R.id.date_text_view);
            date.setText(TimeFormater.longToDateString(comment.getDate()));
            TextView commentTextView = (TextView) v.findViewById(R.id.comment_text_view);
            commentTextView.setText(comment.getComment());

            setupPhotos(v, comment);
            return v;
        }

        private void setupPhotos(View v, Comment comment) {
            setupAuthorPhoto(v, comment);
            setupCommentPhoto(v, comment);
        }

        private void setupAuthorPhoto(View v, Comment comment) {
            Photo authorPhoto = comment.getAuthor().getSmallPhoto();
            ImageView userPhotoView = (ImageView) v.findViewById(R.id.user_photo);
            if (authorPhoto != null) {
                Bitmap userPhoto = authorPhoto.getPhoto();
                if (userPhoto != null) {
                    userPhotoView.setImageBitmap(userPhoto);
                }
            }
        }

        private void setupCommentPhoto(View v, Comment comment) {
            ImageView commentPhotoView = (ImageView) v.findViewById(R.id.comment_photo);
            Photo commentPhotoBytes = comment.getPhoto();
            if (commentPhotoBytes != null) {
                Bitmap commentPhoto = commentPhotoBytes.getPhoto();
                if (commentPhoto != null) {
                    commentPhotoView.setImageBitmap(commentPhoto);
                }
            } else {
                commentPhotoView.setImageDrawable(null);
            }
        }
    }

}
