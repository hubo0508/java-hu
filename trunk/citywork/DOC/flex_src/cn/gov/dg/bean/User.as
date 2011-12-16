package com.project.bean {
	
	[Bindable]
	[RemoteClass(alias="com.hongguaninfo.projectName.privilege.model.bo.User")]
	public class User {
		
		private var _id:Number;
		private var _loginName:String;
		private var _password:String;
		private var _userName:String;
		private var _email:String;
		private var _departId:Number;
		
		public function User() {}
		
		public function get departId():Number
		{
			return _departId;
		}

		public function set departId(value:Number):void
		{
			_departId = value;
		}

		public function get email():String
		{
			return _email;
		}

		public function set email(value:String):void
		{
			_email = value;
		}

		public function get userName():String
		{
			return _userName;
		}

		public function set userName(value:String):void
		{
			_userName = value;
		}

		public function get password():String
		{
			return _password;
		}

		public function set password(value:String):void
		{
			_password = value;
		}

		public function get loginName():String
		{
			return _loginName;
		}

		public function set loginName(value:String):void
		{
			_loginName = value;
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