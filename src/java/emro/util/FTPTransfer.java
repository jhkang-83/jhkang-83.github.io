package emro.util;

import java.io.*;
import java.net.SocketException;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPTransfer {
	
	public FTPTransfer() { 
		
	}
	
    public boolean FtpPut(String ip, int port, String id, String password, 
         String uploaddir, String makedir, String sourceFile) {
 
     boolean result = false;
        FTPClient ftp = null;
        int reply = 0;
        
        try {
            ftp = new FTPClient();
            ftp.connect(ip, port);
            
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return result;
            }
            
            if(!ftp.login(id, password)) {
             ftp.logout();
             return result;
            }
 
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
 
            ftp.changeWorkingDirectory(uploaddir);
            ftp.makeDirectory(makedir);
            ftp.changeWorkingDirectory(makedir);
            
             File uploadFile = new File(sourceFile);
             FileInputStream fis = null;
             try {
                 fis = new FileInputStream(uploadFile);
                 boolean isSuccess = ftp.storeFile(uploadFile.getName(), fis);
                 if (isSuccess) {
                     System.out.println(sourceFile+" 파일 FTP 업로드 성공");
                 }
             } catch(IOException ioe) {
             } finally {
                 if (fis != null) {
                  try { 
                   fis.close(); 
                  } catch(IOException ioe) {
                  }
                 }
             }
//            }
            
            ftp.logout();
            result = true;
            
        } catch (SocketException se) {
        } catch (IOException ioe) {
        } catch (Exception e) {
        } finally {
            if (ftp != null && ftp.isConnected()) {
                try { ftp.disconnect(); } catch (IOException e) {}
            }
        }
 
        return result;
    }
    
    public boolean FtpGet(String ip, int port, String id, String password, 
   String localdir, String serverdir, String fileName) {
        boolean result = false;
        FTPClient ftp = null;
        int reply = 0;
        
        try {
            ftp = new FTPClient();
            
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return result;
            }
            
            if(!ftp.login(id, password)) {
             ftp.logout();
             return result;
            }
 
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
 
            ftp.changeWorkingDirectory(serverdir);
 
            File f = new File(localdir, fileName);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                boolean isSuccess = ftp.retrieveFile(fileName, fos);
                if (isSuccess) {
                    System.out.println("다운로드 성공");
                } else {
                    System.out.println("다운로드 실패");
                }
            } catch(IOException ioe) {
            } finally {
                if (fos != null) try { fos.close(); } catch(IOException ex) {}
            }
            ftp.logout();
        } catch (SocketException se) {
        } catch (IOException ioe) {
        } catch (Exception e) {
        } finally {
            if (ftp != null && ftp.isConnected()) {
                try { ftp.disconnect(); } catch (IOException e) {
                }
            }
        }
 
        return result;
    }
}
