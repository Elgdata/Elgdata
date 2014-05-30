package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Area {

	@Id
	public String	id;

	public String	name;

	@Override
	public String toString() {
		return id;
	}

	public static Area findById(String id) {
		return JPA.em().find(Area.class, id);
	}

	@Transactional
	public void save() {
		JPA.em().persist(this);
	}

	public static Map<String, String> options() {
		@SuppressWarnings("unchecked")
		List<Area> areas = JPA.em().createQuery("FROM Area GROUP BY name ORDER BY name")
				.getResultList();
		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		for (Area a : areas) {
			options.put(a.id.toString(), a.name);
		}
		return options;
	}
}