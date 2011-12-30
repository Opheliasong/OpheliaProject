package nps.anddev.alramFunc;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;


public class AlarmStorege {
	public Map<Integer , Vector<AlarmObject>> m_GroupStoreMap;
	public ArrayList<AlarmObject> m_lstAlarmDevingGroup;
	
	public AlarmStorege() {
		// TODO Auto-generated constructor stub
		
	}
	
	//0.2 버전용 DB 객체 생성자
	public AlarmStorege(ArrayList<AlarmObject> aList){
		m_lstAlarmDevingGroup = aList;
	}
	
	//0.8버전용 DB 객체 생성자
	public AlarmStorege(Map<Integer , Vector<AlarmObject>> aMaps){
		m_GroupStoreMap = aMaps;
	}
	
}
