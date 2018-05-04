package com.jf2978;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Utility {

    // Applies SHA-512 hash function to String input (BouncyCastle API)
    public static String SHA512(String input) {
        // Providers manage particular algorithms to implementation
        Security.addProvider(new BouncyCastleProvider()); // BouncyCastle provides the suite of ciphers/algorithms
        StringBuffer sb = new StringBuffer(); // StringBuffer used for future thread-safe use
        try {
            // Create digest (i.e. "signed" input via SHA-512 hash function)
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] hash = messageDigest.digest(input.getBytes("UTF-8"));

            // Byte -> Hex conversion
            for (byte b : hash) {
                sb.append(String.format("%02X ", b));
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }

    // Applies Elliptic-Curve Digital Signature Algorithm (DSA) -> returns byte[]
    public static byte[] ECDSASignature(PrivateKey dK, String input) {
        byte[] output = new byte[0];
        try {
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(dK);
            dsa.update(input.getBytes("UTF-8"));
            output = dsa.sign();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | UnsupportedEncodingException | SignatureException e) {
            System.out.println(e.getMessage());
        }
        return output;
    }

    // Verifies ECDSA Signature
    public static boolean verifyECDSASignature(PublicKey eK, String data, byte[] signature) {
        try {
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initVerify(eK);
            dsa.update(data.getBytes("UTF-8"));
            return dsa.verify(signature);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | UnsupportedEncodingException | SignatureException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // Public/Private Key -> String
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // Generate merkle root (hash value) from given list of transactions
    public static String getMerkleRoot(List<Transaction> transactions){
        int size = transactions.size();

        // Collect list of transaction hashes to build the merkle tree
        List<String> TXs = new ArrayList<>();
        for(Transaction t : transactions){
            TXs.add(t.id);
        }

        // Calculate each node by concatenating each pair of transaction hashes

        return "";
    }
}