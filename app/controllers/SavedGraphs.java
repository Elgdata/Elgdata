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

import javax.persistence.Query;

import models.UserGraph;
import models.UserGraphCategory;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import be.objectify.deadbolt.java.actions.SubjectPresent;
/**
 * Used save graphs(URLs) from UserDefinedGraph. 
 * @author Anders
 *
 */
public class SavedGraphs extends Controller {

	private static final Form<UserGraphCategory>	categoryForm	= Form.form(UserGraphCategory.class);
	private static final Form<UserGraph>			graphForm		= Form.form(UserGraph.class);

	@Transactional(readOnly = true)
	public static Result showGraphs() {

		Query categoryQuery = JPA.em().createQuery("FROM UserGraphCategory ORDER BY name ASC");
		@SuppressWarnings("unchecked")
		final List<UserGraphCategory> categories = categoryQuery.getResultList();

		Query graphQuery = JPA.em().createQuery("FROM UserGraph ORDER BY name ASC");
		@SuppressWarnings("unchecked")
		final List<UserGraph> graphs = graphQuery.getResultList();

		return ok(views.html.savedgraphs.showGraphs.render("Lagrede grafer", categories, graphs,
				graphForm, categoryForm));
	}

	@Transactional
	@SubjectPresent
	public static Result submitNewCategory() {
		Form<UserGraphCategory> submittedForm = form(UserGraphCategory.class).bindFromRequest();
		if (submittedForm.hasErrors()) {
			return ok();
		} else {
			UserGraphCategory newCategory = submittedForm.get();
			newCategory.save();
			return ok(String.valueOf(Json.toJson(newCategory)));

		}
	}

	@Transactional
	@SubjectPresent
	public static Result submitNewGraph() {
		Form<UserGraph> submittedForm = form(UserGraph.class).bindFromRequest();
		if (submittedForm.hasErrors()) {
			return ok();
		} else {
			UserGraph newGraph = submittedForm.get();
			newGraph.save();
			return ok(String.valueOf(Json.toJson(newGraph)));
		}
	}
	
	@Transactional
	@SubjectPresent
	public static Result submitEditedGraph() {
		Form<UserGraph> submittedForm = form(UserGraph.class).bindFromRequest();
		if (submittedForm.hasErrors()) {
			return ok();
		} else {
			UserGraph newGraph = submittedForm.get();
			newGraph.save();
			return redirect(routes.SavedGraphs.showGraphs());
		}
	}

	@Transactional(readOnly = true)
	public static Result editGraphForm(Integer id) {
		Form<UserGraph> editGraphForm = form(UserGraph.class).fill(UserGraph.findById(id));
		return ok(views.html.savedgraphs.editGraph.render("Rediger graf", editGraphForm));
	}

	@Transactional
	@SubjectPresent
	public static Result deleteGraph(String id) {
		UserGraph.findById(Integer.valueOf(id)).delete();
		return ok();
	}
	
	@Transactional
	@SubjectPresent
	public static Result deleteCategory(String id) {
		UserGraphCategory.findById(Integer.valueOf(id)).delete();
		return ok();
	}

}
