package com.easysql.xml;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.easysql.EasySQL;
import com.easysql.core.Entity;
import com.easysql.core.ObjectManage;
import com.easysql.core.Mapping;
import com.easysql.handlers.EntityFilter;

@SuppressWarnings("unchecked")
public class XmlAnalyze extends ObjectManage {

	public void init() {

		log.info("To initialize EasySQL configuration.");

		Document doc = createDocument();

		putSinge(doc.selectNodes(EasySQL.GENERATOR), EasySQL.GENERATOR, "class");
		putSinge(doc.selectNodes(EasySQL.DATABASE), EasySQL.DATABASE, "class");
		putSinge(doc.selectNodes(EasySQL.GENERATOR_SEQ_VALUE),
				EasySQL.GENERATOR_SEQ_VALUE, "value");

		initEntitys(doc);

		initFieldRule(doc);
	}

	private void initFieldRule(Document doc) {
		putSinge(doc.selectNodes(EasySQL.FIELD_RULE), EasySQL.FIELD_RULE,
				"class");
	}

	private void initEntitys(Document doc) {

		List<Element> list = doc.selectNodes(EasySQL.ENTITY);
		for (Element e : list) {
			String className = e.attributeValue("class");
			try {
				Class clazz = Class.forName(className);
				Object instance = clazz.newInstance();

				Mapping.getInstance().put(className, instance);

				log.info("Initialized entities:" + className);

				initFilterConditions(clazz, instance);
			} catch (Exception e1) {
				log.error(e1.getMessage(), e1);
			}
		}
	}

	private void initFilterConditions(Class clazz, Object instance)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		Method getOldMethod = clazz.getMethod(Entity.NOT_TAKE, new Class[] {});
		EntityFilter ifmap = (EntityFilter) getOldMethod.invoke(instance,
				new Object[] {});
		if (ifmap == null) {
			ifmap = new EntityFilter();
		}

		Mapping.getInstance().put(EasySQL.key(clazz), ifmap);
	}

	@SuppressWarnings("unused")
	private void putSinge(List<Element> list, String keymap, String keyatt) {
		for (Element ele : list) {
			Mapping.getInstance().put(keymap, ele.attributeValue(keyatt));
		}
	}

	private Document createDocument() {

		String file = "D:\\work\\myeclipse6.6\\EasySQL\\src.java\\easySQL.xml";
		try {
			SAXReader reader = new SAXReader();

			Document doc = reader.read(new File(file));

			return doc;
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws DocumentException {

		new XmlAnalyze().init();
		Mapping m = Mapping.getInstance();

		System.out.println(m.get(EasySQL.GENERATOR));
		System.out.println(m.get(EasySQL.DATABASE));
		System.out.println(m.get(EasySQL.GENERATOR_SEQ_VALUE));
	}
}
