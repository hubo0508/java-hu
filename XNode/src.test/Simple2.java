import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hu.xnode.xml.XNode;

import com.xnode.domain.Application;
import com.xnode.domain.Chart;
import com.xnode.domain.Definition;
import com.xnode.domain.Styles;
import com.xnode.domain.Trendlines;
import com.xnode.domain.VLine;
import com.xnode.domain.Set;

/*
 <chart caption='Monthly Sales Summary' subcaption='For the year 2006' xAxisName='Month' yAxisName='Sales' numberPrefix='$'>
 <set label='January' value='17400' />
 <set label='February' value='19800' />
 <set label='March' value='21800' />
 <set label='April' value='23800' />
 <set label='May' value='29600' />
 <set label='June' value='27600' />
 <vLine color='FF5904' thickness='2'/>
 <set label='July' value='31800' />
 <set label='August' value='39700' />
 <set label='September' value='37800' />
 <set label='October' value='21900' />
 <set label='November' value='32900' />
 <set label='December' value='39800' />

 <trendlines>
 <line startValue='22000' color='00cc00' displayValue='Average' />
 </trendlines> 

 <styles>

 <definition>
 <style name='CanvasAnim' type='animation' param='_xScale' start='0' duration='1' />
 </definition>

 <application>
 <apply toObject='Canvas' styles='CanvasAnim' />
 </application>   

 </styles> 
 </chart>* 

 */
public class Simple2 {
	public static void main(String[] args) {

		Chart chart = new Chart();
		chart.setCaption("Monthly Sales Summary");
		chart.setSubcaption("For the year 2006");
		chart.setXAxisName("Month");
		chart.setYAxisName("Sales");
		chart.setNumberPrefix("$");

		List<Set> listSet = new ArrayList<Set>();
		listSet.add(new Set("January", "1"));
		listSet.add(new Set("February", "2"));
		listSet.add(new Set("March", "3"));
		listSet.add(new Set("April", "4"));
		listSet.add(new Set("May", "5"));
		listSet.add(new Set("June", "6"));
		listSet.add(new Set("July", "7"));
		listSet.add(new Set("August", "8"));
		listSet.add(new Set("September", "9"));
		listSet.add(new Set("October", "10"));
		listSet.add(new Set("November", "11"));
		listSet.add(new Set("December", "12"));

		List<Trendlines> trendlines = new ArrayList<Trendlines>();
		trendlines.add(new Trendlines("22000", "00cc00", "Average"));

		List<Definition> definition = new ArrayList<Definition>();
		definition.add(new Definition("CanvasAnim", "animation", "_xScale",
				"0", "1"));

		List<Application> application = new ArrayList<Application>();
		application.add(new Application("Canvas", "CanvasAnim"));

		chart.setSet(listSet);
		chart.setVLine(new VLine("2", "FF5904"));
		chart.setTrendlines(trendlines);
		chart.setStyles(new Styles(definition, application));

		Map<String, Object> node = new HashMap<String, Object>();
		node.put("com.xnode.domain.Trendlines", "line");
		node.put("com.xnode.domain.Definition", "style");
		node.put("com.xnode.domain.Application", "apply");
		node.put("trendlines", XNode.RATING);
		// node.put("styles", true);
		node.put("definition", XNode.RATING);
		node.put("application", XNode.RATING);
		String xml = new XNode(node).xmlInPojo(chart);

		System.err.println(xml);
	}
}
