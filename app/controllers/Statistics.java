/* Copyright 2014 Elgdata (http://www.elgdata.no)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* package controllers;
*/
package controllers;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import models.Elk;
import models.Weather;
import models.statistics.LineGraphWeight;
import models.statistics.StackColumnElk;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
/**
 * Controller used for predefined statistics. IE not UserDefinedGraph.
 *
 */
public class Statistics extends Controller {

	/**
	 * This function queries the database and counts how many elk that is in
	 * each group. The groups are divided up by sex and age.
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result showStackedColumnGraph() {
		final int endYear;
		final int startYear;

		if (request().getQueryString("startYear") != null
				&& !request().getQueryString("startYear").isEmpty()) {
			startYear = Integer.valueOf(request().getQueryString("startYear"));
		} else {
			startYear = 2006;
		}
		if (request().getQueryString("endYear") != null
				&& !request().getQueryString("startYear").isEmpty()) {
			endYear = Integer.valueOf(request().getQueryString("endYear"));
		} else {
			endYear = 2013;
		}

		Query query = JPA.em().createQuery(
				"from Elk as elk WHERE YEAR(date) >= '" + startYear + "' AND YEAR(date) <= '"
						+ endYear + "' ORDER BY YEAR(date) ASC");

		@SuppressWarnings("unchecked")
		final List<Elk> allElk = query.getResultList();

		ArrayList<StackColumnElk> listOfStackElk = new ArrayList<StackColumnElk>();

		StackColumnElk stackColumnElk = new StackColumnElk();
		Calendar date = Calendar.getInstance();
		try {
			date.setTime(allElk.get(0).getDate());
		} catch (IndexOutOfBoundsException e) {
			// Empty database
		}

		stackColumnElk.year = String.valueOf(date.get(Calendar.YEAR));

		for (Elk currentElk : allElk) {
			Calendar currentDate = Calendar.getInstance();
			currentDate.setTime(currentElk.getDate());

			if (currentDate.get(Calendar.YEAR) != Integer.valueOf(stackColumnElk.year)) {
				// new Year
				listOfStackElk.add(stackColumnElk);
				stackColumnElk = new StackColumnElk();
				stackColumnElk.year = String.valueOf(currentDate.get(Calendar.YEAR));
			}

			if (currentElk.getSex().id == 1) {
				if (currentElk.getAge() > 1.5) {
					stackColumnElk.adultMale++;
				} else if (currentElk.getAge() == 1.5) {
					stackColumnElk.youngMale++;
				} else if (currentElk.getAge() == 0.5) {
					stackColumnElk.calfMale++;
				} else {
					stackColumnElk.unknownMale++;
				}
			} else if (currentElk.getSex().id == 2) {
				if (currentElk.getAge() > 1.5) {
					stackColumnElk.adultFemale++;
				} else if (currentElk.getAge() == 1.5) {
					stackColumnElk.youngFemale++;
				} else if (currentElk.getAge() == 0.5) {
					stackColumnElk.calfFemale++;
				} else {
					stackColumnElk.unknownFemale++;
				}
			} else {
				stackColumnElk.unknownSex++;
			}

		}
		listOfStackElk.add(stackColumnElk);

		String jsonString = String.valueOf(Json.toJson(listOfStackElk));

		return ok(views.html.statistics.stackedcolumngraph.render("Statistikk", jsonString));
	}

	/**
	 * This function queries the database and sorts out the different weights
	 * contributed by each of the sexes and their age.
	 * 
	 * @param startYear
	 * @param endYear
	 * @return
	 */

	@Transactional(readOnly = true)
	public static Result lineGraphWeight() {
		final int endYear;
		final int startYear;

		if (request().getQueryString("startYear") != null
				&& !request().getQueryString("startYear").isEmpty()) {
			startYear = Integer.valueOf(request().getQueryString("startYear"));
		} else {
			startYear = 2006;
		}
		if (request().getQueryString("endYear") != null
				&& !request().getQueryString("startYear").isEmpty()) {
			endYear = Integer.valueOf(request().getQueryString("endYear"));
		} else {
			endYear = 2013;
		}

		Query query = JPA.em().createQuery(
				"from Elk as elk WHERE YEAR(date) >= '" + startYear + "' AND YEAR(date) <= '"
						+ endYear + "' ORDER BY YEAR(date) ASC");
		@SuppressWarnings("unchecked")
		final List<Elk> allElk = query.getResultList();

		ArrayList<LineGraphWeight> listOfLineElk = new ArrayList<LineGraphWeight>();
		StackColumnElk stackColumnElk = new StackColumnElk();
		LineGraphWeight lineGraphWeight = new LineGraphWeight();// lager object
																// av klassen
		Calendar date = Calendar.getInstance();

		try {
			date.setTime(allElk.get(0).getDate());
		} catch (IndexOutOfBoundsException e) {
			// Empty database
		}

		lineGraphWeight.year = String.valueOf(date.get(Calendar.YEAR));

		for (Elk currentElk : allElk) {
			Calendar currentDate = Calendar.getInstance();
			currentDate.setTime(currentElk.getDate());

			if (currentDate.get(Calendar.YEAR) != Integer.valueOf(lineGraphWeight.year)) {
				// new Year

				LineGraphWeight lgwCalculated = averageWeightCalc(lineGraphWeight, stackColumnElk);
				listOfLineElk.add(lgwCalculated);
				lineGraphWeight = new LineGraphWeight();
				lineGraphWeight.year = String.valueOf(currentDate.get(Calendar.YEAR));

			}
			if (currentElk.getWeight() > 0) {
				// If male
				if (currentElk.getSex().id == 1) {
					// if adult
					if (currentElk.getAge() > 1.5) {
						stackColumnElk.adultMale++;
						lineGraphWeight.adultMaleWeight += currentElk.getWeight();
					}
					// else if young
					else if (currentElk.getAge() == 1.5) {
						stackColumnElk.youngMale++;
						lineGraphWeight.youngMaleWeight += currentElk.getWeight();

					}
					// else calf
					else if (currentElk.getAge() == 0.5) {
						lineGraphWeight.calfMaleWeight += currentElk.getWeight();
						stackColumnElk.calfMale++;
					} else {
						// unknownMaleWeight
						lineGraphWeight.unknownMaleWeight += currentElk.getWeight();
						stackColumnElk.unknownMale++;
					}

				} else if (currentElk.getSex().id == 2) {
					if (currentElk.getAge() > 1.5) {
						lineGraphWeight.adultFemaleWeight += currentElk.getWeight();
						stackColumnElk.adultFemale++;
					}

					else if (currentElk.getAge() == 1.5) {
						lineGraphWeight.youngFemaleWeight += currentElk.getWeight();
						stackColumnElk.youngFemale++;
					}

					else if (currentElk.getAge() == 0.5) {
						lineGraphWeight.calfFemaleWeight += currentElk.getWeight();
						stackColumnElk.calfFemale++;
					} else {
						lineGraphWeight.unknownFemaleWeight += currentElk.getWeight();
						stackColumnElk.unknownFemale++;
					}
				}

				if (currentElk.getAge() > 1.5) {
					stackColumnElk.adultCount++;
					lineGraphWeight.averageWeightAdult += currentElk.getWeight();
				} else if (currentElk.getAge() == 1.5) {
					stackColumnElk.youngCount++;
					lineGraphWeight.averageWeightYoung += currentElk.getWeight();
				} else if (currentElk.getAge() == 0.5) {
					stackColumnElk.calfCount++;
					lineGraphWeight.averageWeightCalf += currentElk.getWeight();
				}

				lineGraphWeight.sumWeight += currentElk.getWeight();
			}

		}

		LineGraphWeight lgwCalculated = averageWeightCalc(lineGraphWeight, stackColumnElk);
		listOfLineElk.add(lgwCalculated);

		String jsonString = String.valueOf(Json.toJson(listOfLineElk));

		return ok(views.html.statistics.linegraph.render("Linjegraf", jsonString));

	}

	/**
	 * This function calculates the average weight for each of the sexes and age
	 * groups. This function is called by lineGraphWeight.
	 */

	public static LineGraphWeight averageWeightCalc(LineGraphWeight lineGraphWeight,
			StackColumnElk stackColumnElk) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		nf.setRoundingMode(RoundingMode.HALF_UP);
		if (stackColumnElk.adultMale > 0) {
			lineGraphWeight.adultMaleWeight = Double.valueOf(nf.format(
					lineGraphWeight.adultMaleWeight / stackColumnElk.adultMale).replace(",", "."));
		}
		if (stackColumnElk.youngMale > 0) {
			lineGraphWeight.youngMaleWeight = Double.valueOf(nf.format(
					lineGraphWeight.youngMaleWeight / stackColumnElk.youngMale).replace(",", "."));
		}
		if (stackColumnElk.calfMale > 0) {
			lineGraphWeight.calfMaleWeight = Double.valueOf(nf.format(
					lineGraphWeight.calfMaleWeight / stackColumnElk.calfMale).replace(",", "."));
		}
		if (stackColumnElk.adultFemale > 0) {
			lineGraphWeight.adultFemaleWeight = Double.valueOf(nf.format(
					lineGraphWeight.adultFemaleWeight / stackColumnElk.adultFemale).replace(",",
					"."));
		}
		if (stackColumnElk.youngFemale > 0) {
			lineGraphWeight.youngFemaleWeight = Double.valueOf(nf.format(
					lineGraphWeight.youngFemaleWeight / stackColumnElk.youngFemale).replace(",",
					"."));
		}
		if (stackColumnElk.calfFemale > 0) {
			lineGraphWeight.calfFemaleWeight = Double
					.valueOf(nf
							.format(lineGraphWeight.calfFemaleWeight / stackColumnElk.calfFemale)
							.replace(",", "."));
		}
		if (stackColumnElk.unknownFemale > 0) {
			lineGraphWeight.unknownFemaleWeight = Double.valueOf(nf.format(
					lineGraphWeight.unknownFemaleWeight / stackColumnElk.unknownFemale).replace(
					",", "."));
		}
		if (stackColumnElk.unknownMale > 0) {
			lineGraphWeight.unknownMaleWeight = Double.valueOf(nf.format(
					lineGraphWeight.unknownMaleWeight / stackColumnElk.unknownMale).replace(",",
					"."));
		}
		if (stackColumnElk.adultCount > 0) {
			lineGraphWeight.averageWeightAdult = Double.valueOf(nf.format(
					lineGraphWeight.averageWeightAdult / stackColumnElk.adultCount).replace(",",
					"."));
		}
		if (stackColumnElk.youngCount > 0) {
			lineGraphWeight.averageWeightYoung = Double.valueOf(nf.format(
					lineGraphWeight.averageWeightYoung / stackColumnElk.youngCount).replace(",",
					"."));
		}
		if (stackColumnElk.calfCount > 0) {
			lineGraphWeight.averageWeightCalf = Double
					.valueOf(nf
							.format(lineGraphWeight.averageWeightCalf / stackColumnElk.calfCount)
							.replace(",", "."));
		}
		stackColumnElk.adultMale = 0;
		stackColumnElk.adultFemale = 0;
		stackColumnElk.youngMale = 0;
		stackColumnElk.youngFemale = 0;
		stackColumnElk.calfMale = 0;
		stackColumnElk.calfFemale = 0;
		stackColumnElk.unknownFemale = 0;
		stackColumnElk.unknownMale = 0;
		stackColumnElk.adultCount = 0;
		stackColumnElk.youngCount = 0;
		stackColumnElk.calfCount = 0;
		return lineGraphWeight;
	}

	public static List<Long> elkByMonth() {

		Query query = JPA
				.em()
				.createQuery(
						"SELECT COUNT(*) from Elk WHERE date >= '2012-01-01' AND date < '2013-01-01' GROUP BY month(date)");
		@SuppressWarnings("unchecked")
		final List<Long> count = query.getResultList();

		return count;
	}

	/**
	 * Graph that shows the temperature and snow depth for a given date from a
	 * range of startYear - endYear.
	 * 
	 * @param startYear
	 * @param endYear
	 * @return Result
	 */
	@Transactional(readOnly = true)
	public static Result weatherGraph() {

		final int startYear;
		final int endYear;

		if (request().getQueryString("startYear") != null
				&& !request().getQueryString("startYear").isEmpty()) {
			startYear = Integer.valueOf(request().getQueryString("startYear"));
		} else {
			startYear = 2010;
		}
		if (request().getQueryString("endYear") != null
				&& !request().getQueryString("endYear").isEmpty()) {
			endYear = Integer.valueOf(request().getQueryString("endYear"));
		} else {
			endYear = 2013;
		}

		Query query = JPA.em().createQuery(
				"FROM Weather WHERE year(id) >= :fromYear AND year(id) < :toYear ORDER BY id ASC");

		query.setParameter("fromYear", startYear);
		query.setParameter("toYear", endYear);
		@SuppressWarnings("unchecked")
		final List<Weather> weatherList = query.getResultList();

		String jsonString = String.valueOf(Json.toJson(weatherList));
		return ok(views.html.statistics.WeatherGraph.render("Vær", jsonString));
	}

	/**
	 * Cohort for Elk born on start year startYear = 2000 ==> Elk age 0 in 2000.
	 * Elk age 1 in 2001 etc.
	 * 
	 * @param startYear
	 *            []
	 * @param endYear
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result showCohortWeight() {
		return ok(views.html.statistics.cohort.render("Kohort - slaktevekt"));

	}

	/**
	 * Returns the average weight of all Elk born in 'birthYear' with the
	 * specified age.
	 * 
	 * year(date) - e.age ==> Birthyear
	 * 
	 * @param birthYear
	 * @param age
	 */
	public static Double getCohortFor(final Integer birthYear, final Integer age) {

		Query query = JPA
				.em()
				.createQuery(
						"SELECT AVG(weight) FROM Elk AS e WHERE year(date) - e.age + 0.5 >= :birthYear AND year(date) - e.age < :birthYear AND e.age >= :minAge AND e.age <= :maxAge AND Weight > 0 AND e.age > 0");
		query.setParameter("birthYear", Double.valueOf(birthYear));
		query.setParameter("birthYear", Double.valueOf(birthYear));
		query.setParameter("minAge", Double.valueOf(age));
		query.setParameter("maxAge", Double.valueOf(age + 1));
		Double retValue = (Double) query.getSingleResult();
		return retValue;
	}

	/**
	 * Generates a graph string to be used in the View by amCharts to generate a
	 * graph.
	 * 
	 * @param valueField
	 * @return graphString
	 */
	public static String generateGraphString(String valueField) {
		// graph.title = \"2001\";
		final String ret = "var graph = new AmCharts.AmGraph(); graph.title = ' "
				+ valueField
				+ "'; graph.lineColor = ' "
				+ getNextColor()
				+ "'; graph.valueField = '"
				+ valueField
				+ "'; graph.bullet = 'round'; graph.bulletSize = 10; graph.type = 'line'; graph.lineThickness = 3; chart.addGraph(graph);";

		return ret;
	}

	/**
	 * colorindex is used to keep track of which colorlist is to be used next in
	 * getNextColor
	 */
	public static int					colorIndex	= 0;
	public static final List<String>	colorList	= new ArrayList<String>();

	/**
	 * Used by 'generateGraphString' to aquire an eye appealing colors
	 * 
	 * @return Color Hex code
	 */
	public static String getNextColor() {

		if (colorList.size() == 0) {
			colorList.add("#df0000"); // RED
			colorList.add("#079400"); // GREEN
			colorList.add("#004eff"); // BLUE
			colorList.add("#be008c"); // DARK PINK
			colorList.add("#00ace6"); // LIGTH BLUE
			colorList.add("#e57300"); // ORANGE
			colorList.add("#e4cd32"); // GOLD YELLOW
			colorList.add("#8400ff"); // PURPLE
			colorList.add("#545498"); // LIGTH PURPLE
			colorList.add("#a86eaa"); // PINK
			colorList.add("#000000"); // BLACK
			colorList.add("#ffffff"); // WHITE
		}
		if (colorIndex > 11) {
			colorIndex = 0;
		}

		try {
			return colorList.get(colorIndex++);
		} catch (IndexOutOfBoundsException e) {
			colorIndex = 0;
			return colorList.get(colorIndex++);
		}
	}

	@Transactional(readOnly = true)
	public static Result newCohortWeight(String years) {
		String[] list;
		list = years.split(",", -1);
		List<Integer> selectedYearsList = new ArrayList<Integer>();
		for (String year : list) {
			selectedYearsList.add(Integer.valueOf(year));
		}
		Integer minAge = 0;
		Integer maxAge = 20;

		String jsonString = "{\"chartData\" :[";
		for (Integer age = minAge; age < maxAge; age++) {
			jsonString += "{ \"age\" : \"" + age + "\" , ";
			for (Integer currentYear : selectedYearsList) {
				Double cohort = getCohortFor(currentYear, age);
				try {
					if (cohort != null) {
						jsonString += "\"" + currentYear + "\" : \"" + cohort + "\" , ";

					}
				} catch (NullPointerException e) {
					// No value in database
					jsonString += " , ";
				}
			}
			// Remove trailing Comma
			jsonString = jsonString.substring(0, jsonString.length() - 2);
			jsonString += "} ,";
		}
		jsonString = jsonString.substring(0, jsonString.length() - 1);
		jsonString += "]};";
		return ok(String.valueOf(Json.parse(jsonString)));
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public static Result getPopulation() {

		final int endYear;
		final int startYear;

		if (request().getQueryString("startYear") != null
				&& !request().getQueryString("startYear").isEmpty()) {
			startYear = Integer.valueOf(request().getQueryString("startYear"));
		} else {
			startYear = 2006;
		}
		if (request().getQueryString("endYear") != null
				&& !request().getQueryString("startYear").isEmpty()) {
			endYear = Integer.valueOf(request().getQueryString("endYear"));
		} else {
			endYear = 2013;
		}

		List<List<String>> list = new ArrayList<>();
		List<String> populationList = new ArrayList<>();
		List<String> populationVealList = new ArrayList<>();
		List<String> populationMaleList = new ArrayList<>();
		List<String> populationFemaleList = new ArrayList<>();
		List<String> populationYoungList = new ArrayList<>();
		List<String> populationOldList = new ArrayList<>();
		List<String> populationYears = new ArrayList<>();
		List<String> populationColors = new ArrayList<>();
		List<String> populationVealMaleList = new ArrayList<>();
		List<String> populationVealFemaleList = new ArrayList<>();
		List<String> populationYoungMaleList = new ArrayList<>();
		List<String> populationYoungFemaleList = new ArrayList<>();
		List<String> populationOldMaleList = new ArrayList<>();
		List<String> populationOldFemaleList = new ArrayList<>();

		for (int i = 0; i < 12; i++) {
			populationColors.add(getNextColor());
		}

		for (int year = startYear; year <= endYear; year++) {
			populationYears.add(Integer.toString(year));// Adding years for
														// X-axis.

			Query population = JPA.em().createQuery(
					"select count(*) from Elk where year(date)-age<=:year and year(date)>=:year");
			population.setParameter("year", (double) year);
			populationList.addAll(population.getResultList());

			Query populationVeal = JPA
					.em()
					.createQuery(
							"select count(*) from Elk where year(date)-age<=:year and year(date)-age>=:year-1 and year(date)>=:year");
			populationVeal.setParameter("year", (double) year);
			populationVealList.addAll(populationVeal.getResultList());

			Query populationVealMale = JPA
					.em()
					.createQuery(
							"select count(*) from Elk where year(date)-age<=:year and year(date)-age>=:year-1 and year(date)>=:year and sex_id=1");
			populationVealMale.setParameter("year", (double) year);
			populationVealMaleList.addAll(populationVealMale.getResultList());

			Query populationVealFemale = JPA
					.em()
					.createQuery(
							"select count(*) from Elk where year(date)-age<=:year and year(date)-age>=:year-1 and year(date)>=:year and sex_id=2");
			populationVealFemale.setParameter("year", (double) year);
			populationVealFemaleList.addAll(populationVealFemale.getResultList());

			Query populationMale = JPA
					.em()
					.createQuery(
							"select count(*) from Elk where year(date)-age<=:year and year(date)>=:year and sex_id=1");
			populationMale.setParameter("year", (double) year);
			populationMaleList.addAll(populationMale.getResultList());

			Query populationFemale = JPA
					.em()
					.createQuery(
							"select count(*) from Elk where year(date)-age<=:year and year(date)>=:year and sex_id=2");
			populationFemale.setParameter("year", (double) year);
			populationFemaleList.addAll(populationFemale.getResultList());

			Query populationYoung = JPA
					.em()
					.createQuery(
							"select count(*) from Elk where year(date)-age<:year-1.0 and year(date)-age>:year-2.0 and year(date)>=:year");
			populationYoung.setParameter("year", (double) year);
			populationYoungList.addAll(populationYoung.getResultList());

			Query populationYoungMale = JPA
					.em()
					.createQuery(
							"select count(*) from Elk where year(date)-age<:year-1.0 and year(date)-age>:year-2.0 and year(date)>=:year and sex_id=1");
			populationYoungMale.setParameter("year", (double) year);
			populationYoungMaleList.addAll(populationYoungMale.getResultList());

			Query populationYoungFemale = JPA
					.em()
					.createQuery(
							"select count(*) from Elk where year(date)-age<:year-1.0 and year(date)-age>:year-2.0 and year(date)>=:year and sex_id=2");
			populationYoungFemale.setParameter("year", (double) year);
			populationYoungFemaleList.addAll(populationYoungFemale.getResultList());

			Query populationOld = JPA
					.em()
					.createQuery(
							"select count(*) from Elk where year(date)-age<:year-2.0 and year(date)>=:year");
			populationOld.setParameter("year", (double) year);
			populationOldList.addAll(populationOld.getResultList());

			Query populationOldMale = JPA
					.em()
					.createQuery(
							"select count(*) from Elk where year(date)-age<:year-2.0 and year(date)>=:year and sex_id=1");
			populationOldMale.setParameter("year", (double) year);
			populationOldMaleList.addAll(populationOldMale.getResultList());

			Query populationOldFemale = JPA
					.em()
					.createQuery(
							"select count(*) from Elk where year(date)-age<:year-2.0 and year(date)>=:year and sex_id=2");
			populationOldFemale.setParameter("year", (double) year);
			populationOldFemaleList.addAll(populationOldFemale.getResultList());
		}
		List<String> populationNames = new ArrayList<>();

		populationNames.add("Total bestand");
		populationNames.add("Okse");
		populationNames.add("Ku");
		populationNames.add("Kalv");
		populationNames.add("Kalv (okse)");
		populationNames.add("Kalv (ku)");
		populationNames.add("Ungdyr");
		populationNames.add("Ungdyr (okse)");
		populationNames.add("Ungdyr (ku)");
		populationNames.add("Eldre");
		populationNames.add("Eldre (okse)");
		populationNames.add("Eldre (ku)");

		list.add(populationYears);
		list.add(populationList);
		list.add(populationMaleList);
		list.add(populationFemaleList);
		list.add(populationOldList);
		list.add(populationOldMaleList);
		list.add(populationOldFemaleList);
		list.add(populationYoungList);
		list.add(populationYoungMaleList);
		list.add(populationYoungFemaleList);
		list.add(populationVealList);
		list.add(populationVealMaleList);
		list.add(populationVealFemaleList);

		return ok(views.html.statistics.Population.render("Bestand", list, populationColors,
				populationNames));
	}
}
