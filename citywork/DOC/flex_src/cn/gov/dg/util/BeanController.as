package cn.gov.dg.util
{
//	import com.irm.bean.InitBean;
	

	public class BeanController
	{
		public var beanXMLList:XMLList;
		public var beanFactory:Array; //存放着实例化好了的bean
//		public var initBean:InitBean;
		
		static private var __instance:BeanController=null;

		static public function getInstance():BeanController
		{
			if (__instance == null)
			{
				__instance=new BeanController();
			}
			return __instance;
		}
		
		public function getBean(beanName:String):Object{
			var bean:Object;
			if(beanFactory == null) beanFactory = new Array();
			bean = beanFactory[beanName];
			return bean;
		}
		
		
		
		



	}
}