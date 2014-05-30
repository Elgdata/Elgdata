package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class DeerLice {

	@Id
	public Integer	id;

	public String	sum;

	public String toString() {
		return sum;
	}

	public static DeerLice findById(int id) {
		return JPA.em().find(DeerLice.class, id);
	}

	@Transactional
	public void save() {
		JPA.em().persist(this);
	}

	public static Map<String, String> options() {
		@SuppressWarnings("unchecked")
		List<DeerLice> deerlices = JPA.em().createQuery("from DeerLice").getResultList();
		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		for (DeerLice deerlice : deerlices) {
			options.put(deerlice.id.toString(), deerlice.sum);
		}
		return options;
	}
}
