package com.jf2978;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

public class Block<T> {

    // Instance variables
    private String signature; // aims to provide data integrity, origin authenticity + non-repudiation
    private String previousHash; // dependence on the previous block signature guarantees no tampering on previous blocks
    private String timestamp;
    private int nonce;
    private T data;

    // Constructor(s)
    public Block(T d, String prev){
        data = d;
        previousHash = prev;
        timestamp = LocalDateTime.now().toString();
        signature = this.hash();
    }

    // Calculates block digital signature (SHA512 cryptographic hash)
    public String hash() {
        return Utility.SHA512(previousHash + timestamp + data + Integer.toString(nonce));
    }

    // Simulates "mining" to establish proof-of-work concept to verify blocks
    public void mine(int diff){
        String target = StringUtils.leftPad("", diff, '0');
        int count = 0;
        while(!signature.substring(0, diff).equals(target)) {
            System.out.printf("Attempt %d: %s\n", count, signature);
            nonce++;
            signature = hash();
            count++;
        }
        System.out.println("Mining Successful: " + signature);
    }

    public String getSignature(){
        return this.signature;
    }

    public String getPreviousHash(){
        return this.previousHash;
    }
}