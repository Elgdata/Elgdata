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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import play.Play;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

/**
 * Controller responsible for taking backups (dumps) and restoring the database
 * Backups are saved to the users home_directory/back. Note: User is the user
 * running the actuall Play! application on the server.
 * 
 * @author anders
 * 
 */
@Restrict(@Group("Admin"))
public class Backup extends Controller {

	// public static final String backupFolderPath = "\"" +
	// System.getProperty("user.home")
	// + "/backup/" + "\"";

	@Transactional(readOnly = true)
	public static Result showBackupConsole() {
		// System.out.println(getBackupFolder());
		File folder = new File(getBackupFolder());
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File[] arrayOfFiles = folder.listFiles();

		List<String> fileList = new ArrayList<String>();
		for (File currentFile : arrayOfFiles) {
			if (currentFile.isFile()) {
				fileList.add(currentFile.getName());
			}
		}
		return ok(views.html.backup.showConsole.render("Backup", fileList));
	}

	/**
	 * Takes a dump/backup of the mysql database (Hibernate) Backupfile name is
	 * backup_DATE.sql
	 * 
	 */
	@Transactional(readOnly = true)
	public static Result createDatabaseDump() {
		// mysqldump -u <user> -h <host> -p<password> <dbname> > <filename>
		String backupCommand = "mysqldump -u anders -h localhost -p"
				+ Play.application().configuration().getString("db.default.password")
				+ " --databases hibernate";
		Calendar now = Calendar.getInstance();
		String timeDate = now.get(Calendar.HOUR) + "-" + now.get(Calendar.MINUTE) + "-"
				+ now.get(Calendar.SECOND) + "_" + +now.get(Calendar.DAY_OF_MONTH) + "-"
				+ now.get(Calendar.MONTH) + "-" + now.get(Calendar.YEAR);

		File backupFile = new File(getBackupFolder() + "backup-" + timeDate);
		try {
			backupFile.createNewFile();
			FileWriter fileWriter = new FileWriter(backupFile.getAbsoluteFile());
			BufferedWriter bufferedFile = new BufferedWriter(fileWriter);

			Process backupExecution = Runtime.getRuntime().exec(backupCommand);
			BufferedReader bufferedTerminalReader = new BufferedReader(new InputStreamReader(
					backupExecution.getInputStream()));

			String line = null;
			while ((line = bufferedTerminalReader.readLine()) != null) {
				bufferedFile.write(line + "\n");
			}
			bufferedFile.close();
			bufferedTerminalReader.close();

			// Send to all admins
			Email.sendBackupEmailToAdmins(backupFile.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("FAIL");
			return ok("Failed");
		}
		return ok(backupFile.getName());
	}

	/**
	 * Restores the database to the specified backupfile (backup_30_3_2014.sql).
	 * Requires the 'restore' script
	 * 
	 * @param backupFileName
	 *            (/home/anders/SelectedFile.sql)
	 * @return boolean Success
	 */
	@Transactional(readOnly = true)
	public static Result restoreDatabase(String backupFileName) {
		if (createRestoreScript()) {
			String restoreCommand = getBackupFolder() + "scripts/" + "./restore "
					+ getBackupFolder() + backupFileName;
			System.out.println(restoreCommand);
			try {
				Runtime.getRuntime().exec(restoreCommand);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Unable to run command");
				return ok("Fail");
			}
			System.out.println("Restored database to: " + backupFileName);
			return ok("Databasen er naa resatt");
		} else {
			return ok("Fail");
		}
	}

	/**
	 * Creates the restore script if it does not already exist at the users
	 * home_folder/backup
	 * 
	 * @return void
	 */
	// @Transactional(readOnly = true)
	public static boolean createRestoreScript() {

		File scriptFolder = new File(getBackupFolder() + "scripts/");
		File restoreScript = new File(getBackupFolder() + "scripts/" + "restore");
		// Create folder + script if it does not already exist.
		try {
			if (!scriptFolder.exists()) {
				scriptFolder.mkdirs();
			}

			if (!restoreScript.exists()) {
				restoreScript.createNewFile();

				FileWriter fileWriter;
				fileWriter = new FileWriter(restoreScript.getAbsoluteFile());
				BufferedWriter bufferedFile = new BufferedWriter(fileWriter);
				bufferedFile.write("#!/bin/bash \n");
				bufferedFile.write("mysql --user=anders --host=localhost --password="
						+ Play.application().configuration().getString("db.default.password")
						+ " hibernate < $1");
				bufferedFile.close();
				Runtime.getRuntime().exec("chmod +x " + getBackupFolder() + "scripts/" + "restore");

			}
		} catch (IOException e) {
			System.out.println("Backup: Unable to create Restore script");
			return false;
		}
		return true;
	}

	@Transactional(readOnly = true)
	public static Result deleteDatabaseFile(String backupFileName) {

		String deleteCommand = "rm " + getBackupFolder() + backupFileName;
		try {
			Runtime.getRuntime().exec(deleteCommand);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to run command");
			return ok("Fail");
		}
		System.out.println("deleted backup: " + backupFileName);
		return ok("Slettet");
	}

	/**
	 * Replaces spaces in the path with "\ ". This makes the terminal able to
	 * understand spaces in file paths
	 * 
	 * @return Backupfolder path
	 */
	public static String getBackupFolder() {
		String backupFolderPath = System.getProperty("user.home") + "/backup/";
		return backupFolderPath.replace(" ", "\\ ");

	}
	
	@Transactional(readOnly = true)
	public static Result uploadBackupFile() {
		return ok(views.html.backup.uploadBackup.render("Velg fil"));
	}

	@Transactional(readOnly = true)
	public static Result submitBackupFile() throws IOException {
		
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart submittedFile = body.getFile("sqlfile");
		if (submittedFile != null) {
			File submittedBackupFile = submittedFile.getFile();
			submittedBackupFile.setExecutable(false);
			submittedBackupFile.setReadOnly();

			File localFile = new File(getBackupFolder() + "backup-uploaded");

			FileWriter fileWriter = new FileWriter(localFile.getAbsoluteFile());
			BufferedWriter bufferedFile = new BufferedWriter(fileWriter);

			BufferedReader br = new BufferedReader(new FileReader(submittedBackupFile));
			String line = "";

			// write file to disk
			while ((line = br.readLine()) != null) {
				bufferedFile.write(line + "\n");
				System.out.println(line + "\n");
			}
			br.close();
			bufferedFile.close();

		}
		return redirect(routes.Backup.showBackupConsole());
	}
}
