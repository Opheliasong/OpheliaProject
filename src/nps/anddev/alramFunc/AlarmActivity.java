package nps.anddev.alramFunc;

import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AlarmActivity extends Activity implements OnDateChangedListener,
		OnTimeChangedListener {
	private AlarmManager m_AlarmManager; // �˶� �޴���
	private GregorianCalendar m_EditedCalender; // ���� �Ϸ��� Ķ���� ��ü
	private DatePicker m_DatePicker; // ���� ���� Ŭ����
	private TimePicker m_TimePicker; // ���� ���� Ŭ����
	private NotificationManager m_Notofication; // ���� ���� �Ŵ���
	private AlarmObject m_SetAlarmObject;
	private EditText m_AlarmTitle; // �˶��� �̸�
	private Button m_timePickBtn;
	private Context m_Context;
	
	//�ݺ� �� �ð� ��꿡 ���õ� ������
	private int m_iDaysCarry = 0;
	private long m_iTimeDistance = 0;
	private Date m_dateDistanceDay;
	private int m_biRepeatDays = 0;
	private GregorianCalendar m_dateCurrentTime;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		m_Context = this;
		m_dateCurrentTime = new GregorianCalendar();

		// ���� �Ŵ���(Notification Manager)�� OS���� ���´�
		m_Notofication = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// �˶� �Ŵ����� ����Ѵ�
		m_AlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		// ���� ��ư, ���� ��ư�� �����ʸ� ����Ѵ�
		setContentView(R.layout.alarmsetup);

		// �� ��Ƽ��Ƽ���� ���� AlalrmObject�� �������� intent�� �ҷ��´�.
		Intent callerIntent = getIntent();
		Bundle bundle = callerIntent.getExtras();
		m_SetAlarmObject = bundle.getParcelable("data_Alarm");

		findWhatKindsType(m_SetAlarmObject);

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

		Button repeatDatesBtn = (Button) findViewById(R.id.DateButton);
		repeatDatesBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				setRepeatDate();
			}
		});

		// �˶��� Ÿ��Ʋ�� �����´�.
		m_AlarmTitle = (EditText) findViewById(R.id.AlarmTitle);
		if (m_SetAlarmObject.m_AlarmName == null) {
			m_AlarmTitle.setText("���� ����");
		} else {
			m_AlarmTitle.setText(m_SetAlarmObject.m_AlarmName);
		}

		m_timePickBtn = (Button) findViewById(R.id.timePickButton);
		m_timePickBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				pickSetTime();
			}
		});
		m_timePickBtn.setText(m_EditedCalender.getTime().getHours() + "��"
				+ m_EditedCalender.getTime().getMinutes() + "��");

	}

	// �ð� ���� Ŭ������ ���°� ��ȭ �Ǿ������� ������ �̺�Ʈ
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		m_EditedCalender.set(m_DatePicker.getYear(), m_DatePicker.getMonth(),
				m_DatePicker.getDayOfMonth(), hourOfDay, minute);
	}

	// ���� ���� Ŭ������ ���°� ��ȭ �Ǿ������� ������ �̺�Ʈ
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		// Ķ������ ������ ������ �����Ͽ� �ش�.
		m_EditedCalender.set(year, monthOfYear, dayOfMonth,
				m_TimePicker.getCurrentHour(), m_TimePicker.getCurrentMinute());
	}

	// �˶��� ����
	// �˶��� ������ ���� ��� pendingIntent()��� �޼��带 ���� �� ��Ƽ��Ƽ�� ���� �ۼ��Ǵ� Intents�� ����Ѵ�.
	public void setAlarm() {

		// ������ �˶��� �ð��� ������ �ð��� ���� ���Ͽ� ���� �ð��� ��� �Ϸ縦 �߰��Ѵ�..
		checkSetAlarmTime();

		// ������ ����Ʈ�� �ҷ����� Pending Intent�� �����Ѵ�.
		Intent callerIntent = getIntent();
		int index = 0;

		Intent intent = new Intent(AlarmActivity.this, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(AlarmActivity.this,
				index, intent, 0);

		// Intent�� ���� AlarmObject�� �����Ѵ�.
		AlarmObject resultobject = new AlarmObject();

		resultobject.setActivate(true);
		
		// ������ �˶��� �ð��� ���� Alarm Object�� �־��ش�.
		resultobject.m_Calendar = (GregorianCalendar) m_EditedCalender.clone();
		resultobject.m_AlarmName = m_AlarmTitle.getText().toString();
		resultobject.m_biRepeatDays = m_biRepeatDays;
		
		resultobject.calcNextActivateDaysDistance();

		// ��� �۾��� ��ġ�� ���� �ǵ��� �ش�
		callerIntent.putExtra("data_alarm", resultobject);
		setResult(RESULT_OK, callerIntent);

		// ������� �˶�������Ʈ�� ������ ������ �˶� �޴����� �����Ѵ�.
		madeAlarmManger(sender);

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
			if (aType.compareTo("CREATE") == 0) {

				// ���� �ð��� ��� ����
				m_EditedCalender = (GregorianCalendar)m_dateCurrentTime.clone();
				m_iDaysCarry = 0;
				Log.i("Alarm", "Onload ThisTime: "
						+ m_EditedCalender.getTime().toString());
			}

			if (aType.compareTo("EDIT") == 0) {
				m_EditedCalender = aObject.m_Calendar;
				m_biRepeatDays = aObject.m_biRepeatDays;
			}
		}
	}

	// �ð� ���� ��ư�� �̺�Ʈ �ڵ� �Լ�
	private void pickSetTime() {
		TimePickerDialog.OnTimeSetListener timeSetListner = new OnTimeSetListener() {

			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				//Current Time�� Target Time, TimeDistance Var�� ���
				Date targetTime = new Date();
				targetTime.setHours(hourOfDay);
				targetTime.setMinutes(minute);
				Date currentTime = m_dateCurrentTime.getTime();
				m_iTimeDistance = targetTime.getTime() - currentTime.getTime();
				
				int nextAlarmHour = 0;
				
				if(m_iDaysCarry > 0){
					int currentDate = currentTime.getDay();
					int dateValue = 1;
					dateValue = dateValue<<currentDate-1;
					int resultOfDateTerm = 0;
					resultOfDateTerm = calcDaysToDaysTerm(dateValue, m_iDaysCarry,resultOfDateTerm);
					
					//m_dateDistanceDay.setHours(hourOfDay + (resultOfDateTerm * 24));
					nextAlarmHour += (resultOfDateTerm * 24);
				}
				
				//
				//m_EditedCalender.set(m_EditedCalender.HOUR_OF_DAY, hourOfDay);
				m_EditedCalender.set(m_EditedCalender.HOUR_OF_DAY, hourOfDay + nextAlarmHour);
				m_EditedCalender.set(m_EditedCalender.MINUTE, minute);
				
				m_dateDistanceDay = new Date();
				m_dateDistanceDay.setDate(m_EditedCalender.getTime().getDate()-currentTime.getDate());
				m_dateDistanceDay.setHours(m_EditedCalender.getTime().getHours()-currentTime.getHours());
				m_dateDistanceDay.setMinutes(m_EditedCalender.getTime().getMinutes() - currentTime.getMinutes());
				
				m_timePickBtn.setText(hourOfDay + "��" + minute + "��");
				
				String text = new String();
				if(m_dateDistanceDay.getDate() != 0 && m_dateDistanceDay.getDate() != 31){
					text += Integer.toString(m_dateDistanceDay.getDate()) + "��";
				}
				if(m_dateDistanceDay.getHours() != 0){
					text +=  Integer.toString(m_dateDistanceDay.getHours()) + "�ð�" ;
				}
				text += Integer.toString(m_dateDistanceDay.getMinutes()) + "�� �Ŀ� �˶��� �︳�ϴ�.";
				
				Toast.makeText(m_Context, text,Toast.LENGTH_SHORT).show();
			}
		};
		
		m_dateCurrentTime.setTimeInMillis(System.currentTimeMillis());
		new TimePickerDialog(this, timeSetListner, m_EditedCalender.getTime()
				.getHours(), m_EditedCalender.getTime().getMinutes(), false).show();
	}

	// ��¥ �ݺ� ��ư�� �̺�Ʈ �ڵ� �Լ�
	private void setRepeatDate(){
		final Dialog dialog = new Dialog(this);

		dialog.setTitle("��¥ ����");
		
		LayoutInflater inflator = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflator.inflate(R.layout.setdates, null); 
		dialog.setContentView(layout);
		
		Button btnSet = (Button)layout.findViewById(R.id.setOnDates);
		
		final ToggleButton tglButtonsDate[] = new ToggleButton[8];
		tglButtonsDate[0]=(ToggleButton)layout.findViewById(R.id.toggleMon);
		tglButtonsDate[1]=(ToggleButton)layout.findViewById(R.id.toggleTue);
		tglButtonsDate[2]=(ToggleButton)layout.findViewById(R.id.toggleWed);
		tglButtonsDate[3]=(ToggleButton)layout.findViewById(R.id.toggleThr);
		tglButtonsDate[4]=(ToggleButton)layout.findViewById(R.id.toggleFri);
		tglButtonsDate[5]=(ToggleButton)layout.findViewById(R.id.toggleSat);
		tglButtonsDate[6]=(ToggleButton)layout.findViewById(R.id.toggleSun);
		tglButtonsDate[7]=(ToggleButton)layout.findViewById(R.id.toggleAll);
		
		btnSet.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				m_iDaysCarry = 0;
				for(int i = 0; i<=6 ; i++){
					if(tglButtonsDate[i].isChecked()){
						//m_iAddDaysByRepeatDate++;
						int addsValue = 1<<i;
						m_iDaysCarry += addsValue;
					}
				}
				
				Log.i("AlarmTag",Integer.toString(m_iDaysCarry));
			}
		});
		
		Button btnCancle = (Button)layout.findViewById(R.id.closeDateSetting);
		btnCancle.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				dialog.cancel();	
			}
		});
		
		//������ �Բ� ��ܿ� ������!
		final ToggleButton tglAlways = (ToggleButton)layout.findViewById(R.id.toggleAll);
		tglAlways.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				for(int i = 0 ; i<=6; i++){
					tglButtonsDate[i].setChecked(isChecked);
				}
			}
		});
		
		//���Ͽ� ������ ���� �־��ų� �ϴٸ� ��� ���� ���ش�.
		if(m_iDaysCarry > 0 && m_iDaysCarry < 127){
			
			for(int i = 6 ; i >= 0 ; i--){
				int subMaskValue2 = 64;
				if((m_iDaysCarry & subMaskValue2>>>i)== subMaskValue2>>i){
					tglButtonsDate[6-i].setChecked(true);
				}
			}
		}else{
			tglButtonsDate[7].setChecked(true);
		} 
		
		dialog.show();
	}

	// �˶��� ���� ��ư�� �̺�Ʈ �ڵ� �Լ�
	private void setAlarmSound() {

	}

	// �˶� ȭ�� ���ȭ�� ��ư �̺�Ʈ �ڵ� �Լ�
	private void setAlarmBG() {

	}

	private void setRepeatLatency() {

	}

	private void setWakeUpPattern() {

	}

	private void madeAlarmManger(PendingIntent sender) {

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		
		// Alarm �޴������� ������ ���ش�.
		// 1��° �Ķ���� : �˶��� Ÿ��?
		// 2��° �Ķ���� : �˶��� ����� �ð��� ���Ѵ�.
		// alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
		// m_Calender.getTimeInMillis(),10000,sender);
		alarmManager.set(AlarmManager.RTC_WAKEUP, m_EditedCalender.getTimeInMillis(),sender);
	}

	private void checkSetAlarmTime() {

		GregorianCalendar currentTime = new GregorianCalendar();
		if (currentTime.getTimeInMillis() >= m_EditedCalender.getTimeInMillis()) {
			Date currentDate = m_EditedCalender.getTime();
			currentDate.setDate(currentDate.getDate() + m_iDaysCarry);
			m_EditedCalender.setTime(currentDate);
			Log.i("AlarmDay", Integer.toString(m_EditedCalender.getTime().getDate()));
		}
	}
	
	//���ÿ��� ����� �ݺ��ڷ� ������ ���(�ݺ����ϰ��)�� �޾� �ο����� ���̸� ���ϴ� �Լ�.
	private int calcDaysToDaysTerm(int aiTodayValue, int aiSetRepeatValue,int counter){
		
		if(aiTodayValue > aiSetRepeatValue){
			aiSetRepeatValue = aiSetRepeatValue<<7;
		}
		
		//int retunValue;
		if((aiTodayValue & aiSetRepeatValue)==0){
			aiTodayValue = aiTodayValue<<1;
			counter++;
			counter = calcDaysToDaysTerm(aiTodayValue, aiSetRepeatValue,counter);
		}
		
		return counter;
	}
}