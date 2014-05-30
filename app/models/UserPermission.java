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
package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import be.objectify.deadbolt.core.models.Permission;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
@Entity
public class UserPermission implements Permission {
	@Id
	public Long		id;

	@Column(name = "permission_value")
	public String	value;

	public String getValue() {
		return value;
	}

	public static UserPermission findByValue(String value) {
		return JPA.em().find(UserPermission.class, value);
	}
}
