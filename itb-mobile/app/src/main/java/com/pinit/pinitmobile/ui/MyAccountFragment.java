package com.pinit.pinitmobile.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.dao.UsersDao;
import com.pinit.pinitmobile.model.User;
import com.pinit.pinitmobile.util.UserData;

public class MyAccountFragment extends Fragment {

    public static final String TAG = MyAccountFragment.class.getName();
    private static final String ARG_USER_ID = "userId";
    public static final int INTERNAL_USER = -1;
    private long userId = INTERNAL_USER;

    public MyAccountFragment() {
    }

    public static MyAccountFragment newInstance(long userId) {
        MyAccountFragment fragment = new MyAccountFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong(ARG_USER_ID);
        } else setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_account, container, false);
        User user = getUserToShow();

        TextView username = (TextView) v.findViewById(R.id.username);
        username.setText(user.getUsername());
        TextView email = (TextView) v.findViewById(R.id.email);
        email.setText(user.getEmail());
        TextView firstNameLastName = (TextView) v.findViewById(R.id.firstname_lastname);
        firstNameLastName.setText(getUserFirstLastName(user));
        TextView phoneNo = (TextView) v.findViewById(R.id.phoneNumber);
        phoneNo.setText(user.getPhoneNumber());
        TextView comments = (TextView) v.findViewById(R.id.comments_amount);
        comments.setText(String.valueOf(user.getCommentsAmount()));
        TextView positives = (TextView) v.findViewById(R.id.positives_amount);
        positives.setText(String.valueOf(user.getPositivesAmount()));
        TextView negatives = (TextView) v.findViewById(R.id.negatives_amount);
        negatives.setText(String.valueOf(user.getNegativesAmount()));
        ImageView photo = (ImageView) v.findViewById(R.id.user_photo);

        setupUserPhoto(user, photo);

        return v;
    }

    private User getUserToShow() {
        User user;
        if (userId == INTERNAL_USER) {
            user = UserData.getUser();
            getActivity().setTitle(getString(R.string.my_account));
        } else {
            user = UsersDao.getInstance().getUser(userId);
            getActivity().setTitle(user.getEmail());
        }
        return user;
    }

    private String getUserFirstLastName(User user) {
        String first = user.getFirstName() != null ? user.getFirstName() : "";
        String last = user.getLastName() != null ? user.getLastName() : "";
        return first + " " + last;
    }

    private void setupUserPhoto(User user, ImageView photo) {
        if (userId == INTERNAL_USER) {
            if (user.getBigPhoto() != null) {
                Bitmap userPhoto = user.getBigPhoto().getPhoto();
                if (userPhoto != null) {
                    photo.setImageBitmap(userPhoto);
                }
            }
        } else {
            if (user.getSmallPhoto() != null) {
                photo.setImageBitmap(user.getSmallPhoto().getPhoto());
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_edit_account, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            Fragment editAccount = new EditAccountFragment();
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.beginTransaction().addToBackStack(TAG).replace(R.id.content_frame, editAccount).commit();
        } else {
            Log.e(TAG, "Brak akcji dla wybranego elementu!");
        }

        return super.onOptionsItemSelected(item);
    }
}
