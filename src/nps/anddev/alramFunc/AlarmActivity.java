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
	private AlarmManager m_AlarmManager; // 알람 메니저
	private GregorianCalendar m_EditedCalender; // 설정 하려는 캘린더 객체
	private DatePicker m_DatePicker; // 일자 설정 클래스
	private TimePicker m_TimePicker; // 시작 설정 클래스
	private NotificationManager m_Notofication; // 통지 관련 매니저
	private AlarmObject m_SetAlarmObject;
	private EditText m_AlarmTitle; // 알람의 이름
	private Button m_timePickBtn;
	private Context m_Context;
	
	//반복 및 시간 계산에 관련된 변수들
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

		// 통지 매니저(Notification Manager)를 OS에서 얻어온다
		m_Notofication = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// 알람 매니저를 취득한다
		m_AlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		// 설정 버튼, 리셋 버튼의 리스너를 등록한다
		setContentView(R.layout.alarmsetup);

		// 이 엑티비티에서 사용될 AlalrmObject를 가져다준 intent를 불러온다.
		Intent callerIntent = getIntent();
		Bundle bundle = callerIntent.getExtras();
		m_SetAlarmObject = bundle.getParcelable("data_Alarm");

		findWhatKindsType(m_SetAlarmObject);

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

		Button repeatDatesBtn = (Button) findViewById(R.id.DateButton);
		repeatDatesBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				setRepeatDate();
			}
		});

		// 알람의 타이틀을 가져온다.
		m_AlarmTitle = (EditText) findViewById(R.id.AlarmTitle);
		if (m_SetAlarmObject.m_AlarmName == null) {
			m_AlarmTitle.setText("제목 없음");
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
		m_timePickBtn.setText(m_EditedCalender.getTime().getHours() + "시"
				+ m_EditedCalender.getTime().getMinutes() + "분");

	}

	// 시각 설정 클래스의 상태가 변화 되었을때의 리스너 이벤트
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		m_EditedCalender.set(m_DatePicker.getYear(), m_DatePicker.getMonth(),
				m_DatePicker.getDayOfMonth(), hourOfDay, minute);
	}

	// 일자 설정 클래스의 상태가 변화 되었을때의 리스너 이벤트
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		// 캘린더에 인자의 값들을 설정하여 준다.
		m_EditedCalender.set(year, monthOfYear, dayOfMonth,
				m_TimePicker.getCurrentHour(), m_TimePicker.getCurrentMinute());
	}

	// 알람의 설정
	// 알람의 설정과 해지 모두 pendingIntent()라는 메서드를 통해 이 액티비티를 통해 작성되는 Intents를 사용한다.
	public void setAlarm() {

		// 설정된 알람의 시각과 현재의 시각을 먼저 비교하여 이전 시각일 경우 하루를 추가한다..
		checkSetAlarmTime();

		// 리턴할 인텐트를 불러오고 Pending Intent도 제작한다.
		Intent callerIntent = getIntent();
		int index = 0;

		Intent intent = new Intent(AlarmActivity.this, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(AlarmActivity.this,
				index, intent, 0);

		// Intent에 넣을 AlarmObject를 생성한다.
		AlarmObject resultobject = new AlarmObject();

		resultobject.setActivate(true);
		
		// 설정된 알람의 시간을 리턴 Alarm Object에 넣어준다.
		resultobject.m_Calendar = (GregorianCalendar) m_EditedCalender.clone();
		resultobject.m_AlarmName = m_AlarmTitle.getText().toString();
		resultobject.m_biRepeatDays = m_biRepeatDays;
		
		resultobject.calcNextActivateDaysDistance();

		// 모든 작업을 마치고 값을 되돌려 준다
		callerIntent.putExtra("data_alarm", resultobject);
		setResult(RESULT_OK, callerIntent);

		// 만들어진 알람오브젝트의 내용을 가지고 알람 메니저를 생성한다.
		madeAlarmManger(sender);

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
			if (aType.compareTo("CREATE") == 0) {

				// 현재 시각을 얻어 낸다
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

	// 시간 선택 버튼의 이벤트 핸들 함수
	private void pickSetTime() {
		TimePickerDialog.OnTimeSetListener timeSetListner = new OnTimeSetListener() {

			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				//Current Time과 Target Time, TimeDistance Var을 계산
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
				
				m_timePickBtn.setText(hourOfDay + "시" + minute + "분");
				
				String text = new String();
				if(m_dateDistanceDay.getDate() != 0 && m_dateDistanceDay.getDate() != 31){
					text += Integer.toString(m_dateDistanceDay.getDate()) + "일";
				}
				if(m_dateDistanceDay.getHours() != 0){
					text +=  Integer.toString(m_dateDistanceDay.getHours()) + "시간" ;
				}
				text += Integer.toString(m_dateDistanceDay.getMinutes()) + "분 후에 알람이 울립니다.";
				
				Toast.makeText(m_Context, text,Toast.LENGTH_SHORT).show();
			}
		};
		
		m_dateCurrentTime.setTimeInMillis(System.currentTimeMillis());
		new TimePickerDialog(this, timeSetListner, m_EditedCalender.getTime()
				.getHours(), m_EditedCalender.getTime().getMinutes(), false).show();
	}

	// 날짜 반복 버튼의 이벤트 핸들 함수
	private void setRepeatDate(){
		final Dialog dialog = new Dialog(this);

		dialog.setTitle("날짜 선택");
		
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
		
		//언제나 함께 즐겨요 피자헛!
		final ToggleButton tglAlways = (ToggleButton)layout.findViewById(R.id.toggleAll);
		tglAlways.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				for(int i = 0 ; i<=6; i++){
					tglButtonsDate[i].setChecked(isChecked);
				}
			}
		});
		
		//요일에 설정된 값이 있었거나 하다면 토글 불을 켜준다.
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

	// 알람음 설정 버튼의 이벤트 핸들 함수
	private void setAlarmSound() {

	}

	// 알람 화면 배경화면 버튼 이벤트 핸들 함수
	private void setAlarmBG() {

	}

	private void setRepeatLatency() {

	}

	private void setWakeUpPattern() {

	}

	private void madeAlarmManger(PendingIntent sender) {

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		
		// Alarm 메니저에게 설정을 해준다.
		// 1번째 파라메터 : 알람의 타입?
		// 2번째 파라메터 : 알람이 울려질 시간을 말한다.
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
	
	//오늘요일 계수와 반복자로 설정된 계수(반복요일계수)를 받아 두요일의 차이를 구하는 함수.
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