import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vo.Categories;
import com.vo.Chart;
import com.vo.Dataset;
import com.vo.Set;

public class Test {

	public static void main(String[] args) {

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

		List<Dataset> listDataset = new ArrayList<Dataset>();

		Dataset datasetA = new Dataset("A26", "AFD8F8");
		List<Set> listSetA = new ArrayList<Set>();
		listSetA.add(new Set("223"));
		listSetA.add(new Set("123"));
		listSetA.add(new Set("241"));
		listSetA.add(new Set("521"));
		listSetA.add(new Set("352"));
		listSetA.add(new Set("235"));
		listSetA.add(new Set("123"));
		datasetA.setSet(listSetA);
		listDataset.add(datasetA);

		Dataset datasetB = new Dataset("A27", "F6BD0F");
		List<Set> listSetB = new ArrayList<Set>();
		listSetB.add(new Set("223"));
		listSetB.add(new Set("123"));
		listSetB.add(new Set("241"));
		listSetB.add(new Set("521"));
		listSetB.add(new Set("352"));
		listSetB.add(new Set("235"));
		listSetB.add(new Set("123"));
		datasetB.setSet(listSetB);
		listDataset.add(datasetB);

		Dataset datasetC = new Dataset("A28", "8BBA00");
		List<Set> listSetC = new ArrayList<Set>();
		listSetC.add(new Set("223"));
		listSetC.add(new Set("123"));
		listSetC.add(new Set("241"));
		listSetC.add(new Set("521"));
		listSetC.add(new Set("352"));
		listSetC.add(new Set("235"));
		listSetC.add(new Set("123"));
		datasetC.setSet(listSetC);
		listDataset.add(datasetC);

		chart.setDataset(listDataset);

		Map<String, Boolean> displayNode = new HashMap<String, Boolean>();
		displayNode.put("categories", true);

//		String root = BuildEntityXML.setNodeAttributeInEntity(chart,
//				displayNode);
//		System.out.println(root);

	}
}
