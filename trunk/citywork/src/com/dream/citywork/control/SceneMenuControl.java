/**
 * @Project: citywork
 * @Package com.dream.citywork.control.SceneMenuControl.java
 * @date Oct 2, 2011 2:22:05 PM
 * @Copyright: 2011 HUBO Inc. All rights reserved.
 * @version V1.0  
 */
package com.dream.citywork.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.flex.remoting.RemotingInclude;
import org.springframework.stereotype.Component;

import com.dream.citywork.core.ListResults;
import com.dream.citywork.service.ISceneMenuService;

/**
 * @Description: TODO
 * @author HUBO
 */
@Component("sceneMenuControl")
@RemotingDestination(channels = { "my-amf", "my-secure-amf" })
public class SceneMenuControl {

	protected static Log logger = LogFactory.getLog(SceneMenuControl.class);

	@Autowired
	private ISceneMenuService sceneMenuService;

	/**
	 * @date Oct 2, 2011 3:56:40 PM
	 * @author HUBO
	 * @Description: 查询初始化菜单
	 * @return ListResults
	 */
	@SuppressWarnings("unchecked")
	@RemotingInclude
	public ListResults queryInitialMenuList() {

		ListResults lr = new ListResults();
		try {
			lr.setList(sceneMenuService.queryInitialMenuList());
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			lr.setMessage("查询错误,请稍后再试!");
		}

		return lr;
	}

	/**
	 * @date Oct 2, 2011 3:56:49 PM
	 * @author HUBO
	 * @Description: 根据一级菜单查询二级菜单数据
	 * @param id:String
	 *            一级菜单ID
	 * @return ListResults
	 */
	@SuppressWarnings("unchecked")
	@RemotingInclude
	public ListResults querySecondaryMenuListById(String id) {

		ListResults lr = new ListResults();
		try {
			lr.setList(sceneMenuService.querySecondaryMenuListById(id));
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			lr.setMessage("查询错误,请稍后再试!");
		}

		return lr;
	}

	/**
	 * @date Oct 2, 2011 4:17:12 PM
	 * @author HUBO
	 * @Description: 点击二级页面某项，进入三级页面 右侧
	 * @param sceneId:String
	 *            点击菜单ID(二级菜单)
	 * @param sceneCode:String
	 *            点击菜单Code(parent_node_id)(二级菜单)
	 * @return ListResults
	 */
	@SuppressWarnings("unchecked")
	@RemotingInclude
	public ListResults queryThreeMenuRightList(String sceneId, String sceneCode) {

		ListResults lr = new ListResults();
		try {
			lr.setList(sceneMenuService.queryThreeMenuRightList(sceneId,
					sceneCode));
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			lr.setMessage("查询错误,请稍后再试!");
		}

		return lr;
	}

	/**
	 * @date Oct 2, 2011 9:05:35 PM
	 * @author HUBO
	 * @Description: 点击三级页面佑侧菜单，显示子级
	 * @param sceneId:String
	 *            点击菜单ID(三级菜单)
	 * @param sceneCode:String
	 *            点击菜单Code(parent_node_id)(三级菜单)
	 * @return ListResults
	 */
	@SuppressWarnings("unchecked")
	@RemotingInclude
	public ListResults querySubThreeMenuListByParentId(String sceneId,
			String sceneCode) {

		ListResults lr = new ListResults();
		try {
			lr.setList(sceneMenuService.querySubThreeMenuListByParentId(
					sceneId, sceneCode));
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			lr.setMessage("查询错误,请稍后再试!");
		}

		return lr;
	}

}
