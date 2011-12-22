import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hu.xnode.xml.XEntityNode;

import com.xnode.domain.Chart;
import com.xnode.domain.Line;
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
		listSet.add(new Set("January","1"));
		listSet.add(new Set("February","2"));
		listSet.add(new Set("March","3"));
		listSet.add(new Set("April","4"));
		listSet.add(new Set("May","5"));
		listSet.add(new Set("June","6"));
		listSet.add(new Set("July","7"));
		listSet.add(new Set("August","8"));
		listSet.add(new Set("September","9"));
		listSet.add(new Set("October","10"));
		listSet.add(new Set("November","11"));
		listSet.add(new Set("December","12"));
		
		chart.setSet(listSet);
		chart.setLine(new Line("2","FF5904"));
		
		Map<String, Object> node = new HashMap<String, Object>();
		node.put("com.xnode.domain.Line", "vLine");
		node.put("categories", true);
		XEntityNode.node = node;
		String xml = XEntityNode.entitiesIntoNode(chart);

		System.err.println(xml);
	}
}
