package com.jf2978;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.*;

public class SimpleBlockChain<T> {

    // Static Variables
    public static List<Block> blockchain;
    public static Map<PublicKey, Set<TransactionOutput>> UTXOs; // map of public key -> unspent transaction outputs
    public static Map<PublicKey, Float> fees; // map of individualized fee rates; public key -> rate
    public static int difficulty = 2; // "# of 0s" needed to solve PoW

    // Constructor(s)
    public SimpleBlockChain(){
        blockchain = new ArrayList<>();
        UTXOs = new HashMap<>();
        fees = new HashMap<>();
    }

    // Adds block to chain
    public static void add(Block block) {
        block.mine(difficulty);
        blockchain.add(block);
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