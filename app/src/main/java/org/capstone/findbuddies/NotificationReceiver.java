package org.capstone.findbuddies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.capstone.findbuddies.NotificationService.KEY_REPLY;

public class NotificationReceiver extends BroadcastReceiver {
    private static String KEY_NOTIFICATION_ID = "key_noticiation_id";
    private static String KEY_MESSAGE_ID = "key_message_id";
    public static String REPLY_ACTION = "REPLY_ACTION";

    public static Intent getReplyMessageIntent(Context context, int notificationId, int messageId) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(REPLY_ACTION);
        intent.putExtra(KEY_NOTIFICATION_ID, notificationId);
        intent.putExtra(KEY_MESSAGE_ID, messageId);
        return intent;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (REPLY_ACTION.equals(intent.getAction())) {
            // do whatever you want with the message. Send to the server or add to the db.
            // for this tutorial, we'll just show it in a toast;
            CharSequence message = getReplyMessage(intent);
            int messageId = intent.getIntExtra(KEY_MESSAGE_ID, 0);

            Log.d("Test","message id : "+messageId+",   message: "+message);

            // update notification
//            int notifyId = intent.getIntExtra(KEY_NOTIFICATION_ID, 1);
//            updateNotification(context, notifyId);
            UploadMemo(message);
            context.startService(new Intent(context,NotificationService.class));
        }
    }

    private void UploadMemo(CharSequence message) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        if(User!=null){
            String Email = User.getEmail();
            long Now = System.currentTimeMillis();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss", Locale.KOREA);
            String date = simpleDateFormat.format(new Date(Now));

            SaveMemo saveMemo = new SaveMemo();
            saveMemo.setUploaderEmail(Email);
            saveMemo.setLastEditDate(date);
            saveMemo.setEditSystemTime(Now);
            saveMemo.setCheckGroupNo(0);
            saveMemo.setMemo(message.toString());
            database.getReference().child("MemoList").push().setValue(saveMemo);
        }
        else{
            Log.d("Test","메일 불러올 수 없음");
        }
    }

    private CharSequence getReplyMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_REPLY);
        }
        return null;
    }
}
