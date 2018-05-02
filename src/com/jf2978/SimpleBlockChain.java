package com.jf2978;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SimpleBlockChain<T> {

    // Instance Variables
    private List<Block> blockchain;

    // Static Variables
    public static int difficulty = 2;


    // Constructor(s)
    public SimpleBlockChain(){
        blockchain = new ArrayList<>();
    }

    // Adds block to chain
    public void add(Block b) {
        blockchain.add(b);
    }

    // (Getter) Returns internal blockchain size
    public int size(){
        return blockchain.size();
    }

    // Checks if current state of blockchain is valid (current/prev hashes + proof-of-work)
   public boolean isValid() {
        String target = StringUtils.leftPad("", difficulty, '0');
        for (int i = 1; i < blockchain.size(); i++) {
            Block previous = blockchain.get(i - 1);
            Block current = blockchain.get(i);

            // Verify hash value is the same as calculated hash
            if (!current.getSignature().equals(current.hash())) {
                System.out.println("Current block corrupted");
                return false;
            }

            // Check if previous signature is the current 'previousHash' value
            if (!previous.getSignature().equals(current.getPreviousHash())) {
                System.out.println("Previous block corrupted");
                return false;
            }

            // Proof-of-work
            if(!current.getSignature().substring( 0, difficulty).equals(target)) {
                System.out.println("This block hasn't been mined (verified by the proof-of-work protocol)");
                return false;
            }
        }
        return true;
    }
}