package cn.guo.util;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransUtil {

	private final static Logger logger = LoggerFactory.getLogger(TransUtil.class);

	private final static String classBegin = "public class %s {";
	private final static String classEnd = "}";
	private final static String attr = "	private %s %s ;";
	private final static String attrStr = "	private String %s ;";
	private final static String attrList = "	private List<%s> %s ;";
	private final static String wrap = "\r\n";
	private final static String eclipsePath = "src" + File.separator + "main" + File.separator + "java";
	private final static String systemPath = System.getProperty("user.dir");
	private static final String packageInfo = "package ";
	private static final String semicolon = ";";
	private static final String importList = "import java.util.List;";
	private static final String getStrHead = "	public %s get%s() {";
	private static final String getStrMiddle = "        return %s;";
	private static final String getStrEnd = "    }";
	private static final String setStrHead = "    public void set%s(%s %s) {";
	private static final String setStrMiddle = "        this.%s = %s;";
	private static final String setStrEnd = "    }";
	private static final String stringType = "String";
	private static final String listType = "List<%s>";
	private static final String annotation = "	"+ConfigUtil.getProperty("annotation");
	/**
	 * 根据xml文件生成java代码
	 * @param path xml文件所在的全路径
	 * @param pageName 生成文件所在的包名
	 */
	public static void writeClassByFile(String path, String pageName) {
		SAXReader reader = new SAXReader();
		try {
			File file = new File(path);
			Document parseText = reader.read(file);
			Element rootElement = parseText.getRootElement();
			writeClass(rootElement, pageName);
		} catch (DocumentException e) {
			logger.error("xml文件读取失败!!!");
		}
	}
	/**
	 * 根据xml字符串生成java代码
	 * @param xmlStr xml字符串
	 * @param pageName 生成文件所在的包名
	 */
	public static void writeClassByString(String xmlStr, String pageName) {
		try {
			Document parseText = DocumentHelper.parseText(xmlStr);
			Element rootElement = parseText.getRootElement();
			writeClass(rootElement, pageName);
		} catch (DocumentException e) {
			logger.error("xml字符串读取失败!!!");
		}
	}

	/**
	 * 自动生成代码的主方法
	 * 
	 * @param element
	 * @param pageName
	 */
	private static void writeClass(Element element, String pageName) {
		String filePath = systemPath + File.separator + eclipsePath + File.separator
				+ pageName.replace(".", File.separator);
		FileUtils.createFileIsNotExists(filePath);
		@SuppressWarnings("unchecked")
		List<Element> elements = element.elements();
		StringBuffer sb = new StringBuffer();
		HashSet<String> set = new HashSet<String>();
		String fileAllPath = filePath + File.separator + upperCaseFirst(element.getName()) + ".java";
		if (!elements.isEmpty()) {
			sb = writeClassHeader(sb, pageName, element);
			sb = writeClassMiddle(sb, elements, set);
			set.clear();
			sb = writeClassEnd(sb);
			FileUtils.writeFile(fileAllPath, sb.toString());
			logger.info(sb.toString());
			for (Element ele : elements) {
				if (!set.contains(ele.getName())) {
					writeClass(ele, pageName);
					set.add(ele.getName());
				}
			}
		}
	}

	/**
	 * 写入类的尾信息
	 * 
	 * @param sb
	 * @return
	 */
	private static StringBuffer writeClassEnd(StringBuffer sb) {
		sb.append(wrap);
		sb.append(classEnd);
		return sb;
	}

	/**
	 * 写入类的头信息
	 * 
	 * @param sb
	 * @param pageName
	 * @param element
	 * @return
	 */
	private static StringBuffer writeClassHeader(StringBuffer sb, String pageName, Element element) {
		addClassHeader(sb, pageName);
		sb.append(String.format(classBegin, upperCaseFirst(element.getName())));
		sb.append(wrap);
		sb.append(wrap);
		return sb;
	}

	private static StringBuffer writeClassMiddle(StringBuffer sb, List<Element> elements, HashSet<String> set) {
		Map<String, String> map = new HashMap<String, String>();
		for (Element ele : elements) {
			@SuppressWarnings("unchecked")
			List<Element> listEle = ele.elements();
			if (listEle.isEmpty()) {
				sb.append(String.format(annotation, ele.getName()));
				sb.append(wrap);
				sb.append(String.format(attrStr, downCaseFirst(ele.getName())));
				map.put(ele.getName(), stringType);
			} else {
				if (set.contains(ele.getName())) {
					continue;
				}
				int i = 0;
				for (Element el : elements) {
					if (el.getName().equals(ele.getName())) {
						i++;
					}
				}
				if (i == 0) {
					sb.append(String.format(annotation, ele.getName()));
					sb.append(wrap);
					sb.append(String.format(attr, upperCaseFirst(ele.getName()), downCaseFirst(ele.getName())));
					map.put(ele.getName(), upperCaseFirst(ele.getName()));
				} else {
					sb.append(String.format(annotation, ele.getName()));
					sb.append(wrap);
					sb.append(String.format(attrList, upperCaseFirst(ele.getName()), downCaseFirst(ele.getName())));
					set.add(ele.getName());
					map.put(ele.getName(), String.format(listType, upperCaseFirst(ele.getName())));
				}

			}
			sb.append(wrap);
		}
		for (String key : map.keySet()) {
			sb = writeGetMethod(map, key, sb);
			sb = writeSetMethod(map, key, sb);
		}
		return sb;
	}

	/**
	 * get方法的编写
	 * 
	 * @param map
	 * @param key
	 * @param sb
	 * @return
	 */
	private static StringBuffer writeGetMethod(Map<String, String> map, String key, StringBuffer sb) {
		sb.append(wrap);
		sb.append(String.format(getStrHead, map.get(key), upperCaseFirst(key)));
		sb.append(wrap);
		sb.append(String.format(getStrMiddle, downCaseFirst(key)));
		sb.append(wrap);
		sb.append(getStrEnd);
		return sb;
	}

	/**
	 * set方法的编写
	 * 
	 * @param map
	 * @param key
	 * @param sb
	 * @return
	 */
	private static StringBuffer writeSetMethod(Map<String, String> map, String key, StringBuffer sb) {
		sb.append(wrap);
		sb.append(String.format(setStrHead, upperCaseFirst(key), map.get(key), downCaseFirst(key)));
		sb.append(wrap);
		sb.append(String.format(setStrMiddle, downCaseFirst(key), downCaseFirst(key)));
		sb.append(wrap);
		sb.append(setStrEnd);
		return sb;
	}

	/**
	 * 首字符大写
	 * 
	 * @param str
	 * @return
	 */
	private static String upperCaseFirst(String str) {
		char[] ch = str.toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
		}
		return new String(ch);
	}

	/**
	 * 首字符小写
	 * 
	 * @param str
	 * @return
	 */
	private static String downCaseFirst(String str) {
		char[] ch = str.toCharArray();
		if (ch[0] >= 'A' && ch[0] <= 'Z') {
			ch[0] = (char) (ch[0] + 32);
		}
		return new String(ch);
	}

	/**
	 * 添加类中的包信息和导入信息
	 * 
	 * @param sb
	 * @param pageName
	 * @return
	 */
	private static StringBuffer addClassHeader(StringBuffer sb, String pageName) {
		sb.append(packageInfo);
		sb.append(pageName);
		sb.append(semicolon);
		sb.append(wrap);
		sb.append(wrap);
		sb.append(importList);
		sb.append(wrap);
		sb.append(wrap);
		return sb;
	}
}
