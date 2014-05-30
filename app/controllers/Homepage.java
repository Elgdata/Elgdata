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

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
/**
 * Simple controllers used for rendering the mainpage views.
 * @author anders
 *
 */
public class Homepage extends Controller {

	@Transactional
	public static Result index() {
		return ok(views.html.homepage.index.render(HomePageStatistics.getStats()));
	}

	public static Result showcsv() {
		return ok(views.html.homepage.showCSV.render("How to export to CSV format"));
	}
	
	public static Result csvMal() {
		return ok(views.html.help.CSVmal.render("CSV Mal"));
	}

}
