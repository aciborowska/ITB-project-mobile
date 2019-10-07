package com.pinit.pinitmobile.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.pinit.pinitmobile.model.Comment;

import org.json.JSONException;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CommentService extends Service {
    private static final String TAG = CommentService.class.getName();
    private WebSocket webSocketClient;
    private List<CommentsClient> commentsClientList = new ArrayList<>(2);
    private LocalBinder localBinder = new LocalBinder();
    private Thread wsThread;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return localBinder;
    }

    public void connectToCommentsServer(final URI uri) {
        Log.d(TAG, "connectToCommentServer " + uri.toString());
        connectToChat(uri.toString());
    }

    public void disconnect() {
        Log.d(TAG, "disconnect");
        if (webSocketClient != null) {
            webSocketClient.disconnect();
            wsThread.interrupt();
        }
        commentsClientList.clear();
    }

    public void addCommentsClient(CommentsClient commentsClient) {
        Log.d(TAG, "addCommentClient");
        commentsClientList.add(commentsClient);
    }

    public void removeCommentsClient(CommentsClient commentsClient) {
        Log.d(TAG, "removeCommentClient");
        commentsClientList.remove(commentsClient);
    }

    public void sendMessage(Comment comment) {
        if (webSocketClient != null) {
            try {
                String message = Comment.commentToJSONObject(comment);
                Log.d(TAG, "sending " + message);
                webSocketClient.sendText(message);
            } catch (JSONException e) {
                Log.e(TAG, "Mapping Comment object to JSON error: " + e.getMessage());
            }
        }
    }

    public void connectToChat(final String uri) {
        wsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    webSocketClient = new WebSocketFactory()
                            .setConnectionTimeout(5000)
                            .createSocket(uri)
                            .addListener(new CommentWebSocketAdapter())
                            .connect();
                } catch (WebSocketException | IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
        wsThread.start();
    }

    private class CommentWebSocketAdapter extends WebSocketAdapter {

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            if (commentsClientList != null) {
                for (CommentsClient client : commentsClientList) {
                    client.onConnected();
                }
            }
        }

        @Override
        public void onTextMessage(WebSocket websocket, String message) {
            Log.d(TAG, message);
            Comment c = null;
            if (commentsClientList != null) {
                try {
                    c = Comment.jsonObjectToComment(message);
                } catch (JSONException e) {
                    Log.e(TAG, "Mapping JSON to Comment object problem: " + e.getMessage());
                } finally {
                    if (c != null) {
                        for (CommentsClient client : commentsClientList) {
                            client.onMessageReceived(c);
                        }
                    }
                }
            }
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
                                   boolean closedByServer) throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            Log.d(TAG, "closed by server " + closedByServer + ",reason " + serverCloseFrame.getCloseReason());
            for (CommentsClient client : commentsClientList) {
                client.onDisconnected();
            }
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
            super.onError(websocket, cause);
            Log.e(TAG, "Error: " + cause.getMessage() + ", " + cause.getError() + ", ");
            cause.printStackTrace();
        }
    }

    public boolean isConnected() {
        return webSocketClient != null;
    }

    public interface CommentsClient {
        void onConnected();

        void onMessageReceived(Comment comment);

        void onDisconnected();
    }

    public class LocalBinder extends Binder {
        public CommentService getService() {
            return CommentService.this;
        }
    }
}
