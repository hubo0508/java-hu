package cn.gov.dg.bean
{
	/***
	 * 场景实体
	 * */
	[Bindable]
	[RemoteClass(alias = "cn.gov.dg.bean.SceneBean")]
	public class SceneBean
	{
		private var _sceneType:String;// 场景类型
		private var _sceneId:String;// 场景ID
		private var _sceneName:String;// 场景名称

		public function get sceneName():String
		{
			return _sceneName;
		}

		public function set sceneName(value:String):void
		{
			_sceneName = value;
		}

		public function get sceneId():String
		{
			return _sceneId;
		}

		public function set sceneId(value:String):void
		{
			_sceneId = value;
		}

		public function get sceneType():String
		{
			return _sceneType;
		}

		public function set sceneType(value:String):void
		{
			_sceneType = value;
		}

	}
}