package cn.guo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

	private final static Logger logger = LoggerFactory.getLogger(FileUtils.class);

	/**
	 * 读取文件
	 * 
	 * @param path
	 *            文件路径
	 * @param code
	 *            文件字符集
	 * @return
	 */
	public static String getXmlStr(String path, String code) {
		FileInputStream inputStream = null;
		InputStreamReader read = null;
		BufferedReader bufferedReader = null;
		StringBuffer sb = new StringBuffer();
		try {
			File file = new File(path);// 文件源
			inputStream = new FileInputStream(file);
			String encoding = code;
			String lineTxt = "";
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				read = new InputStreamReader(inputStream, encoding);// 考虑到编码格式
				bufferedReader = new BufferedReader(read);
				while ((lineTxt = bufferedReader.readLine()) != null) {
					sb.append(lineTxt);
				}
				read.close();

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != bufferedReader) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(null != read) {
				try {
					read.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if (null != inputStream) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	/**
	 * 读取xml文件 默认系统编码
	 * @param path
	 * @return
	 */
	public static String getXmlStr(String path) {
		StringBuffer sb = new StringBuffer();
		FileInputStream fileInputStream = null;
		InputStreamReader reader = null;
		BufferedReader br = null;
		try {
			File file = new File(path); // 要读取以上路径的input。txt文件
			fileInputStream = new FileInputStream(file);
			reader = new InputStreamReader(fileInputStream); // 建立一个输入流对象reader
			br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line); // 一次读入一行数据
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(null != fileInputStream) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	/**
	 * 写入文件
	 * @param path
	 * @param contect
	 */
	public static void writeFileAddEnd(String path,String contect){
		FileWriter fileWriter = null;
		try {
			File file = new File(path);
			fileWriter = new FileWriter(file, true);
			fileWriter.write(contect);
		} catch (IOException e) {
			logger.error("写入文件错误:",e);
		} finally {
			if(fileWriter != null){
				try {
					fileWriter.close();
				} catch (IOException e) {
					logger.error("关闭写入流错误:",e);
				}
			}
		}
	}
	/**
	 * 写入文件(文件追加)
	 * @param path
	 * @param contect
	 */
	public static void writeFile(String path,String contect){
		FileWriter fileWriter = null;
		try {
			File file = new File(path);
			fileWriter = new FileWriter(file);
			fileWriter.write(contect);
		} catch (IOException e) {
			logger.error("写入文件错误:",e);
		} finally {
			if(fileWriter != null){
				try {
					fileWriter.close();
				} catch (IOException e) {
					logger.error("关闭写入流错误:",e);
				}
			}
		}
	}
	/**
	 * 创建文件夹(如果不存在)
	 * @param filePath
	 */
	public static void createFileIsNotExists(String filePath) {
		File file = new File(filePath);
		if(!file.exists()) {
			file.mkdirs();
		}
	}
}
