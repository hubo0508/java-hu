package com.wangscu.rpc
{
	import hessian.client.HessianService;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.ResultEvent;

	public class SimpleResponser implements IResponder
	{
		private var callback:Function;

		public function SimpleResponser(callback:Function)
		{
			this.callback=callback;
		}

		public function result(data:Object):void
		{
			var result:*=ResultEvent(data).result;

			if (callback != null)
			{
				callback(result.data);
			}
		}

		public function fault(fault:Object):void
		{
			Alert.show("ERROR:" + fault);
		}
	}
}