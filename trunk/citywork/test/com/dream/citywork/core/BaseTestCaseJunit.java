/**
 * @Project: citywork
 * @Package com.dream.citywork.core.BaseTestCaseJunit.java
 * @date Oct 2, 2011 2:04:13 PM
 * @Copyright: 2011 HUBO Inc. All rights reserved.
 * @version V1.0  
 */
package com.dream.citywork.core;

import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Description: TODO
 * @author HUBO
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class BaseTestCaseJunit {

	protected Logger logger = Logger.getLogger(BaseTestCaseJunit.class);
}
