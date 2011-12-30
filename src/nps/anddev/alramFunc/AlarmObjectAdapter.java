package nps.anddev.alramFunc;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AlarmObjectAdapter extends ArrayAdapter implements OnClickListener{
	private Activity context;
	private ArrayList<AlarmObject> m_lstAlarmObjects;

	public AlarmObjectAdapter(Activity context, ArrayList<AlarmObject> aList) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.alarmitem, aList);
		this.context = context;
		m_lstAlarmObjects = aList;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View raw = convertView;
		AlarmWrapper wrapper;

		if (raw == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			raw = inflater.inflate(R.layout.alarmitem, null);
			raw.setOnClickListener(this);
			wrapper = new AlarmWrapper();

			wrapper.timeText = (TextView) raw.findViewById(R.id.time);
			wrapper.dateText = (TextView) raw.findViewById(R.id.Dates);
			wrapper.onOffButton = (ToggleButton) raw.findViewById(R.id.toggleButton1);
			wrapper.position = position;

			raw.setTag(wrapper);
		} else {
			wrapper = (AlarmWrapper) raw.getTag();
		}
		
		//wrapper.timeText.setText("Hello Time Text");
		String timeString = m_lstAlarmObjects.get(position).m_Calendar.getTime().toLocaleString();
		wrapper.timeText.setText(timeString);
		wrapper.dateText.setText("Hello Date Text");

		return raw;
	}

	class AlarmWrapper {
		public TextView timeText;
		public TextView dateText;
		public ToggleButton onOffButton;
		public int position;

	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		AlarmWrapper viewsTag = (AlarmWrapper)v.getTag();

		//Activity를 호출할 Intent를 만든다
		Intent callIntent = new Intent(context,AlarmActivity.class);
		
		//Intent에 넣어서 보낼 AlarmObject를 가져온다,
		AlarmObject sendingObject = m_lstAlarmObjects.get(viewsTag.position);
		
		//AlarmObject의 보내는 타입을 Edit으로 수정
		sendingObject.setEditPhase();
		
		callIntent.putExtra("data_Alarm", sendingObject);
		
		context.startActivityForResult(callIntent, 2);
		//int position = viewsTag.position;
		//Toast.makeText(context, Integer.toString(position), Toast.LENGTH_SHORT).show();
		
	}
}
