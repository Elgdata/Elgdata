package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;

import akka.DbExecutionContext;
import play.db.jpa.JPA;
import play.libs.F;
import play.libs.F.Function0;
import play.mvc.Http;

@Entity
public class HuntingField {

	@Id
	public String	id;

	public String	name;

	@Override
	public String toString() {
		return id;
	}

	public static HuntingField findById(String id) {
		return JPA.em().find(HuntingField.class, id);
	}

	public void save() {
		JPA.em().persist(this);
	}

	public static Map<String, String> options() {
		@SuppressWarnings("unchecked")
		List<HuntingField> huntingfields = JPA.em()
				.createQuery("FROM HuntingField GROUP BY name ORDER BY name").getResultList();
		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		for (HuntingField currentHuntingField : huntingfields) {
			options.put(currentHuntingField.toString(), currentHuntingField.name);
		}
		return options;

	}
}
