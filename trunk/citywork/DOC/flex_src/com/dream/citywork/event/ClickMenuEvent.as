/**
 * @Project CityWork
 * @Title ClickMenuEvent.as
 * @Package com.dream.citywork.event
 * @date Oct 1, 2011 11:16:33 PM
 * @version V1.0  
 */
package com.dream.citywork.event
{
	import flash.events.Event;
	
	
	/** 
	 * author： HUBO hubo.0508ⓐgmail.com    /  date：Jan 23, 2011 7:08:40 PM
	 */
	public class ClickMenuEvent extends Event
	{
		
		public static const CLICK_MENU_EVENT:String = "clickMenuEvent";
		
		public var label:String;
		
		public var itemObj:Object;
		
		public var eventType:String;
		
		public var id:String;
		
		public function ClickMenuEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
	}
}