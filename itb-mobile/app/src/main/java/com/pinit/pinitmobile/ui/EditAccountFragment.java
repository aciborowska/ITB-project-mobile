package com.pinit.pinitmobile.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.User;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.tasks.user.ChangePasswordTask;
import com.pinit.pinitmobile.tasks.user.UpdateUserPhoto;
import com.pinit.pinitmobile.tasks.user.UpdateUserTask;
import com.pinit.pinitmobile.util.Credentials;
import com.pinit.pinitmobile.util.MD5Encryption;
import com.pinit.pinitmobile.util.PhotoUtil;
import com.pinit.pinitmobile.util.UserData;
import com.pinit.pinitmobile.util.Validation;

public class EditAccountFragment extends Fragment implements AsyncTaskCallback {

    private static final int REQUEST_CHOOSE_IMAGE = 1;
    private static final int REQUEST_CROP_IMAGE = 2;
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNo;
    private EditText nick;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText repeatedPassword;
    private Button changePassword;
    private ImageView userPhoto;
    private User user;
    private ProgressBar progressBar;
    private boolean isPhotoUpdated = false;

    public EditAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_account, container, false);
        user = UserData.getUser();
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        nick = (EditText) v.findViewById(R.id.nick);
        nick.setText(user.getUsername());
        TextView email = (TextView) v.findViewById(R.id.email);
        email.setText(user.getEmail());
        firstName = (EditText) v.findViewById(R.id.first_name);
        firstName.setText(user.getFirstName());
        lastName = (EditText) v.findViewById(R.id.last_name);
        lastName.setText(user.getLastName());
        phoneNo = (EditText) v.findViewById(R.id.phone_no);
        phoneNo.setText(user.getPhoneNumber());

        setupChangePassword(v);
        setupPhotoChange(v);
        setupSaveButton(v);
        setupChangePhotoButton(v);
        return v;
    }

    private void setupChangePhotoButton(View v) {
        Button changePhoto = (Button) v.findViewById(R.id.change_photo);
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST_CHOOSE_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            performCrop(data.getData());
        } else if (requestCode == REQUEST_CROP_IMAGE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap photo = extras.getParcelable("data");
            userPhoto.setImageBitmap(photo);
            isPhotoUpdated = true;
        }
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQUEST_CROP_IMAGE);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void setupChangePassword(View v) {
        oldPassword = (EditText) v.findViewById(R.id.old_password);
        newPassword = (EditText) v.findViewById(R.id.new_password);
        repeatedPassword = (EditText) v.findViewById(R.id.repeated_password);
        changePassword = (Button) v.findViewById(R.id.change_password);

        if (WelcomeActivity.isFbLogin()) {
            setChangePasswordInvisible(v);
        } else {
            changePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newPasswordValidation();
                }
            });
        }
    }

    private void setChangePasswordInvisible(View v) {
        changePassword.setVisibility(View.GONE);
        oldPassword.setVisibility(View.GONE);
        repeatedPassword.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        v.findViewById(R.id.change_passord_tv).setVisibility(View.GONE);
        v.findViewById(R.id.divider).setVisibility(View.GONE);
    }

    private void newPasswordValidation() {
        String oldPasswd = oldPassword.getText().toString();
        String newPasswd = newPassword.getText().toString();
        String changePasswd = repeatedPassword.getText().toString();

        if (oldPasswd.isEmpty()) {
            oldPassword.setError(getString(R.string.field_cannot_be_empty));
            return;
        }

        if (!MD5Encryption.encrypt(oldPasswd).equals(Credentials.getCredentials().getPassword())) {
            oldPassword.setError(getString(R.string.wrong_password_provided));
            return;
        }

        if (newPasswd.isEmpty()) {
            newPassword.setError(getString(R.string.field_cannot_be_empty));
            return;
        }

        if (changePasswd.isEmpty()) {
            changePassword.setError(getString(R.string.field_cannot_be_empty));
            return;
        }

        if (!newPasswd.equals(changePasswd)) {
            newPassword.setError(getString(R.string.diffrent_passwords));
            changePassword.setError(getString(R.string.diffrent_passwords));
            return;
        }

        if (Validation.isPasswordFormatValid(newPasswd)) {
            ChangePasswordTask changePasswordTask = new ChangePasswordTask(progressBar, getActivity(), MD5Encryption.encrypt
                    (newPasswd), this);
            changePasswordTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        } else {
            newPassword.setError(getString(R.string.wrong_password_format));
        }
    }

    private void setupPhotoChange(View v) {
        userPhoto = (ImageView) v.findViewById(R.id.user_photo);
        updateUserPhotoView();
    }

    private void updateUserPhotoView() {
        if (user.getBigPhoto() != null) {
            Bitmap photo = user.getBigPhoto().getPhoto();
            if (photo != null) {
                userPhoto.setImageBitmap(photo);
            }
        }
    }

    private void setupSaveButton(View v) {
        Button save = (Button) v.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User updatedUser = user;
                updatedUser.setLastLoginDate(null);
                if (nick.getText().toString().isEmpty()) {
                    user.setUsername(null);
                } else {
                    user.setUsername(nick.getText().toString());
                }
                if (lastName.getText().toString().isEmpty()) {
                    user.setLastName(null);
                } else {
                    user.setLastName(lastName.getText().toString());
                }
                if (firstName.getText().toString().isEmpty()) {
                    user.setFirstName(null);
                } else {
                    user.setFirstName(firstName.getText().toString());
                }
                if (phoneNo.getText().toString().isEmpty()) {
                    user.setPhoneNumber(null);
                } else {
                    user.setPhoneNumber(phoneNo.getText().toString());
                }
                if (user.getSmallPhoto() != null) {
                    Bitmap smallPhoto = user.getSmallPhoto().getPhoto();
                    if (smallPhoto != null) {
                        updatedUser.setPhotoSmall(PhotoUtil.bitmapToByteArray(smallPhoto));
                    }
                }

                UpdateUserTask updateUserTask = new UpdateUserTask(progressBar, getActivity(), updatedUser, EditAccountFragment
                        .this);
                updateUserTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                if (isPhotoUpdated) {
                    runUpdateUserPhotoTask();
                }
            }
        });
    }

    private void runUpdateUserPhotoTask() {
        Bitmap photo = ((BitmapDrawable) userPhoto.getDrawable()).getBitmap();
        if (photo != null) {
            UpdateUserPhoto updateUserPhoto = new UpdateUserPhoto(progressBar, getActivity(), photo, EditAccountFragment.this);
            updateUserPhoto.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        } else {
            Toast.makeText(getActivity(), R.string.photo_change_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void afterExecute(int taskId, int httpStatusCode) {
        if (taskId == UpdateUserTask.TASK_ID && httpStatusCode == Globals.EXECUTE_SUCCESS && !isPhotoUpdated) {
            getFragmentManager().popBackStack();
        } else if (taskId == UpdateUserPhoto.TASK_ID && httpStatusCode == Globals.EXECUTE_SUCCESS) {
            isPhotoUpdated = false;
            getFragmentManager().popBackStack();
        } else if (taskId == ChangePasswordTask.TASK_ID && httpStatusCode == Globals.EXECUTE_SUCCESS) {
            oldPassword.setText("");
            newPassword.setText("");
            repeatedPassword.setText("");
            getFragmentManager().popBackStack();
        }
    }


}
