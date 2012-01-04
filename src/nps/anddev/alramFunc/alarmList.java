package nps.anddev.alramFunc;

import java.util.ArrayList;
import java.util.List;

import nps.anddev.alramFunc.AlarmObjectAdapter.AlarmWrapper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class alarmList extends Activity {
	//List를 관리할 변수부
	private ListView m_AlarmList;
	private AlarmObjectAdapter m_AlarmAdapter;
	//private ArrayList<String> m_Strings = new ArrayList<String>();
	private int m_iAlarmIndex = 0;
	private ArrayList<AlarmObject> m_lstAlarmObject;
	
	//레이아웃의 알람 생성, 그룹 생성 버튼
	private Button m_createGroup;
	private Button m_createAlarm;
	
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	
        //레이아웃에서 List를 연결하기 위해 List를 가져온다
        m_AlarmList = (ListView)findViewById(R.id.listView1);
        m_lstAlarmObject = new ArrayList<AlarmObject>();
        
        //Testing 용 Alarm Object 생성
        //AlarmObject object = new AlarmObject();
        //m_lstAlarmObject.add(object);        
        
        m_AlarmAdapter = new AlarmObjectAdapter(this,m_lstAlarmObject);
        m_AlarmList.setAdapter(m_AlarmAdapter);
        m_AlarmList.setItemsCanFocus(false);        
        m_AlarmList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //m_AlarmList.setOnItemClickListener(m_AlarmAdapter);
        
        //레이아웃에서 Button을 연결하기 위해 Button widget 2개를 가져온다.
        m_createAlarm = (Button)findViewById(R.id.AddAlarm);
        m_createGroup = (Button)findViewById(R.id.AddGroup);
        
        //Alarm 생성 버튼의 Click Event Listener 설정
        m_createAlarm.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				createAlarm();
			}
		});
        
        m_createGroup.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				createGroup();
			}
		});
	}
    
    private void createAlarm(){
    	Intent callIntent = new Intent(alarmList.this, AlarmActivity.class);
    	
    	//Alarm Object를 생성한다
    	AlarmObject newbornAlarm = new AlarmObject();
    	    	
    	//Alarm Object의 Index number를 설정한다
    	newbornAlarm.m_Index = m_iAlarmIndex;
    	    	
    	//Alarm Object의 상태를 설정한다
    	newbornAlarm.setCreatePhase();
    	
    	//전송한다.
    	callIntent.putExtra("data_Alarm", newbornAlarm);
    	
    	m_iAlarmIndex++;
    	startActivityForResult(callIntent, 1);
		Log.i("HelloAlarmActivity","Index Num at List : "+ m_iAlarmIndex);
    }

    private void createGroup(){
    	
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(resultCode == RESULT_OK){
    		if(requestCode ==1){
    	    	Bundle bundle = data.getExtras();
    	    	AlarmObject object = bundle.getParcelable("data_alarm");
    			
    			m_lstAlarmObject.add(object);
    			m_AlarmAdapter.notifyDataSetChanged();
    		}
    		else if(requestCode ==2){
    	    	Bundle bundle = data.getExtras();
    	    	AlarmObject object = bundle.getParcelable("data_alarm");
    			
    	    	AlarmObject changedTarget = m_lstAlarmObject.get(object.m_Index);
    	    	changedTarget.m_Calendar = object.m_Calendar;
    	    	changedTarget.m_AlarmName = object.m_AlarmName;
    	    	changedTarget.m_biRepeatDays = object.m_biRepeatDays;
    			m_AlarmAdapter.notifyDataSetChanged();
    		}
    	}
    }
    
    
    @Override
    public void onDestroy(){
    	Bundle outState = new Bundle();
    	
    	onSaveInstanceState(outState);
    	Log.i("AlarmDestory","onDestroy Called");
    	super.onDestroy();
    }
}
