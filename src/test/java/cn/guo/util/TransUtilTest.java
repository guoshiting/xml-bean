package cn.guo.util;

import org.dom4j.DocumentException;
import org.junit.Test;

public class TransUtilTest {

	@Test
	public void testWriteClass() throws DocumentException {
		String path = "D:\\eclipse-workspase\\个人信用报告解析.xml";
		String pageName = ConfigUtil.getProperty("pageName");
		TransUtil.writeClassByFile(path,pageName);
	}
}
