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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import play.db.jpa.JPA;

import javax.persistence.Query;

/**
 * HomePageStats calculates all the data for the home page.
 * 
 * thisYearList - contains data under the "Aaret 2013" panel. yearList - contains
 * year from data in weightList - average weight shotList - sum shot elk from
 * every year cohortList - sum elk from same cohort cohortYearList - respective
 * year to cohortList populationList - sum total population elk
 * populationYearList - respective year to populationList mostAntlers - contains
 * most antlers on one elk
 * 
 * @author Espen
 */
@SuppressWarnings("unchecked")
public class HomePageStatistics {
	static List<Integer>	thisYearList		= new ArrayList<>();
	static List<String>		yearList			= new ArrayList<>();
	static List<String>		weightList			= new ArrayList<>();
	static List<String>		shotList			= new ArrayList<>();
	static List<String>		cohortList			= new ArrayList<>();
	static List<String>		cohortYearList		= new ArrayList<>();
	static List<String>		populationList		= new ArrayList<>();
	static List<String>		populationYearList	= new ArrayList<>();
	static boolean			shot				= true;
	static boolean			seen				= true;
	static boolean			dataChanged			= true;

	/**
	 * All calculations and SQL queries Contains all data in one list
	 * 
	 * 
	 * */
	public static void generateStatistics() {
		if (dataChanged) {
			clearLists();
			int count = 0;
			double averagePercent = 0;
			double percent = 0;
			int shotThisYear = 0;
			int countShotElk = 0;
			int cohort;
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			int thisYear = currentYear - 1;
			int population = 0;
			int sumPopulation = 0;
			int tempHighestPopulation = 0;
			int highestPopulation = 0;
			int highestShotElk = 0;
			int tempHighestShotElk = 0;
			int yearCounter;
			List<Integer> populationYear = new ArrayList<>();

			if (seen) {
				Query seenElk = JPA.em().createQuery(
						"select(kuEn+kuTo+kuIngen+okse+ukjent) from SeenElk where year='"
								+ thisYear + "'");
				thisYearList.addAll(seenElk.getResultList());
			} else {
				thisYearList.add(0);
			}
			if (shot) {
				Query thisyearQ = JPA.em().createQuery("select max(year(date)) from Elk");
				thisYear = Integer.valueOf(String.valueOf(thisyearQ.getResultList().get(0)));
				Query shotElk = JPA.em()
						.createQuery("select count(*) from Elk group by year(date)");
				Query shotElkThisYear = JPA.em().createQuery(
						"select count(*) from Elk where year(date)=" + thisYear
								+ "group by year(date)");
				Query weightElk = JPA.em().createQuery(
						"select sum(weight) from Elk where year(date)='" + thisYear + "'");
				Query year = JPA.em().createQuery("select year(date) from Elk group by year(date)");

				shotList = shotElk.getResultList();
				List<Integer> years = year.getResultList();
				thisYearList.addAll((shotElkThisYear.getResultList()));
				thisYearList.add(round(weightElk.getResultList()));

				for (int i = 0; i < years.size(); i++) {
					Query query = JPA
							.em()
							.createQuery(
									"SELECT COUNT(*) FROM Elk WHERE year(date) - age+1 >= :year AND year(date) - age < :year AND age > 0");
					Query query2 = JPA.em().createQuery(
							"SELECT COUNT(*) FROM Elk WHERE age<=1 AND age>0 AND year(date)=:year");

					query.setParameter("year", (double) years.get(i));
					query2.setParameter("year", years.get(i));
					if (Double.valueOf(String.valueOf(query2.getResultList().get(0)))
							/ Double.valueOf(String.valueOf(query.getResultList().get(0))) > 0) {
						if (years.get(i) < thisYear) {
							countShotElk++;
							percent += Double
									.valueOf(String.valueOf(query2.getResultList().get(0)))
									/ Double.valueOf(String.valueOf(query.getResultList().get(0)));
						} else {
							shotThisYear = Integer.valueOf(String.valueOf(query2.getResultList()
									.get(0)));
						}
					}
				}

				averagePercent = percent / (double) (countShotElk);
				cohort = (int) ((double) shotThisYear / averagePercent);
				thisYearList.add(cohort);

				boolean run = true;
				yearCounter = thisYear;
				while (run) {
					Query query3 = JPA
							.em()
							.createQuery(
									"select count(*) from Elk where year(date)-age<=:year and year(date)>=:year");
					query3.setParameter("year", (double) yearCounter);
					yearCounter--;
					if (Integer.valueOf(String.valueOf(query3.getResultList().get(0))) > 0) {
						if (yearCounter > 2000 && yearCounter < 2010) {
							count++;
							populationList.add(String.valueOf(query3.getResultList().get(0)));
							populationYearList.add(String.valueOf(yearCounter));
							populationYear.add(Integer.valueOf(String.valueOf(query3
									.getResultList().get(0))));
							sumPopulation += Integer.valueOf(String.valueOf(query3.getResultList()
									.get(0)));
							if (Integer.valueOf(String.valueOf(query3.getResultList().get(0))) > tempHighestPopulation) {
								highestPopulation = yearCounter;
								tempHighestPopulation = Integer.valueOf(String.valueOf(query3
										.getResultList().get(0)));
							}
						}
					} else {
						if (count != 0) {
							population = sumPopulation / count;
						}
						thisYearList.add(population);
						thisYearList.add(highestPopulation);
						run = false;
					}
				}

				for (int i = 0; i < years.size(); i++) {
					Query shotElk1 = JPA.em().createQuery(
							"select count(*) from Elk where year(date)=:year");
					shotElk1.setParameter("year", years.get(i));
					if (Integer.valueOf(String.valueOf(shotElk1.getResultList().get(0))) > tempHighestShotElk) {
						highestShotElk = years.get(i);
						tempHighestShotElk = Integer.valueOf(String.valueOf(shotElk1
								.getResultList().get(0)));
					}
				}
				thisYearList.add(highestShotElk);

				yearList = convertToStringList(years);
				Query weightAll = JPA.em().createQuery(
						"select avg(weight) from Elk group by year(date)");
				weightList = (List<String>) roundList(weightAll.getResultList());

				int cohortYear = 2012;
				for (int i = 0; i < 10; i++) {
					Query cohort2 = JPA
							.em()
							.createQuery(
									"SELECT COUNT(*) FROM Elk WHERE year(date) - age+1 >= :year AND year(date) - age < :year AND age > 0");
					cohort2.setParameter("year", (double) cohortYear);
					cohortList.add(String.valueOf(cohort2.getResultList().get(0)));
					cohortYearList.add(String.valueOf(cohortYear));
					cohortYear--;
				}

				Query antlers = JPA.em().createQuery("SELECT MAX(antlers) FROM Elk");
				thisYearList.addAll(antlers.getResultList());

				Query antlersThisYear = JPA.em().createQuery(
						"SELECT MAX(antlers) FROM Elk where year(date)=:year");
				antlersThisYear.setParameter("year", thisYear);
				thisYearList.addAll(antlersThisYear.getResultList());

				Query biggest = JPA.em().createQuery("SELECT MAX(weight) FROM Elk");
				thisYearList.add(round(biggest.getResultList()));

				Query biggestThisYear = JPA.em().createQuery(
						"SELECT MAX(weight) FROM Elk where year(date)=:year");
				biggestThisYear.setParameter("year", thisYear);
				thisYearList.add(round(biggestThisYear.getResultList()));

				Query averageWeight = JPA.em().createQuery(
						"SELECT AVG(weight) FROM Elk where year(date)=:year");
				averageWeight.setParameter("year", thisYear);
				thisYearList.add(round(averageWeight.getResultList()));

				Query shotElkAll = JPA.em().createQuery("SELECT count(*) FROM Elk");
				thisYearList.addAll(shotElkAll.getResultList());

				Query avgWeightAll = JPA.em().createQuery("SELECT avg(weight) FROM Elk");
				thisYearList.add(round(avgWeightAll.getResultList()));

			} else {
				for (int i = 0; i < 15; i++) {
					thisYearList.add(0);
				}
				yearList.add("0");
				weightList.add("0");
				shotList.add("0");
				cohortList.add("0");
				cohortYearList.add("0");
				populationList.add("0");
				populationYearList.add("0");
			}
			dataChanged = false;
		}

	}

	/**
	 * Remove decimals from doubels
	 * 
	 * @param list
	 * @return
	 */
	public static int round(List<Double> list) {
		double d = (double) list.get(0);
		int i = (int) d;
		return i;
	}

	/**
	 * Remove decimals from values in list
	 * @param list
	 * @return
	 */
	public static ArrayList<?> roundList(List<Double> list) {
		ArrayList<Integer> list1 = new ArrayList<>();
		for(int i =0; i<list.size(); i++){
			double d = (double) list.get(i);
			list1.add((int) d);
		}		
		return list1;
	}
	
	/**
	 * Turn the list back up
	 * 
	 * @param inputList
	 * */
	public static List<String> backForward(List<String> inputList) {
		List<String> tempList = new ArrayList<>();
		List<String> list = new ArrayList<>();
		for (int i = inputList.size() - 1; i >= 0; i--) {
			tempList.add(inputList.get(i));
		}
		list = tempList;
		return list;
	}

	/**
	 * Return all data from this controller in one list
	 */
	@SuppressWarnings("unused")
	public static List<List<String>> getStats() {
		int shotTest;
		int seenTest;
		List<List<String>> allList = new ArrayList<>();
		shot = true;
		seen = true;

		try {
			Query thisyearQ = JPA.em().createQuery("select max(year(date)) from Elk");
			shotTest = Integer.valueOf(String.valueOf(thisyearQ.getResultList().get(0)));
		} catch (NumberFormatException e) {
			shot = false;
		}

		try {
			Query seenQ = JPA.em().createQuery("select max(year) from SeenElk");
			seenTest = Integer.valueOf(String.valueOf(seenQ.getResultList().get(0)));
		} catch (NumberFormatException e) {
			seen = false;
		}


		generateStatistics();

		checkStringList(yearList);
		checkStringList(shotList);
		checkStringList(weightList);

		allList.add(convertToStringList(thisYearList));
		allList.add(yearList);
		allList.add(weightList);
		allList.add(shotList);
		allList.add(backForward(populationList));
		allList.add(backForward(populationYearList));
		allList.add(backForward(cohortList));
		allList.add(backForward(cohortYearList));

		return allList;
	}

	/**
	 * Convert any list to List<String>
	 * 
	 * @param inputList
	 * */
	private static List<String> convertToStringList(List<?> inputList) {
		List<String> newList = new ArrayList<String>(inputList.size());
		try {
			for (Object myNumber : inputList) {
				newList.add(String.valueOf(myNumber));
			}

		} finally {
		}
		return newList;

	}

	/**
	 * If list is empty add zero
	 * 
	 * @param list
	 * */
	private static void checkStringList(List<String> list) {
		if (list.isEmpty()) {
			list.add("0");
		}
	}

	/**
	 * Clear list for reuse
	 */
	private static void clearLists() {
		thisYearList.clear();
		populationList.clear();
		populationYearList.clear();
		cohortList.clear();
		cohortYearList.clear();
		weightList.clear();
		shotList.clear();
		yearList.clear();
	}

}
