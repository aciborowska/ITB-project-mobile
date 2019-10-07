package com.pinit.pinitmobile.dao;


import android.util.Log;

import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.model.Comment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommentsDao {

    private static final String TAG = CommentsDao.class.getName();
    private static CommentsDao instance;
    private List<Comment> comments;

    public static CommentsDao getInstance() {
        if (instance == null)
            instance = new CommentsDao();
        return instance;
    }

    private CommentsDao() {
        this.comments = new ArrayList<>();
    }

    public void add(Comment c) {
        comments.add(c);
    }

    public List<Comment> get() {
        return comments;
    }

    public void deleteAll() {
        Log.d(TAG, "delete comments");
        comments.clear();
        Comment.lastId = 0;
        cleanPhotosDirectory();
    }

    public static void cleanPhotosDirectory() {
        cleanDirectory(Globals.COMMENT_PHOTO_DIR);
        cleanDirectory(Globals.COMMENT_USER_DIR);
    }

    private static void cleanDirectory(String directory) {
        File comments = new File(directory);
        if (comments.isDirectory()) {
            for (File f : comments.listFiles()) {
                f.delete();
            }
        }
    }
}
