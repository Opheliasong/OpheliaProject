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
		m_DaySetOn = new SetOndays(); // �Ϲ����� ����
		m_AlarmSoundUri = Uri.EMPTY; // �ʱⰪ�� ����.
		m_AlarmBackUri = Uri.EMPTY; // �ʱⰪ�� ����.
		m_OnActivate = false; // �ʱⰪ�� �˶� ����
		m_Calendar = null;
	}

	// parcel�� �ٸ���
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

	// �ٷ��� Parcel�� �ҷ��ͼ� �о� ���δ�.
	// �ٷ��� Parcel�� write�� ������ �����ؾ� �Ѵ�. �̰͸� �� �����ϸ� �ȴ�.
	
	public AlarmObject(Parcel in) {
		m_AlarmSoundUri = (Uri) in.readValue(Uri.class.getClassLoader());
		m_Calendar = (GregorianCalendar) in.readValue(GregorianCalendar.class.getClassLoader());
		m_Index = in.readInt();
		m_CurrentType = in.readString();

		Log.i("Parcel", "Alarm Parcel Reading");
	}

	// Parcelable �߿��� �������̽��� �ϳ��� CREATOR �������̽��� ������.
	// �� Creator�� ���� ��� Parcelable ����� ��ü�� Exception�� �߻��Ѵ�
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

	// �˶� ������Ʈ ��ü�� Ÿ���� ���丮 �Լ��� �� Getter
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
