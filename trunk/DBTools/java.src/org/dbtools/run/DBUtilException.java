package org.dbtools.run;

public class DBUtilException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public final static String QUERY_UNIQUE_EXCEPTION = "QueryUniqueException";

	private String type;

	public DBUtilException(String message, Throwable cause, String type) {
		super(type + ":" + message, cause);

		this.type = type;
	}

	public DBUtilException(String message, String type) {
		super(type + ":" + message);

		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
