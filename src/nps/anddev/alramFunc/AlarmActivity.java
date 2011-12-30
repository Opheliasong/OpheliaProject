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
	private AlarmManager m_AlarmManager; // �˶� �޴���
	private GregorianCalendar m_Calender; // ���� �Ͻ�
	private DatePicker m_DatePicker; // ���� ���� Ŭ����
	private TimePicker m_TimePicker; // ���� ���� Ŭ����
	private NotificationManager m_Notofication; // ���� ���� �Ŵ���

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ���� �Ŵ���(Notification Manager)�� OS���� ���´�
		m_Notofication = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// �˶� �Ŵ����� ����Ѵ�
		m_AlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		// ���� ��ư, ���� ��ư�� �����ʸ� ����Ѵ�
		setContentView(R.layout.alarmsetup);

		// 1)���� ��ư�� ������ ���
		Button SetButton = (Button) findViewById(R.id.setBtn);

		SetButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				setAlarm();
			}
		});

		// 2)���� ��ư�� ������ ���
		Button ResetButton = (Button) findViewById(R.id.resetBtn);
		ResetButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				resetAlarm();
			}
		});

		// ���̾ƿ��� ���� �Է����� ��ü�� ��� �´�
		m_DatePicker = (DatePicker) findViewById(R.id.datePicker1);
		// ���̾ƿ��� �ð� �Է����� ��ü�� ��� �´�
		m_TimePicker = (TimePicker) findViewById(R.id.timePicker1);

		Intent callerIntent = getIntent();

		Bundle bundle = callerIntent.getExtras();
		AlarmObject object = bundle.getParcelable("data_Alarm");

		findWhatKindsType(object);

		// DatePicker�� TimePicker�� ��¥�� �ð��� m_Calender�� ������ �������� �����Ѵ�.
		m_DatePicker.init(m_Calender.get(Calendar.YEAR),
				m_Calender.get(Calendar.MONTH),
				m_Calender.get(Calendar.DAY_OF_MONTH), this);

		m_TimePicker.setCurrentHour(m_Calender.get(Calendar.HOUR_OF_DAY));
		m_TimePicker.setCurrentMinute(m_Calender.get(Calendar.MINUTE));
		m_TimePicker.setOnTimeChangedListener(this);
	}

	// �ð� ���� Ŭ������ ���°� ��ȭ �Ǿ������� ������ �̺�Ʈ
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		m_Calender.set(m_DatePicker.getYear(), m_DatePicker.getMonth(),
				m_DatePicker.getDayOfMonth(), hourOfDay, minute);
		// Log.i("HelloAlarmActivity", "widgetTime : " +
		// m_Calender.getTime().toString());
	}

	// ���� ���� Ŭ������ ���°� ��ȭ �Ǿ������� ������ �̺�Ʈ
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		// Ķ������ ������ ������ �����Ͽ� �ش�.
		m_Calender.set(year, monthOfYear, dayOfMonth,
				m_TimePicker.getCurrentHour(), m_TimePicker.getCurrentMinute());
	}

	// �˶��� ����
	// �˶��� ������ ���� ��� pendingIntent()��� �޼��带 ���� �� ��Ƽ��Ƽ�� ���� �ۼ��Ǵ� Intents�� ����Ѵ�.
	public void setAlarm() {

		Intent callerIntent = getIntent();
		//Intent callerIntent = new Intent(AlarmActivity.this,alarmList.class);
		int index = 0;
		// index = callerIntent.getIntExtra("data_indexNum", 0);

		Intent intent = new Intent(AlarmActivity.this, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(AlarmActivity.this,
				index, intent, 0);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		// Alarm �޴������� ������ ���ش�.
		// 1��° �Ķ���� : �˶��� Ÿ��?
		// 2��° �Ķ���� : �˶��� ����� �ð��� ���Ѵ�.
		alarmManager.set(AlarmManager.RTC_WAKEUP, m_Calender.getTimeInMillis(),
				sender);

		String text = m_Calender.getTime().toString();

		AlarmObject resultobject = new AlarmObject();

		resultobject.setActivate(true);
		resultobject.m_Calendar = (GregorianCalendar) m_Calender.clone();

		// ��� �۾��� ��ġ�� ���� �ǵ��� �ش�
		callerIntent.putExtra("data_Time", text);
		callerIntent.putExtra("data_alarm", resultobject);
		setResult(RESULT_OK, callerIntent);

		Log.i("HelloAlarmActivity", "Alarm Index Num : " + index);
		finish();
	}

	// �˶��� ����
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

	// Intent�� �Ѱ� ���� Alarm��ü�� ���¿� ���� �б⸦ �ش�.

	private void findWhatKindsType(AlarmObject aObject) {

		String aType = aObject.getCurrentType();

		if (aObject != null) {

			// 1. ����Ÿ���� �˶� ��ü�� ���
			if (aType.compareTo("CREATE")==0) {

				// ���� �ð��� ��� ����
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