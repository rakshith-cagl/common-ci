package com.iexceed.security;

import android.util.Base64;

public class HashXor {
	   
	   
	   public final String hashValue(String pimie,String pimsi,String puid , String puname,String pInpin ,  String pDate)
	   {
		   
		  
		   byte[] encodedBytes=null;

		  
			   final String uname = puname;
			   //String inpin = "1234";
			   //String uname = "uname";
			   final String inpin = pInpin;
			   
			  
				   
			   
			   final String imie = pimie;
			   final String imsi = pimsi;
			   
			   String inconcatstr;
			   
			   
//			   System.out.println(newDate);
			   final String dtstr = pDate;
			   //System.out.println(dtstr);
	
			   final String day = dtstr.substring(0,3);
	//		   System.out.println(day);
			   final String hr = dtstr.substring(16,18);
	//		   System.out.println(hr);
			   final String min = dtstr.substring(19,21);
			   final String sec = dtstr.substring(22,24);
	//		   System.out.println(min);
			   final String yr = dtstr.substring(13,15);
	//		   System.out.println(yr);
			   final String dd = dtstr.substring(5,7);
	//		   System.out.println(dd);
			   final String mm = dtstr.substring(8,10);
	//		   System.out.println(mm);
			   
			   inconcatstr = hr+min+day+yr+dd+mm+sec+uname+imie+imsi;
			  
				   
			   //System.out.println("Concatenated Value : " + inconcatstr);
			   //String shainstr;
			   // Get the concatenated value
			   try
			   {
				   // Sha-256 value of concatenated String
				   //System.out.println("SHA-256 Concatenated String : " + ShaUtil.toSha256String(inconcatstr));
				   // Sha-256 value of 4-digit pin repeated twice to get 8-digits
				   //System.out.println("SHA-256 Pin : " + ShaUtil.toSha256String(inpin));
			       final HashXor xx = new HashXor();
			       // XOR Sha-256 values of concatenated String and PIN
			       //System.out.println("Xor Result : " + xx.xorHex(ShaUtil.toSha256String(inconcatstr), ShaUtil.toSha256String(inpin)));
			       final String hshxor = xx.xorHex(ShaUtil.toSha256String(inconcatstr), ShaUtil.toSha256String(inpin));
			     
			       // First Byte of XOR'ed Sha-256 value of concatenated String and PIN
			       //System.out.println("First Byte : " + hshxor.substring(0,16));
			       final String fbyte = hshxor.substring(0,16);
			       
			       
			       // Last Byte of XOR'ed Sha-256 value of concatenated String and PIN
			       //System.out.println("Last Byte : " + hshxor.substring(48));
			       final String lbyte = hshxor.substring(48);
			       // XOR First And Last Bytes
			       final String fblbxor = xx.xorHex(fbyte, lbyte);
			       //System.out.println("XOR FB + LB : " + fblbxor);
			    // XOR Above with Pin Hash
			      // System.out.println("XOR FB + LB + PinHash: " + xx.xorHex(fblbxor, ShaUtil.toSha256String(inpin)));
			       final String hshxor2 =xx.xorHex(fblbxor, ShaUtil.toSha256String(inpin));
			      
			       // Round 2 -> First Byte of XOR'ed Sha-256 value of concatenated String and PIN
			       //System.out.println("First Byte : " + hshxor2.substring(0,8));
			       final String fbyte2 = hshxor2.substring(0,8);
			       // Round 2 -> Last Byte of XOR'ed Sha-256 value of concatenated String and PIN
			       //System.out.println("Last Byte : " + hshxor2.substring(8));
			       final String lbyte2 = hshxor2.substring(8);
			       // Round 2 -> XOR First And Last Bytes
			       final String fblbxor2 = xx.xorHex(fbyte2, lbyte2);
			       //System.out.println("XOR FB + LB : " + fblbxor2);
			    // Round 2 -> XOR Above with Pin Hash
//			       System.out.println("XOR FB + LB + PinHash: " + xx.xorHex(fblbxor2, ShaUtil.toSha256String(inpin)));
			       final String finalstr = xx.xorHex(fblbxor2, ShaUtil.toSha256String(inpin));
			       
			       
			       //System.out.println("Final Str"+finalstr);
			    // Finally, the base64 encoded value
//			       BASE64Encoder encoder = new BASE64Encoder();
			       //encodedBytes = Base64.encodeBase64(finalstr.getBytes());
			       encodedBytes = Base64.encode(finalstr.getBytes(), Base64.NO_WRAP);
			       
			       //System.out.println("Final OTP : " + encodedBytes);
			       
			       
			       //System.out.println("new :"+ newcode);
			       
//			       BASE64Decoder decoder = new BASE64Decoder();
			       
			       //System.out.println("imie :"+ imie);
			       //System.out.println("val :"+ decodedstring);
			       
			       
			   }
			     catch(Exception   e)
			     {
//			       System.out.println(e.toString());
//			       System.out.println("Usage: Sha256 <text> or Sha256 -f<filename>");
			     }
			   
			   
			   return new String(encodedBytes);
		   
		   
	   }
	   
// XOR Truth Table from two Hex Strings 	   
public final String xorHex(String a, String b) {
    final char[] chars = new char[a.length()];
    for (int i = 0; i < chars.length; i++) {
    	
    	//System.out.println(fromHex(a.charAt(i)) ^ fromHex(b.charAt(i)));
        chars[i] = toHex(fromHex(a.charAt(i)) ^ fromHex(b.charAt(i)));
    }
    return new String(chars);
}
// char wise Hex to int

private static int fromHex(char c) {
    if (c >= '0' && c <= '9') {
        return c - '0';
    }
    if (c >= 'A' && c <= 'F') {
        return c - 'A' + 10;
    }
    if (c >= 'a' && c <= 'f') {
        return c - 'a' + 10;
    }
    throw new IllegalArgumentException();
}

//char wise int to Hex
private char toHex(int nybble) {
    if (nybble < 0 || nybble > 15) {
        throw new IllegalArgumentException();
    }
    return "0123456789ABCDEF".charAt(nybble);
}
    
}
