package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.data.validation.Constraints;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Tick {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer	id;

	@Constraints.Required
	public String	sum;

	@Override
	public String toString() {
		return sum;
	}

	public static Tick findById(int id) {
		return JPA.em().find(Tick.class, id);
	}
	
	@Transactional
	public void save() {
		JPA.em().persist(this);
	}


	public static Map<String, String> options() {

		@SuppressWarnings("unchecked")
		List<Tick> ticks = JPA.em().createQuery("from Tick").getResultList();
		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		for (Tick currentTick : ticks) {
			options.put(currentTick.id.toString(), currentTick.sum);
		}
		return options;

	}
}
