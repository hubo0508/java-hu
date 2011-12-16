/**
 * @Project: citywork
 * @Package com.dream.citywork.service.domain.SceneMenuServiceImpl.java
 * @date Oct 2, 2011 2:20:38 PM
 * @Copyright: 2011 HUBO Inc. All rights reserved.
 * @version V1.0  
 */
package com.dream.citywork.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import com.dream.citywork.service.ISceneMenuService;
import com.dream.citywork.util.SceneMenuUtil;
import com.dream.citywork.vo.MenuVo;

/**
 * @Description: TODO
 * @author HUBO
 */
@Service("sceneMenuServiceImpl")
public class SceneMenuServiceImpl implements ISceneMenuService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	protected static Log logger = LogFactory.getLog(SceneMenuServiceImpl.class);

	/*
	 * HUBO / Oct 2, 2011 2:21:14 PM
	 * 
	 * @see com.dream.citywork.service.ISceneMenuService#queryInitialMenu()
	 */
	@Override
	public List<MenuVo> queryInitialMenuList() throws Exception {

		final List<MenuVo> listmenu = new ArrayList<MenuVo>();
		try {
			String sql = "SELECT t.scene_type_id, t.scene_type_code, t.scene_type_name FROM flx_scene_type t";
			jdbcTemplate.query(sql, new Object[] {}, new RowCallbackHandler() {
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					MenuVo menu = new MenuVo();
					menu.setId(rs.getLong("scene_type_id"));
					menu.setEventtype(rs.getString("scene_type_code"));
					menu.setLabel(rs.getString("scene_type_name"));
					listmenu.add(menu);
				}
			});

		} catch (RuntimeException e) {
			logger.info(e.getMessage(), e);
			throw e;
		}

		return listmenu;
	}

	/*
	 * HUBO / Oct 2, 2011 3:39:30 PM
	 * 
	 * @see com.dream.citywork.service.ISceneMenuService#querySecondaryMenuListById(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public List<MenuVo> querySecondaryMenuListById(String id, String type)
			throws Exception {

		final List<MenuVo> listmenu = new ArrayList<MenuVo>();
		try {
			String sql = "SELECT t.scene_id,t.scene_name,t.scene_code FROM flx_scene t WHERE t.scene_type_id =? AND t.scene_status = ?";
			jdbcTemplate.query(sql, new Object[] { id, type },
					new RowCallbackHandler() {
						@Override
						public void processRow(ResultSet rs)
								throws SQLException {
							MenuVo menu = new MenuVo();
							menu.setId(rs.getLong("scene_id"));
							menu.setEventtype(rs.getString("scene_code"));
							menu.setLabel(rs.getString("scene_name"));
							listmenu.add(menu);
						}
					});

		} catch (RuntimeException e) {
			logger.info(e.getMessage(), e);
			throw e;
		}

		return listmenu;
	}

	/*
	 * HUBO / Oct 2, 2011 3:46:15 PM
	 * 
	 * @see com.dream.citywork.service.ISceneMenuService#querySecondaryMenuListById(java.lang.String)
	 */
	@Override
	public List<MenuVo> querySecondaryMenuListById(String id) throws Exception {

		final List<MenuVo> listmenu = new ArrayList<MenuVo>();
		try {
			String sql = "SELECT t.scene_id,t.scene_name,t.scene_code FROM flx_scene t WHERE t.scene_type_id =?";
			jdbcTemplate.query(sql, new Object[] { id },
					new RowCallbackHandler() {
						@Override
						public void processRow(ResultSet rs)
								throws SQLException {
							MenuVo menu = new MenuVo();
							menu.setId(rs.getLong("scene_id"));
							menu.setEventtype(rs.getString("scene_code"));
							menu.setLabel(rs.getString("scene_name"));
							listmenu.add(menu);
						}
					});

		} catch (RuntimeException e) {
			logger.info(e.getMessage(), e);
			throw e;
		}

		return listmenu;
	}

	/*
	 * HUBO / Oct 2, 2011 4:10:39 PM
	 * 
	 * @see com.dream.citywork.service.ISceneMenuService#queryThreeMenuRight(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public List<MenuVo> queryThreeMenuRightList(String sceneId, String sceneCode)
			throws Exception {

		final List<MenuVo> listmenu = new ArrayList<MenuVo>();
		try {
			String sql = "SELECT t.intro_cate_code, t.node_id, t.code FROM flx_scene_node t "
					+ "WHERE t.scene_id = ? AND parent_node_id in (select code from flx_scene_node "
					+ "where scene_id = ? and  cate_code = ?)";
			logger.info("SELECT t.intro_cate_code, t.node_id, t.code FROM flx_scene_node t "
					+ "WHERE t.scene_id = "+sceneId+" AND parent_node_id in (select code from flx_scene_node "
					+ "where scene_id = "+sceneId+" and  cate_code = "+sceneCode+")");
			jdbcTemplate.query(sql,
					new Object[] { sceneId, sceneId, sceneCode },
					new RowCallbackHandler() {
						@Override
						public void processRow(ResultSet rs)
								throws SQLException {
							MenuVo menu = new MenuVo();
							menu.setId(rs.getLong("node_id"));
							menu.setEventtype(rs.getString("code"));
							menu.setLabel(rs.getString("intro_cate_code"));
							listmenu.add(menu);
						}
					});

		} catch (RuntimeException e) {
			logger.info(e.getMessage(), e);
			throw e;
		}

		return listmenu;
	}

	/*
	 * HUBO / Oct 2, 2011 9:03:50 PM
	 * 
	 * @see com.dream.citywork.service.ISceneMenuService#queryThreeMenuListByParentId(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public List<MenuVo> querySubThreeMenuListByParentId(String sceneId,
			String sceneCode) throws Exception {

		final List<MenuVo> listmenu = new ArrayList<MenuVo>();
		try {
			String sql = "SELECT t.resources, t.intro_cate_code, t.node_id,  t.parent_node_id,t.code FROM flx_scene_node t WHERE t.scene_id = ? AND parent_node_id = ?";
			logger.info("SELECT t.resources, t.intro_cate_code, t.node_id,  t.parent_node_id,t.code FROM flx_scene_node t WHERE t.scene_id = "+sceneId+" AND parent_node_id ="+sceneCode);
			jdbcTemplate.query(sql, new Object[] { sceneId, sceneCode },
					new RowCallbackHandler() {
						@Override
						public void processRow(ResultSet rs)
								throws SQLException {
							MenuVo menu = new MenuVo();
							menu.setId(rs.getLong("node_id"));
							menu.setEventtype(rs.getString("code"));
							menu.setLabel(rs.getString("intro_cate_code"));
							String[] str = SceneMenuUtil.getURL(rs
									.getString("resources"));
							menu.setResources(str[0]);
							menu.setType(str[1]);

							listmenu.add(menu);
						}
					});

		} catch (RuntimeException e) {
			logger.info(e.getMessage(), e);
			throw e;
		}

		return listmenu;
	}
}
