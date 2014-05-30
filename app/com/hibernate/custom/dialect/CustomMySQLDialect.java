package com.hibernate.custom.dialect;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
/**
 * Custom SQL dialect configurations. Used for registering new functions with Hibernate.
 * @author Thomas
 *
 */
public class CustomMySQLDialect extends MySQL5Dialect {
	public CustomMySQLDialect() {
		super();
		registerFunction("median", new StandardSQLFunction("median"));
	}
}
