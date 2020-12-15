package emro.util;

import java.util.*;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <PRE>
 * Filename	: PropertyManager.java <BR>
 * Package	: com.emro.util <BR>
 * Function	: <BR>
 * Comment	: Property파일을 다루는데 유용한 각종 유틸리티 메쏘드 <BR>
 * History	: 2009/04/01,  v1.0, 최초작성 <BR>
 * </PRE>
 * @version	1.0
 * @author 	이창진 Copyright (c) 2009 by EMRO Corp. All Rights Reserved.
 *
 */
public class PropertyManager {

	/** 파일의 실제 저장 위치를 담음 */
	private String actualFilePath = null;
	
	/** Property 데이터를 담음 */
	private Map dataMap = new TreeMap();
	
	private final Log log = LogFactory.getLog(getClass());

	/**
	 * 주어진 fileName으로부터 Property 정보를 읽고 저장하는 PropertyManager 객체를 생성한다.
	 */
	public PropertyManager(String fileName) {
		if (fileName.indexOf("/") != -1 || fileName.indexOf("\\") != -1) {
			actualFilePath = fileName;
		}
		
		try {
			if (fileName.indexOf("\\") != -1 || fileName.indexOf("/") != -1) {
				actualFilePath = fileName;
			}
			else {
				URL url = this.getClass().getClassLoader().getResource(fileName);
				String path = URLDecoder.decode(url.getFile(), "UTF-8");
				actualFilePath = path;
			}
		} catch (Exception e) {
//			e.printStackTrace();
			return;
		}
		
		reload();
		
	}

	/**
	 * Property 정보를 파일로부터 다시 읽어옴.
	 */
	public void reload() {
		dataMap.clear();
		List rowList = null;
		try {
			FileInputStream fis = new FileInputStream(actualFilePath);
			StringBuffer contents = new StringBuffer();
			String newLine = "";
			try {
				BufferedReader br = new BufferedReader( new InputStreamReader(fis, "UTF-8"));
				while( (newLine=br.readLine()) != null ) {
					contents.append( newLine + "\n" );
				}
			    fis.close();
			}
			catch( NullPointerException e ) {
//			    e.printStackTrace();
				return;
			}
			rowList = StringUtil.toList(contents.toString(), "\n");
		} catch (Exception e) {
//			e.printStackTrace();
			return;
		}

		for (Iterator i = rowList.iterator(); i.hasNext(); ) {
			String row = (String) i.next();
			int sepIndex = row.indexOf("=");
			if (sepIndex != -1) {
				String key = row.substring(0, sepIndex);
				String value = unescape(row.substring(sepIndex + 1));
				dataMap.put(key, value);
			}
		}
	}
		
	/**
	 * 설정 정보를 파일에 저장함.
	 */
	public void save() {
		if (actualFilePath == null) {
			return;
		}
		
		String fileStr = toFileString();
		
		try {
			ResourceUtil.saveToFile(actualFilePath, fileStr);
		} catch (Exception e) {
//			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * 현재 설정 정보를 읽어온 Property 파일의 위치를 반환함.
	 */
	public String getFileLocation() {
		return actualFilePath;
	}
	
	/**
	 * 주어진 name에 대응되는 Property 값을 반환함.
	 */
	public String get(String name) {
		return (String) dataMap.get(name);
	}
	
	/**
	 * 주어진 name에 대응되는 Property 값을 반환함.
	 * 만약 해당 name에 대응되는 값이 없으면 defaultValue를 반환함.
	 */
	public String get(String name, String defaultValue) {
		String value = get(name);
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}
	
	/**
	 * 주어진 value를 주어진 name에 해당하는 Property 값으로 설정함.
	 */
	public String put(String name, String value) {
		return (String) dataMap.put(name, value);
	}
	
	/**
	 * Property 정보가 저장되어 있는 Map 객체를 반환함.
	 */
	public Map getMap() {
		return dataMap;
	}
	
	/**
	 * 주어진 Map을 이용하여 전체 Property 정보를 뒤집어 씌움.
	 */
	public void setMap(Map map) {
		dataMap = new TreeMap(map);
	}
	
	/**
	 * Property의 내용을 File 저장시 사용되는 문자열로 반환한다.
	 */
	public String toFileString() {
		String fileStr = "";
		Set entrySet = dataMap.entrySet();
		for (Iterator i = entrySet.iterator(); i .hasNext(); ) {
			Map.Entry entry = (Map.Entry) i.next();
			fileStr += (entry.getKey() + "=" + escape((String) entry.getValue()) + "\n");
		}
		return fileStr;
	}
	
	/**
	 * Property의 내용을 HTML 형식의 문자열로 반환한다.
	 */
	public String toHtmlString() {
		String fileStr = "<table border=1>";
		Set entrySet = dataMap.entrySet();
		for (Iterator i = entrySet.iterator(); i .hasNext(); ) {
			Map.Entry entry = (Map.Entry) i.next();
			fileStr += ("<tr><td>" + entry.getKey() + "</td><td>" + escape((String) entry.getValue()) + "</td></tr>");
		}
		fileStr += "</table>";
		return fileStr;
	}
	
	/**
	 * 주어진 문자열에 나타나는 ${name} 부분을 Property에 저장된 ${name} 의 값으로 바꾸어준다.
	 */
	public String apply(String input) {
		if (input != null) {
			
			int firstStartIndex = input.indexOf("${");
			if (firstStartIndex != -1) {
				int firstEndIndex = input.indexOf("}", firstStartIndex + 2);
				if (firstEndIndex != -1) {
					int secondStartIndex = input.indexOf("${", firstEndIndex + 1);
					if (secondStartIndex == -1) {
						String foundName = input.substring(firstStartIndex + 2, firstEndIndex);
						String foundValue = (String) dataMap.get(foundName);
						if (foundValue != null) {
							return StringUtil.replace(input, new String[] { "${" + foundName + "}" }, new String[] { foundValue });
						}
					}
				}
			}

			List fromList = new ArrayList();
			List toList = new ArrayList();
			int startIndex = firstStartIndex;
			int endIndex = 0;
			while (startIndex != -1) {
				endIndex = input.indexOf("}", startIndex + 2);
				if (endIndex != -1) {
					String foundName = input.substring(startIndex + 2, endIndex);
					String foundValue = (String) dataMap.get(foundName);
					if (foundValue != null) {
						fromList.add("${" + foundName + "}");
						toList.add(foundValue);
					}
				} else {
					break;
				}
				startIndex = input.indexOf("${", endIndex + 1);
			}
			
			String[] fromStr = (String[]) fromList.toArray(new String[fromList.size()]);
			String[] toStr = (String[]) toList.toArray(new String[toList.size()]);
			return StringUtil.replace(input, fromStr, toStr);
		}
		return null;
	}
	
	
	/**
	 * 기본 toString()은 toFIleString()과 동일하게 구현함.
	 */
	public String toString() {
		return toFileString();
	}

	/**
	 * Escape 처리된 문자열을 원상 복귀 시킴
	 */
	private static String unescape(String input) {
		String[] fromStr = { "\\n", "\\r", "\\t", "\\\\" };
		String[] toStr = { "\n", "\r", "\t", "\\" };
		return StringUtil.replace(input, fromStr, toStr);
	}
	
	/**
	 * 문자열을 Escape 처리함
	 */
	private static String escape(String input) {
		String[] fromStr = { "\n", "\r", "\t", "\\" };
		String[] toStr = { "\\n", "\\r", "\\t", "\\\\" };
		return StringUtil.replace(input, fromStr, toStr);
	}
}