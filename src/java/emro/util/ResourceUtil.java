package emro.util; 

import java.io.*; 

/**
 * <PRE>
 * Filename	: ResourceUtil.java <BR>
 * Package	: com.emro.util <BR>
 * Function	: <BR>
 * Comment	: 파일 처리관련 메소드를 다루는데 유용한 각종 유틸리티 메쏘드 <BR>
 * History	: 2009/04/01,  v1.0, 최초작성 <BR>
 * </PRE>
 * @version	1.0
 * @author 	이창진 Copyright (c) 2009 by EMRO Corp. All Rights Reserved.
 *
 */
public class ResourceUtil { 
	/**
	 * 최대 500번까지 읽는 작업을 할 수 있다.
	 */
	public static int MAX_COUNT = 500;
	public static int MAX_INIT_TRY = 1000;
	public static String OUTPUT_STREAM_WRITER_CHARSET = "UTF-8";
	public static String INPUT_STREAM_READER_CHARSET = "UTF-8";

	/**
	 * 파일 바이트 배열을 파일로 저장한다.
	 */
	public static void saveToFile(String fileStr, byte[] src) throws java.io.IOException {
		FileOutputStream fos = new FileOutputStream( fileStr, false);
		fos.write(src);
		fos.close();
	}
	
	/**
	 * 특정한 String을 주어진 파일로 저장한다.
	 */
	public static void saveToFile(String fileStr, String content) throws java.io.IOException {
		FileOutputStream fos = new FileOutputStream( fileStr, false);
		OutputStreamWriter osw = new OutputStreamWriter(fos, OUTPUT_STREAM_WRITER_CHARSET);
		osw.write(content);
		osw.close();
	}
	
	/**
	 * 파일을 읽어 byte로 리턴한다.
	 */
	public static byte[] getByteArrayFromFile(String fileStr) throws java.io.IOException {
		int count = 0; 
		int totalBytesRead = 0; 
		int bytesRead = 0;
		int availablelength = 0; 
		int currArraySize = 0;
		byte[] curContent = new byte[currArraySize];
				
		InputStream input = new FileInputStream(fileStr);
		BufferedInputStream in = new BufferedInputStream(input);
		
		int tryLimit = 0;
		while( availablelength <= 0 && tryLimit < MAX_INIT_TRY ) { 
			availablelength = in.available();
			tryLimit++;
		}
		
		while( (bytesRead > 0 || availablelength != 0) && count < MAX_COUNT) 
		{ 
			availablelength = in.available();
			if(currArraySize < availablelength) {
				currArraySize = availablelength;
				byte[] preContent = curContent;
				curContent = new byte[currArraySize];
				System.arraycopy( preContent, 0, curContent, 0, totalBytesRead );
			}
			bytesRead = in.read(curContent,totalBytesRead,availablelength);
			count++;
			totalBytesRead += bytesRead;
		}
		
		totalBytesRead = totalBytesRead - bytesRead;
		in.close(); 
		return curContent;
	}

	public static String toString(InputStream inputStream) throws java.io.IOException {
		StringBuffer contents = new StringBuffer();
		String newLine = "";
		try {
			BufferedReader br = new BufferedReader( new InputStreamReader(inputStream, INPUT_STREAM_READER_CHARSET) );
			while( (newLine=br.readLine()) != null ) {
				contents.append( newLine + "\n" );
			}
		    inputStream.close();
		}
		catch( NullPointerException e ) {
		    e.printStackTrace();
		}
		return contents.toString();
	}
	
	public static String getStringFromFile(String fileStr) throws java.io.IOException {
		FileInputStream fis = new FileInputStream(fileStr);
		return ResourceUtil.toString(fis);
	}

	public static boolean mkdirs(String dirStr) throws java.lang.SecurityException {
		File dir = new File(dirStr);
		return dir.mkdirs();
	}

	public static boolean mkdir(String dirStr) throws java.lang.SecurityException {
		File dir = new File(dirStr);
		return dir.mkdir();
	}

	public static boolean delete(String fileStr) throws java.lang.SecurityException {
		File dir = new File(fileStr);
		return dir.delete();
	}

	public static boolean exist(String fileStr) throws java.lang.SecurityException {
		File dir = new File(fileStr);
		return dir.exists();
	}

}