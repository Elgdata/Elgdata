//package controllers;
//
//import static play.data.Form.form;
//import models.UserGraph;
//import models.UserGraphCategory;
//import play.data.Form;
//import play.db.jpa.Transactional;
//import play.mvc.Controller;
//import play.mvc.Result;
//
///*
// * /*
// * 
// * @author anders Controller brukt for aa teste nye funksjoner
// */
///*
// */
//public class Application extends Controller {
//
//	private static final Form<UserGraphCategory>	ugCategoryForm	= Form.form(UserGraphCategory.class);
//	private static final Form<UserGraph>			userGraphForm	= Form.form(UserGraph.class);
//
//	@Transactional
//	public static Result index() {
//		return ok(views.html.statistics.addUserDefinedGraphCategory.render("Velkommen til ElgData",
//				ugCategoryForm));
//	}
//
//	@Transactional
//	public static Result submitNewCategory() {
//		Form<UserGraphCategory> submittedForm = form(UserGraphCategory.class).bindFromRequest();
//		if (submittedForm.hasErrors()) {
//			flash("message", Messages.getMessageDivFor("Ugyldig verdier", 2));
//			return ok(views.html.statistics.addUserDefinedGraphCategory.render("Edit Elg - Error",
//					submittedForm));
//		} else {
//			flash("message", Messages.getMessageDivFor("Ny elg opprettet", 1));
//			UserGraphCategory newCategory = submittedForm.get();
//			newCategory.save();
//			return ok("Hei!");
//
//		}
//	}
//
//	@Transactional
//	public static Result newGraph() {
//		return ok(views.html.statistics.addUserGraph.render("Velkommen til ElgData",
//				userGraphForm));
//	}
//
//	@Transactional
//	public static Result submitNewGraph() {
//		Form<UserGraph> submittedForm = form(UserGraph.class).bindFromRequest();
//		if (submittedForm.hasErrors()) {
//			flash("message", Messages.getMessageDivFor("Ugyldig verdier", 2));
//			return ok(views.html.statistics.addUserGraph.render("Edit Elg - Error",
//					submittedForm));
//		} else {
//			flash("message", Messages.getMessageDivFor("Ny elg opprettet", 1));
//			UserGraph newGraph = submittedForm.get();
//			newGraph.save();
//			return redirect("/");
//
//		}
//	}
//
//}
///*
// * 
// * 
// * // public static Result newelg() { // Form<Elk> elgForm =
// * Form.form(Elk.class); // return ok(views.html.newelg.render(elgForm)); // }
// * 
// * // public static Result submitelg() { // Form<Elk> submittedForm =
// * elgForm.bindFromRequest(); // if (submittedForm.hasErrors()) { // return
// * ok(views.html.newelg.render(submittedForm)); // } else { // Elk elg =
// * submittedForm.get(); // elg.save(); // return
// * redirect(routes.Application.index()); // } // // }
// * 
// * }
// */