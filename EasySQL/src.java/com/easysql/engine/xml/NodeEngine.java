package com.easysql.engine.xml;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.easysql.core.ObjectManage;

@SuppressWarnings("unchecked")
public class NodeEngine extends ObjectManage {

	private static Object initLock = new Object();

	private static NodeEngine nodeEngine = null;

	public Map<String, Object> node = new HashMap<String, Object>();

	static {
		NodeEngine.getInstance().init();
	}

	private void init() {
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

				NodeEngine.getInstance().node.put(className, instance);
			} catch (Exception e1) {
				log.error(e1.getMessage(), e1);
			}
		}
	}

	public static NodeEngine getInstance() {
		if (nodeEngine == null) {
			synchronized (initLock) {
				nodeEngine = new NodeEngine();
			}
		}
		return nodeEngine;
	}

	@SuppressWarnings("unused")
	private void putSinge(List<Element> list, String keymap, String keyatt) {
		for (Element ele : list) {
			NodeEngine.getInstance().node.put(keymap, ele
					.attributeValue(keyatt));
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
		System.out.println(NodeEngine.getInstance().node
				.get(NodeNamespace.GENERATOR));
		System.out.println(NodeEngine.getInstance().node
				.get(NodeNamespace.ENTITY));
	}
}
