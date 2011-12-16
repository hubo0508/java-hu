package com.project.action {
	
	import com.project.service.UserService;
	
	import mx.controls.Alert;
	import mx.rpc.events.ResultEvent;
	
	public class UserViewAction {
		public var userService:UserService;
		
		public function displayInfo():void {
			userService.findUserByName("admin", function(data:Object):void {
				var rs:ResultEvent = data as ResultEvent;
				var arr:Array = rs.result as Array;
				
				Alert.show(arr + " Array");
			});
		}
		
	}
}