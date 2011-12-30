package nps.anddev.alramFunc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
	//Reciver�� Recive�ɶ� ó��
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		//�佺Ʈ �޽����� �����ش�
		//Log.i("HelloAlarmActivity","onReceived!");
		
		Toast.makeText(context, R.string.app_name, Toast.LENGTH_SHORT).show();
		
		showNotification(context, R.drawable.ic_launcher,"�˶�!!", "���� �̷��� ���� �ð� ����."); 
	}
	
	//Context�� StatusBar�� IconID, StatusBarText�� ID, Detail�� �ؽ�Ʈ�� ID�� �Ѱ� �޴´�.
	private void showNotification(Context context, int statusBarIconID, 
			String statusBarTextID, String detaildTextID){
		Intent contentIntent = new Intent(context, AlarmResult.class);
		PendingIntent theAppIntent =
				PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		CharSequence from = "�˶�";
		CharSequence message = "�������� �ؾ� �˶��� ��������";
		
		Notification notif = new Notification(statusBarIconID, null, System.currentTimeMillis());
		notif.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6"); //Ring URI;
		notif.flags |= Notification.FLAG_NO_CLEAR;
		notif.setLatestEventInfo(context, from, message, theAppIntent);
		notif.ledARGB = Color.GREEN;
		NotificationManager notifManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
	
		notifManager.notify(1234,notif);
	}

}
