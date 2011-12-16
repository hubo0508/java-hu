package com.wangscu.rpc
{
	import hessian.client.HessianService;
	
	import mx.controls.Alert;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;

	public class RPC
	{
		public static function call(operation:*, callback:Function=null):*
		{
			Alert.show("operation = "+operation);
			var service:HessianService=new HessianService("http://localhost:8090/SceneView/flex/rpc");
			var responser:IResponder=new SimpleResponser(callback);
			var token:AsyncToken=service.call.send(operation);
			token.addResponder(responser);
		}
	}
}