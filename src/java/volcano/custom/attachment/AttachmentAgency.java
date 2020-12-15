package volcano.custom.attachment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Blob;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import cryto.AES256Util;
import cryto.CrytoUtil;

import emro.util.Crypt;
import emro.util.JsonUtil;
import emro.util.StringUtil;
import eswf.dataobject.Map;
import eswf.description.ElementXMLDescriptor;
import eswf.exception.FoundationException;
import eswf.jdbc.JdbcAgency;
import eswf.managers.SessionManager;
import eswf.managers.TransactionManager;
import eswf.managers.XMLManager;
import eswf.util.XMLUtils;

public class AttachmentAgency {
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected JdbcAgency jdbc = new JdbcAgency();

	private static boolean initialized = false;
	public static String uploadRoot;
	public static String uploadTemp;
	public static String storeType ;
	private static int factorySizeThreshold = -1;
	private static int maxSizeFile = -1;

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyyMMddHHmmssSSS");
	private static final SimpleDateFormat sdfdate = new SimpleDateFormat("yyyyMMdd");
	public static final String STORE_TYPE_FILE = "file";
	public static final String STORE_TYPE_DBMS = "dbms";
	
	public AttachmentAgency() throws FoundationException {
		jdbc.setDefaults(SessionManager.getInstance().getSession());
	}

	public AttachmentAgency(HttpServletRequest request,
			HttpServletResponse response) throws FoundationException {
		this();
		if (!initialized) {
			initialize(request);
			initialized = true;
		}
		this.request = request;
		this.response = response;
		
	}
	
	public static void initialize(HttpServletRequest request)
			throws FoundationException {
		ElementXMLDescriptor exmld = XMLManager.getInstance().getDescriptor(
				"attachment");
		if (exmld == null)
			throw new RuntimeException("첨부파일 관련정보가 기술되어 있는 attachment.xml 이 없습니다.");
		storeType = exmld.getDescriptorElement("type.store").getTextTrim();
		uploadRoot = exmld.getDescriptorElement("path.file.store.root").getText().replaceAll("\\{web.inf\\}",
				correctFilePath(request.getSession().getServletContext().getRealPath("/")));
		uploadTemp = uploadRoot + "/temp";
		uploadTemp = correctFilePath(uploadTemp);
		File urFile = new File(uploadRoot);
		if (!urFile.exists())
			urFile.mkdir();
		File utFile = new File(uploadTemp);
		if (!utFile.exists())
			utFile.mkdir();
		if (storeType.equals(STORE_TYPE_FILE)) {
			factorySizeThreshold = Integer.parseInt(exmld.getDescriptorElement(
					"size.treshold.memory.using").getTextTrim()) * 1024 * 1024;
			maxSizeFile = Integer.parseInt(exmld.getDescriptorElement(
					"max.size.file").getTextTrim()) * 1024 * 1024;
		}
		System.out.println("uploadRoot >> " + uploadRoot);
		System.out.println("storeType >> " + storeType);
	}

	public static void main(String args[]) {
	}

	public String produceIdentity() {
		return sdf.format(new Date()) + request.getSession().getId()
				+ (Math.random() * 10000000000L);
	}

	private String extractExtension(String filename) {
		if (filename.lastIndexOf(".") > -1)
			return filename.substring(filename.lastIndexOf(".") + 1);
		else
			return "";
	}

	private byte[] toByteArray(File f) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] temp = new byte[512];
		int idx = 0;
		while ((idx = fis.read(temp)) != -1) {
			baos.write(temp, 0, idx);
		}
		baos.flush();
		byte[] result = baos.toByteArray();
		baos.close();
		return result;
	}

	public static String correctFilePath(String s) {
		if (s == null)
			return null;
		return s.replaceAll("[\\/\\\\]+", "/");
	}

	public void upload() throws Exception {
		StringBuffer responseXML = new StringBuffer("<?xml version\"1.0\" encoding=\"utf-8\" ?>\n");
		TransactionManager.getInstance().transact();
		try {
			File uploadRootFile = new File(uploadRoot);
			File uploadTempFile = new File(uploadTemp);
			System.out.println("upload >>>" + uploadTempFile);
			ElementXMLDescriptor exmld = XMLManager.getInstance().getDescriptor("attachment");
			if(exmld == null)
			{
				throw new RuntimeException("첨부파일 관련정보가 기술되어 있는 attachment.xml 이 없습니다.");
			}
			
			String serverFileType = exmld.getDescriptorElement("upload.file.type.server").getTextTrim();
			String[] permissionType = null;
			int k;
			int permissionSize = 0;
			Boolean typeCheck = false;
			if(serverFileType.length() > 0)
			{
				permissionType = serverFileType.split(",");
				permissionSize = permissionType.length;
			}
			
			System.out.println("upload permissionType >>>" + permissionType);
			System.out.println("upload permissionSize >>>" + permissionSize);
			
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(factorySizeThreshold);
			factory.setRepository(uploadTempFile);
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(maxSizeFile);
			upload.setHeaderEncoding("utf-8");
			List<FileItem> items = upload.parseRequest(request);
			Iterator<FileItem> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = iter.next();
				if (item.isFormField()) {
					continue;
				}

				String fullName = item.getName();
				long filesize = item.getSize();
				String filename = fullName
						.substring(fullName.lastIndexOf("\\") + 1);
				String filetype = extractExtension(filename);
				System.out.println("filename >>> " + filename);
				System.out.println("filesize >>> " + filesize);
				if(filetype == null || filetype.length() == 0)
				{
					throw new Exception("파일 전송중 에러가 발생하였습니다.\n[업로드 파일의 확장자가 존재하지 않습니다].");
				}
				
				if(permissionSize > 0)
				{
					for(k=0;k<permissionSize;k++)
					{
						if((filetype.toLowerCase()).equals(permissionType[k]))
						{
							typeCheck = true;
							break;
						}
					}
				}
				
				if(typeCheck == false)
				{
					throw new Exception("파일 전송중 에러가 발생하였습니다.\n[업로드 할 수 없는 확장자가 존재합니다].");
				}
				
				String filekey;
				File uploadedFile; 
				do{
					filekey = produceIdentity() + "." + filetype;
					uploadedFile = new File(correctFilePath(uploadTempFile
							.getAbsolutePath()
							+ File.separator + filekey));
				}while (uploadedFile.exists());
				item.write(uploadedFile);

				java.util.Map<Object, Object> attach = jdbc.executeQuery("ext/attachment", "select.attachment.seq.nextval", null);
				attach.put("grp_cd", request.getParameter("shared_group"));
				
				responseXML.append("<data>");
				responseXML.append("<filecount>1</filecount>");
				responseXML.append("<filesize>" + filesize + "</filesize>");
				responseXML.append("<filename>" + XMLUtils.exchangeNamedEntity(filename) + "</filename>");
				responseXML.append("<att_seq>" + attach.get("att_seq") + "</att_seq>");
				responseXML.append("<grp_cd>" + attach.get("grp_cd") + "</grp_cd>");
				responseXML.append("<att_file_nm>" + filekey + "</att_file_nm>");
				responseXML.append("<orgn_file_nm>" + XMLUtils.exchangeNamedEntity(filename) + "</orgn_file_nm>");
				responseXML.append("<att_file_siz>" + filesize + "</att_file_siz>");
				responseXML.append("<fileEncoding>utf-8</fileEncoding>");
				responseXML.append("</data>");
				attach.put("att_file_nm", filekey);
				attach.put("orgn_file_nm", filename);
				attach.put("att_file_siz", filesize);
				
				System.out.println("attach >>> " + attach);
				System.out.println("responseXML >>> " + responseXML);
				if (storeType.equals(STORE_TYPE_FILE)) {
					String filePath = sdfdate.format(new Date());
					File targetDirectory = new File(correctFilePath(uploadRootFile.getAbsolutePath()+ File.separator + filePath));
					if (!targetDirectory.exists())
						targetDirectory.mkdirs();
					File targetFile = new File(correctFilePath(targetDirectory.getAbsolutePath() + File.separator + filekey));
					uploadedFile.renameTo(targetFile);
					attach.put("att_file_path", filePath);
				} else if (storeType.equals(STORE_TYPE_DBMS)) {
					byte[] fileArray = toByteArray(uploadedFile);
					attach.put("file_cont", fileArray);
				}
					
				jdbc.executeUpdate("ext/attachment", "insert.attachment", attach);
			}
			
			System.out.println("success~~~~");
			TransactionManager.getInstance().accept();
		} catch (SizeLimitExceededException e) {
			e.printStackTrace();
			
			responseXML.append("<data><msg>파일사이즈가 초과되었습니다.(업로드 사이즈 :"
					+ e.getActualSize() + ", 업로드 가능한 사이즈:"
					+ e.getPermittedSize() + ")</msg></data>");
			TransactionManager.getInstance().restore();
		} catch (FileUploadException e) {
			
			e.printStackTrace();
			responseXML
					.append("<data><msg>" + e.getMessage() + "</msg></data>");
			TransactionManager.getInstance().restore();
		} catch (Exception e) {	
			
			e.printStackTrace();
			responseXML = new StringBuffer("<?xml version\"1.0\" encoding=\"utf-8\" ?>\n");
			responseXML.append("<data><msg>" + e.getMessage() + "</msg></data>");
			TransactionManager.getInstance().restore();
		} finally {
			
			TransactionManager.getInstance().removeSession();
			System.gc();
		}
		
		JSONObject resultJson = XML.toJSONObject(responseXML.toString());
		
		System.out.println("resultJson >> " + resultJson);
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(resultJson.toString());
		writer.flush();
		
	}

	public void download() throws Exception {
		java.util.Map<String, Object> map = new Map();
		map.put("att_seq", request.getParameter("att_seq"));
		map.put("grp_cd", request.getParameter("grp_cd"));
		TransactionManager.getInstance().transact();
		try{
			map = jdbc.executeQuery("ext/attachment", "select.attachment", map);
			TransactionManager.getInstance().accept();
		}catch(Exception e){
			TransactionManager.getInstance().restore();
		}finally{
			TransactionManager.getInstance().removeSession();
		}
		FileInputStream fis = null;
		BufferedInputStream bin = null;
		BufferedOutputStream bos = null;
		try {
			response.resetBuffer();
			response.setContentLength(Integer.parseInt((String) map.get("att_file_siz")));
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=\""+ new String(((String) map.get("orgn_file_nm")).getBytes("euc-kr"), "ISO8859_1") + "\"");
			if (storeType.equals(STORE_TYPE_FILE)) {
				File file = new File(correctFilePath(uploadRoot
						+ File.separator + map.get("att_file_path")
						+ File.separator + map.get("att_file_nm")));
				fis = new FileInputStream(file);
				bin = new BufferedInputStream(fis);
			} else if (storeType.equals(STORE_TYPE_DBMS)) {
				bin = new BufferedInputStream(((Blob) map.get("file_cont")).getBinaryStream());
			}
			bos = new BufferedOutputStream(response.getOutputStream());
			byte buf[] = new byte[2048];
			for (int read = 0; (read = bin.read(buf)) != -1;)
				bos.write(buf, 0, read);
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if (bos != null)
				bos.close();
			if (bin != null)
				bin.close();
			if (fis != null)
				fis.close();
		}
	}
	
	public void list() throws Exception{ 
		response.setHeader("Pragma","no-cache"); 
	    response.setDateHeader("Expires",0); 
	    response.setHeader("Cache-Control","no-store");
		response.setContentType("text/xml;charset=UTF-8");
		
		java.util.Map<String, Object> map = new Map();
		JSONObject objJson = new JSONObject(request.getReader().readLine());
		String shared_group = objJson.getString("shared_group");
		map.put("grp_cd", shared_group);
		List<java.util.Map> list = null;
		TransactionManager.getInstance().transact();
		try{
			list = jdbc.executeQueryList("ext/attachment", "select.attachment.list", map);
			TransactionManager.getInstance().accept();
		}catch(Exception e){
			TransactionManager.getInstance().restore();
		}finally{
			TransactionManager.getInstance().removeSession();
		}
		
		net.minidev.json.JSONArray arrayJson = JsonUtil.getJsonArrayFromList(list);
//		StringBuffer fileListXML = new StringBuffer("<?xml version='1.0' encoding='UTF-8' ?>\n<filelist>");
//		for (int i=0; i<list.size(); i++)
//		{
//			java.util.Map fileMap = list.get(i);
//			XMLUtils.exchangeNamedEntityMap(fileMap);
//			fileListXML.append("<file " +
//					" name='" + fileMap.get("orgn_file_nm") + "'" +
//					" size='" + fileMap.get("att_file_siz") + "'" + 
//					" att_seq='" + fileMap.get("att_seq") + "'" +
//					" grp_cd='" + fileMap.get("grp_cd") + "'" +
//					" att_file_nm='" + fileMap.get("att_file_nm") + "'" +
//					" att_file_path='" + fileMap.get("att_file_path") + "'" +
//					" reg_nm='" + fileMap.get("reg_nm") + "'" +
//					" reg_dt='" + fileMap.get("reg_dt") + "'" +
//					"/>");
//			fileListXML.append("\n");
//		}
//		fileListXML.append("</filelist>");
//		
//		System.out.println("fileListXML >> " + fileListXML);
		JSONObject resultJson = new JSONObject(arrayJson);
		
		System.out.println("resultJson >> " + arrayJson);
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(arrayJson.toJSONString());
		writer.flush();
		
	}
	 
	public List getFilelist(String grpCd) throws Exception{ 
		java.util.Map<String, Object> map = new Map();
		map.put("grp_cd", grpCd);
		List<java.util.Map> list = null;
		TransactionManager.getInstance().transact();
		try{
			list = jdbc.executeQueryList("ext/attachment", "select.attachment.list", map);
			TransactionManager.getInstance().accept();
		}catch(Exception e){
			TransactionManager.getInstance().restore();
		}finally{
			TransactionManager.getInstance().removeSession();
		}
		
		return list;
	}
	
	public String save(String group, List transmits, List deletes) throws Exception{
		if(group != null && group.equals("undefined")){
			group = null;
		}
		if(group == null || group.trim().equals("") || group.equals("tYOJCP+Qrqob53MuDRwyOw==")){
			if(transmits.size() > 0){
				java.util.Map<Object, Object> attach = jdbc.executeQuery("ext/attachment", "select.attachment.shared.group.seq.nextval", null);
				group = (String)attach.get("group");
			}
		}
		for(int i=0 ; i < transmits.size() ; i++){
			java.util.Map m = (java.util.Map)transmits.get(i);
			m.put("grp_cd", group);
		}
		jdbc.executeBatch("ext/attachment", "update.attachment.grouping", transmits);
		jdbc.executeBatch("ext/attachment", "delete.attachment", deletes);
		return group;
	}
	
	public String copy(String group) throws Exception{
		String copyGroup = null;
		
		if(group != null){
			java.util.Map<Object, Object> attach_grp = jdbc.executeQuery("ext/attachment", "select.attachment.shared.group.seq.nextval", null);
			
			eswf.dataobject.Map attach = new eswf.dataobject.Map();
			
			attach.put("group", attach_grp.get("group"));
			attach.put("old_group", group);
			
			jdbc.executeUpdate("ext/attachment", "copy.attachment.group", attach);
			
			copyGroup = (String)attach_grp.get("group");
		}
		
		return copyGroup;
	}
	
	public String copyList(List<Map> group) throws Exception{
		String grp_cd = null;
		
		if( group.size() > 0 )
		{
			eswf.dataobject.List<Map> attachList = new eswf.dataobject.List<Map>();
			Map attachData = null;
			
			java.util.Map<Object, Object> attach_grp = jdbc.executeQuery("ext/attachment", "select.attachment.shared.group.seq.nextval", null);
			
			for( int i = 0; i < group.size(); i++ )
			{
				Map param = new Map();
					param.put( "grp_cd", group.get(i).get("att_no") );
					
				List<java.util.Map> list = jdbc.executeQueryList("ext/attachment", "get.attachment.group", param);
				
				for( int j = 0; j < list.size(); j++ )
				{
					attachData = new Map();
					attachData.put("new_grp_cd",    attach_grp.get("group")          );
					attachData.put("orgn_file_nm",  list.get(j).get("orgn_file_nm")  );
					attachData.put("att_file_nm",   list.get(j).get("att_file_nm")   );
					attachData.put("att_file_path", list.get(j).get("att_file_path") );
					attachData.put("att_file_siz",  list.get(j).get("att_file_siz")  );
					attachData.put("file_cont",     list.get(j).get("file_cont")     );
					attachData.put("rem",           list.get(j).get("rem")           );
					attachData.put("reg_comp_cd",   list.get(j).get("reg_comp_cd")   );
					attachData.put("mod_comp_cd",   list.get(j).get("mod_comp_cd")   );
					
					attachList.add(attachData);
				}
			}
			
			jdbc.executeBatch("ext/attachment", "insert.attachment.new_group", attachList);
			
			grp_cd = (String)attach_grp.get("group");
		}
		
		return grp_cd;
	}
	
	public String copyAttSeqList(List<Map> list, String grp_cd) throws Exception
	{
		if( grp_cd == null )
		{
			java.util.Map<Object, Object> attach_grp = jdbc.executeQuery("ext/attachment", "select.attachment.shared.group.seq.nextval", null);
			
			grp_cd = attach_grp.get("group").toString();
		}
		
		for( int i = 0; i < list.size(); i++ )
		{
			Map item = list.get(i);
				item.put( "new_grp_cd", grp_cd );
		}
		
		jdbc.executeBatch("ext/attachment", "insert.attachment.new_group", list);
		
		return grp_cd;
	}
	
	protected DecimalFormat dformat = new DecimalFormat("#########################0.#########");
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	protected SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyyMMddhhmmss");
	
	protected Workbook fatoryWorkbook(InputStream is, String type) throws IOException{
		
		if(type.equals("xlsx"))
			return new XSSFWorkbook(is);
		else
			return  new HSSFWorkbook(new POIFSFileSystem( new BufferedInputStream(is)));
	}
	public eswf.dataobject.List getExcelInfo(eswf.dataobject.Map param) throws Exception
	{
		JdbcAgency jdbc = new JdbcAgency();
		eswf.dataobject.Map rtnMap = new eswf.dataobject.Map();
		List<java.util.Map> list = null;
		TransactionManager.getInstance().transact();
		try{
			list = jdbc.executeQueryList("ext/attachment", "select.attachment.excel.list", param);
			TransactionManager.getInstance().accept();
		}catch(Exception e){
			TransactionManager.getInstance().restore();
		}finally{
			TransactionManager.getInstance().removeSession();
		}
		
		eswf.dataobject.List<eswf.dataobject.Map> fileList = new eswf.dataobject.List<eswf.dataobject.Map>();
		
		for(int fileCnt = 0 ; fileCnt < list.size() ; fileCnt++)
		{
			eswf.dataobject.Map filerow = new eswf.dataobject.Map();
			
			java.util.Map excelFileInfo = list.get(fileCnt);
			FileInputStream fis = null;
			BufferedInputStream bin = null;
			Workbook workbook = null;
			try
			{
				String filename = (String)(excelFileInfo.get("orgn_file_nm"));
				if (storeType.equals(STORE_TYPE_FILE)) {
					File file = new File(correctFilePath(uploadRoot
							+ File.separator + excelFileInfo.get("att_file_path")
							+ File.separator + excelFileInfo.get("att_file_nm")));
					fis = new FileInputStream(file);
					workbook = fatoryWorkbook(fis, filename.substring(filename.lastIndexOf(".")+ 1));
				} else if (storeType.equals(STORE_TYPE_DBMS)) {
					workbook = fatoryWorkbook(((Blob) excelFileInfo.get("file_cont")).getBinaryStream(), filename.substring(filename.lastIndexOf(".") + 1));
				}
			}
			catch(Exception e)
			{
				filerow.put("filename", excelFileInfo.get("orgn_file_nm")+"(error)");
				fileList.add(filerow);
//				e.printStackTrace();
				continue;
			}
			filerow.put("filename", excelFileInfo.get("orgn_file_nm"));
			filerow.put("att_file_siz", excelFileInfo.get("att_file_siz"));
			filerow.put("att_seq", excelFileInfo.get("att_seq"));
			filerow.put("grp_cd", excelFileInfo.get("grp_cd"));
			filerow.put("att_file_nm", excelFileInfo.get("att_file_nm"));
			filerow.put("att_file_path", excelFileInfo.get("att_file_path"));
			
			eswf.dataobject.List<eswf.dataobject.Map> sheetList = new eswf.dataobject.List<eswf.dataobject.Map>();
			if(workbook != null)
			{
				int sheetLength = workbook.getNumberOfSheets();
				for(int sheetnum = 0 ; sheetnum < sheetLength ; sheetnum++)
				{
					eswf.dataobject.Map sheetrow =  new eswf.dataobject.Map();
					Sheet sheet = workbook.getSheetAt(sheetnum);
					
					sheetrow.put("sheet_name", workbook.getSheetName(sheetnum));
					eswf.dataobject.List<eswf.dataobject.Map> rowList = new eswf.dataobject.List<eswf.dataobject.Map>();
					int rowCnt = sheet.getLastRowNum();
					for(int rownum = 0 ; rownum <= rowCnt ; rownum++)
					{
						Row row = sheet.getRow(rownum);
						eswf.dataobject.Map rowMap = new eswf.dataobject.Map();
						if(row != null)
						{
							int cellCnt = row.getLastCellNum();
							for(short cellnum = 0 ; cellnum <= cellCnt ; cellnum++)
							{
								Cell cell = row.getCell(cellnum);
								if(cell != null)
								{
									CellStyle cellStyle = cell.getCellStyle();
									String value = "";
									switch( cell.getCellType())
									{
										case Cell.CELL_TYPE_FORMULA : 
											value = cell.getCellFormula();
											break;
										case Cell.CELL_TYPE_NUMERIC :
											String formatString = cellStyle.getDataFormatString();
											if(formatString.toUpperCase().indexOf("YY") >= 0) {
												if(formatString.toUpperCase().indexOf("HH") >= 0) {
													value = datetimeFormat.format(cell.getDateCellValue());
												} else {
													value = dateFormat.format(cell.getDateCellValue());
												}
											} else {
												value = dformat.format(cell.getNumericCellValue());
											}
											break;
										case Cell.CELL_TYPE_STRING : 
											value = cell.getStringCellValue();
											break;
										case Cell.CELL_TYPE_BLANK : 
											value = "";
											break;
										case Cell.CELL_TYPE_ERROR : 
											value = dformat.format(cell.getErrorCellValue());
											break;
										default :
									}
									if(value == null || value.equals("null"))
										value = "";
									rowMap.put("value"+cellnum, value);
								}//end cell check if
							}//end Cell for
						}//end row Check if
						else
						{
							continue;
						}
						rowList.add(rowMap);
					}//end row for
					sheetrow.put("rows", rowList);
					sheetList.add(sheetrow);
				}//end sheet for
				filerow.put("rows", sheetList);
				fileList.add(filerow);
			}
			
			
		}
		return fileList;
	}
	
	public void getImagefileList() throws Exception{ 
		java.util.Map<String, Object> map = new Map();
		map.put("grp_cd", request.getParameter("grp_cd"));
		List<java.util.Map> list = null;
		TransactionManager.getInstance().transact();
		try{
			list = jdbc.executeQueryList("ext/attachment", "select.attachment.list", map);
			TransactionManager.getInstance().accept();
		}catch(Exception e){
			TransactionManager.getInstance().restore();
		}finally{
			TransactionManager.getInstance().removeSession();
		}
		java.util.Map<String, Object> fileMap = null;
		
		response.setContentType("text/xml;charset=euc-kr");
		
		String imgUrl = "/downloadAttachfiles.do?att_seq="; 
		
		StringBuffer fileListXML = new StringBuffer("<?xml version='1.0' encoding='UTF-8' ?>");
		fileListXML.append("\n<data>");
		
		for (int i=0; i<list.size(); i++)
		{
			fileMap = list.get(i);
			
			fileListXML.append("\n<row>");
				fileListXML.append("\n<title>" + fileMap.get("orgn_file_nm") + "</title>"); 
				fileListXML.append("\n<thumbnail>" + imgUrl + fileMap.get("att_seq") + "</thumbnail>");
				fileListXML.append("\n<source>" + imgUrl + fileMap.get("att_seq") + "</source>");			
			fileListXML.append("\n</row>");						
		}
		fileListXML.append("\n</data>");
		
        response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.println(fileListXML.toString());
	}		
	
	/**
	 * SK M 프로젝트 파일 업로드 샘플
	 */
	public String fileUpload() throws Exception {
		String attachments = "";
		TransactionManager.getInstance().transact();
		StringBuffer responseXML = new StringBuffer("<?xml version\"1.0\" encoding=\"utf-8\" ?>\n<servicegroup><datagroup>");
		try {
			File uploadRootFile = new File(uploadRoot);
			File uploadTempFile = new File(uploadTemp);
			
			ElementXMLDescriptor exmld = XMLManager.getInstance().getDescriptor("attachment");
			if(exmld == null)
			{
				throw new RuntimeException("첨부파일 관련정보가 기술되어 있는 attachment.xml 이 없습니다.");
			}
			
			String serverFileType = exmld.getDescriptorElement("upload.file.type.server").getTextTrim();
			String[] permissionType = null;
			int k;
			int permissionSize = 0;
			Boolean typeCheck = false;
			
			if(serverFileType.length() > 0)
			{
				permissionType = serverFileType.split(",");
				permissionSize = permissionType.length;
			}
			
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(factorySizeThreshold);
			factory.setRepository(uploadTempFile);
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(maxSizeFile);
			upload.setHeaderEncoding("utf-8");
			List<FileItem> items = upload.parseRequest(request);
			
			
			Iterator<FileItem> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = iter.next();
				if (item.isFormField()) {
					continue;
				}

				String fullName = item.getName();
				long filesize = item.getSize();
				String filename = fullName
						.substring(fullName.lastIndexOf("\\") + 1);
				String filetype = extractExtension(filename);
				String filekey;
				
				if(filetype == null || filetype.length() == 0)
				{
					throw new Exception("파일 전송중 에러가 발생하였습니다.[업로드 파일의 확장자가 존재하지 않습니다].(외부전송파일)");
				}
				
				if(permissionSize > 0)
				{
					for(k=0;k<permissionSize;k++)
					{
						if((filetype.toLowerCase()).equals(permissionType[k]))
						{
							typeCheck = true;
							break;
						}
					}
				}
				
				if(typeCheck == false)
				{
					throw new Exception("첨부파일에 업로드 할 수 없는 확장자가 존재합니다.(외부전송파일)");
				}
				
				File uploadedFile; 
				do{
					filekey = produceIdentity() + "." + filetype;
					uploadedFile = new File(correctFilePath(uploadTempFile
							.getAbsolutePath()
							+ File.separator + filekey));
				}while (uploadedFile.exists());
				item.write(uploadedFile);
				
				if(!StringUtil.isEmpty(filename))
				{
					java.util.Map<Object, Object> attach = jdbc.executeQuery(
							"ext/attachment", "select.attachment.seq.nextval", null);
					attach.put("grp_cd", request.getParameter("shared_group"));
					attach.put("att_file_nm", filekey);
					attach.put("orgn_file_nm", filename);
					attach.put("att_file_siz", filesize);
					if (storeType.equals(STORE_TYPE_FILE)) {
						String filePath = sdfdate.format(new Date());
						File targetDirectory = new File(correctFilePath(uploadRootFile.getAbsolutePath()+ File.separator + filePath));
						if (!targetDirectory.exists())
							targetDirectory.mkdirs();
						File targetFile = new File(correctFilePath(targetDirectory.getAbsolutePath() + File.separator + filekey));
						uploadedFile.renameTo(targetFile);
						attach.put("att_file_path", filePath);
					} else if (storeType.equals(STORE_TYPE_DBMS)) {
						byte[] fileArray = toByteArray(uploadedFile);
						attach.put("file_cont", fileArray);
					}
					
					
					jdbc.executeUpdate("ext/attachment", "insert.attachment", attach);
					attachments += (String)attach.get("att_seq");
					if(iter.hasNext()) {
						attachments += ",";
					}
				}
				typeCheck = false;
			}
			
			if(StringUtil.isEmpty(attachments))
			{
				throw new Exception("파일 전송중 에러가 발생하였습니다.(외부전송파일)");
			}
			TransactionManager.getInstance().accept();
		} catch (SizeLimitExceededException e) {
			attachments="Error : 파일사이즈가 초과되었습니다.(업로드 사이즈 :" + (e.getActualSize() / 1024 / 1024) + ", 업로드 가능한 사이즈:"+ (e.getPermittedSize() / 1024 / 1024 ) +")";
//			e.printStackTrace();
			TransactionManager.getInstance().restore();
		} catch (FileUploadException e) {
			attachments="Error : [upload] 파일 전송중 오류가 발생하였습니다. 파일삭제 후 임시저장하십시요.(외부전송파일)";
//			e.printStackTrace();
			TransactionManager.getInstance().restore();
		} catch (Exception e) {
			attachments="Error : " + e.getMessage();
//			e.printStackTrace();
			TransactionManager.getInstance().restore();
		} finally {
			TransactionManager.getInstance().removeSession();
			System.gc();
		}
		return attachments;
	}
}
