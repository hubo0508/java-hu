package org.hu.xnode.xml;

public class XNodeException extends RuntimeException {

	private static final long serialVersionUID = -1942475691680232903L;

	public XNodeException(String msg) {
		super(msg);
	}

	public XNodeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public XNodeException(Throwable cause) {
		super(cause);
	}
}
