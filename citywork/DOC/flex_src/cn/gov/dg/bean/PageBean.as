package cn.gov.dg.bean
{
	/**
	 * 初始化页面显示的要素实体
	 * @author	Phirothing
	 * @date	Sep 24, 2011
	 */
	[Bindable]
	[RemoteClass(alias="cn.gov.dg.bean.PageBean")]
	public class PageBean
	{
		private var _bgImag:String;		// 背景图片
		private var _backButton:String;     // 返回按钮图片
		private var _rightMenuTop:String;	// 距离顶部距离
		private var _rightMenuBottom:String;	// 距离底部距离
		private var _rightMenuLeft:String;	// 距离左侧距离
		private var _rightMenuRight:String;	// 距离右侧距离
		private var _rightMenuUpButton:String;		// 向上翻页button图片
		private var _rightMenuDownButton:String;	// 向下翻页button图片
		private var _rightMenuButtonText:String;	// 右侧按钮显示文本
		
		public function MenuBean()
		{
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

		public function get backButton():String
		{
			return _backButton;
		}

		public function set backButton(value:String):void
		{
			_backButton = value;
		}

		public function get bgImag():String
		{
			return _bgImag;
		}

		public function set bgImag(value:String):void
		{
			_bgImag = value;
		}

	}
}