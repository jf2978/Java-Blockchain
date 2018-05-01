package com.jf2978;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class Utility {

    // Applies SHA-512 hash function to String input (BouncyCastle API)
    public static String SHA512(String input) {
        // Providers manage particular algorithms to implementation
        Security.addProvider(new BouncyCastleProvider()); // BouncyCastle provides the suite of ciphers/algorithms
        StringBuffer sb = new StringBuffer();
        try{
            // Create digest (i.e. "signed" input via SHA-512 hash function)
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] hash = messageDigest.digest(input.getBytes("UTF-8"));

            // Byte -> Hex conversion
            for(byte b : hash){
                sb.append(String.format("%02X ", b));
            }
        }catch(NoSuchAlgorithmException | UnsupportedEncodingException e){
            System.out.println(e.getMessage());
        }


        return sb.toString();
    }
}
