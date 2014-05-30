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

import java.util.List;

import models.AuthorisedUser;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import play.Play;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.F;
import play.libs.F.Function0;
import play.mvc.Controller;
import play.mvc.Http;
import akka.DbExecutionContext;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

/**
 * Controller used to send emails. Uses the email address defined in the
 * 'build.sbt' file (apache.email.address) Only supports Gmail accounts.
 * 
 * @author anders
 * 
 */

@Restrict(@Group("Admin"))
public class Email extends Controller {

	private final static String	emailAddress	= Play.application().configuration()
														.getString("apache.email.address");

	/**
	 * Function for sending plain emails.
	 * 
	 * @param subject
	 * @param message
	 * @param eMailAddress
	 */
	public static void sendEmail(final String subject, final String message,
			final String eMailAddress) {
		F.Promise.promise(new Function0<Void>() {
			@Override
			public Void apply() throws Throwable {
				org.apache.commons.mail.Email email = new SimpleEmail();
				email.setSmtpPort(587);
				email.setHostName("smtp.gmail.com");
				email.setAuthentication(emailAddress,
						Play.application().configuration().getString("apache.email.password"));
				email.setStartTLSEnabled(true);
				try {
					email.setFrom(emailAddress, "Elgdata");
					email.setSubject(subject);
					email.setMsg(message);
					email.addTo(eMailAddress);
					email.send();
				} catch (EmailException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		}, DbExecutionContext.ctx);
	}

	@Transactional(readOnly = true)
	public static void sendBackupEmailToAdmins(final String fullBackupFilePath) {
		F.Promise.promise(new Function0<Void>() {
			@Override
			public Void apply() throws Throwable {
				Http.Context.current.remove();
				return JPA.withTransaction(new F.Function0<Void>() {
					@Override
					public Void apply() throws Throwable {

						// Add attachment
						EmailAttachment attachment = new EmailAttachment();

						attachment.setPath(fullBackupFilePath);
						attachment.setDisposition(EmailAttachment.ATTACHMENT);
						attachment.setName("Backup script");

						MultiPartEmail email = new MultiPartEmail();
						;
						email.setSmtpPort(587);
						email.setHostName("smtp.gmail.com");
						email.setAuthentication(emailAddress, Play.application().configuration()
								.getString("apache.email.password"));
						email.setStartTLSEnabled(true);
						try {
							email.setFrom(emailAddress, "Elgdata - Automatisk backup");
							email.setSubject("Backup:");
							email.setMsg("Vedlagt ligger Backup fil.\n\n Merk: Denne filen burde ikke gis eller deles med andre. \n");
							email.attach(attachment);

							@SuppressWarnings("unchecked")
							List<AuthorisedUser> adminEmailList = JPA.em()
									.createQuery("FROM AuthorisedUser WHERE role.id = 1")
									.getResultList();
							for (AuthorisedUser currentAdmin : adminEmailList) {
								email.addTo(currentAdmin.username);
							}

							email.send();
						} catch (EmailException e) {
							e.printStackTrace();
						}
						return null;
					}
				});
			}
		}, DbExecutionContext.ctx);
	}

}
