package cn.guo.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
	private static Properties p = new Properties();

	// 用静态代码块
	static {
		
		InputStream isConfig = null;
		try {
			
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			
			// irb配置
			isConfig = cl.getResourceAsStream("config.properties");
			p.load(isConfig);
		} catch (Exception e) {
			logger.error("ConfigUtil类的静态代码块出错", e);
		} finally {
			try {
				if(null != isConfig){
					isConfig.close();
					isConfig = null;
				}
			} catch (IOException e) {
				logger.error("释放批量配置资源异常!", e);
			}
		}
	}
	// 通过Property name 获取Property
	public static String getProperty(String propertyName) {
		return p.getProperty(propertyName);
	}
}
