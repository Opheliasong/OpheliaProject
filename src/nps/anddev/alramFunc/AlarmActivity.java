package nps.anddev.alramFunc;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class AlarmActivity extends Activity implements OnDateChangedListener,
		OnTimeChangedListener {
	private AlarmManager m_AlarmManager; // 알람 메니저
	private GregorianCalendar m_Calender; // 설정 일시
	private DatePicker m_DatePicker; // 일자 설정 클래스
	private TimePicker m_TimePicker; // 시작 설정 클래스
	private NotificationManager m_Notofication; // 통지 관련 매니저

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 통지 매니저(Notification Manager)를 OS에서 얻어온다
		m_Notofication = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// 알람 매니저를 취득한다
		m_AlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		// 설정 버튼, 리셋 버튼의 리스너를 등록한다
		setContentView(R.layout.alarmsetup);

		// 1)설정 버튼의 리스너 등록
		Button SetButton = (Button) findViewById(R.id.setBtn);

		SetButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				setAlarm();
			}
		});

		// 2)리셋 버튼의 리스너 등록
		Button ResetButton = (Button) findViewById(R.id.resetBtn);
		ResetButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				resetAlarm();
			}
		});

		// 레이아웃의 일정 입력폼의 객체를 들고 온다
		m_DatePicker = (DatePicker) findViewById(R.id.datePicker1);
		// 레이아웃의 시각 입력폼의 객체를 들고 온다
		m_TimePicker = (TimePicker) findViewById(R.id.timePicker1);

		Intent callerIntent = getIntent();

		Bundle bundle = callerIntent.getExtras();
		AlarmObject object = bundle.getParcelable("data_Alarm");

		findWhatKindsType(object);

		// DatePicker와 TimePicker의 날짜와 시간을 m_Calender가 생성된 시점으로 설정한다.
		m_DatePicker.init(m_Calender.get(Calendar.YEAR),
				m_Calender.get(Calendar.MONTH),
				m_Calender.get(Calendar.DAY_OF_MONTH), this);

		m_TimePicker.setCurrentHour(m_Calender.get(Calendar.HOUR_OF_DAY));
		m_TimePicker.setCurrentMinute(m_Calender.get(Calendar.MINUTE));
		m_TimePicker.setOnTimeChangedListener(this);
	}

	// 시각 설정 클래스의 상태가 변화 되었을때의 리스너 이벤트
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		m_Calender.set(m_DatePicker.getYear(), m_DatePicker.getMonth(),
				m_DatePicker.getDayOfMonth(), hourOfDay, minute);
		// Log.i("HelloAlarmActivity", "widgetTime : " +
		// m_Calender.getTime().toString());
	}

	// 일자 설정 클래스의 상태가 변화 되었을때의 리스너 이벤트
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		// 캘린더에 인자의 값들을 설정하여 준다.
		m_Calender.set(year, monthOfYear, dayOfMonth,
				m_TimePicker.getCurrentHour(), m_TimePicker.getCurrentMinute());
	}

	// 알람의 설정
	// 알람의 설정과 해지 모두 pendingIntent()라는 메서드를 통해 이 액티비티를 통해 작성되는 Intents를 사용한다.
	public void setAlarm() {

		Intent callerIntent = getIntent();
		//Intent callerIntent = new Intent(AlarmActivity.this,alarmList.class);
		int index = 0;
		// index = callerIntent.getIntExtra("data_indexNum", 0);

		Intent intent = new Intent(AlarmActivity.this, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(AlarmActivity.this,
				index, intent, 0);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		// Alarm 메니저에게 설정을 해준다.
		// 1번째 파라메터 : 알람의 타입?
		// 2번째 파라메터 : 알람이 울려질 시간을 말한다.
		alarmManager.set(AlarmManager.RTC_WAKEUP, m_Calender.getTimeInMillis(),
				sender);

		String text = m_Calender.getTime().toString();

		AlarmObject resultobject = new AlarmObject();

		resultobject.setActivate(true);
		resultobject.m_Calendar = (GregorianCalendar) m_Calender.clone();

		// 모든 작업을 마치고 값을 되돌려 준다
		callerIntent.putExtra("data_Time", text);
		callerIntent.putExtra("data_alarm", resultobject);
		setResult(RESULT_OK, callerIntent);

		Log.i("HelloAlarmActivity", "Alarm Index Num : " + index);
		finish();
	}

	// 알람의 해지
	public void resetAlarm() {
		// m_AlarmManager.cancel(pendingIntent());
		finish();
	}

	public PendingIntent pendingIntent() {
		Intent intents = new Intent(getApplicationContext(),
				AlarmActivity.class);
		PendingIntent pendingInstance = PendingIntent.getActivity(this, 0,
				intents, 0);
		return pendingInstance;
	}

	// Intent로 넘겨 받은 Alarm객체의 상태에 따라 분기를 준다.

	private void findWhatKindsType(AlarmObject aObject) {

		String aType = aObject.getCurrentType();

		if (aObject != null) {

			// 1. 생성타입의 알람 객체의 경우
			if (aType.compareTo("CREATE")==0) {

				// 현재 시각을 얻어 낸다
				m_Calender = new GregorianCalendar();
				Log.i("HelloAlarmActivity", "Onload ThisTime: "
						+ m_Calender.getTime().toString());
			}

			if (aType.compareTo("EDIT")==0) {
				m_Calender = aObject.m_Calendar;
			}
		}
	}
}