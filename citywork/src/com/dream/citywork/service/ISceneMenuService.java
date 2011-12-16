/**
 * @Project: citywork
 * @Package com.dream.citywork.service.IMenu.java
 * @date Oct 2, 2011 2:05:39 PM
 * @Copyright: 2011 HUBO Inc. All rights reserved.
 * @version V1.0  
 */
package com.dream.citywork.service;

import java.util.List;

import com.dream.citywork.vo.MenuVo;

/**
 * @Description: TODO
 * @author HUBO
 */
public interface ISceneMenuService {

	/**
	 * @date Oct 2, 2011 2:19:51 PM
	 * @author HUBO
	 * @Description: 查询初始化菜单
	 * @return List<MenuVo>
	 */
	public List<MenuVo> queryInitialMenuList() throws Exception;

	/**
	 * @date Oct 2, 2011 3:37:48 PM
	 * @author HUBO
	 * @Description: 根据一级菜单查询二级菜单数据
	 * @param id:String
	 *            一级菜单ID
	 * @param type:String
	 *            一级菜单类别
	 * @return List<MenuVo>
	 */
	public List<MenuVo> querySecondaryMenuListById(String id, String type)
			throws Exception;

	/**
	 * @date Oct 2, 2011 3:37:48 PM
	 * @author HUBO
	 * @Description: 根据一级菜单查询二级菜单数据
	 * @param id:String
	 *            一级菜单ID
	 * @return List<MenuVo>
	 */
	public List<MenuVo> querySecondaryMenuListById(String id) throws Exception;

	/**
	 * @date Oct 2, 2011 4:09:05 PM
	 * @author HUBO
	 * @Description: 点击二级页面某项，进入三级页面 右侧
	 * @param sceneId:String
	 *            点击菜单ID(二级菜单)
	 * @param sceneCode:String
	 *            点击菜单Code(parent_node_id)(二级菜单)
	 * @return List<MenuVo>
	 */
	public List<MenuVo> queryThreeMenuRightList(String sceneId, String sceneCode)
			throws Exception;

	/**
	 * @date Oct 2, 2011 4:55:38 PM
	 * @author HUBO
	 * @Description: 点击三级页面佑侧菜单，显示子级
	 * @param sceneId:String
	 *            点击菜单ID(三级菜单)
	 * @param sceneCode:String
	 *            点击菜单Code(parent_node_id)(三级菜单)
	 * @return List<MenuVo>
	 */
	public List<MenuVo> querySubThreeMenuListByParentId(String sceneId,
			String sceneCode) throws Exception;
}
