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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import be.objectify.deadbolt.core.models.Role;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
@Entity
public class SecurityRole implements Role {
	@Id
	@Column(name = "SecurityRole_ID")
	public Long		id;

	public String	name;

	public String getName() {
		return name;
	}

	public static SecurityRole findById(Long id) {
//		System.out.println("SecurityRole: " + JPA.em().find(SecurityRole.class, Name));
		return JPA.em().find(SecurityRole.class, id);
	}

	public static Map<String, String> options() {
		@SuppressWarnings("unchecked")
		List<SecurityRole> securityyRole = JPA.em().createQuery("from SecurityRole order by name")
				.getResultList();
		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		for (SecurityRole sr : securityyRole) {
			options.put(sr.id.toString(), sr.name);
		}
		return options;
	}

	@Transactional
	public void save() {
		JPA.em().persist(this);
	}
	
	@Transactional
	public void merge() {
		JPA.em().merge(this);
	}

}
