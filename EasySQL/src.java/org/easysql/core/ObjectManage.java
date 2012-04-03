package org.easysql.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ObjectManage<T extends java.io.Serializable> {

	protected Log log = LogFactory.getLog(ObjectManage.class);

	private String canonicalName;

	private Class<Entity> clazz;
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		List list = new ArrayList();
		if(list.getClass().newInstance() instanceof List){
			System.out.println("xx");
		}
		
		if(list.getClass().isInstance(list)){
			System.out.println("xx");
		}
	}
	
	protected static Map<String, Boolean> collection = new HashMap<String, Boolean>();

	static {
		collection.put("interface java.util.List", true);
		collection.put("interface java.util.Map", true);
		collection.put("interface java.util.Set", true);
	}

	public ObjectManage() {
		super();
	}
	
	protected boolean isExtendsEntity() {

		if (clazz == null) {
			throw new RuntimeException("NULL class");
		}

		if (clazz.getSuperclass().getCanonicalName().toString().equals(
				"org.easysql.core.Entity")) {
			return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public ObjectManage(String canonicalName) {
		super();
		this.canonicalName = canonicalName;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	public Class<Entity> getClazz() {
		return clazz;
	}

	public void setClazz(Class<Entity> clazz) {
		this.clazz = clazz;
	}

}
