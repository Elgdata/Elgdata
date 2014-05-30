package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class UserGraph {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer				id;

	@Required
	public String				name;

	@Required
	@MaxLength(3000)
	@MinLength(20)
	@Column(length=3000)
	public String				url;

	@Required
	public String				shortInfo;

	@ManyToOne
	@Required
	public UserGraphCategory	category;

	public static UserGraph findById(Integer id) {
		return JPA.em().find(UserGraph.class, id);
	}

	@Transactional
	public void save() {
		// Check if new graph else merge
		if (this.id == null) {
			JPA.em().persist(this);
		}
		JPA.em().merge(this);
	}
	
	//@Transactional
	public void delete() {
		JPA.em().remove(this);
	}

	@Override
	public String toString() {
		return "UserGraph [id=" + id + ", name=" + name + ", url=" + url + ", shortInfo="
				+ shortInfo + ", category=" + category + "]";
	}
	
}