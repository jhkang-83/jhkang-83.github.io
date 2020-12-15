package emro.util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import wdf.dataobject.BUObject;
import wdf.dataobject.BUObjectCache;

public class Crypt
{
	public final static String FIXED_KEY = "STW";
	
	public Crypt()
	{
	}

	public static String makeKey()
	{
		SimpleDateFormat simpledateformat = null;
		Date date = null;
		String s = null;
		Object obj = null;
		String s1 = "";
		int i = 0;
		boolean flag = false;
		Random random = new Random();
		simpledateformat = new SimpleDateFormat("yyMMddHHmmss");
		date = new Date();
		s = simpledateformat.format(date);
		i = (new Integer(s.substring(0, 8))).intValue() % 62;
		for(int j = 0; j < 8; j++)
		{
			s1 = s1 + clueToKey[i];
			i = random.nextInt(62);
		}

		return s1;
	}

	private static String encryption(String s, String s1)
		throws UnsupportedEncodingException
	{
		String s2 = "";
		int i = s.getBytes("ksc5601").length;
		int j = s1.getBytes("ksc5601").length;
		byte abyte0[] = new byte[i];
		byte abyte1[] = new byte[j];
		abyte0 = s.getBytes("ksc5601");
		abyte1 = s1.getBytes("ksc5601");
		boolean flag = false;
		for(int l = 0; l < i; l++)
		{
			int i1 = abyte0[l] ^ abyte1[l % 8];
			int k;
			if(i1 < 0)
				k = i1 + 256;
			else
				k = i1;
			s2 = s2 + mask[k];
		}

		return s2;
	}

	private static String decryption(String s, String s1)
		throws UnsupportedEncodingException
	{
		int i = s.length() / 2;
		byte abyte0[] = new byte[i];
		byte abyte1[] = new byte[s1.getBytes("ksc5601").length];
		byte abyte2[] = new byte[i];
		abyte1 = s1.getBytes("ksc5601");
		Object obj = null;
		for(int j = 0; j < i; j++)
		{
			String s3 = s.substring(2 * j, 2 * j + 2);
			for(int k = 0; k < mask.length; k++)
				if(mask[k].equals(s3))
					abyte0[j] = (byte)k;

			abyte2[j] = (byte)(abyte0[j] ^ abyte1[j % 8]);
		}

		String s2 = new String(abyte2, "ksc5601");
		return s2;
	}

	public static String encrypt(String s, String s1)
		throws UnsupportedEncodingException
	{
		return encryption(s, s1);
	}

	public static String decrypt(String s, String s1)
		throws UnsupportedEncodingException
	{
		return decryption(s, s1);
	}
	
	public static String encrypt(String s)
		throws UnsupportedEncodingException
	{
		String varKey = makeKey();
		return (varKey + encrypt(s, varKey));
	}
	
	public static String decrypt(String s)
		throws UnsupportedEncodingException
	{
		return decrypt(s.substring(8), s.substring(0, 8));
	}

	
	public static String fixedEncrypt(String s)
		throws UnsupportedEncodingException
	{
		return encryption(s, FIXED_KEY);
	}
	
	
	public static String fixedDecrypt(String s)
		throws UnsupportedEncodingException
	{
		return decryption(s, FIXED_KEY);
	}
	
	public static String encryptToFixedEncrypt(String s) 
		throws UnsupportedEncodingException {
		String decryptSource = decrypt(s.substring(8), s.substring(0, 8));
		return fixedEncrypt(decryptSource);
	}
	
	public static String fixedEncryptToEncrypt(String s) 
		throws UnsupportedEncodingException {
		String varKey = makeKey();
		String fixedDecryptSource = fixedDecrypt(s);
		return (varKey + encrypt(fixedDecryptSource, varKey));
	}

	
	private static String mask[] = {
		"b7", "Qv", "hO", "Qf", "mj", "9Q", "Ls", "oT", "Dx", "tp", 
		"AC", "fj", "6J", "IP", "Ol", "YR", "Jy", "rw", "vj", "P2", 
		"1d", "Hn", "wL", "XR", "S3", "LQ", "2V", "rb", "hx", "Dh", 
		"Jg", "IH", "a8", "am", "Vp", "MY", "qc", "Lk", "bR", "sp", 
		"S7", "6O", "Ck", "wZ", "CS", "lE", "mz", "rF", "9R", "eN", 
		"lg", "vD", "Fc", "lx", "QK", "ZL", "On", "GQ", "yT", "aJ", 
		"Tx", "V0", "ho", "4N", "NA", "7s", "Ki", "ZH", "sX", "uW", 
		"Np", "0M", "UY", "I7", "Bk", "kb", "VG", "c2", "FT", "Fw", 
		"Lx", "0i", "nO", "37", "c5", "L2", "Lj", "1e", "NH", "KN", 
		"l7", "4o", "yc", "hp", "7z", "Ga", "b0", "dE", "XO", "nx", 
		"wE", "7j", "KJ", "wA", "Ra", "uu", "0Q", "gI", "bb", "CV", 
		"IK", "y5", "5X", "LF", "BH", "WH", "hi", "kQ", "OQ", "id", 
		"OW", "Pk", "ty", "eD", "DE", "GL", "Ht", "OR", "N7", "rS", 
		"uw", "qM", "7Y", "c0", "1J", "Om", "Sn", "8x", "aw", "16", 
		"lj", "8E", "Lz", "u4", "Hc", "Ac", "5S", "hz", "Y1", "7H", 
		"TJ", "5M", "Wr", "OD", "ce", "eP", "Ws", "pp", "mL", "UE", 
		"nQ", "YM", "iR", "nn", "LO", "ee", "2Y", "mZ", "HZ", "LD", 
		"Ih", "5h", "Xc", "9T", "n1", "Xu", "T4", "3C", "LG", "48", 
		"rR", "tL", "xa", "Vh", "H6", "tx", "rf", "om", "0q", "NC", 
		"un", "1U", "XY", "Gf", "JD", "Xq", "tu", "aF", "oo", "mx", 
		"vc", "aW", "gp", "kP", "oE", "Gt", "Cn", "Fy", "tJ", "NT", 
		"7G", "Gi", "ro", "Co", "cf", "Cm", "dv", "Ab", "WD", "3W", 
		"Gy", "jc", "Xp", "L6", "qn", "Hr", "lk", "UC", "pY", "VV", 
		"jY", "65", "j5", "F5", "SH", "Kw", "n9", "XQ", "5O", "ni", 
		"rH", "AS", "us", "v3", "RE", "NB", "lQ", "xn", "hg", "LE", 
		"qm", "ri", "bz", "9B", "g6", "e5"
	};
	private static String clueToKey[] = {
		"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", 
		"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", 
		"u", "v", "w", "x", "y", "z", "A", "B", "C", "D", 
		"E", "F", "G", "H", "I", "J", "K", "L", "M", "N", 
		"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", 
		"Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", 
		"8", "9"
	};
	
	
	/**
	 * BUObject를 상속받은 dataobject의 특정파라미터의 값을 decrypt
	 * 
	 * @param object
	 * @param s :  파라미터명 배열
	 * @throws UnsupportedEncodingException
	 */
	public static void decryptDataObject(BUObject object, String[] s) 
		throws UnsupportedEncodingException {
		if(object != null) { 
			for(int i=0;i<s.length;i++) {
				String s1 = object.getParameter(s[i]);	
				if(s1 != null && s1.length() > 0) {	
					object.setParameter(s[i], decrypt(object.getParameter(s[i])));
				}	
			}
		}	
	}
	
	/**
	 * BUObject를 상속받은 dataobject의 특정파라미터의 값을 encrypt
	 * 
	 * @param object
	 * @param s :  파라미터명 배열
	 * @throws UnsupportedEncodingException
	 */
	public static void encryptDataObject(BUObject object, String[] s) 
		throws UnsupportedEncodingException {
		if(object != null) { 	
			for(int i=0;i<s.length;i++) {
				String s1 = object.getParameter(s[i]);	
				if(s1 != null && s1.length() > 0) {	
					object.setParameter(s[i], encrypt(object.getParameter(s[i])));
				}		
			}
		}	
	}
	
	/**
	 * BUObjectCache에 담긴 dataobject의 특정파라미터의 값을 decrypt
	 * 
	 * @param objectCache
	 * @param s :  파라미터명 배열
	 * @throws UnsupportedEncodingException
	 */
	public static void decryptObjectCache(BUObjectCache objectCache, String[] s) 
		throws UnsupportedEncodingException {
		for(int i=0;i<objectCache.size();i++) {
			decryptDataObject((BUObject)objectCache.get(i), s); 
		}	
	}
	
	/**
	 * BUObjectCache에 담긴 dataobject의 특정파라미터의 값을 encrypt
	 * 
	 * @param objectCache
	 * @param s :  파라미터명 배열
	 * @throws UnsupportedEncodingException
	 */
	public static void encryptObjectCache(BUObjectCache objectCache, String[] s) 
		throws UnsupportedEncodingException {
		for(int i=0;i<objectCache.size();i++) {
			encryptDataObject((BUObject)objectCache.get(i), s); 
		}	
	}
	
	/**
	 * HashMap의 특정파라미터의 값을 decrypt
	 * 
	 * @param h
	 * @param s :  파라미터명 배열
	 * @throws UnsupportedEncodingException
	 */
	public static void decryptHashMap(HashMap h, String[] s) 
		throws UnsupportedEncodingException {
		String paramValue;
		for(int i=0;i<s.length;i++) {
			if(h.containsKey(s[i])) {
				paramValue = (String) h.get(s[i]);	
				h.put(s[i], decrypt(paramValue));
			}	
		}
	}
	
	/**
	 * HashMap의 특정파라미터의 값을 encrypt
	 * 
	 * @param h
	 * @param s :  파라미터명 배열
	 * @throws UnsupportedEncodingException
	 */
	public static void encryptHashMap(HashMap h, String[] s) 
		throws UnsupportedEncodingException {
		String paramValue;
		for(int i=0;i<s.length;i++) {
			if(h.containsKey(s[i])) {
				paramValue = (String) h.get(s[i]);	
				h.put(s[i], encrypt(paramValue));
			}	
		}
	}
}
