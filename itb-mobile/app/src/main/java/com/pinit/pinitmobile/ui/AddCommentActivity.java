package com.pinit.pinitmobile.ui;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.Comment;
import com.pinit.pinitmobile.service.CommentService;
import com.pinit.pinitmobile.util.UserData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AddCommentActivity extends AppCompatActivity implements ServiceConnection, CommentService.CommentsClient {

    private static final String TAG = AddCommentActivity.class.getName();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CHOOSE_IMAGE = 2;
    private EditText comment;
    private ImageView photoImageView;
    private Uri imageUri = null;
    private boolean isPhoto = false;
    private ProgressBar progressBar;
    private CommentService commentService;
    private static boolean isBounded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        photoImageView = (ImageView) findViewById(R.id.event_photo);
        comment = (EditText) findViewById(R.id.comment_edit_text);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        preparePhotoFile();
        setupButtons();
    }

    private void setupButtons() {
        setupTakePhotoButton();
        setupChoosePhotoButton();
        setupAddCommentButton();
    }

    private void setupTakePhotoButton() {
        Button takePhoto = (Button) findViewById(R.id.take_photo_button);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    if (imageUri != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });
    }

    private void setupChoosePhotoButton() {
        Button choosePhoto = (Button) findViewById(R.id.choose_photo_button);
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST_CHOOSE_IMAGE);
            }
        });
    }

    private void setupAddCommentButton() {

        Button addComment = (Button) findViewById(R.id.add_comment_button);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String commentText = comment.getText().toString();
                if (commentText.isEmpty()) {
                    comment.setError(getString(R.string.field_cannot_be_empty));
                    return;
                }

                Comment c = new Comment();
                c.setComment(commentText);
                c.setAuthor(UserData.getUser());
                if (isPhoto) {
                    Bitmap photo = ((BitmapDrawable) photoImageView.getDrawable()).getBitmap();
                    c.setPhoto(photo);
                    isPhoto = false;
                }
                commentService.sendMessage(c);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            grabImage();
        } else if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            ContentResolver contentResolver = getContentResolver();
            try (InputStream imageStream = contentResolver.openInputStream(selectedImage)) {
                Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                photoImageView.setImageBitmap(yourSelectedImage);
                isPhoto = true;
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void grabImage() {
        getContentResolver().notifyChange(imageUri, null);
        ContentResolver cr = getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
            photoImageView.setImageBitmap(bitmap);
            isPhoto = true;
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.cannot_load_picture), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to load: " + e.getMessage());
        }
    }

    private void preparePhotoFile() {
        File photo;
        try {
            photo = createTemporaryFile("picture", ".jpg");
            photo.delete();
            imageUri = Uri.fromFile(photo);
        } catch (Exception e) {
            Log.d(TAG, "Can't create file to take picture!");
            Toast.makeText(this, getString(R.string.cannot_access_SD_card), Toast.LENGTH_SHORT).show();
        }
    }

    private File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir = Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected");
        commentService = ((CommentService.LocalBinder) service).getService();
        if (commentService.isConnected()) {
            isBounded = true;
            commentService.addCommentsClient(this);
            Log.d(TAG, "CommentService already connected");
        } else {
            Log.e(TAG, "CommentService not connected");
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected");
        doUnbindService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        Intent intent = new Intent(this, CommentService.class);
        isBounded = bindService(intent, this, Context.BIND_AUTO_CREATE);
        Log.d(TAG, String.valueOf(isBounded));
    }

    @Override
    protected void onStop() {
        super.onStop();
        doUnbindService();
    }

    private void doUnbindService() {
        if (isBounded) {
            unbindService(this);
            isBounded = false;
        }
    }

    @Override
    public void onConnected() {
        Log.d(TAG, "onConnected");
    }

    @Override
    public void onMessageReceived(Comment comment) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddCommentActivity.this, getString(R.string.comment_send), Toast.LENGTH_SHORT).show();
                commentService.removeCommentsClient(AddCommentActivity.this);
                doUnbindService();
                finish();
            }
        });

    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "onDisconnected");
    }
}
