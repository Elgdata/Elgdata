package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class UserGraphCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer	id;

	@Required
	public String	name;

	public static UserGraphCategory findById(String CategoryName) {
		return JPA.em().find(UserGraphCategory.class, CategoryName);
	}

	@Transactional
	public void save() {
		JPA.em().persist(this);
	}
	
	public static UserGraphCategory findById(Integer id) {
		return JPA.em().find(UserGraphCategory.class, id);
	}

	public static Map<String, String> options() {
		@SuppressWarnings("unchecked")
		List<UserGraphCategory> categories = JPA.em()
				.createQuery("FROM UserGraphCategory GROUP BY name ORDER BY name").getResultList();
		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		for (UserGraphCategory c : categories) {
			options.put(c.id.toString(), c.name);
		}
		return options;
	}
	
	//@Transactional
	public void delete() {
		JPA.em().remove(this);
	}
}