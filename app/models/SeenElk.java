package models;

import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

/**
 * Model for SettElg. Ku har 0 til 2 kalver ID er aar ettersom det kun skal
 * finnes et felt pr. aar
 * 
 * @author anders
 * 
 */
@Entity
public class SeenElk {
	@Id
	public Long	year;
	public int	okse;
	public int	kuIngen;
	public int	kuEn;
	public int	kuTo;
	public int	ukjent;

	@Transactional
	public void save() {

		JPA.em().persist(this);
	}

	@Transactional
	public void update() {
		// this.id = id;
		JPA.em().merge(this);
	}

	public String toTableData() {
		String table = "<td>" + getYear() + "</td>" + "<td>" + getOkse() + "</td>" + "<td>"
				+ getKuIngen() + "</td>" + "</td>" + "<td>" + getKuEn() + "</td>" + "<td>"
				+ getKuTo() + "</td>" + "<td>" + getUkjent() + "</td>";

		return table;
	}

	public String tableNorwegianhHeaders() {
		String headers = "<tr>" + "<td>År</td>" + "<td>Okse</td>" + "<td>Ku uten kalv</td>"
				+ "<td>Ku med en kalv</td>" + "<td>Ku med to kalver</td>" + "<td>Ukjent</td>"
				+ "</tr>";
		return headers;
	}

	public Long getYear() {
		return year;
	}

	public void setYear(Long year) {
		this.year = year;
	}

	public int getOkse() {
		return okse;
	}

	public void setOkse(int okse) {
		this.okse = okse;
	}

	public int getKuIngen() {
		return kuIngen;
	}

	public void setKuIngen(int kuIngen) {
		this.kuIngen = kuIngen;
	}

	public int getKuEn() {
		return kuEn;
	}

	public void setKuEn(int kuEn) {
		this.kuEn = kuEn;
	}

	public int getKuTo() {
		return kuTo;
	}

	public void setKuTo(int kuTo) {
		this.kuTo = kuTo;
	}

	public int getUkjent() {
		return ukjent;
	}

	public void setUkjent(int ukjent) {
		this.ukjent = ukjent;
	}

}