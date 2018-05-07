package com.jf2978;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/** =====
 * The Utility class is used to contain overarching static helper functions such as the hash and signature algorithms.
 * This is used as a catch-all static class for now, but can be split if functionality becomes more expansive over
 * time.
 *
 * @author jf2978
 */
public class Utility {

    /** =====
     * Applies SHA-512 hash function to String input (BouncyCastle API)
     *
     * @param input String to hash
     * @return Hex string output from
     */
    public static String SHA512(String input) {
        // Providers manage particular algorithms to implementation
        Security.addProvider(new BouncyCastleProvider()); // BouncyCastle provides the suite of ciphers/algorithms
        StringBuffer sb = new StringBuffer(); // StringBuffer used for future thread-safe use
        try {
            // Create digest (i.e. "signed" input via SHA-512 hash function)
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] hash = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Byte -> Hex conversion
            for (byte b : hash) {
                sb.append(String.format("%02X ", b));
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }

    /** =====
     * Applies Elliptic-Curve Digital Signature Algorithm (DSA) → returns byte[]
     *
     * @param input String to sign
     * @param dK Private key to sign the input with
     * @return Signature as a byte array
     */
    public static byte[] ECDSASignature(String input, PrivateKey dK) {
        byte[] output = new byte[0];
        try {
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(dK);
            dsa.update(input.getBytes(StandardCharsets.UTF_8));
            output = dsa.sign();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            System.out.println(e.getMessage());
        }
        return output;
    }

    /** =====
     * Verifies ECDSA Signature
     *
     * @param data Expected result upon reversing signature
     * @param eK Public key of user to be verified
     * @param signature Signature we are verifying
     * @return Verification result
     */
    public static boolean verifyECDSASignature(String data, PublicKey eK, byte[] signature) {
        try {
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initVerify(eK);
            dsa.update(data.getBytes(StandardCharsets.UTF_8));
            return dsa.verify(signature);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /** =====
     * Converts key to string
     *
     * @param key Input key
     * @return Converted key to string
     */
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /** =====
     * Generate merkle root (hash value) from given list of transactions
     *
     * @param transactions List of transactions
     * @return Merkle tree root
     */
    public static String getMerkleRoot(List<Transaction> transactions){

        // Populate list of transaction hashes to build the first level of merkle tree
        List<String> TXHashes = new ArrayList<>();
        for(Transaction t : transactions){
            TXHashes.add(t.id);
        }

        // Create new Merkle Tree + return root hash
        MerkleTree merkleTree = new MerkleTree(TXHashes);
        return merkleTree.getRoot().hash;
    }

    //

    /** =====
     * Calculates transaction fees based on a income-progressive algorithm
     *
     * @param amount amount of transaction to calculate
     * @return Calculated fee amount
     */

    public static float calculateFee(float amount){
        // If a "casual" payment (arbitrary limit for now), no fee attached
        if(amount < 50f){
            return 0f;
        }
        // Placeholder for individual progressive fee
        else{
            return 0.1f;
        }
    }
}