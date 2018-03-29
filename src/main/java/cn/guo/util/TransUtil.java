package cn.guo.util;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

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
	private final static String eclipsePath = "src"+File.separator + "main" + File.separator + "java";
	private final static String systemPath = System.getProperty("user.dir");
	private static final String packageInfo = "package ";
	private static final String semicolon = ";";
	private static final String importList = "import java.util.List;";

	public static void main(String[] args) throws DocumentException {
		String path = "D:\\eclipse-workspase\\个人信用报告解析.xml";
		//String str = "<class><student><name>aaaaaa</name><age>21</age></student><student><name>bbbbbb</name><age>22</age></student></class>";
		// 创建saxReader对象
		SAXReader reader = new SAXReader();
		// 通过read方法读取一个文件 转换成Document对象D:\\eclipse-workspase\\xml-bean\\src\\main\\java\\
		Document parseText = reader.read(new File(path));
		//Document parseText = DocumentHelper.parseText(str);
		Element rootElement = parseText.getRootElement();
		//System.out.println(System.getProperty("user.dir"));
		String pageName = ConfigUtil.getProperty("pageName");
		writeClass(rootElement,pageName);
	}
	
	public static void writeClass(Element element,String pageName) {
		String filePath = systemPath + File.separator + eclipsePath + File.separator + pageName.replace(".", File.separator);
		FileUtils.createFileIsNotExists(filePath);
		@SuppressWarnings("unchecked")
		List<Element> elements = element.elements();
		StringBuffer sb = new StringBuffer();
		HashSet<String> set = new HashSet<String>();
		String fileAllPath = filePath + File.separator+upperCaseFirst(element.getName()) + ".java";
		if(!elements.isEmpty()) {
			sb = writeClassHeader(sb,pageName,element);
			sb = writeClassMiddle(sb,elements,set);
			set.clear();
			sb = writeClassEnd(sb);
			FileUtils.writeFile(fileAllPath, sb.toString());
			logger.info(sb.toString());
			for (Element ele : elements) {
				if(!set.contains(ele.getName())) {
					writeClass(ele,pageName);
					set.add(ele.getName());
				}
			}
		}
	}
	
	private static StringBuffer writeClassEnd(StringBuffer sb) {
		sb.append(wrap);
		sb.append(classEnd);
		return sb;
	}
	private static StringBuffer writeClassHeader(StringBuffer sb,String pageName,Element element) {
		addClassHeader(sb, pageName);
		sb.append(String.format(classBegin, upperCaseFirst(element.getName())));
		sb.append(wrap);
		sb.append(wrap);
		return sb;
	}
	private static StringBuffer writeClassMiddle(StringBuffer sb,List<Element> elements,HashSet<String> set) {
		for (Element ele : elements) {
			@SuppressWarnings("unchecked")
			List<Element> listEle = ele.elements();
			if(listEle.isEmpty()) {
				sb.append(String.format(attrStr, ele.getName()));
			}else {
				if(set.contains(ele.getName())) {
					continue;
				}
				int i =0;
				for (Element el : elements) {
					if(el.getName().equals(ele.getName())) {
						i++;
					}
				}
				if(i == 0) {
					sb.append(String.format(attr, upperCaseFirst(ele.getName()), ele.getName()));
				}else {
					sb.append(String.format(attrList, upperCaseFirst(ele.getName()), ele.getName()));
					set.add(ele.getName());
				}

			}
			sb.append(wrap);
		}
		return sb;
	}
	/**
	 * 首字符大写
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
	 * 添加类中的包信息和导入信息
	 * @param sb
	 * @param pageName
	 * @return
	 */
	private static StringBuffer addClassHeader(StringBuffer sb,String pageName) {
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
