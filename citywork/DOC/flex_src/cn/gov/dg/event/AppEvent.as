package cn.gov.dg.event
{
	import flash.events.Event;
	
	public class AppEvent extends Event 
	{
		public static const BEAN_LOADED:String = "beanLoaded";
		public static const CONFIG_LOADED:String = "configLoaded";
		public static const SUBNETWORK_CONFIG_LOADED:String = "subnetworkConfigLoaded";
		public static const SHOW_INFOWINDOW:String = "SHOW_INFOWINDOW"; //显示信息框
		public static const LOAD_WIDGET:String = "LOAD_WIDGET";
		public static const CLOSED_WIDGET:String = "CLOSED_WIDGET";
		public static const WIDGET_MENU_CLICKED:String = "widget_menu_clicked";
		private var _data:Object;
		public function AppEvent(type:String,  data:Object=null,bubbles:Boolean=false, cancelable:Boolean=false)
		{
			if (data != null) this.data = data;
			super(type, bubbles, cancelable);
		}
		
		public function  set data(_data:Object):void{
			this._data = _data;
		}
		
		public function get data():Object{
			return this._data;
		}

	}
}