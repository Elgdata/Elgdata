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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import models.DeerLice;
import models.HuntingField;
import models.Sex;
import models.Tick;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import akka.DbExecutionContext;

/**
 * UserDefinedGraph is used to allow users to create their own queries and
 * generate the corresponding grap First it gets parameters from the URL that
 * contains what the user wants to see. Then translates that to SQL staments.
 * Afterwards it does calculations and finally it returns the results to the view.
 * 
 * @author Thomas Bakken
 */

public class UserDefinedGraph extends Controller {
	/**
	 * Returns the Edit-page in the view
	 * 
	 * @return OK
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public static Result edit() {
		List<HuntingField> listHuntingField = JPA.em()
				.createQuery("FROM HuntingField GROUP BY name ORDER BY name").getResultList();
		List<Sex> listSex = JPA.em().createQuery("From Sex").getResultList();
		List<Tick> listTick = JPA.em().createQuery("from Tick").getResultList();
		List<DeerLice> listDeerLice = JPA.em().createQuery("from DeerLice").getResultList();

		return ok(views.html.userdefinedgraph.Edit.render("Rediger graf", listHuntingField,
				listSex, listTick, listDeerLice));
	}

	/**
	 * Returns the graph(s) in the view
	 * 
	 * @param totalAmountForms
	 * @param contWeather
	 * @param countLogging
	 * @return OK
	 */
	@Transactional
	public static Promise<Result> showUserDefinedGraph(final Integer totalAmountForms,
			final Integer inputCountWeather, final Integer countLogging) {
		final Map<String, String[]> urlParameters = getParameters();
		return Promise.promise(new Function0<Result>() {
			@Override
			public Result apply() throws Throwable {
				// Http.Context.current.remove();
				return JPA.withTransaction(new Function0<Result>() {
					@Override
					public Result apply() throws Throwable {
						try {
							Integer countWeather = inputCountWeather;
							List<List<String>> finalXGraphs = new ArrayList<List<String>>();
							List<List<String>> finalYGraphs = new ArrayList<List<String>>();
							List<List<String>> finalStdGraphs = new ArrayList<List<String>>();
							List<Integer> stdIndex = new ArrayList<Integer>();
							Map<Integer, String> standardDeviationBool = new HashMap<Integer, String>();
							List<String> colorList = new ArrayList<String>();

							Integer standardDeviation = 0;

							if (totalAmountForms != 0) {
								for (int currentForm = 0; currentForm < totalAmountForms; currentForm++) {

									List<String> xAxisGraph = convertToStringList(xAxisGraph(
											convertParametersToSQL(currentForm, urlParameters),
											currentForm, totalAmountForms));

									List<String> yAxisGraph = convertToStringList(yAxisGraph(
											convertParametersToSQL(currentForm, urlParameters),
											currentForm, totalAmountForms));

									finalXGraphs.add(xAxisGraph);
									if (urlParameters.containsKey("dev" + (currentForm + 1))) {
										finalYGraphs.add(yAxisGraph.subList(0,
												yAxisGraph.size() / 3));

										List<String> upper = new ArrayList<String>(yAxisGraph
												.subList(yAxisGraph.size() / 3,
														(yAxisGraph.size() / 3) * 2));
										List<String> lower = new ArrayList<String>(yAxisGraph
												.subList((yAxisGraph.size() / 3) * 2,
														yAxisGraph.size()));

										finalStdGraphs.add(upper);
										finalStdGraphs.add(lower);
										stdIndex.add(currentForm, standardDeviation);
										standardDeviation += 2;
										standardDeviationBool.put(currentForm, "true");

									} else {
										finalYGraphs.add(yAxisGraph);
										standardDeviationBool.put(currentForm, "false");
										stdIndex.add(currentForm, 0);
									}
									colorList.add(Statistics.getNextColor());
								}
							}
							// getting the weather-graphs
							if (countWeather != 0) {
								for (int currentWeather = 0; currentWeather < countWeather; currentWeather++) {
									List<String> currentYWeatherGraph = convertToStringList(weatherGraph(
											convertParametersToSQL(currentWeather, urlParameters),
											currentWeather, "count"));

									List<String> currentXWeatherGraph = convertToStringList(weatherGraph(
											convertParametersToSQL(currentWeather, urlParameters),
											currentWeather, "year"));

									standardDeviationBool.put(totalAmountForms + currentWeather,
											"weather");
									stdIndex.add(totalAmountForms + currentWeather, 0);
									colorList.add(Statistics.getNextColor());
									finalYGraphs.add(currentYWeatherGraph);
									finalXGraphs.add(currentXWeatherGraph);
									// adding a X-value to pair with sum of
									// weatherGraphs.
									if ((currentWeather == countWeather - 1) && (countWeather > 1))
										finalXGraphs.add(currentXWeatherGraph);
								}
								// add sum of all weathergraphs and put in graph
								if (countWeather > 1) {
									calculateSumOfGraphs("weather", finalYGraphs,
											standardDeviationBool);
									standardDeviationBool.put(standardDeviationBool.size(),
											"weatherSum");
									stdIndex.add(totalAmountForms + countWeather, 0);
									colorList.add(Statistics.getNextColor());
									countWeather++;
								}
							}
							// getting the logging-graphs
							if (countLogging != 0) {
								for (int currentLogging = 0; currentLogging < countLogging; currentLogging++) {
									List<String> currentYLoggingGraph = convertToStringList(loggingGraph(
											convertParametersToSQL(currentLogging, urlParameters),
											currentLogging, "Y"));

									List<String> currentXLoggingGraph = convertToStringList(loggingGraph(
											convertParametersToSQL(currentLogging, urlParameters),
											currentLogging, "X"));

									finalXGraphs.add(currentXLoggingGraph);

									standardDeviationBool.put(totalAmountForms + countWeather
											+ currentLogging, "logging");
									stdIndex.add(
											(totalAmountForms + countWeather + currentLogging), 0);
									colorList.add(Statistics.getNextColor());
									finalYGraphs.add(currentYLoggingGraph);

								}
							}
							compareDataValues(finalXGraphs, finalYGraphs, finalStdGraphs);
							List<String> graphNames = getGraphNames(urlParameters,
									standardDeviationBool, standardDeviation);
							List<String> yValues = getYValues(urlParameters, standardDeviationBool,
									totalAmountForms + countWeather + countLogging);

							return ok(views.html.userdefinedgraph.Show.render("Statistikk",
									totalAmountForms, standardDeviation, countWeather,
									countLogging, colorList, graphNames, yValues,
									standardDeviationBool, finalStdGraphs, finalXGraphs,
									finalYGraphs, stdIndex));
						} catch (Exception e) {
							return redirect(routes.UserDefinedGraph.edit());
						}
					}

				});
			}
		}, DbExecutionContext.ctx);

	}

	/**
	 * This method retrieves the parameters from the URL and puts it in a map
	 * 
	 * @return parameters
	 */
	public static Map<String, String[]> getParameters() {
		final Set<Map.Entry<String, String[]>> entries = request().queryString().entrySet();
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		for (Map.Entry<String, String[]> entry : entries) {
			final String key = entry.getKey();
			String[] value = entry.getValue();
			parameters.put(key, value);
		}
		String[] data = new String[1];
		data[0] = "År";
		parameters.put("xAxis", data);
		return parameters;
	}

	/**
	 * This method gets the graphNames and put it in a list
	 * 
	 * @param parameters
	 * @param bool
	 * @param std
	 * @return graphNames
	 */
	public static List<String> getGraphNames(Map<String, String[]> parameters,
			Map<Integer, String> bool, Integer std) {
		List<String> graphNames = new ArrayList<String>();
		if (!(parameters.get("graphName") == (null))) {
			for (int i = 0; i < parameters.get("graphName").length; i++) {
				String name = "Linje " + (i + 1) + ": " + parameters.get("graphName")[i];
				if (parameters.get("graphName")[i].equals("")) {
					name = "Linje " + (i + 1) + ": " + parameters.get("yAxis")[i];
				}
				graphNames.add(name);
			}
		}
		if (!(parameters.get("weatherCondition") == (null))) {
			for (int i = 0; i < parameters.get("weatherCondition").length; i++) {
				graphNames.add("Værdata " + (i + 1));
			}
			if (parameters.get("weatherCondition").length > 1)
				graphNames.add("Sum Værdata");
		}
		if (!(parameters.get("logging") == (null))) {
			for (int i = 0; i < parameters.get("logging").length; i++) {
				graphNames.add(parameters.get("logging")[i]);
			}
		}
		return graphNames;
	}

	/**
	 * This method gets the yParameter and show associated valueAxis in
	 * view.Edit()
	 * 
	 * @param parameters
	 * @param bool
	 * @return yValues
	 */
	public static List<String> getYValues(Map<String, String[]> parameters,
			Map<Integer, String> bool, Integer total) {
		List<String> yValues = new ArrayList<String>();

		if (!(parameters.get("graphName") == (null))) {
			for (int i = 0; i < parameters.get("yAxis").length; i++) {
				String yAxis = parameters.get("yAxis")[i];
				if (yAxis.equals("Alder"))
					yValues.add("age");
				else if (yAxis.equals("Vekt"))
					yValues.add("weight");
				else if (yAxis.equals("SettElg"))
					yValues.add("weight");
				else if (yAxis.equals("Bestand"))
					yValues.add("weight");
			}
		}
		if (!(parameters.get("weatherCondition") == (null))) {
			for (int i = 0; i < parameters.get("weatherCondition").length; i++) {
				yValues.add("weather");
			}
			if (parameters.get("weatherCondition").length > 1)
				yValues.add("weather");
		}
		if (!(parameters.get("logging") == (null))) {
			for (int i = 0; i < parameters.get("logging").length; i++) {
				yValues.add("logging");
			}
		}
		for (int i = 0; i < parameters.get("yAxis").length; i++) {
			String yAxis = parameters.get("yAxis")[i];
			if (bool.get(i).equals("true")) {
				if (yAxis.equals("Alder")) {
					yValues.add("age");
					yValues.add("age");
				} else if (yAxis.equals("Vekt")) {
					yValues.add("weight");
					yValues.add("weight");
				}
			}
		}
		return yValues;
	}

	/**
	 * This method converts the parameters from view over to DBformat
	 * 
	 * @param currentForm
	 * @param parameters
	 * @return sqlQueries
	 */
	public static Map<String, List<String>> convertParametersToSQL(Integer currentForm,
			Map<String, String[]> parameters) {

		Map<String, List<String>> sqlQueries = new HashMap<String, List<String>>();

		for (String key : parameters.keySet()) {
			List<String> translatedValues = new ArrayList<String>();

			String[] value = parameters.get(key);
			for (String x : value) {
				switch (x) {
					case "År":
						if (currentForm < parameters.get("yAxis").length) {
							if (parameters.get("yAxis")[currentForm].equals("SettElg"))
								x = "year";
							else
								x = "year(date)";
						} else
							x = "year(date)";
						break;
					case "Tagger":
						x = "antlers";
						break;
					case "Vekt":
						x = "weight";
						break;
					case "Alder":
						x = "age";
						break;
					case "Kjønn":
						x = "sex_id";
						break;
					case "Tvilling":
						x = "twin";
						break;
					case "Kalv":
						x = "veal";
						break;
					case "Område":
						x = "huntingfield_id";
						break;
					case "Eq":
						x = "=";
						break;
					case "Neq":
						x = "!=";
						break;
					case "Gt":
						x = ">";
						break;
					case "Lt":
						x = "<";
						break;
					case "Stigende":
						x = "ASC";
						break;
					case "Synkende":
						x = "DESC";
						break;
					case "Flått":
						x = "sumTick_id";
						break;
					case "Hjortelus":
						x = "sumLice_id";
						break;
					case "":
						x = "not present";
						break;
					case "avg":
						x = "average";
						break;
					case "dev":
						x = "standard deviation";
						break;
					case "med":
						x = "median";
						break;
					case "Temperatur":
						x = "temperature";
						break;
					case "Snødybde":
						x = "snowdepth";
						break;
					case "Hogstdata":
						x = "logging";
						break;
					case "Ryddedata":
						x = "clearance";
						break;
					case "Beitedata":
						x = "grazing";
						break;
				}
				translatedValues.add(x);
			}
			sqlQueries.put(key, translatedValues);
		}
		return sqlQueries;
	}

	/**
	 * This method calculate the sum of the graphs chosen to be viewed.
	 * 
	 * @param graph
	 * @param yList
	 * @param bool
	 */
	public static void calculateSumOfGraphs(String graph, List<List<String>> yList,
			Map<Integer, String> bool) {
		List<String> container = new ArrayList<String>();
		if (graph == "weather") {
			for (int i = 0; i < yList.size(); i++) {
				if (bool.get(i).equals("weather")) {
					if (!(container.isEmpty())) {
						for (int j = 0; j < yList.get(i).size(); j++) {
							int oldValue = Integer.parseInt(container.get(j));
							int newValue = Integer.parseInt(yList.get(i).get(j));
							int sumValue = (oldValue + newValue);
							container.set(j, Integer.toString(sumValue));
						}
					} else
						container.addAll(yList.get(i));
				}
			}
		}
		yList.add(container);
	}

	/**
	 * This method makes an formula edited by user in editUserDefinedGraph
	 * 
	 * @param toBeCalculated
	 * @return container
	 */
	public static List<Integer> calculateFormulaOnGraphs(List<List<Integer>> toBeCalculated,
			Map<String, List<String>> translatedSQL, Integer currentLogging) {
		List<Integer> container = new ArrayList<Integer>();
		for (int i = 0; i < toBeCalculated.get(0).size(); i++) {
			int sumLogging = 0;
			int sumClearance = 0;
			if (i - (Integer.parseInt(translatedSQL.get("yearLogging").get(currentLogging)) - 1) > 0) {
				for (int j = 0; j < Integer.parseInt(translatedSQL.get("yearLogging").get(
						currentLogging)); j++) {
					sumLogging += toBeCalculated.get(0).get(i - j);
				}
				for (int j = 0; j < Integer.parseInt(translatedSQL.get("yearClearance").get(
						currentLogging)); j++) {
					sumClearance += toBeCalculated.get(1).get(i - j);
				}
			}

			container.add(sumLogging
					+ (sumClearance * Integer.parseInt(translatedSQL.get("loggingMultiplier").get(
							currentLogging))));
		}
		return container;
	}

	/**
	 * This method make SQLcommands and receive a list in return for the
	 * logging-axis
	 * 
	 * @param translatedSQL
	 * @param currentLogging
	 * @param axis
	 * @return loggingGraph
	 */
	@SuppressWarnings("unchecked")
	public static List<?> loggingGraph(Map<String, List<String>> translatedSQL,
			Integer currentLogging, String axis) {
		String stringQuery = "";
		if ((translatedSQL.get("logging").get(currentLogging).equals("Formel"))
				&& (axis.equals("Y"))) {
			Query query = JPA.em().createQuery("SELECT logging FROM Logging");
			List<List<Integer>> loggingToBeCalculated = new ArrayList<List<Integer>>();
			loggingToBeCalculated.add(query.getResultList());
			query = JPA.em().createQuery("SELECT clearance FROM Logging");
			loggingToBeCalculated.add(query.getResultList());
			final List<?> loggingGraph = calculateFormulaOnGraphs(loggingToBeCalculated,
					translatedSQL, currentLogging);
			return loggingGraph;
		} else {
			String where = "";
			String groupBy = "";
			String orderBy = "";
			if (axis.equals("X"))
				stringQuery = "SELECT id FROM Logging" + " ";
			else {
				stringQuery = "SELECT " + translatedSQL.get("logging").get(currentLogging)
						+ " FROM Logging" + " ";

			}
			if (translatedSQL.get("xAxisCondition").get(0).equals("year")) {
				where = "WHERE " + "id" + translatedSQL.get("operatorsX").get(0) + "'"
						+ translatedSQL.get("textAreaX").get(0) + "'" + " ";
			} else

			if (!(translatedSQL.get("xAxis").get(0).equals("not present"))) {
				groupBy = "GROUP BY " + "id" + " ";
				orderBy = "ORDER BY " + "id" + " " + translatedSQL.get("orderByParam").get(0) + " ";
			}

			List<String> loggingKeys = new ArrayList<String>();
			loggingKeys.add("xAxisCondition");
			loggingKeys.add("xAxis");
			loggingKeys.add("orderBy");

			for (int j = 0; j < loggingKeys.size(); j++) {
				if (!(translatedSQL.get(loggingKeys.get(j)).get(0).equals("not present"))) {
					switch (j) {
						case 0:
							if (!(translatedSQL.get("xAxisCondition").get(0).equals("not present")))
								stringQuery += where;
							break;
						case 1:
							if (!(translatedSQL.get("xAxis").get(0).equals("not present")))
								stringQuery += groupBy;
							break;
						case 2:
							if (!(translatedSQL.get("orderBy").get(0).equals("not present")))
								stringQuery += orderBy;
							break;
					}
				}
			}

			Query query = JPA.em().createQuery(stringQuery);
			final List<?> loggingGraph = query.getResultList();
			return loggingGraph;
		}
	}

	/**
	 * This method make SQLcommands and receive a list in return for the
	 * weather-axis
	 * 
	 * @param translatedSQL
	 * @param currentWeather
	 * @param axis
	 * @return weatherGraph
	 */
	public static List<?> weatherGraph(Map<String, List<String>> translatedSQL,
			Integer currentWeather, String axis) {
		String stringQuery = "SELECT " + axis + "(id) FROM Weather" + " ";
		String where = translatedSQL.get("weatherCondition").get(currentWeather)
				+ translatedSQL.get("operatorsWeather").get(currentWeather) + "'"
				+ translatedSQL.get("textAreaWeather").get(currentWeather) + "'" + "";
		String whereDate = "(DATE_FORMAT(id, '%m%d') BETWEEN '"
				+ translatedSQL.get("fromWeather").get(currentWeather) + "' AND '"
				+ translatedSQL.get("toWeather").get(currentWeather) + "' OR '"
				+ translatedSQL.get("fromWeather").get(currentWeather) + "' > '"
				+ translatedSQL.get("toWeather").get(currentWeather)
				+ "' AND (DATE_FORMAT(id, '%m%d') >= '"
				+ translatedSQL.get("fromWeather").get(currentWeather)
				+ "' OR DATE_FORMAT(id, '%m%d') <= '"
				+ translatedSQL.get("toWeather").get(currentWeather) + "')) ";
		String groupBy = "";
		String orderBy = "";

		if (!(translatedSQL.get("xAxis").get(0).equals("not present"))) {
			groupBy = "GROUP BY " + translatedSQL.get("xAxis").get(0) + " ";
			orderBy = "ORDER BY " + translatedSQL.get("orderBy").get(0) + " "
					+ translatedSQL.get("orderByParam").get(0) + " ";

			if (translatedSQL.get("xAxis").get(0).contains("year"))
				groupBy = "GROUP BY year(id)";
			if (translatedSQL.get("orderBy").get(0).contains("year"))
				orderBy = "ORDER BY year(id) " + translatedSQL.get("orderByParam").get(0) + " ";
		}
		List<String> weatherKeys = new ArrayList<String>();
		weatherKeys.add("weatherCondition");
		weatherKeys.add("fromWeather");
		weatherKeys.add("xAxis");
		weatherKeys.add("orderBy");

		for (int j = 0; j < weatherKeys.size(); j++) {
			if (!(translatedSQL.get(weatherKeys.get(j)).get(0).equals("not present"))) {
				switch (j) {
					case 0:
						if (!(translatedSQL.get("weatherCondition").get(currentWeather)
								.equals("not present"))) {
							stringQuery += " WHERE " + where;
						}
						break;
					case 1:
						if (!(translatedSQL.get("fromWeather").get(currentWeather)
								.equals("not present"))) {

							if (!(translatedSQL.get("weatherCondition").get(currentWeather)
									.equals("not present"))) {
								stringQuery += " AND " + whereDate;
							} else
								stringQuery += " WHERE " + whereDate;
						}
						break;
					case 2:
						if (!(translatedSQL.get("xAxis").get(0).equals("not present")))
							stringQuery += groupBy;
						break;
					case 3:
						if (!(translatedSQL.get("orderBy").get(0).equals("not present")))
							stringQuery += orderBy;
						break;
				}
			}
		}
		Query query = JPA.em().createQuery(stringQuery);
		final List<?> weatherGraph = query.getResultList();
		return weatherGraph;
	}

	/**
	 * This method make SQLcommands and receive a list in return for the X-axis
	 * for Elk
	 * 
	 * @param translatedSQL
	 * @param currentForm
	 * @param totalAmountForms
	 * @return xAxisGraph
	 */
	public static List<?> xAxisGraph(Map<String, List<String>> translatedSQL, Integer currentForm,
			Integer totalAmountForms) {
		String whereParameter = translatedSQL.get("textAreaY").get(currentForm);
		if (!(translatedSQL.get("yAxisConditionField").get(currentForm).equals("not present")))
			whereParameter = translatedSQL.get("yAxisConditionField").get(currentForm);
		else if (!(translatedSQL.get("yAxisConditionSex").get(currentForm).equals("not present")))
			whereParameter = translatedSQL.get("yAxisConditionSex").get(currentForm);
		else if (!(translatedSQL.get("yAxisConditionTick").get(currentForm).equals("not present")))
			whereParameter = translatedSQL.get("yAxisConditionTick").get(currentForm);
		else if (!(translatedSQL.get("yAxisConditionDeerLice").get(currentForm)
				.equals("not present")))
			whereParameter = translatedSQL.get("yAxisConditionDeerLice").get(currentForm);

		String table = "Elk";
		if (translatedSQL.get("yAxis").get(currentForm).equals("SettElg"))
			table = "SeenElk";

		String stringQuery = "SELECT " + translatedSQL.get("xAxis").get(0) + " " + "FROM " + table
				+ " ";
		String where = "WHERE " + translatedSQL.get("xAxisCondition").get(0)
				+ translatedSQL.get("operatorsX").get(0) + "'"
				+ translatedSQL.get("textAreaX").get(0) + "'" + " ";
		String whereOnlyYcon = "WHERE " + translatedSQL.get("yAxisCondition").get(currentForm)
				+ translatedSQL.get("operatorsY").get(currentForm) + "'" + whereParameter + "'"
				+ " ";
		String whereWithYcon = "AND " + translatedSQL.get("yAxisCondition").get(currentForm)
				+ translatedSQL.get("operatorsY").get(currentForm) + "'" + whereParameter + "'"
				+ " ";
		String groupBy = "GROUP BY " + translatedSQL.get("xAxis").get(0) + " ";
		String orderBy = "";
		if (!translatedSQL.get("option" + (currentForm + 1)).get(0).equals("not present")) {
			if (translatedSQL.get("option" + (currentForm + 1)).get(0).equals("average")) {
				orderBy = "ORDER BY AVG(" + translatedSQL.get("orderBy").get(0) + ") "
						+ translatedSQL.get("orderByParam").get(0) + " ";
			} else {
				orderBy = "ORDER BY " + translatedSQL.get("option" + (currentForm + 1)).get(0)
						+ "(" + translatedSQL.get("orderBy").get(0) + ") "
						+ translatedSQL.get("orderByParam").get(0) + " ";
			}
		} else {
			orderBy = "ORDER BY " + translatedSQL.get("orderBy").get(0) + " "
					+ translatedSQL.get("orderByParam").get(0) + " ";
		}
		List<String> xKeys = new ArrayList<String>();
		xKeys.add("xAxisCondition");
		xKeys.add("xAxis");
		xKeys.add("orderBy");

		for (int j = 0; j < xKeys.size(); j++) {
			if (!(translatedSQL.get(xKeys.get(j)).get(0).equals("not present"))
					|| (!(translatedSQL.get("yAxisCondition").get(currentForm)
							.equals("not present")))) {
				switch (j) {
					case 0:
						if (!(translatedSQL.get("yAxisCondition").get(currentForm)
								.equals("not present"))) {
							if (translatedSQL.get("xAxisCondition").get(0).equals("not present")) {
								stringQuery += whereOnlyYcon;
							} else
								stringQuery += where + whereWithYcon;
						} else {
							stringQuery += where;
						}
						break;
					case 1:
						if (!(translatedSQL.get(xKeys.get(j)).get(0).equals("not present")))
							stringQuery += groupBy;
						break;
					case 2:
						if (!(translatedSQL.get(xKeys.get(j)).get(0).equals("not present")))
							stringQuery += orderBy;
						break;
				}
			}
		}

		Query query = JPA.em().createQuery(stringQuery);
		final List<?> xListGraph = query.getResultList();
		return xListGraph;
	}

	/**
	 * This method make SQLcommands and receive a list in return for the Y-axis
	 * for Elk
	 * 
	 * @param translatedSQL
	 * @param currentForm
	 * @param totalAmountForms
	 * @return yAxisGraph
	 */
	@SuppressWarnings("unchecked")
	public static List<?> yAxisGraph(Map<String, List<String>> translatedSQL, Integer currentForm,
			Integer totalAmountForms) {
		String whereParameter = translatedSQL.get("textAreaY").get(currentForm);
		if (!(translatedSQL.get("yAxisConditionField").get(currentForm).equals("not present")))
			whereParameter = translatedSQL.get("yAxisConditionField").get(currentForm);
		else if (!(translatedSQL.get("yAxisConditionSex").get(currentForm).equals("not present")))
			whereParameter = translatedSQL.get("yAxisConditionSex").get(currentForm);
		else if (!(translatedSQL.get("yAxisConditionTick").get(currentForm).equals("not present")))
			whereParameter = translatedSQL.get("yAxisConditionTick").get(currentForm);
		else if (!(translatedSQL.get("yAxisConditionDeerLice").get(currentForm)
				.equals("not present")))
			whereParameter = translatedSQL.get("yAxisConditionDeerLice").get(currentForm);

		String table = "Elk";
		if (translatedSQL.get("yAxis").get(currentForm).equals("SettElg"))
			table = "SeenElk";

		String stringQuery = "SELECT " + translatedSQL.get("yAxis").get(currentForm) + " "
				+ "FROM " + table + " ";

		if (translatedSQL.get("yAxis").get(currentForm).equals("SettElg")) {
			stringQuery = "";
			stringQuery = "SELECT SUM(kuEn+kuIngen+kuTo+okse+ukjent)" + "FROM " + table + " ";
		} else if (translatedSQL.get("yAxis").get(currentForm).equals("Bestand")) {
			stringQuery = "";
			Query query;
			if (!translatedSQL.get("xAxisCondition").get(0).equals("not present")) {
				query = JPA.em().createQuery(
						"SELECT year(date) from Elk WHERE year(date)"
								+ translatedSQL.get("operatorsX").get(0) + "'"
								+ translatedSQL.get("textAreaX").get(0) + "' GROUP BY year(date)");
			} else
				query = JPA.em().createQuery("SELECT year(date) from Elk GROUP BY year(date)");

			final List<Double> yearList = query.getResultList();
			List<Double> population = new ArrayList<Double>();
			for (int i = 0; i < yearList.size(); i++) {
				stringQuery = "select count(*) from Elk where year(date)-age<=" + yearList.get(i)
						+ " and year(date)>=" + yearList.get(i) + "";

				Query populationQuery = JPA.em().createQuery(stringQuery);
				population.addAll(populationQuery.getResultList());
			}

			return population;
		}
		String where = "WHERE " + translatedSQL.get("yAxisCondition").get(currentForm)
				+ translatedSQL.get("operatorsY").get(currentForm) + "'" + whereParameter + "'"
				+ " ";
		String whereOnlyXcon = "WHERE " + translatedSQL.get("xAxisCondition").get(0)
				+ translatedSQL.get("operatorsX").get(0) + "'"
				+ translatedSQL.get("textAreaX").get(0) + "'" + " ";
		String whereWithXcon = "AND " + translatedSQL.get("xAxisCondition").get(0)
				+ translatedSQL.get("operatorsX").get(0) + "'"
				+ translatedSQL.get("textAreaX").get(0) + "'" + " ";
		String groupBy = "GROUP BY " + translatedSQL.get("xAxis").get(0) + " ";
		String orderBy = "";
		if (!translatedSQL.get("option" + (currentForm + 1)).get(0).equals("not present")) {
			if (translatedSQL.get("option" + (currentForm + 1)).get(0).equals("average")) {
				orderBy = "ORDER BY AVG(" + translatedSQL.get("orderBy").get(0) + ") "
						+ translatedSQL.get("orderByParam").get(0) + " ";
			} else {
				orderBy = "ORDER BY " + translatedSQL.get("option" + (currentForm + 1)).get(0)
						+ "(" + translatedSQL.get("orderBy").get(0) + ") "
						+ translatedSQL.get("orderByParam").get(0) + " ";
			}
		} else {
			orderBy = "ORDER BY " + translatedSQL.get("orderBy").get(0) + " "
					+ translatedSQL.get("orderByParam").get(0) + " ";
		}
		String average = "SELECT AVG(" + translatedSQL.get("yAxis").get(currentForm) + ") "
				+ "FROM " + table + " ";
		String median = "SELECT MEDIAN(" + translatedSQL.get("yAxis").get(currentForm) + ") "
				+ "FROM " + table + " ";
		String sum = "SELECT COUNT(" + translatedSQL.get("yAxis").get(currentForm) + ") " + "FROM "
				+ table + " ";

		List<String> yKeys = new ArrayList<String>();
		yKeys.add("option" + (currentForm + 1));
		yKeys.add("yAxisCondition");
		yKeys.add("xAxis");
		yKeys.add("orderBy");

		for (int j = 0; j < yKeys.size(); j++) {
			if (!(translatedSQL.get(yKeys.get(j)).get(0).equals("not present"))
					|| (!(translatedSQL.get("xAxisCondition").get(0).equals("not present")))) {
				switch (j) {
					case 0:
						if (translatedSQL.get(yKeys.get(j)).get(0).equals("average")) {
							stringQuery = "";
							stringQuery = average;
						} else if (translatedSQL.get(yKeys.get(j)).get(0).equals("median")) {
							stringQuery = "";
							stringQuery = median;
						} else if (translatedSQL.get(yKeys.get(j)).get(0).equals("count")) {
							stringQuery = "";
							stringQuery = sum;
						}
						break;
					case 1:
						if (!(translatedSQL.get("xAxisCondition").get(0).equals("not present"))) {
							if (translatedSQL.get("yAxisCondition").get(currentForm)
									.equals("not present")) {
								stringQuery += whereOnlyXcon;
							} else
								stringQuery += where + whereWithXcon;
						} else {
							if (translatedSQL.get("yAxisCondition").get(currentForm)
									.equals("not present")) {
								break;
							} else
								stringQuery += where;
						}
						break;
					case 2:
						if (!(translatedSQL.get(yKeys.get(j)).get(0).equals("not present")))
							stringQuery += groupBy;
						break;
					case 3:
						if (!(translatedSQL.get(yKeys.get(j)).get(0).equals("not present")))
							stringQuery += orderBy;
						break;

				}
			}
		}
		Query query = JPA.em().createQuery(stringQuery);
		final List<Double> yListGraph = query.getResultList();
		if (translatedSQL.containsKey("dev" + (currentForm + 1))) {
			calculateStandardDeviation(yListGraph, stringQuery);
		}

		return yListGraph;
	}

	/**
	 * This method calculate the Standard deviation of the Y-axis, and take a
	 * list, string, integer as parameter.
	 * 
	 * @param yList
	 * @param oldQuery
	 */
	public static void calculateStandardDeviation(List<Double> yList, String oldQuery) {
		String newQuery = "";
		if (oldQuery.contains("AVG"))
			newQuery = oldQuery.replaceAll("AVG", "STDDEV");
		else if (oldQuery.contains("MEDIAN"))
			newQuery = oldQuery.replaceAll("MEDIAN", "STDDEV");
		else
			newQuery = oldQuery;

		Query query = JPA.em().createQuery(newQuery);
		@SuppressWarnings("unchecked")
		final List<Double> devGraph = query.getResultList();
		List<Double> highDev = new ArrayList<Double>();
		List<Double> lowDev = new ArrayList<Double>();

		for (int i = 0; i < yList.size(); i++) {
			Double newPointAdd = yList.get(i) + devGraph.get(i);
			highDev.add(newPointAdd);
		}

		for (int i = 0; i < yList.size(); i++) {
			Double newPointSub = yList.get(i) - devGraph.get(i);
			lowDev.add(newPointSub);
		}
		for (int i = 0; i < highDev.size(); i++)
			yList.add((highDev.get(i)));
		for (int i = 0; i < lowDev.size(); i++)
			yList.add((lowDev.get(i)));
	}

	/**
	 * This method takes two lists of list<String> as parameters and compare the
	 * elements. i.e Add 0 to yList if 2007 is present in one list but not in
	 * another.
	 * 
	 * @param xList
	 * @param yList
	 * @param stdList
	 */
	public static void compareDataValues(List<List<String>> xList, List<List<String>> yList,
			List<List<String>> stdList) {
		// copies the first list to check with later.
		List<String> originalFirstElement = new ArrayList<String>(xList.get(0));
		List<String> container = xList.get(0);

		// adding x-values to first element in xList
		for (int i = 0; i < xList.size(); i++) {
			for (int j = 0; j < xList.get(i).size(); j++) {
				if (!(container.contains(xList.get(i).get(j)))) {
					container.add(j, xList.get(i).get(j));
				}
			}
		}
		// adding zero for all elements in xList that is not present in yList
		for (int i = 0; i < xList.size(); i++) {
			for (int j = 0; j < container.size(); j++) {
				if (i == 0) {
					if (!(originalFirstElement.contains(container.get(j)))) {
						yList.get(0).add(j, "0");
					}
				} else if (!(xList.get(i).contains(container.get(j)))) {
					yList.get(i).add(j, "0");
				}
			}
		}
		// adding zero to the last elements in the list to match sizes.
		for (int i = 0; i < yList.size(); i++) {
			if (!(container.size() == yList.get(i).size())) {
				for (int j = yList.get(i).size(); j < container.size(); j++)
					yList.get(i).add(j, "0");
			}
		}
		if (!stdList.isEmpty()) {
			for (int j = 0; j < stdList.size(); j++) {
				int stdSize = stdList.get(j).size();
				for (int i = 0; i < container.size() - stdSize; i++) {
					stdList.get(j).add(i, "0");
				}
			}
		}
	}

	/**
	 * This method take a List as a parameter and convert it to a StringList
	 * 
	 * @param inputList
	 * @return newList
	 */
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

}// end class
