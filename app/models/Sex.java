package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Sex {

	@Id
	public Integer	id;

	@Constraints.Required
	public String	type;

	@Override
	public String toString() {
		return type;
	}

	public static Sex findById(int id) {
		return JPA.em().find(Sex.class, id);
	}
	
	@Transactional
	public void save() {
		JPA.em().persist(this);
	}


	public static Map<String, String> options() {
		@SuppressWarnings("unchecked")
		List<Sex> sex = JPA.em().createQuery("From Sex").getResultList();
		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		for (Sex currentSex : sex) {
			options.put(currentSex.id.toString(), currentSex.type);
		}
		return options;
	}

}
