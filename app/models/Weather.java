package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Weather {
	@Id
	@Formats.DateTime(pattern = "dd-MM-yyyy")
	private java.util.Date	id;
	private Double			temperature;
	private Double			snowdepth;

	public Weather() {
	}

	public Weather(Double temp, Double snow) {
		temperature = temp;
		snowdepth = snow;
	}

	/**
	 * Save will try to find the current date previously saved. If it fails the
	 * catch clause is executed. If it does find the date it will merge the
	 * data.
	 */
	@Transactional
	public void save() {
		Weather oldData = JPA.em().find(Weather.class, this.id);
		if (oldData == null) {
			JPA.em().persist(this);

		} else {
			if (this.temperature == null && oldData != null) {
				this.temperature = oldData.temperature;
			}
			if (this.snowdepth == null && oldData.snowdepth != null) {
				this.snowdepth = oldData.snowdepth;
			}
			JPA.em().merge(this);

			// }
			// System.out.println("OLDDATA" + oldData);
			// Date exist. Use oldData if newData (this) is null.
			// Note: oldData may also be null

			// } catch (NoResultException e) {
			// We do not have the current date in our database

		}

	}

	public java.util.Date getId() {
		return id;
	}

	public void setId(java.util.Date id) {
		this.id = id;
	}

	public void setIdWithInt(int day, int month, int year) throws ParseException {
		String currentDate = day + "-" + month + "-" + year;
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		java.util.Date formattedDate = formatter.parse(currentDate);
		this.id = formattedDate;
	}

	public String getYear() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.id);
		return String.valueOf(cal.get(Calendar.YEAR));
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Double getSnowdepth() {
		return snowdepth;
	}

	public void setSnowdepth(Double snowdepth) {
		this.snowdepth = snowdepth;
	}

	@Override
	public String toString() {
		return "Weather [id=" + id + ", temperature=" + temperature + ", snowdepth=" + snowdepth
				+ "]";
	}

	public String jsonElement() {
		return "{\"year\":\"" + this.getYear() + "\",\"temperature\":\"" + this.temperature
				+ "\",\"snow\":\"" + this.snowdepth + "\"}";
	}
}
