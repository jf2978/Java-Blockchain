package com.jf2978;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class Utility {

    public static String SHA512(String input){
        try{
            Security.setProperty("crypto.policy", "unlimited");
            Security.addProvider(new BouncyCastleProvider());

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] hash = messageDigest.digest(input.getBytes("UTF-8"));
            return new String(hash, StandardCharsets.UTF_8);
        }
        catch(NoSuchAlgorithmException | UnsupportedEncodingException e){
            System.out.println(e.getMessage());
        }
        return "";
    }
}
