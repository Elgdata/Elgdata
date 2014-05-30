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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import models.AuthorisedUser;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectNotPresent;

/**
 * Controller used for the security aspect of the application (Logon, new users
 * etc)
 * 
 * @author Anders Candasamy
 * 
 */

public class Security extends Controller {

	@Transactional(readOnly = true)
	@Restrict(@Group("Admin"))
	public static Result showUsers() {
		@SuppressWarnings("unchecked")
		List<AuthorisedUser> allUsers = JPA.em().createQuery("FROM AuthorisedUser").getResultList();

		Form<AuthorisedUser> registerForm = form(AuthorisedUser.class);

		return ok(views.html.security.showUsers.render("Administrer brukere", allUsers,
				registerForm));
	}

	@Transactional(readOnly = true)
	@SubjectNotPresent
	public static Result loginForm() {
		Form<AuthorisedUser> loginForm = form(AuthorisedUser.class);
		return ok(views.html.security.login.render("Login", loginForm));
	}

	@Transactional
	public static Result submitLoginForm() {
		Form<AuthorisedUser> submittedForm = form(AuthorisedUser.class).bindFromRequest();
		if (submittedForm.hasErrors()) {
			// Empty/Illegal fields
			return ok(views.html.security.login.render("Login", submittedForm));
		}
		AuthorisedUser currentUser = submittedForm.get();
		// See if this user is registered.
		if (AuthorisedUser.findByUserNameWithPassword(currentUser.username, currentUser.password) != null) {
			// This is a user. Store username in session.
			session("username", currentUser.username);
			flash("message",
					Messages.getMessageDivFor("Du har blitt logget inn som bruker "
							+ currentUser.username, 1));
			return redirect("/");
		}
		flash("message", Messages.getMessageDivFor("Feil brukernavn/passord", 2));
		return redirect("/security/login");
	}

	@Transactional(readOnly = true)
	// @SubjectPresent
	@Restrict(@Group("Admin"))
	public static Result registerUserForm() {
		Form<AuthorisedUser> registerForm = form(AuthorisedUser.class);
		return ok(views.html.security.register.render("Register ny bruker", registerForm));
	}

	@Transactional
	@Restrict(@Group("Admin"))
	/**
	 * Creates a new user if information given is valid. Creates a salt for the MD5 hashed password
	 */
	public static Result submitRegisterForm() {
		Form<AuthorisedUser> submittedUserForm = form(AuthorisedUser.class).bindFromRequest();
		if (submittedUserForm.hasErrors()) {
			// Empty fields submitted
			// return ok(views.html.security.register.render("Register",
			// submittedUserForm));
			return ok("");
		}
		// Try to register newUser from input
		AuthorisedUser newUser = submittedUserForm.get();
		if (AuthorisedUser.findByUserName(newUser.username) == null) {
			// Username is available
			newUser.salt = org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(32);
			newUser.password += newUser.salt;
			newUser.password = Security.getMD5Hashed(newUser.password);
			newUser.save();

			Email.sendEmail(
					"Velkommen til Elgdata",
					"Hei "
							+ newUser.username
							+ "\n\n Du mottar denne mailen fordi du er blitt opprettet som bruker hos Elgdata.no \n\n Med vennlig hilsen \n Elgdata.no",
					newUser.username);

			String returnString = newUser.id + ";" + newUser.username + ";"
					+ newUser.role.getName();
			return ok(returnString);

		} else {
			// Username is already in use.
			flash("message", Messages.getMessageDivFor("Brukernavn er opptatt", 2));
			return ok();
		}

	}

	@Transactional
	@Restrict(@Group("Admin"))
	public static Result deleteUser(String id) {
		Long sumAdmins = (Long) JPA.em()
				.createQuery("SELECT COUNT(*) FROM AuthorisedUser WHERE role.id = 1")
				.getSingleResult();
		if (sumAdmins > 1) {
			AuthorisedUser.findById(Long.parseLong(id)).delete();
			return ok("Slettet");
		} else {
			return ok("");
		}

	}

	@Transactional
	public static Result logout() {
		session().clear();
		flash("message", Messages.getMessageDivFor("Du har blitt logget ut", 1));
		return redirect("/");

	}

	final public static String getMD5Hashed(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes("UTF-8"));
			byte[] digest = md.digest();
			BigInteger bigPasswordInt = new BigInteger(1, digest);
			return bigPasswordInt.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
