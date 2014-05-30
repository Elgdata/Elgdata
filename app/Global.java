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

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.api.mvc.EssentialFilter;
import play.db.jpa.Transactional;
import play.filters.gzip.GzipFilter;

/**
 * Global contains global configurations for the Play framework.
 * 
 * @author Anders
 * 
 */
public class Global extends GlobalSettings {
	/**
	 * Server START
	 */
	@Transactional
	public void onStart(Application app) {
		Logger.info("Elgdata server has now started...");
		System.out.println("Elgdata server has now started...");

	}

	/**
	 * Server STOP
	 */
	public void onStop(Application app) {
		Logger.info("Elgdata server has shutdown.");
		System.out.println("Goodbye");
	}

	/**
	 * Sends all responses as Gzip responses
	 */
	@SuppressWarnings("unchecked")
	public <T extends EssentialFilter> Class<T>[] filters() {
		return new Class[] { GzipFilter.class };
	}
}
