package com.easysql.jdbc;

import java.lang.reflect.ParameterizedType;

public class JdbcTemplate<T,F extends java.io.Serializable> {

	private Class<T> clazz;

	@SuppressWarnings("unchecked")
	public JdbcTemplate() {

		setClazz((Class<T>) ((ParameterizedType) this.getClass()

		.getGenericSuperclass()).getActualTypeArguments()[0]);

	}

	public void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}

	public Class<T> getClazz() {
		return clazz;
	}
}
