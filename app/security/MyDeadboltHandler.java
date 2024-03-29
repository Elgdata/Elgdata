/*
 * Copyright 2012 Steve Chaloner
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package security;

import controllers.routes;
import models.AuthorisedUser;
import play.libs.F;
import play.mvc.Http;
import play.mvc.SimpleResult;
import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;

/**
 * @author Steve Chaloner (steve@objectify.be) Tar seg av security Annotations
 */
public class MyDeadboltHandler extends AbstractDeadboltHandler {
	public F.Promise<SimpleResult> beforeAuthCheck(Http.Context context) {
		// returning null means that everything is OK. Return a real result if
		// you want a redirect to a login page or
		// somewhere else
		return F.Promise.pure(null);
	}

	public Subject getSubject(Http.Context context) {
		// in a real application, the user name would probably be in the session
		// following a login process
		String username = context.session().get("username");
		if (username != null) {
			return AuthorisedUser.findByUserName(username);
		} else {
			return null;
		}
	}

	public DynamicResourceHandler getDynamicResourceHandler(Http.Context context) {
		return new MyDynamicResourceHandler();
	}

	/**
	 * If user is logged on redirect to index else to logon page.
	 */
	@Override
	public F.Promise<SimpleResult> onAuthFailure(final Http.Context context, String content) {
		// you can return any result from here - forbidden, etc
		return F.Promise.promise(new F.Function0<SimpleResult>() {
			@Override
			public SimpleResult apply() throws Throwable {

				if (context.session().get("username") == null) {
					return redirect(routes.Security.loginForm());
				} else {
					return redirect("/");
				}
			}
		});
	}
}
