package cn.gov.dg.bean
{
	/**
	 * 菜单交互需要的实体Bean
	 * @author	Phirothing
	 * @date	Sep 24, 2011
	 */
	[Bindable]
	[RemoteClass(alias="cn.gov.dg.bean.MenuBean")]
	public class MenuBean
	{
		private var _id:String;//菜单id
		private var _parentId:String;//父级菜单id
		private var _leftMenuTop:String;// 距离顶部距离
		private var _leftMenuBottom:String;// 距离底部距离
		private var _leftMenuLeft:String;// 距离左侧距离
		private var _leftMenuRight:String;// 距离右侧距离
		private var _leftMenuLinkButton:String;// button图片
		private var _leftMenuUpButton:String;// 向上翻页button图片
		private var _leftMenuDownButton:String;// 向下翻页button图片
		private var _leftMenuLevelButton:String;// 菜单层级按钮图片
		private var _leftMenuText:String;// 左侧菜单显示文本
		private var _rightMenuTop:String;// 距离顶部距离
		private var _rightMenuBottom:String// 距离底部距离
		private var _rightMenuLeft:String;// 距离左侧距离
		private var _rightMenuRight:String;// 距离右侧距离
		private var _rightMenuLinkButton:String;// button图片
		private var _rightMenuUpButton:String;// 向上翻页button图片
		private var _rightMenuDownButton:String;// 向下翻页button图片
		private var _rightMenuButtonText:String;// 右侧按钮显示文本
		private var _rightMenuButtonLinkType:String;// 右侧按钮动作类型
		private var _rightMenuButtonLinkText:String;// 右侧按钮点击弹出页面
		private var _rightMenuButtonLinkUrl:String;// 右侧按钮点击弹出页面
		
		public function get parentId():String
		{
			return _parentId;
		}

		public function set parentId(value:String):void
		{
			_parentId = value;
		}

		public function get id():String
		{
			return _id;
		}

		public function set id(value:String):void
		{
			_id = value;
		}

		public function get rightMenuButtonLinkUrl():String
		{
			return _rightMenuButtonLinkUrl;
		}

		public function set rightMenuButtonLinkUrl(value:String):void
		{
			_rightMenuButtonLinkUrl = value;
		}

		public function get rightMenuButtonLinkText():String
		{
			return _rightMenuButtonLinkText;
		}

		public function set rightMenuButtonLinkText(value:String):void
		{
			_rightMenuButtonLinkText = value;
		}

		public function get rightMenuButtonLinkType():String
		{
			return _rightMenuButtonLinkType;
		}

		public function set rightMenuButtonLinkType(value:String):void
		{
			_rightMenuButtonLinkType = value;
		}

		public function get rightMenuButtonText():String
		{
			return _rightMenuButtonText;
		}

		public function set rightMenuButtonText(value:String):void
		{
			_rightMenuButtonText = value;
		}

		public function get rightMenuDownButton():String
		{
			return _rightMenuDownButton;
		}

		public function set rightMenuDownButton(value:String):void
		{
			_rightMenuDownButton = value;
		}

		public function get rightMenuUpButton():String
		{
			return _rightMenuUpButton;
		}

		public function set rightMenuUpButton(value:String):void
		{
			_rightMenuUpButton = value;
		}

		public function get rightMenuLinkButton():String
		{
			return _rightMenuLinkButton;
		}

		public function set rightMenuLinkButton(value:String):void
		{
			_rightMenuLinkButton = value;
		}

		public function get rightMenuRight():String
		{
			return _rightMenuRight;
		}

		public function set rightMenuRight(value:String):void
		{
			_rightMenuRight = value;
		}

		public function get rightMenuLeft():String
		{
			return _rightMenuLeft;
		}

		public function set rightMenuLeft(value:String):void
		{
			_rightMenuLeft = value;
		}

		public function get rightMenuBottom():String
		{
			return _rightMenuBottom;
		}

		public function set rightMenuBottom(value:String):void
		{
			_rightMenuBottom = value;
		}

		public function get rightMenuTop():String
		{
			return _rightMenuTop;
		}

		public function set rightMenuTop(value:String):void
		{
			_rightMenuTop = value;
		}

		public function get leftMenuText():String
		{
			return _leftMenuText;
		}

		public function set leftMenuText(value:String):void
		{
			_leftMenuText = value;
		}

		public function get leftMenuLevelButton():String
		{
			return _leftMenuLevelButton;
		}

		public function set leftMenuLevelButton(value:String):void
		{
			_leftMenuLevelButton = value;
		}

		public function get leftMenuDownButton():String
		{
			return _leftMenuDownButton;
		}

		public function set leftMenuDownButton(value:String):void
		{
			_leftMenuDownButton = value;
		}

		public function get leftMenuUpButton():String
		{
			return _leftMenuUpButton;
		}

		public function set leftMenuUpButton(value:String):void
		{
			_leftMenuUpButton = value;
		}

		public function get leftMenuLinkButton():String
		{
			return _leftMenuLinkButton;
		}

		public function set leftMenuLinkButton(value:String):void
		{
			_leftMenuLinkButton = value;
		}

		public function get leftMenuRight():String
		{
			return _leftMenuRight;
		}

		public function set leftMenuRight(value:String):void
		{
			_leftMenuRight = value;
		}

		public function get leftMenuLeft():String
		{
			return _leftMenuLeft;
		}

		public function set leftMenuLeft(value:String):void
		{
			_leftMenuLeft = value;
		}

		public function get leftMenuBottom():String
		{
			return _leftMenuBottom;
		}

		public function set leftMenuBottom(value:String):void
		{
			_leftMenuBottom = value;
		}

		public function get leftMenuTop():String
		{
			return _leftMenuTop;
		}

		public function set leftMenuTop(value:String):void
		{
			_leftMenuTop = value;
		}

	}
}