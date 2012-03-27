package com.easysql.engine.xml;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.easysql.Const;
import com.easysql.IfMap;
import com.easysql.core.Entity;
import com.easysql.core.ObjectManage;
import com.easysql.core.Mapping;

@SuppressWarnings("unchecked")
public class XmlAnalyze extends ObjectManage {

	public void init() {
		Document doc = createDocument();

		putSinge(doc.selectNodes(XmlNamespace.GENERATOR),
				XmlNamespace.GENERATOR, "class");

		initEntitys(doc);

		initFieldRule(doc);
	}

	private void initFieldRule(Document doc) {
		putSinge(doc.selectNodes(XmlNamespace.FIELD_RULE),
				XmlNamespace.FIELD_RULE, "class");
	}

	private void initEntitys(Document doc) {
		List<Element> list = doc.selectNodes(XmlNamespace.ENTITY);
		for (Element e : list) {
			String className = e.attributeValue("class");
			try {
				Class clazz = Class.forName(className);
				Object instance = clazz.newInstance();

				Mapping.getInstance().put(className, instance);

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
		IfMap ifmap = (IfMap) getOldMethod.invoke(instance, new Object[] {});
		if (ifmap.isEmpty()) {
			ifmap = new IfMap();
		}

		Mapping.getInstance().put(Const.key(clazz), ifmap);
	}

	@SuppressWarnings("unused")
	private void putSinge(List<Element> list, String keymap, String keyatt) {
		for (Element ele : list) {
			Mapping.getInstance().put(keymap, ele.attributeValue(keyatt));
		}
	}

	private Document createDocument() {

		String file = "D:\\work\\myeclipse6.6\\easySQL\\src.config\\easySQL.xml";
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
		System.out.println(Mapping.getInstance().get(XmlNamespace.GENERATOR));
		System.out.println(Mapping.getInstance().get(XmlNamespace.ENTITY));
	}
}