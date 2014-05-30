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

import static play.data.Form.form;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import models.Area;
import models.DeerLice;
import models.Elk;
import models.HuntingField;
import models.Logging;
import models.SeenElk;
import models.Sex;
import models.Tick;
import models.UserGraph;
import models.UserGraphCategory;
import models.Weather;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

/**
 * @author Espen, Thomas og Haakon
 * @version 0.1
 */
/**
 * FileIO is used to first extract information from a CSV file. Then, if the
 * file is valid, it will add it to the database. If the file isn't valid it
 * will ask the user to decide what to do (Not on critical errors).
 */
public class FileIO extends Controller {
	static ArrayList<ArrayList<String>>	DataList			= new ArrayList<ArrayList<String>>();
	static ArrayList<String>			areaId				= new ArrayList<String>();
	static ArrayList<String>			areaName			= new ArrayList<String>();
	static ArrayList<String>			huntingFieldId		= new ArrayList<String>();
	static ArrayList<String>			huntingFieldName	= new ArrayList<String>();
	static ArrayList<String>			tick				= new ArrayList<String>();
	static ArrayList<String>			sexList				= new ArrayList<String>();
	static ArrayList<String>			fly					= new ArrayList<String>();
	static ArrayList<String>			antlers				= new ArrayList<String>();
	static ArrayList<String>			veal				= new ArrayList<String>();
	static ArrayList<String>			twin				= new ArrayList<String>();
	static ArrayList<String>			day					= new ArrayList<String>();
	static ArrayList<String>			month				= new ArrayList<String>();
	static ArrayList<String>			year				= new ArrayList<String>();
	static ArrayList<String>			weight				= new ArrayList<String>();
	static ArrayList<String>			age					= new ArrayList<String>();
	static ArrayList<Integer>			ColumnOrder			= new ArrayList<Integer>();
	static ArrayList<Integer>			ErrorIndex			= new ArrayList<Integer>();
	static ArrayList<String>			ErrorRow			= new ArrayList<String>();
	static ArrayList<String>			messageArray		= new ArrayList<String>();
	static ArrayList<String>			textError			= new ArrayList<String>();
	static ArrayList<String>			valueError			= new ArrayList<String>();
	static ArrayList<String>			år					= new ArrayList<String>();
	static ArrayList<String>			okse				= new ArrayList<String>();
	static ArrayList<String>			kuEn				= new ArrayList<String>();
	static ArrayList<String>			kuIngen				= new ArrayList<String>();
	static ArrayList<String>			kuTo				= new ArrayList<String>();
	static ArrayList<String>			ukjent				= new ArrayList<String>();
	static ArrayList<SeenElk>			seenElk				= new ArrayList<>();
	static ArrayList<String>			seenElkYear			= new ArrayList<String>();
	static boolean						columnError			= false;
	static boolean						upload				= true;
	static boolean						run					= true;
	static boolean						fatalError			= false;
	static boolean						shootenElk			= false;
	static boolean						duplicateYear		= false;

	public static Result upload() {
		return ok(views.html.fileio.upload.render("Velg fil"));
	}

	public static Result uploadError(String message) {
		return ok(views.html.fileio.uploaderror.render("Feilmelding", message));
	}

	public static Result checkError(List<String> message, List<String> row) {
		return ok(views.html.fileio.checkerror.render("Feilmelding", message, row));
	}

	public static Result message(String message, Integer time) {
		return ok(views.html.fileio.message.render("Melding", message, time));
	}

	@Transactional
	public static Result uploadfile() throws ParseException {
		shootenElk = true;
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart submittedFile = body.getFile("csvfile");
		try {
			if (submittedFile != null) {
				File file = submittedFile.getFile();
				file.setExecutable(false);
				file.setReadOnly();
				// Read CSV file and return an ArrayList<Elk>
				ArrayList<Elk> elkArray = new ArrayList<Elk>();
				elkArray = readElkFromCsv(file);
				if (upload) {
					for (Elk elk : elkArray) {
						elk.save();
					}
				}
				if (columnError) {
					return uploadError("Sett kolonner");
				} else if (upload) {
					HomePageStatistics.dataChanged=true;
					return message("Fil vellykket opplastet!", 1300);
				} else {
					return checkError(getMessage(),ErrorRow);
					// return ok(getMessage());
				}

			} else {
				flash("error", "Mangler fil");
				return redirect(routes.Homepage.index());
			}
		} catch (Exception e) {
			return message("Støtter ikke dette filformatet, kun .CSV", 4000);
		}

	}

	@Transactional
	private static ArrayList<Elk> readElkFromCsv(File elkFile) throws ParseException {

		clearLists();
		DataList.add(areaId);
		DataList.add(areaName);
		DataList.add(huntingFieldId);
		DataList.add(huntingFieldName);
		DataList.add(day);
		DataList.add(month);
		DataList.add(year);
		DataList.add(sexList);
		DataList.add(weight);
		DataList.add(age);
		DataList.add(antlers);
		DataList.add(veal);
		DataList.add(twin);
		DataList.add(tick);
		DataList.add(fly);
		textError.clear();
		initColumnOrder(ColumnOrder);
		columnError = false;
		upload = true;
		try {
			// Fill ArrayList
			final BufferedReader br = new BufferedReader(new FileReader(elkFile));
			String line = "";
			String[] list = null;

			while ((line = br.readLine()) != null) {
				list = line.split(";", -1);
				areaId.add(list[ColumnOrder.get(0)]);
				areaName.add(list[ColumnOrder.get(1)]);
				huntingFieldId.add(list[ColumnOrder.get(2)]);
				huntingFieldName.add(list[ColumnOrder.get(3)]);
				day.add(list[ColumnOrder.get(4)]);
				month.add(list[ColumnOrder.get(5)]);
				year.add(list[ColumnOrder.get(6)]);
				sexList.add(list[ColumnOrder.get(7)]);
				weight.add(list[ColumnOrder.get(8)]);
				age.add(list[ColumnOrder.get(9)]);
				antlers.add(list[ColumnOrder.get(10)]);
				veal.add(list[ColumnOrder.get(11)]);
				twin.add(list[ColumnOrder.get(12)]);
				tick.add(list[ColumnOrder.get(13)]);
				fly.add(list[ColumnOrder.get(14)]);
			}
			br.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// return to error page
		}
		ArrayList<Elk> elkArray = new ArrayList<Elk>();
		checkColumns(DataList);
		if (upload) {
			AllTest();
			if (upload) {
				uploadFile(elkArray);
			}
		}

		return elkArray;
	}

	/**
	 *  fills empty places
	 * @param list
	 */
	private static void fillEmpty(ArrayList<String> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).isEmpty()) {
				list.set(i, "0");
			}
		}
	}

	/**
	 *  check for realistic number
	 * @param list
	 */
	private static void checkNumber(ArrayList<String> list) {
		for (int i = 1; i < list.size(); i++) {
			switch (list.get(0)) {
				case "Alder":
					if (Double.parseDouble(list.get(i)) > 40 || Double.parseDouble(list.get(i)) <= 0) {
						if(Double.parseDouble(list.get(i)) == 0){
							setMessage("Alder på rad " + i + " er ikke registrert. ");
						}
						else{
						setMessage("Alder på rad " + i + " er for høy/lav. " + "(" + list.get(i)
								+ " år)");
						}
						upload = false;
						ErrorIndex.add(i);
						valueError.add("Age");
						ErrorRow.add("Rad "+i);

					}
					break;

				case "Veid vekt":
					if (Double.parseDouble(list.get(i)) > 500 || Double.parseDouble(list.get(i)) <= 0) {
						if(Double.parseDouble(list.get(i)) == 0){
							setMessage("Vekt på rad " + i + " er ikke registrert. ");
						}
						else{
						setMessage("Vekt på rad " + i + " er for høy/lav. " + "(" + list.get(i)
								+ " kg)");
						}
						upload = false;
						ErrorIndex.add(i);
						valueError.add("Weight");
						ErrorRow.add("Rad "+i);
					}
					break;

				case "Tagger":
					if (Double.parseDouble(list.get(i)) > 30 || Double.parseDouble(list.get(i)) < 0) {
						setMessage("Tagger på rad " + i + " er for høy/lav. " + "(" + list.get(i)
								+ " tagger)");			
						upload = false;
						ErrorIndex.add(i);
						valueError.add("Antlers");
						ErrorRow.add("Rad "+i);
					}
					break;
			}
		}
	}

	/**
	 *  Checks the status on lice/ticks, and returns corresponding ID
	 * @param amount
	 * @return
	 */
	public static int assignAmountId(String amount) {
		final int sum;

		switch (amount) {
			case "Ingen":
				sum = 1;
				break;
			case "Få":
				sum = 2;
				break;
			case "Moderat":
				sum = 3;
				break;
			case "Mange":
				sum = 4;
				break;
			default:
				sum = 5;
				break;
		}

		return sum;
	}

	/**
	 *  Change name to male and female
	 * @param list
	 */
	public static void changeSexName(ArrayList<String> list) {
		String a = "han";
		String b = "hann";
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i).equalsIgnoreCase(a) || list.get(i).equalsIgnoreCase(b)) {
				list.set(i, "male");
			} else if (list.get(i).equalsIgnoreCase("0")) {
				list.set(i, "Not Registered");
			} else {
				list.set(i, "female");
			}

		}
	}

	/**
	 *  Check that all content in list is integer
	 * @param list
	 */
	private static void checkIfInt(ArrayList<String> list) {
		int rad = 1;
		String t = "";
		for (int i = 1; i < list.size(); i++) {
			try {
				Double.parseDouble(list.get(i));
				rad++;
			} catch (Exception e) {
				t = list.get(i);
				setMessage("Rad " + rad
						+ ", er det et felt som er beregnet for tall forvekslet med tekst. " + t);
				textError.add("Rad " + rad
						+ ", er det et felt som er beregnet for tall forvekslet med tekst. " + t);
				fatalError = true;
				upload = false;
			}
		}
	}

	/**
	 *  check that the columns are correctly positioned
	 * @param list
	 * @return
	 */
	private static boolean checkColumns(ArrayList<ArrayList<String>> list) {
		String[] ColumnList = { "Valdnr.", "Valdnavn", "Jaktfeltnr.", "Jaktfeltnavn", "Dag", "Mnd",
				"År", "Kjønn", "Veid vekt", "Alder", "Tagger", "Kalv", "Tvilling", "Flått",
				"Hjortelusflue" };
		for (int i = 0; i < ColumnList.length; i++) {
			if (list.get(i).get(0).equals(ColumnList[i])) {
			} else {
				upload = false;
				columnError = true;
				return false;
			}
		}
		return true;
	}

	private static void setMessage(String message) {

		messageArray.add(message);
	}

	private static List<String> getMessage() {
		ArrayList<String> message = new ArrayList<String>();
		// String message = "";
		if (messageArray.isEmpty() && columnError == false) {
			// message = "Success";
			message.add("Success");
		} else if (columnError) {
			// message = "Kollone mangler, eller ikke i riktig rekkefølge";
			message.add("Kollone mangler, eller ikke i riktig rekkefølge");
		} else {
			for (int i = 0; i < messageArray.size(); i++) {
				// message += "-" + messageArray.get(i) + "<br>";
				message.add(messageArray.get(i));
				// messageArray.remove(i);
			}
		}
		return message;
	}

	/**
	 *  Get the parameter from URL
	 * @param id
	 * @return
	 * @throws ParseException
	 */
	@Transactional
	public static Result GetColumnOrder(List<Integer> id) throws ParseException {

		ArrayList<Elk> elkArray = new ArrayList<Elk>();
		ArrayList<Integer> list = new ArrayList<Integer>();
		ArrayList<String> newData = new ArrayList<String>();
		
		if (columnError) {
			for (int i = 0; i < id.size(); i++) {
				list.add(id.get(i));
			}
			ColumnOrder = list;
			if (CheckDuplicates(ColumnOrder)) {
				return message("OBS!! Du har satt flere kolonner til å representere det samme. Prøv på nytt.", 5000);
			} else {
				int j;
				boolean flag = true;
				while (flag) {
					flag = false;
					for (j = 0; j < ColumnOrder.size() - 1; j++) {
						if (ColumnOrder.get(j) > ColumnOrder.get(j + 1)) {
							swapList(DataList.get(j), DataList.get(j + 1));
							Collections.swap(ColumnOrder, j, j + 1);
							flag = true;
						}
					}
				}
			}
			AllTest();
			if (upload) {
				uploadFile(elkArray);
				for (Elk elk : elkArray) {
					elk.save();
				}
			}
			if (getMessage().get(0) == "Success") {
				HomePageStatistics.dataChanged=true;
				return message("Fil vellykket opplastet!", 1300);
			} else {
				return checkError(getMessage(),ErrorRow);
			}
		} else {
			for (int i = 0; i < id.size(); i++) {
				newData.add(String.valueOf(id.get(i)));
			}

			for (int i = 0; i < valueError.size(); i++) {
				switch (valueError.get(i)) {
					case "Weight":
						weight.set(ErrorIndex.get(i), newData.get(i));
						break;
					case "Age":
						age.set(ErrorIndex.get(i), newData.get(i));
						break;
					case "Antlers":
						antlers.set(ErrorIndex.get(i), newData.get(i));
						break;
				}
			}
			AllTest();
			if (upload) {
				uploadFile(elkArray);
				for (Elk elk : elkArray) {
					elk.save();
				}
			}
			if (getMessage().get(0) == "Success") {
				HomePageStatistics.dataChanged=true;
				return message("Fil vellykket opplastet!", 1300);
			} else {
				return checkError(getMessage(),ErrorRow);
			}
		}

	}

	public static void swapList(ArrayList<String> list1, ArrayList<String> list2) {
		ArrayList<String> tmpList = new ArrayList<String>(list1);
		list1.clear();
		list1.addAll(list2);
		list2.clear();
		list2.addAll(tmpList);
	}

	/**
	 *  Check for duplicates in a list
	 * @param input
	 * @return
	 */
	public static boolean CheckDuplicates(ArrayList<Integer> input) {
		for (int j = 0; j < input.size(); j++)
			for (int k = j + 1; k < input.size(); k++)
				if (k != j && input.get(k) == input.get(j)) {
					return true;
				}
		return false;
	}

	public static void initColumnOrder(ArrayList<Integer> list) {

		for (int i = 0; i < 15; i++) {
			list.add(i);
		}
	}

	/**
	 * Check if collumn are correct
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static boolean test(File file) throws IOException {
		String[] ColumnList = { "Valdnr.", "Valdnavn", "Jaktfeltnr.", "Jaktfeltnavn", "Dag", "Mnd",
				"År", "Kjønn", "Veid vekt", "Alder", "Tagger", "Kalv", "Tvilling", "Flått",
				"Hjortelusflue" };
		try {

			// Fill ArrayList

			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			String[] list = null;
			String csvSplitBy = ";";

			while ((line = br.readLine()) != null) {
				list = line.split(csvSplitBy);
			}
			for (int i = 0; i < ColumnList.length; i++) {
				if (list[i].equals(ColumnList[i])) {
				} else {
					upload = false;
					columnError = true;
					return false;
				}
			}
			br.close();

		}

		catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		return true;
	}

	public static void checkDate(ArrayList<String> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).isEmpty()) {
				list.set(i, "1");
			}
		}
	}

	/**
	 * Calculete the most common year
	 * @param list
	 */
	public static void setYear(ArrayList<String> list) {
		String mostCommon = null;
		int max = -1;

		for (String s : list) {
			int freq = Collections.frequency(list, s);
			if (freq > max) {
				mostCommon = s;
				max = freq;
			}
		}
		for (int i = 0; i < list.size(); i++) {
			list.set(i, mostCommon);
		}
	}

	/**
	 * Test på all data 
	 */
	public static void AllTest() {
		columnError = false;
		upload = true;
		fatalError = false;
		messageArray.clear();
		textError.clear();
		ErrorIndex.clear();
		valueError.clear();

		// fill empty -- veal twin antlers
		fillEmpty(age);
		fillEmpty(twin);
		fillEmpty(veal);
		fillEmpty(antlers);
		fillEmpty(weight);
		checkDate(day);
		checkDate(month);
		setYear(year);
		fillEmpty(fly);
		fillEmpty(tick);
		fillEmpty(sexList);

		// change sexName
		changeSexName(sexList);

		// check if integer
		checkIfInt(age);
		checkIfInt(day);
		checkIfInt(month);
		checkIfInt(year);
		checkIfInt(weight);
		checkIfInt(antlers);
		checkIfInt(veal);
		checkIfInt(twin);

		// check for realistic number
		checkNumber(age);
		checkNumber(weight);
		checkNumber(antlers);
	}

	@Transactional
	public static void uploadFile(ArrayList<Elk> elkArray) throws ParseException {
		for (int i = 1; i < areaId.size(); i++) {
			Area area = new Area();
			area.id = areaId.get(i);
			area.name = areaName.get(i);
			HuntingField hf = new HuntingField();
			hf.id = huntingFieldId.get(i);
			hf.name = huntingFieldName.get(i);
			boolean isNewArea = (Area.findById(area.id) == null) ? true : false;
			if (isNewArea) {
				area.save();
			}
			boolean isNewHuntingField = (HuntingField.findById(hf.id) == null) ? true : false;
			if (isNewHuntingField) {
				hf.save();
			}
			// Have to set date to certain format to ensure it always
			// "yyyy-MM-dd", not "yyyy-M-d"
			String currentDate = year.get(i) + "-" + month.get(i) + "-" + day.get(i);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date formattedDate = formatter.parse(currentDate);
			java.sql.Date sqlDate = new java.sql.Date(formattedDate.getTime());
			Sex sex = new Sex();
			if (sexList.get(i).equals("male")) {
				sex.id = 1;
			} else if (sexList.get(i).equals("female")) {
				sex.id = 2;
			} else {
				sex.id = 3;
			}
			Elk currentElk = new Elk();
			currentElk.setArea(area);
			currentElk.setHuntingfield(hf);
			currentElk.setSex(sex);
			// date
			currentElk.setDate(sqlDate);
			currentElk.setWeight(Integer.parseInt(weight.get(i)));
			currentElk.setAge(Double.parseDouble(age.get(i)));
			currentElk.setAntlers(Integer.parseInt(antlers.get(i)));
			currentElk.setVeal(Integer.parseInt(veal.get(i)));
			currentElk.setTwin(Integer.parseInt(twin.get(i)));
			DeerLice currentLice = new DeerLice();
			Tick currentTick = new Tick();
			// Sets the corresponding ID to field with (Ingen, få, Moderat,
			// Mange)
			currentTick.id = assignAmountId(tick.get(i));
			currentLice.id = assignAmountId(fly.get(i));
			// Adds the updated tick/lice object to the elk object
			currentElk.setSumTick(currentTick);
			currentElk.setSumLice(currentLice);
			elkArray.add(currentElk);
		}
	}

	public static void clearLists() {
		DataList.clear();
		areaId.clear();
		areaName.clear();
		huntingFieldId.clear();
		huntingFieldName.clear();
		day.clear();
		month.clear();
		year.clear();
		sexList.clear();
		weight.clear();
		age.clear();
		antlers.clear();
		veal.clear();
		twin.clear();
		tick.clear();
		fly.clear();
		messageArray.clear();
		år.clear();
		okse.clear();
		kuIngen.clear();
		kuEn.clear();
		kuTo.clear();
		ukjent.clear();
		seenElk.clear();
		ErrorIndex.clear();
		valueError.clear();

	}

	@Transactional
	public static Result sendData() throws ParseException {
		if (shootenElk) {
			ArrayList<Elk> elkArray = new ArrayList<Elk>();
			if (fatalError) {
				return message("OBS! Kan ikke laste opp fil grunnet felt som er beregnet for tall er forvekslet med tekst. Se: "
						+ getTextError(), 5000);
			} else {
				uploadFile(elkArray);
				for (Elk elk : elkArray) {
					elk.save();
				}
				HomePageStatistics.dataChanged=true;
				return message("Fil vellykket opplastet!", 1300);
			}
		} else {
			ArrayList<SeenElk> seenElkArray = new ArrayList<>();
			seenElkArray = seenElk;
			if (fatalError) {
				return message("OBS! Kan ikke laste opp fil, da år mangler.. Fiks dette og prøv igjen.", 5000);
			} else {
				for (SeenElk seenelk : seenElkArray) {
					seenelk.save();
				}
				HomePageStatistics.dataChanged=true;
				return message("Fil vellykket opplastet!", 1300);
			}
		}
	}

	public static String getTextError() {
		String message = "";
		for (int i = 0; i < textError.size(); i++) {
			message += textError.get(i) + " | ";
		}
		return message;
	}

	/**
	 * Result upload seen elk 
	 * @return
	 */
	public static Result uploadSeenElk() {

		return ok(views.html.fileio.uploadSeenElk.render("Velg fil"));
	}

	/**
	 * Upload seen elk to the database
	 * @return
	 * @throws ParseException
	 */
	@Transactional
	public static Result uploadSeenElkFile() throws ParseException {
		shootenElk = false;
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart submittedFile = body.getFile("csvfile");
		try {
			if (submittedFile != null) {
				File file = submittedFile.getFile();
				file.setExecutable(false);
				file.setReadOnly();
				// Read CSV file and return an ArrayList<Elk>
				ArrayList<SeenElk> seenElkArray = new ArrayList<SeenElk>();
				seenElkArray = readSeenElkFromCsv(file);
				if (upload) {
					for (SeenElk seenelk : seenElkArray) {
						seenelk.save();
					}
					HomePageStatistics.dataChanged=true;
					return message("Fil vellykket opplastet!", 1300);
				} else if (fatalError) {
					if (duplicateYear) {
						return message("OBS! Allerede lastet opp data fra dette året. Fiks dette og prøv igjen.", 5000);
					} else {
						return message("OBS! Kan ikke laste opp fil, da år mangler.. Fiks dette og prøv igjen.", 5000);
					}

				} else {
					return checkError(getMessage(),ErrorRow);
				}
			}
		} catch (Exception e) {
			return message("Støtter ikke dette filformatet, kun .CSV", 4000);
		}
		return ok(views.html.fileio.uploadSeenElk.render("Velg fil"));

	}

	/**
	 * Read seen elk file
 * @param elkFile
 * @return
 * @throws ParseException
 */
	@SuppressWarnings("unchecked")
	@Transactional
	private static ArrayList<SeenElk> readSeenElkFromCsv(File elkFile) throws ParseException {
		// Fill ArrayList
		upload = true;
		fatalError = false;
		duplicateYear = false;
		ArrayList<SeenElk> seenElkArray = new ArrayList<>();
		clearLists();

		BufferedReader br;
		Query query = JPA.em().createQuery("SELECT year FROM SeenElk");
		seenElkYear = (ArrayList<String>) query.getResultList();
		try {
			br = new BufferedReader(new FileReader(elkFile));
			String line = "";
			String[] list = null;
			while ((line = br.readLine()) != null) {
				list = line.split(",", -1);
				år.add(list[0].replaceAll("\"", ""));
				okse.add(list[2].replaceAll("\"", ""));
				kuIngen.add(list[3].replaceAll("\"", ""));
				kuEn.add(list[4].replaceAll("\"", ""));
				kuTo.add(list[5].replaceAll("\"", ""));
				ukjent.add(list[7].replaceAll("\"", ""));
			}

			fillEmpty(år);
			fillEmpty(okse);
			fillEmpty(kuEn);
			fillEmpty(kuTo);
			fillEmpty(kuIngen);
			fillEmpty(ukjent);
			testSeenElk();
			br.close();
			for (int i = 1; i < år.size(); i++) {
				SeenElk seenElk = new SeenElk();
				seenElk.setYear(Long.parseLong(år.get(i)));
				seenElk.setOkse(Integer.parseInt(okse.get(i)));
				seenElk.setKuIngen(Integer.parseInt(kuIngen.get(i)));
				seenElk.setKuEn(Integer.parseInt(kuEn.get(i)));
				seenElk.setKuTo(Integer.parseInt(kuTo.get(i)));
				seenElk.setUkjent(Integer.parseInt(ukjent.get(i)));
				seenElkArray.add(seenElk);
			}
			seenElk = seenElkArray;

			return seenElkArray;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		flash("message", Messages.getMessageDivFor("Feil med fil - Ingenting lagret", 2));
		return null;
	}

	/**
	 * GET request for uploadweather. Recieves a CSV file
	 * 
	 * @return
	 */
	public static Result uploadWeather() {

		return ok(views.html.fileio.uploadWeather.render("Velg VAER fil"));
	}

	/**
	 * POST request for uploading weather data.
	 * 
	 * @return
	 * @throws ParseException
	 */
	@Transactional
	public static Result uploadWeatherFile() throws ParseException {
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart submittedFile = body.getFile("csvfile");
		if (submittedFile != null) {
			File file = submittedFile.getFile();
			file.setExecutable(false);
			file.setReadOnly();
			// Read CSV file and return an ArrayList<Elk>
			ArrayList<Weather> weatherArray = new ArrayList<Weather>();
			// Loop through file for all Weather data (Could be several years)
			int iteration = 0;
			do {
				weatherArray = readYearWeatherFromCsv(file, iteration++);

				if (weatherArray != null) {
					for (Weather currentWeather : weatherArray) {
						currentWeather.save();
					}
				}

			} while (weatherArray != null);
			flash("message", Messages.getMessageDivFor("Filen har blitt opplastet og lagret!", 1));
			return ok(views.html.fileio.uploadWeather.render("Fil lastet opp!"));

		}
		flash("message", Messages.getMessageDivFor("Feil med fil - ingenting lagret", 2));
		return ok(views.html.fileio.uploadWeather.render("Velg NY VAER fil"));

	}

	/**
	 * Reads a CSV file from eKlima.no containing either temperatur data, snow
	 * data or both. Incase of several years in one file, we recieve an
	 * 'iteration' that holds count of how many times the method has been
	 * called. This makes sure we don't read the same year several times.
	 * 
	 * @param elkFile
	 * @param mainIteration
	 * @return ArrayList<Weather> year
	 */
	private static ArrayList<Weather> readYearWeatherFromCsv(File elkFile, int mainIteration) {
		// Fill ArrayList
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(elkFile));
			String line = "";
			String[] list = null;
			String csvSplitBy = " ";

			int thisIteration = 0;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("36560 NELAUG, TAM") && thisIteration++ == mainIteration) {
					// We found TEMPERATURE data - 36560 NELAUG, TAM
					list = line.split(csvSplitBy);
					ArrayList<Weather> currentWeather = temperatureDataFrom(br,
							Integer.valueOf(list[3]));
					br.close();
					return currentWeather;
				} else if (line.startsWith("36560 NELAUG, SA") && thisIteration++ == mainIteration) {
					// We found SNOW data - 36560 NELAUG, SA CURRENTYEAR

					list = line.split(csvSplitBy);
					ArrayList<Weather> currentWeather = snowDataFrom(br, Integer.valueOf(list[3]));
					br.close();
					return currentWeather;
				}
			}

			br.close();
			return null;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		flash("message", Messages.getMessageDivFor("Feil med fil - Feil HER", 2));
		return null;
	}

	/**
	 * Test data from seen elk
	 */
	private static void testSeenElk() {
		for (int i = 1; i < år.size(); i++) {
			if (checkSeenElkYear(år, seenElkYear)) {
				upload = false;
				fatalError = true;
				duplicateYear = true;
			}
			if (Integer.parseInt(år.get(i)) == 0) {
				upload = false;
				fatalError = true;
			}
			if (Integer.parseInt(okse.get(i)) == 0) {
				setMessage("Okse ikke registrert i " + år.get(i));
				upload = false;
			}
			if (Integer.parseInt(kuIngen.get(i)) == 0) {
				setMessage("Ku uten kalv ikke registrert i " + år.get(i));
				upload = false;
			}
			if (Integer.parseInt(kuEn.get(i)) == 0) {
				setMessage("Ku med en kalv ikke registrert i " + år.get(i));
				upload = false;
			}
			if (Integer.parseInt(kuTo.get(i)) == 0) {
				setMessage("Ku med to kalver ikke registrert i " + år.get(i));
				upload = false;
			}
			if (Integer.parseInt(ukjent.get(i)) == 0) {
				setMessage("\"Ukjent\" ikke registrert i " + år.get(i));
				upload = false;
			}

		}
	}

	/**
	 * Check if the year already exist
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean checkSeenElkYear(ArrayList<String> a, ArrayList<String> b) {
		for (int i = 0; i < a.size(); i++) {
			for (int t = 0; t < b.size(); t++) {
				if (String.valueOf(b.get(t)).equals(a.get(i))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets temperature data (TAM) from CSV (BufferedReader) for a certain Year
	 * 
	 * @param br
	 * @return ArrayList<Weather> YEAR
	 * @throws IOException
	 * @throws ParseException
	 */
	public static ArrayList<Weather> temperatureDataFrom(BufferedReader br, Integer currentYear)
			throws IOException, ParseException {
		String line = "";
		String[] list = null;
		String csvSplitBy = ";";

		line = br.readLine(); // Get rid of headers for data

		ArrayList<Weather> arrayWeather = new ArrayList<Weather>();
		boolean firstValue = true;
		Weather previousWeatherTemp = new Weather(0.0, 0.0);

		int currentDay = -1;
		int monthNumber = 1;
		while ((line = br.readLine()) != null && !(line.startsWith("Antall;"))) {

			list = line.split(csvSplitBy);
			// Process line for day
			for (String dayTemp : list) {
				if (firstValue) {
					currentDay = Integer.valueOf(dayTemp);
					firstValue = false;
				} else {
					Weather currentWeatherTemp = new Weather();
					try {
						currentWeatherTemp.setTemperature(Double.valueOf(dayTemp));
						currentWeatherTemp.setIdWithInt(currentDay, monthNumber, currentYear);

						arrayWeather.add(currentWeatherTemp);
					} catch (NumberFormatException e) {
						// Empty temperature field
						// Make exceptions for Feb (29,30) and 31 for all
						// months.

						if ((monthNumber == 2 && currentDay < 29)
								|| (monthNumber != 2 && currentDay != 31)) {

							// Set temperature to be equal to last day
							currentWeatherTemp.setTemperature(previousWeatherTemp.getTemperature());
							currentWeatherTemp.setIdWithInt(currentDay, monthNumber, currentYear);

							arrayWeather.add(currentWeatherTemp);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						// setidwithint
						e.printStackTrace();
					}
					monthNumber++;
					previousWeatherTemp = currentWeatherTemp;
				}
			}
			firstValue = true;
			monthNumber = 1;

		}
		return arrayWeather;

	}

	/**
	 * Gets snow data (SA) from a CSV file (BufferedReader) for a certain Year
	 * 
	 * @param br
	 * @return ArrayList<Weather> YEAR
	 * @throws IOException
	 * @throws ParseException
	 */
	public static ArrayList<Weather> snowDataFrom(BufferedReader br, Integer currentYear)
			throws IOException, ParseException {
		String line = "";
		String[] list = null;
		String csvSplitBy = ";";

		line = br.readLine(); // Get rid of headers for data

		ArrayList<Weather> arrayWeather = new ArrayList<Weather>();
		boolean firstValue = true;

		int currentDay = -1;
		int monthNumber = 1;
		while ((line = br.readLine()) != null && !(line.startsWith("Antall;"))) {

			list = line.split(csvSplitBy);
			// Process line for day
			for (String dayTemp : list) {
				if (firstValue) {
					currentDay = Integer.valueOf(dayTemp);
					firstValue = false;
				} else {
					Weather currentWeatherSnow = new Weather();
					try {
						currentWeatherSnow.setSnowdepth(Double.valueOf(dayTemp));
						currentWeatherSnow.setIdWithInt(currentDay, monthNumber, currentYear);

						arrayWeather.add(currentWeatherSnow);
					} catch (NumberFormatException e) {
						// Empty SNOW field - set to ZERO
						// Make exceptions for Feb (29,30) and 31 for all
						// months.

						if ((monthNumber == 2 && currentDay < 29)
								|| (monthNumber != 2 && currentDay != 31)) {

							// Set temperature to be equal to last day
							currentWeatherSnow.setSnowdepth(0.0);
							currentWeatherSnow.setIdWithInt(currentDay, monthNumber, currentYear);

							arrayWeather.add(currentWeatherSnow);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						// setidwithint
						e.printStackTrace();
					}
					monthNumber++;
				}
			}
			firstValue = true;
			monthNumber = 1;

		}
		return arrayWeather;

	}
	@Transactional
	@SubjectPresent
	public static Result showLoggingData(){
		Form<Logging> loggingForm = form(Logging.class);
		
		Query loggingQuery = JPA.em().createQuery("FROM Logging");
		@SuppressWarnings("unchecked")
		final List<Logging> loggingData = loggingQuery.getResultList();
		return ok(views.html.fileio.showLogging.render("Hogstdata",loggingForm, loggingData));
	}
	
	@Transactional
	@SubjectPresent
	public static Result submitNewLoggingData() {
		Form<Logging> submittedForm = form(Logging.class).bindFromRequest();
		if (submittedForm.hasErrors()) {
			return ok("");
		} else {
			Logging newLoggingData = submittedForm.get();
			newLoggingData.save();
			return ok(String.valueOf(Json.toJson(newLoggingData)));

		}
	}
	@Transactional
	@SubjectPresent
	public static Result deleteLoggingData(String id) {
		Logging.findById(Integer.valueOf(id)).delete();
		return ok("");
	}
}
