package cn.gov.dg.util
{
	import flash.events.Event;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	
	import mx.controls.Alert;

	/***
	 * 读取配置文件
	 * */
	public class config
	{
		[Bindable]
		static public var myxml:XML;
		static public function xmlrequest():void
		{
			var request:URLRequest = new URLRequest("cn/gov/dg/config/properties.xml");
			var loader:URLLoader = new URLLoader();
			loader.load(request);
			loader.addEventListener(Event.COMPLETE,onComplete);
		}
		static private function onComplete(event:Event):void
		{
			myxml = XML(event.target.data);
		}

		

	}
}