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

import java.util.List;

import models.Elk;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.F.Function;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import akka.DbExecutionContext;
/**
 * Controller that shows all Elk in a table format. Also has CRUD operations on the Elk. 
 * @author anders
 *
 */
public class TableIO extends Controller {
/**
 * Display Elk in a table
 * @return Promise<Result>
 */
	public static Promise<Result> showelk() {
		final int fromYear;
		final int toYear;

		if (request().getQueryString("startYear") != null
				&& !request().getQueryString("startYear").isEmpty()) {
			fromYear = Integer.valueOf(request().getQueryString("startYear"));
		} else {
			fromYear = 2010;
		}
		if (request().getQueryString("startYear") != null
				&& !request().getQueryString("startYear").isEmpty()) {
			toYear = Integer.valueOf(request().getQueryString("endYear"));
		} else {
			toYear = 2013;
		}

		Promise<List<Elk>> allElk = getAllElk(fromYear, toYear);
		Promise<Result> result = allElk.map(new Function<List<Elk>, Result>() {

			@Override
			public Result apply(List<Elk> allElk) throws Throwable {
				return ok(views.html.tableio.showelk.render("Elg tabeller", allElk));
			}
		});
		return result;

	}
/**
 * Queries database and retrives all elk in the period fromYear to toYear
 * @param fromYear
 * @param toYear
 * @return Promise<List<allElk>>
 */
	public static Promise<List<Elk>> getAllElk(final Integer fromYear, final Integer toYear) {
		return Promise.promise(new Function0<List<Elk>>() {
			@Override
			public List<Elk> apply() throws Throwable {
				Http.Context.current.remove();
				return JPA.withTransaction(new Function0<List<Elk>>() {
					@Override
					public List<Elk> apply() throws Throwable {
						List<Elk> allElk = JPA
								.em()
								.createQuery(
										"FROM Elk AS elk WHERE YEAR(date) >= '" + fromYear
												+ "' AND YEAR(date) <= '" + toYear
												+ "' ORDER BY YEAR(date) ASC", Elk.class)
								.getResultList();
						return allElk;
					}
				});
			}
		}, DbExecutionContext.ctx);

	}

	@Transactional(readOnly = true)
	public static Result addNewElk() {
		Form<Elk> editElkForm = form(Elk.class);
		return ok(views.html.tableio.newElk.render("Opprett ny Elg", editElkForm));
	}

	@Transactional(readOnly = true)
	public static Result editElk(Long id) {
		Form<Elk> editElkForm = form(Elk.class).fill(Elk.findById(id));
		return ok(views.html.tableio.editelk.render("Rediger Elg", editElkForm));
	}

	@Transactional
	public static Result submitEditedElk() {
		Form<Elk> submittedForm = form(Elk.class).bindFromRequest();
		if (submittedForm.hasErrors()) {
			flash("message", Messages.getMessageDivFor("Ugyldig verdier", 2));
			return redirect("/showelk");
		} else {
			flash("message", Messages.getMessageDivFor("Elgen har blitt oppdatert", 1));
			Elk updatedElk = submittedForm.get();
			updatedElk.update();
			HomePageStatistics.dataChanged = true;
			return redirect("/editelk/" + updatedElk.getId());

		}
	}

	@Transactional
	public static Result submitNewElk() {
		Form<Elk> submittedForm = form(Elk.class).bindFromRequest();
		if (submittedForm.hasErrors()) {
			flash("message", Messages.getMessageDivFor("Ugyldig verdier", 2));
			return redirect("/showelk");
		} else {
			flash("message", Messages.getMessageDivFor("Ny elg opprettet", 1));
			Elk newElk = submittedForm.get();
			newElk.save();
			HomePageStatistics.dataChanged = true;
			return redirect("/editelk/" + newElk.getId());

		}
	}

	@Transactional
	public static Result deleteElk(String id) {
		Elk.findById(Long.valueOf(id)).delete();
		HomePageStatistics.dataChanged = true;
		return ok("Elg slettet");
	}
}
