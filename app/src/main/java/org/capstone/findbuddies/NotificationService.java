package org.capstone.findbuddies;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

public class NotificationService extends IntentService {
    public static String REPLY_ACTION = "REPLY_ACTION";
    public static String KEY_REPLY = "key_reply_message";

    private int NotificationId;
    private int MessageId;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            showNotification();
        }
    }

    private void showNotification() {
        NotificationId = 1;
        MessageId = 123; // dummy message id, ideally would come with the push notification

        String replyLabel = "메모를 입력하세요.";
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                .setLabel(replyLabel)
                .build();

        // 2. Build action
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                android.R.drawable.sym_action_chat, replyLabel, getReplyPendingIntent())
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();

        // 3. Build notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,"ChannelId")
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle("메모장")
                .setContentText("메모를 바로 저장해 보세요.")
                .setShowWhen(false)
                .addAction(replyAction)
                .setDefaults(Notification.FLAG_NO_CLEAR);

        NotificationManagerCompat NotificationManager = NotificationManagerCompat.from(this);
        NotificationManager.notify(NotificationId, mBuilder.build());
    }

    private PendingIntent getReplyPendingIntent() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // start a
            // (i)  broadcast receiver which runs on the UI thread or
            // (ii) service for a background task to b executed , but for the purpose of this codelab, will be doing a broadcast receiver
            intent = NotificationReceiver.getReplyMessageIntent(this, NotificationId, MessageId);
            return PendingIntent.getBroadcast(getApplicationContext(), 100, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            // start your activity

            return null;
        }
    }
}
