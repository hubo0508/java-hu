package com.dream.citywork.service.impl;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dream.citywork.core.BaseTestCaseJunit;
import com.dream.citywork.service.ISceneMenuService;
import com.dream.citywork.vo.MenuVo;

public class SceneMenuServiceImplTest extends BaseTestCaseJunit {

	@Autowired
	private ISceneMenuService sceneMenuService;

	// @Test
	public void queryInitialMenuList() {

		try {
			List<MenuVo> list = sceneMenuService.queryInitialMenuList();
			for (MenuVo menuVo : list) {
				System.out.println(menuVo.getLabel() + "|||||||");
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Test
	public void querySecondaryMenuListById() {

		try {
			List<MenuVo> list = sceneMenuService
					.querySecondaryMenuListById("2");
			for (MenuVo menuVo : list) {
				System.out.println(menuVo.getLabel() + "|||||||");
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
