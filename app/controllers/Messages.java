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

/**
 * 
 * Contains a BootStrap alertbox. Used mosly for Flash messages
 * 
 * @author Anders
 */
public class Messages {

	/**
	 * Type can be either Success(1) Danger(2) Warning(3) or default Info(0).
	 * 
	 * @param message
	 * @param type
	 * @return divMessage
	 */
	final public static String getMessageDivFor(String message, int type) {
		final String stringType;
		switch (type) {
			case (1):
				stringType = "success";
				break;
			case (2):
				stringType = "danger";
				break;
			case (3):
				stringType = "warning";
				break;
			default:
				stringType = "info";
		}
		return "<div id=\"message\" class=\"alert alert-"
				+ stringType
				+ " alert-dismissable\">"
				+ "  <button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>"
				+ "  <strong> " + message + "</strong> </div>";

	}

}
