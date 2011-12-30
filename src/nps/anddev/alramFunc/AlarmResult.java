package nps.anddev.alramFunc;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class AlarmResult extends Activity {
	Button m_SnoozeButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState){ 
		Log.i("AlarmResults", "Do onCreate Result method");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showresult);
		
		//레이아웃에서 버튼을 받아오기
		m_SnoozeButton = (Button)findViewById(R.id.Snooze);
		
		m_SnoozeButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doSnooze();
			}
		});
	}
	
	public void doSnooze(){
		NotificationManager notiManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		//Notification Canceled!
		notiManager.cancel(1234);
		finish();
	}
}
