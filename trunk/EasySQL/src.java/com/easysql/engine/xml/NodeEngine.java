package com.easysql.engine.xml;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.easysql.core.ObjectManage;
import com.easysql.core.SqlMap;

@SuppressWarnings("unchecked")
public class NodeEngine extends ObjectManage {

	public void init() {
		Document doc = createDocument();

		putSinge(doc.selectNodes(NodeNamespace.GENERATOR),
				NodeNamespace.GENERATOR, "class");

		initEntitys(doc);
	}

	private void initEntitys(Document doc) {
		List<Element> list = doc.selectNodes(NodeNamespace.ENTITY);
		for (Element e : list) {
			String className = e.attributeValue("class");
			try {
				Object instance = Class.forName(className).newInstance();

				SqlMap.getInstance().put(className, instance);
			} catch (Exception e1) {
				log.error(e1.getMessage(), e1);
			}
		}
	}

	@SuppressWarnings("unused")
	private void putSinge(List<Element> list, String keymap, String keyatt) {
		for (Element ele : list) {
			SqlMap.getInstance().put(keymap, ele.attributeValue(keyatt));
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
		System.out.println(SqlMap.getInstance().get(NodeNamespace.GENERATOR));
		System.out.println(SqlMap.getInstance().get(NodeNamespace.ENTITY));
	}
}
