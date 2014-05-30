package models;

// import java.sql.Date;

import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Elk {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long			id;

	@Required
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	private java.util.Date	date;

	@Required
	private double			weight;

	@Required
	private double			age;

	@Required
	private int				antlers;

	@Required
	private int				veal;

	@Required
	private int				twin;

	@Required
	@ManyToOne
	private Tick			sumTick;

	@Required
	@ManyToOne
	private DeerLice		sumLice;

	@Required
	@ManyToOne
	private Sex				sex;

	@Required
	@ManyToOne
	private Area			area;

	@Required
	@ManyToOne
	private HuntingField	huntingfield;

	public static Elk findById(Long id) {
		return JPA.em().find(Elk.class, id);
	}

	@Transactional
	public void save() {
		this.sumTick = Tick.findById(sumTick.id);
		this.sumLice = DeerLice.findById(sumLice.id);
		this.sex = Sex.findById(sex.id);
		JPA.em().persist(this);
	}

	/**
	 * Update this ELk
	 */
	@Transactional
	public void update() {
		JPA.em().merge(this);
	}

	@Transactional
	public void delete() {
		JPA.em().remove(this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public java.util.Date getDate() {
		return date;
	}

	public String getStringDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat.format(date);
	}

	public void setDate(java.util.Date date) {
		this.date = date;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public double getAge() {
		return age;
	}

	public void setAge(double age) {
		this.age = age;
	}

	public int getAntlers() {
		return antlers;
	}

	public void setAntlers(int antlers) {
		this.antlers = antlers;
	}

	public int getVeal() {
		return veal;
	}

	public void setVeal(int veal) {
		this.veal = veal;
	}

	public int getTwin() {
		return twin;
	}

	public void setTwin(int twin) {
		this.twin = twin;
	}

	public Tick getSumTick() {
		return sumTick;
	}

	public void setSumTick(Tick sumTick) {
		this.sumTick = sumTick;
	}

	public DeerLice getSumLice() {
		return sumLice;
	}

	public void setSumLice(DeerLice sumLice) {
		this.sumLice = sumLice;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public HuntingField getHuntingfield() {
		return huntingfield;
	}

	public void setHuntingfield(HuntingField huntingfield) {
		this.huntingfield = huntingfield;
	}

}
