/**
 * @Project CityWork
 * @Title ClickNavMenu.as
 * @Package com.dream.citywork.event
 * @date Sep 25, 2011 3:54:32 PM
 * @version V1.0  
 */
package com.dream.citywork.event
{
	import flash.events.Event;
	
	
	/** 
	 * author： HUBO hubo.0508ⓐgmail.com    /  date：Jan 23, 2011 7:08:40 PM
	 */
	public class ClickNavMenu extends Event
	{
		
		public static const CLICK_NAV_MENU:String = "clickNavMenu";
		
		public static const NAV_MENU_TYPE_RIGHT:String = "RIGHT";
		
		public static const NAV_MENU_TYPE_LEFT:String = "LEFT";
		
		public var eventtype:String;
		public var menuName:String;
		public var navmenutype:String;
		public var id:String;
		
		public var menutype:String;
		public var resources:String;
		
		public function ClickNavMenu(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
	}
}