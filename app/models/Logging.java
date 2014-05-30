package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Logging {

	@Id
	@Required
	public Integer	id;
	
	@Required
	public Integer	logging;
	
	@Required
	public Integer	clearance;
	
	@Required
	public Integer	grazing;

	public static Logging findById(Integer id) {
		return JPA.em().find(Logging.class, id);
	}

	@Transactional
	public void save() {
		JPA.em().persist(this);
	}
	
	@Transactional
	public void delete() {
		JPA.em().remove(this);
	}
}
