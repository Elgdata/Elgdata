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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.validator.constraints.Email;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import controllers.Security;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
@Entity
public class AuthorisedUser implements Subject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AuthorisedUser_ID")
	public Long			id;

	@Required
	@Email
	public String		username;
	@Required
	public String		password;

	public String		salt;

	@ManyToOne
	public SecurityRole	role;

	@Override
	public List<? extends Role> getRoles() {
		List<SecurityRole> securityRole = new ArrayList<SecurityRole>();
		securityRole.add(role);
		return securityRole;
	}

	@Override
	public List<? extends Permission> getPermissions() {
		return null;
	}

	@Override
	public String getIdentifier() {
		return username;
	}

	public static AuthorisedUser findByUserName(String userName) {
		Query query = JPA.em().createQuery(
				"FROM  " + AuthorisedUser.class.getName() + " WHERE username = :username ");
		query.setParameter("username", userName.toLowerCase());

		try {
			return (AuthorisedUser) query.getSingleResult();

		} catch (NoResultException e) {
			// no user
			return null;
		}

	}

	public static AuthorisedUser findById(Long id) {
		Query query = JPA.em().createQuery(
				"FROM  " + AuthorisedUser.class.getName() + " WHERE id = :id ");
		query.setParameter("id", id);

		try {
			return (AuthorisedUser) query.getSingleResult();

		} catch (NoResultException e) {
			// no user
			return null;
		}

	}

	/**
	 * First attempts to find the user with 'findByUserName' Then salts the
	 * password and generates the MD5 hash
	 * 
	 * @param userName
	 * @param inputPassword
	 * @return AuthorisedUser object
	 */
	public static AuthorisedUser findByUserNameWithPassword(String userName, String inputPassword) {
		AuthorisedUser tryLoginUser = findByUserName(userName);
		if (tryLoginUser != null) {
			// Username is valid. Try to match password.
			if (tryLoginUser.password.equals(Security.getMD5Hashed(inputPassword
					+ tryLoginUser.salt))) {
				return tryLoginUser;
			} else {
				return null;
			}

		}
		return null;
	}

	public void save() {
		JPA.em().persist(this);
	}

	public void delete() {
		JPA.em().remove(this);
	}

	public void merge() {
		JPA.em().merge(this);
		
	}
}