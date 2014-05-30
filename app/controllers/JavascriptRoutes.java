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

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
/**
 * Contains all the routes for JavaScript method calls. Makes javascript able to run methods in controllers.
 * @author Anders
 *
 */
public class JavascriptRoutes extends Controller {

	// Javascript Routes
	public static Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(Routes.javascriptRouter(
				"jsRoutes",
				// Routes

				// Statistics
				controllers.routes.javascript.Statistics.newCohortWeight(),
				// Saved Graphs
				controllers.routes.javascript.SavedGraphs.deleteGraph(),
				controllers.routes.javascript.SavedGraphs.submitNewGraph(),
				controllers.routes.javascript.SavedGraphs.deleteCategory(),
				controllers.routes.javascript.SavedGraphs.submitNewCategory(),
				// Security
				controllers.routes.javascript.Security.deleteUser(),
				controllers.routes.javascript.Security.submitRegisterForm(),
				// TableIO
				controllers.routes.javascript.TableIO.deleteElk(),
				// FileIO
				controllers.routes.javascript.FileIO.deleteLoggingData(),
				controllers.routes.javascript.FileIO.submitNewLoggingData(),
//				 Backup
				controllers.routes.javascript.Backup.createDatabaseDump(),
				controllers.routes.javascript.Backup.restoreDatabase(),
				controllers.routes.javascript.Backup.deleteDatabaseFile()
				

		));
	}
}
