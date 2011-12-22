import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hu.xnode.xml.XEntityNode;

import com.xnode.vo.Categories;
import com.xnode.vo.Chart;

public class Test {

	public static void main(String[] args) {

		//entitiesIntoNode1();
		//entitiesIntoNode2();
		entitiesIntoNode3();
		//entitiesIntoNodeList1();
		//entitiesIntoNodeList2();
	}
	
	/*
	 <categories label="A小区"/>
	 <categories label="B小区"/>
	 <categories label="C小区"/>
	 <categories label="D小区"/>
	 <categories label="E小区"/>
	 */
	public static void entitiesIntoNodeList1() {

		List<Categories> listCategories = new ArrayList<Categories>();
		listCategories.add(new Categories("A小区"));
		listCategories.add(new Categories("B小区"));
		listCategories.add(new Categories("C小区"));
		listCategories.add(new Categories("D小区"));
		listCategories.add(new Categories("E小区"));

		String xml = XEntityNode.entitiesIntoNodeList(listCategories);

		System.err.println(xml);
	}
	
	/*
	 <newNode label="A小区"/>
	<newNode label="B小区"/>
	<newNode label="C小区"/>
	<newNode label="D小区"/>
	<newNode label="E小区"/>
	 */
	public static void entitiesIntoNodeList2() {

		List<Categories> listCategories = new ArrayList<Categories>();
		listCategories.add(new Categories("A小区"));
		listCategories.add(new Categories("B小区"));
		listCategories.add(new Categories("C小区"));
		listCategories.add(new Categories("D小区"));
		listCategories.add(new Categories("E小区"));

		Map<String, Object> replaceNode = new HashMap<String, Object>();
		replaceNode.put("com.vo.Categories", "newNode");
		XEntityNode.node = replaceNode;

		String xml = XEntityNode.entitiesIntoNodeList(listCategories);

		System.err.println(xml);
	}

	/*
	 * <chart caption="Business Results 25 v 26" xAxisName="Month"
	 * yAxisName="Revenue" showValues="0" decimals="0" formatNumberScale="0"
	 * palette="4"/>
	 */
	public static void entitiesIntoNode1() {

		Chart chart = new Chart();
		chart.setDefaultValue();
		chart.setCaption("Business Results 25 v 26");
		chart.setxAxisName("Month");
		chart.setyAxisName("Revenue");
		chart.setShowValues("0");
		
		String xml = XEntityNode.entitiesIntoNode(chart);

		System.err.println(xml);
	}
	
	/*
		<chart caption="Business Results 25 v 26" xAxisName="Month" yAxisName="Revenue" showValues="0" decimals="0" formatNumberScale="0" palette="4">
		  <categories label="A小区"/>
		  <categories label="B小区"/>
		  <categories label="C小区"/>
		  <categories label="D小区"/>
		  <categories label="E小区"/>
		</chart>
	 */
	public static void entitiesIntoNode2() {

		Chart chart = new Chart();
		chart.setDefaultValue();
		chart.setCaption("Business Results 25 v 26");
		chart.setxAxisName("Month");
		chart.setyAxisName("Revenue");
		chart.setShowValues("0");

		List<Categories> listCategories = new ArrayList<Categories>();
		listCategories.add(new Categories("A小区"));
		listCategories.add(new Categories("B小区"));
		listCategories.add(new Categories("C小区"));
		listCategories.add(new Categories("D小区"));
		listCategories.add(new Categories("E小区"));

		chart.setCategories(listCategories);		
		
		String xml = XEntityNode.entitiesIntoNode(chart);

		System.err.println(xml);
	}
	
	/*
	 <chart caption="Business Results 25 v 26" xAxisName="Month" yAxisName="Revenue" showValues="0" decimals="0" formatNumberScale="0" palette="4">
	  <categories>
	    <categories label="A小区"/>
	    <categories label="B小区"/>
	    <categories label="C小区"/>
	    <categories label="D小区"/>
	    <categories label="E小区"/>
	  </categories>
	</chart>
	 */
	public static void entitiesIntoNode3() {

		Chart chart = new Chart();
		chart.setDefaultValue();
		chart.setCaption("Business Results 25 v 26");
		chart.setxAxisName("Month");
		chart.setyAxisName("Revenue");
		chart.setShowValues("0");

		List<Categories> listCategories = new ArrayList<Categories>();
		listCategories.add(new Categories("A小区"));
		listCategories.add(new Categories("B小区"));
		listCategories.add(new Categories("C小区"));
		listCategories.add(new Categories("D小区"));
		listCategories.add(new Categories("E小区"));

		chart.setCategories(listCategories);

		Map<String, Object> node = new HashMap<String, Object>();
		node.put("com.vo.Categories", "newNode");
		node.put("categories", true);
		
		XEntityNode.node = node;
		String xml = XEntityNode.entitiesIntoNode(chart);

		System.err.println(xml);
	}
}
