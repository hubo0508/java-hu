package com.dream.citywork.view.effects
{
	import com.dream.citywork.event.ClickMenuEvent;
	import com.dream.citywork.event.ClickNavMenu;
	import com.dream.citywork.view.ThreeMenu;
	import com.dream.citywork.view.TwoMenu;
	
	import flash.display.DisplayObject;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import mx.collections.ArrayCollection;
	import mx.containers.Box;
	import mx.containers.Canvas;
	import mx.containers.VBox;
	import mx.containers.ViewStack;
	import mx.controls.CheckBox;
	import mx.controls.ColorPicker;
	import mx.controls.Label;
	import mx.core.Container;
	import mx.effects.Effect;
	import mx.effects.easing.Back;
	import mx.effects.easing.Bounce;
	import mx.effects.easing.Circular;
	import mx.effects.easing.Cubic;
	import mx.effects.easing.Elastic;
	import mx.effects.easing.Exponential;
	import mx.effects.easing.Linear;
	import mx.effects.easing.Quadratic;
	import mx.effects.easing.Quartic;
	import mx.effects.easing.Quintic;
	import mx.effects.easing.Sine;
	import mx.events.FlexEvent;
	import mx.events.ItemClickEvent;
	import mx.events.ScrollEvent;
	
	import spark.components.BorderContainer;
	import spark.components.Button;
	
	[Event(name="clickMenuEvent",type="com.dream.citywork.event.ClickMenuEvent")]
	
	public class ViewStackEffectExplorer extends VBox
	{
		
		public static const MARK1:String = "MARK1";
		public static const MARK2:String = "MARK2";
		public static const MARK3:String = "MARK3";
		
		private var _viewStack:ViewStack;
		private var _vbox1:VBox;	
		private var _vbox2:VBox;
		private var _vbox3:VBox;
		private var _twomenu:TwoMenu;
		private var _effect:Effect;
		
		private var _boxWidth:int = 100;
		private var _boxHeight:int = 100;
		
		private var timer:Timer;
		
		public var scrollSpeed:Number = 500;
		
		
		public function ViewStackEffectExplorer()
		{
			super();
			
//			this.setStyle("paddingLeft",20);
//			this.setStyle("paddingTop",
			
			//timer = new Timer(scrollSpeed);
		}	
		
		//type current
		public function initMenu(type:String,value:Object):void
		{
			if (value == null)return;
			
			if(value is ArrayCollection){
				if(type == MARK1){//Click on the current item
					if (_vbox1.numElements > 0)
					{
						_vbox1.removeAllElements();
					}
					this.displayMenu(0,_vbox1,value as ArrayCollection);
				} 
				
				if(type == MARK2){//Click on the current item
//					if (_twomenu.numElements > 0)
//					{
//						_twomenu.removeAllElements();
//					}
					//this.displayMenu(1,_vbox2,value as ArrayCollection);
					var flag:Boolean = _twomenu.initMenuItem(value as ArrayCollection);
					if(flag){ 
						this.selectIndex(1);
					}		
				}		
			}
			
//			if(value is TwoMenu && type == MARK2){
//				if (_vbox2.numElements > 0)
//				{
//					_vbox2.removeAllElements();
//				}
//				_vbox2.addChild(value as DisplayObject);
//				this.selectIndex(1);
//			}	
			
			if(value is ThreeMenu && type == MARK3){
				if (_vbox3.numElements > 0)
				{
					_vbox3.removeAllElements();
				}
				_vbox3.addChild(value as DisplayObject);
				this.selectIndex(2);
			}			
		}
		
		public function selectIndex(index:int):void
		{
			_viewStack.selectedIndex = index;
		}

		private function displayMenu(index:int,box:VBox,value:ArrayCollection):void
		{
			for (var i:int=0; i < value.length; i++)
			{
				var item:Object=value.getItemAt(i);
				var btnMenu:Button=this.createMenu(item);
				btnMenu.addEventListener(MouseEvent.CLICK, btnMenu_ClickHandler, false, 0, true);
				box.addChild(btnMenu);
			}
			
			//this.addBoxDynamicBackgroundStyle(box);
			
			
			this.selectIndex(index);
			
//			if(timer.running) {
//				timer.stop();
//			}

//			timer = new Timer(this.scrollSpeed);
//			timer.addEventListener(TimerEvent.TIMER, function removeStyle(event:TimerEvent):void{
//				removeBoxDynamicBackgroundStyle(box);
//			},false,0,true);
//			
//			timer.start();
		}		
		
		protected function btnMenu_ClickHandler(event:MouseEvent):void
		{
			var btnMenu:Button = event.currentTarget as Button;
			
			var e:ClickMenuEvent = new ClickMenuEvent(ClickMenuEvent.CLICK_MENU_EVENT);
			e.eventType = btnMenu.name;
			e.label = btnMenu.label; 
			e.id = btnMenu.id;
			
			this.dispatchEvent(e);
		}
		
		
		override protected function createChildren():void
		{
			setStyle( "horizontalAlign", "center" );	
			
			_viewStack = new ViewStack();
			_viewStack.width = 530
			_viewStack.height = 360;
			//_viewStack.setStyle("paddingTop",2);
//			_viewStack.setStyle("paddingLeft",32);
			
			addChild( _viewStack );	
			
			if(_vbox1 == null){
				_vbox1 = new VBox();
				_vbox1.top = 0;
				_vbox1.bottom = 0;
				_vbox1.left = 0;
				_vbox1.right = 0;
				this._viewStack.addChild(_vbox1);
				this.setBoxBaseStyle(_vbox1);
			}
			
			if(_vbox2 == null){
				_vbox2 = new VBox();
				_vbox2.top = 0;
				_vbox2.bottom = 0;
				_vbox2.left = 0;
				_vbox2.right = 0;
				this._viewStack.addChild(_vbox2);
				this.setBoxBaseStyle(_vbox2);
			}
			
			if(_vbox3 == null){
				_vbox3 = new VBox();
				_vbox3.top = 0;
				_vbox3.bottom = 0;
				_vbox3.left = 0;
				_vbox3.right = 0;
				
				this._viewStack.addChild(_vbox3);
				this.setBoxBaseStyle(_vbox3);
			}
			
			if(_twomenu == null){
				_twomenu = new TwoMenu();
				_twomenu.addEventListener(ClickNavMenu.CLICK_NAV_MENU,clickNavMenuHandler,false,0,true);
				if(_vbox2){
					_vbox2.addChild(_twomenu);
				}
			}
			
			super.createChildren();
		}

		protected function clickNavMenuHandler(event:ClickNavMenu):void
		{
			var e:ClickMenuEvent = new ClickMenuEvent(ClickMenuEvent.CLICK_MENU_EVENT);
			e.eventType = event.eventtype
			e.label = event.menuName;
			e.id = event.id;
			
			this.dispatchEvent(e);
		}
		
		private function addBoxDynamicBackgroundStyle(box:Box):void
		{
			if(box){ 
				box.setStyle("backgroundColor","#FF9609");
				//box.setStyle("backgroundAlpha",.2);
			}
		}
		
		private function removeBoxDynamicBackgroundStyle(box:Box):void
		{
			if(box){ 
				box.setStyle("backgroundAlpha",0);
			}
		} 
		
		private function createMenu(item:Object):Button
		{
			var btn:Button=new Button();
			btn.width=353;
			btn.height=50;
			btn.label=item.label;
			btn.name=item.eventtype;
			btn.id = item.id;
			btn.useHandCursor = true;
			btn.buttonMode = true;
			
			return btn;
		}
		
		
		private function setBoxBaseStyle(box:Box):void
		{
			if(box){ 
				box.setStyle("verticalAlign","middle");
				box.setStyle("horizontalAlign","center");
				//box.width = boxWidth;
				//box.height = boxHeight;
				box.percentHeight = boxHeight;
				box.percentWidth = boxWidth;
//				box.setStyle("borderColor","#FF9609");
//				box.setStyle("borderStyle","solid");
//				box.setStyle("paddingTop",-20);
			} 
		}
		
		
		protected function onControlChange( event:Event ):void
		{
			invalidateProperties();
		}
		
		override protected function commitProperties():void
		{
			super.commitProperties();
			
			try
			{
				//				if( _effect is ViewStackEffect )
				//				{
				//					var viewStackEffect:ViewStackEffect = ViewStackEffect( _effect );
				//
				//					viewStackEffect.duration = _duration.value;
				//					viewStackEffect.popUp = _popUp.selected;
				//					viewStackEffect.modal = _modal.selected;
				//					viewStackEffect.modalTransparency = _modalTransparency.value / 100;
				//					viewStackEffect.modalTransparencyColor = _modalTransparencyColor.selectedColor;
				//					viewStackEffect.modalTransparencyBlur = _modalTransparencyBlur.value;
				//					viewStackEffect.modalTransparencyDuration = _modalTransparencyDuration.value;
				//				}
				//				else if( _effect is ViewStackTweenEffect )
				//				{
				//					var viewStackTweenEffect:ViewStackTweenEffect = ViewStackTweenEffect( _effect );
				//					viewStackTweenEffect.duration = _duration.value;
				//					viewStackTweenEffect.easingFunction = _easingFunction.selectedItem.data;
				//					viewStackTweenEffect.popUp = _popUp.selected;
				//					viewStackTweenEffect.modal = _modal.selected;
				//					viewStackTweenEffect.modalTransparency = _modalTransparency.value / 100;
				//					viewStackTweenEffect.modalTransparencyColor = _modalTransparencyColor.selectedColor;
				//					viewStackTweenEffect.modalTransparencyBlur = _modalTransparencyBlur.value;
				//					viewStackTweenEffect.modalTransparencyDuration = _modalTransparencyDuration.value;
				//				}
				//				
				//				_viewStack.clipContent = _clipContent.selected;
				//				
				//				
				//				_modal.enabled = _popUp.selected;
				//				_modalTransparencyLabel.enabled = _modalTransparency.enabled = _popUp.selected;
				//				_modalTransparencyColorLabel.enabled = _modalTransparencyColor.enabled = _popUp.selected;
				//				_modalTransparencyBlurLabel.enabled = _modalTransparencyBlur.enabled = _popUp.selected;
				//				_modalTransparencyDurationLabel.enabled = _modalTransparencyDuration.enabled = _popUp.selected;
				
				var canvas:Container;
				for( var i:int = 0; i < _viewStack.numChildren; i++ )
				{
					canvas = Container( _viewStack.getChildAt( i ) );
					canvas.setStyle( "hideEffect", _effect );
					canvas.setStyle( "showEffect", _effect );
				}
				
			}
			catch( error:Error )
			{
				
			}
		}
		
		private function onHScrollBarCreationComplete( event:FlexEvent ):void
		{
			//_hScrollBar.setScrollProperties( 8, 0, 8, 1 );
		}
		
		private function onHScrollBarScroll( event:ScrollEvent ):void
		{
			_viewStack.selectedIndex = event.position;
		}
		
		private function onToggleButtonBarItemClick( event:ItemClickEvent ):void
		{
			//_hScrollBar.scrollPosition = event.index;
		}
		
		
		public function get effect( ):Effect
		{
			return _effect;
		}
		public function set effect( value:Effect ):void
		{
			if( !value ) return;
			
			_effect = value;
			invalidateProperties();			
		}

		public function get boxWidth():int
		{
			return _boxWidth;
		}

		public function set boxWidth(value:int):void
		{
			_boxWidth = value;
		}

		public function get boxHeight():int
		{
			return _boxHeight;
		}

		public function set boxHeight(value:int):void
		{
			_boxHeight = value;
		}

		
	}
}