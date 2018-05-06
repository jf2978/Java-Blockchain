package com.jf2978;

import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.PublicKey;
import java.security.Security;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        // TODO: @param documentation for all internal methods
        // TODO: List erroneous use cases + ensure graceful exit

        // Create wallets
        Wallet alice = new Wallet();
        Wallet bob = new Wallet();
        Wallet coinbase = new Wallet();

        // Create genesis transaction to release funds into the blockchain
        Transaction genesisTransaction = new Transaction(coinbase.eK, alice.eK, 100f);
        genesisTransaction.sign(coinbase.dK);
        SimpleBlockChain.UTXOs.put(alice.eK, genesisTransaction.outputs);

        // Create + mine Genesis block
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        SimpleBlockChain.add(genesis);

        System.out.printf("Alice Balance: %s\n", alice.balance());

        // If Alice tries to send money she doesn't have, should return null Transaction obj
        Transaction toBob = alice.send(bob.eK, 500f);
        if(toBob != null){
            toBob.sign(alice.dK);
        }
        System.out.printf("Alice Balance: %s\n", alice.balance());

        // Test Successful transaction, should be fine
        Transaction toBob2 = alice.send(bob.eK, 50f);
        if(toBob2 != null){
            toBob2.sign(alice.dK);
        }
        System.out.printf("Alice Balance: %s\n", alice.balance());

        // Create new block with test transactions B/T Alice and Bob
        Block b = new Block(genesis.signature);
        System.out.printf("Transaction Processed? %b\n", b.addTransaction(toBob));
        System.out.printf("Transaction Processed? %b\n", b.addTransaction(toBob2));
        SimpleBlockChain.add(b);    

        // Print Blockchain state + information
        System.out.println("Alice's Final Balance: " + alice.balance());
        System.out.println("Bob's Final Balance: " + bob.balance());
        System.out.println(SimpleBlockChain.prettyPrint());
    }

    // Static Nested Class - SimpleBlockChain (useful for declaring a top-level static class)
    public static class SimpleBlockChain {

        // Static Variables
        public static List<Block> blockchain = new ArrayList<>();
        public static Map<PublicKey, Set<TransactionOutput>> UTXOs = new HashMap<>(); // eK -> unspent transaction outputs
        public static Map<PublicKey, Float> fees = new HashMap<>(); // eK -> individualized fee rate
        public static int difficulty = 2; // "# of 0s" needed to solve PoW

        // Adds block to chain
        public static void add(Block block) {
            block.mine(difficulty);
            blockchain.add(block);
        }

        // Checks if current state of blockchain is valid (current/prev hashes + proof-of-work)
        public static boolean isValid() {
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
                    System.out.println("This block hasn't been mined (verified by the proof-of-work)");
                    return false;
                }
            }
            return true;
        }

        public static String prettyPrint(){
            // TODO: Implement custom SimpleBlockChain prettyPrint() method
            return new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        }
    }
}