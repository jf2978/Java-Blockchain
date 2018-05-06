package com.jf2978;

import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Block {

    // Instance variables
    public String signature; // aims to provide data integrity, origin authenticity + non-repudiation
    public String previousHash; // dependence on the previous block signature guarantees no tampering on previous blocks
    public String merkleRoot; // hash tree root for verifying transaction list
    public List<Transaction> transactions;
    public String timestamp;
    public int nonce;

    // Constructor(s)
    public Block(String prev){
        previousHash = prev;
        transactions = new ArrayList<>();
        timestamp = LocalDateTime.now().toString();
        signature = this.hash();
    }

    // Calculates block digital signature (SHA512 cryptographic hash)
    public String hash() {
        return Utility.SHA512(previousHash + timestamp + merkleRoot + Integer.toString(nonce));
    }

    // Simulates "mining" to establish proof-of-work concept to verify blocks
    public void mine(int diff){
        merkleRoot = Utility.getMerkleRoot(transactions);
        String target = StringUtils.leftPad("", diff, '0'); // Appends diff * ('0') to ""
        int count = 0;
        while(!signature.substring(0, diff).equals(target)) {
            System.out.printf("Attempt %d: %s\n", count, signature);
            nonce++;
            signature = hash();
            count++;
        }
        System.out.println("Mining Successful: " + signature);
    }

    public boolean addTransaction(Transaction t){
        // Valid Transaction check
        if(t == null || t.outputs == null){ return false; }

        // Check if not genesis block
        if(!previousHash.equals("0")){
            if(!t.process()) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(t);
        return true;
    }

    public String getSignature(){
        return this.signature;
    }

    public String getPreviousHash(){
        return this.previousHash;
    }

    public String toString(){
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}