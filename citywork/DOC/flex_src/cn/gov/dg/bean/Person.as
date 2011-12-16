package cn.gov.dg.bean {
	
	[Bindable]
	[RemoteClass(alias="cn.gov.dg.bean.Person")]
	public class Person {
		private var _id:Number;
		private var _name:String;
		
		public function Person()
		{
		}

		public function get name():String
		{
			return _name;
		}

		public function set name(value:String):void
		{
			_name = value;
		}

		public function get id():Number
		{
			return _id;
		}

		public function set id(value:Number):void
		{
			_id = value;
		}

	}
}