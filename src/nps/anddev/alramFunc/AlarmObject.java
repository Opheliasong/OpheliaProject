package nps.anddev.alramFunc;

import java.util.GregorianCalendar;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AlarmObject implements Parcelable {
	private SetOndays m_DaySetOn;
	private Uri m_AlarmSoundUri;
	private Uri m_AlarmBackUri;
	private boolean m_OnActivate;
	public GregorianCalendar m_Calendar;
	public int m_Index;
	public String m_CurrentType;

	public class SetOndays {

		public boolean m_Monday;
		public boolean m_Tuesday;
		public boolean m_Wednesday;
		public boolean m_Thursday;
		public boolean m_Friday;
		public boolean m_Saturday;
		public boolean m_Sunday;

		public SetOndays() {
		}
	}

	public AlarmObject() {
		// m_setDateOption = 0;
		m_DaySetOn = new SetOndays(); // 일반적인 생성
		m_AlarmSoundUri = Uri.EMPTY; // 초기값은 없다.
		m_AlarmBackUri = Uri.EMPTY; // 초기값은 없다.
		m_OnActivate = false; // 초기값은 알람 해지
		m_Calendar = null;
	}

	// parcel에 꾸린다
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeValue(m_AlarmSoundUri);
		dest.writeValue(m_Calendar);
		dest.writeInt(m_Index);
		dest.writeString(m_CurrentType);
		
		/*
		 * dest.writeValue(m_AlarmBackUri); dest.writeValue(m_DaySetOn);
		 * dest.writeValue(m_AfterNoonOption); dest.writeValue(m_OnActivate);
		 * dest.writeValue(m_Calendar);
		 */

		Log.i("Parcel", "Alarm Parcel Writing");
	}

	// 꾸려진 Parcel을 불러와서 읽어 들인다.
	// 꾸려진 Parcel은 write한 순서와 동일해야 한다. 이것만 잘 주의하면 된다.
	
	public AlarmObject(Parcel in) {
		m_AlarmSoundUri = (Uri) in.readValue(Uri.class.getClassLoader());
		m_Calendar = (GregorianCalendar) in.readValue(GregorianCalendar.class.getClassLoader());
		m_Index = in.readInt();
		m_CurrentType = in.readString();

		Log.i("Parcel", "Alarm Parcel Reading");
	}

	// Parcelable 중요한 인터페이스중 하나인 CREATOR 인터페이스의 구현부.
	// 이 Creator가 없을 경우 Parcelable 방식의 객체는 Exception을 발생한다
	public static final Parcelable.Creator<AlarmObject> CREATOR = new Creator<AlarmObject>() {

		public AlarmObject createFromParcel(Parcel arg0) {
			// TODO Auto-generated method stub
			AlarmObject retAlarm = new AlarmObject(arg0);
			return retAlarm;
		}

		public AlarmObject[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return new AlarmObject[arg0];
		}
	};

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public SetOndays getDayArray() {
		return m_DaySetOn;
	}

	public Uri getAlamSoundURI() {
		return m_AlarmSoundUri;
	}

	public Uri getAlarmBackGroundURI() {
		return m_AlarmBackUri;
	}

	public boolean getIsActivate() {
		return m_OnActivate;
	}

	public void setDateOption(SetOndays aSettingDays) {
		m_DaySetOn = aSettingDays;
	}

	public void setActivate(boolean aOnActivate) {
		m_OnActivate = aOnActivate;
	}

	public void setCalander(GregorianCalendar aCalander) {
		m_Calendar = aCalander;
	}

	// 알람 오브젝트 객체의 타입의 팩토리 함수군 및 Getter
	public void setCreatePhase() {
		m_CurrentType = "CREATE";
	}

	public void setEditPhase() {
		m_CurrentType = "EDIT";
	}

	public void setDestroyPHase() {
		m_CurrentType = "DESTORY";
	}

	public String getCurrentType() {
		return m_CurrentType;
	}

}
