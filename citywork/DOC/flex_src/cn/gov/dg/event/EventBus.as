package cn.gov.dg.event
{
	import flash.events.Event;
	import flash.events.EventDispatcher;
	
	
	
	public class EventBus extends EventDispatcher
	{
		private static var _eventBus:EventBus;
		
		public static function getInstance():EventBus{
			if(_eventBus == null)
				_eventBus = new EventBus();
			return _eventBus;
		}
		
		

	}
}