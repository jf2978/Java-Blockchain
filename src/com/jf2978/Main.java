package com.jf2978;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.PublicKey;
import java.security.Security;
import java.util.*;

/** =====
 * Wrapper class for main() entry point to the blockchain program - typically includes a variety of
 * test cases for the classes within the package
 *
 * @author jf2978
 */
public class Main {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        // TODO: List erroneous use cases + ensure graceful exit

        // Create wallets
        Wallet alice = new Wallet();
        Wallet bob = new Wallet();
        Wallet coinbase = new Wallet();

        // Create genesis transaction to release funds into the blockchain
        Transaction genesisTransaction = new Transaction("0",coinbase.eK, alice.eK, 100f);
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
        SimpleBlockChain.prettyPrint();
    }

    /** =====
     * The SimpleBlockChain static nested class provides overarching structure to our transactions; primarily
     * created without regards to a real-world network of nodes
    */
    public static class SimpleBlockChain {

        // Static Variables
        public static List<Block> blockchain = new ArrayList<>();
        public static Map<PublicKey, Set<TransactionOutput>> UTXOs = new HashMap<>(); // eK -> unspent transaction outputs
        public static Map<PublicKey, Float> fees = new HashMap<>(); // eK -> individualized fee rate
        public static int difficulty = 2; // "# of 0s" needed to solve PoW

        /** =====
         * Adds Block object to SimpleBlockChain
         *
         * @param block Block to be mined + added
         */
        public static void add(Block block) {
            block.mine(difficulty);
            blockchain.add(block);
        }

        /** =====
         * Checks if current state of blockchain is valid (current/prev hashes + proof-of-work)
         *
         * @return Validity flag/result
         */
        public static boolean isValid() {
            String target = StringUtils.leftPad("", difficulty, '0');
            for (int i = 1; i < blockchain.size(); i++) {

                Block previous = blockchain.get(i - 1);
                Block current = blockchain.get(i);

                // Verify hash value is the same as calculated hash
                if (!current.signature.equals(current.hash())) {
                    System.out.println("Current block corrupted");
                    return false;
                }

                // Check if previous signature is the current 'previousHash' value
                if (!previous.signature.equals(current.previousHash)) {
                    System.out.println("Previous block corrupted");
                    return false;
                }

                // Proof-of-work
                if(!current.signature.substring( 0, difficulty).equals(target)) {
                    System.out.println("This block hasn't been mined (un-verified by the proof-of-work)");
                    return false;
                }
            }
            return true;
        }

        /** =====
            Prints current blockchain state

            @return String to be printed
         */
        public static void prettyPrint(){
            // TODO: Implement custom SimpleBlockChain prettyPrint() method
            ascii();
            Gson gson = new Gson();
            StringBuilder blockNo = new StringBuilder();
            StringBuilder times = new StringBuilder();
            StringBuilder sig = new StringBuilder();
            StringBuilder prev = new StringBuilder();
            StringBuilder merkles = new StringBuilder();
            StringBuilder transactions = new StringBuilder();

            int count = 0;
            for(Block block : blockchain){
                blockNo.append(String.format("// %s //      ", StringUtils.center(Integer.toString(count++), 41)));
                times.append(String.format("// Timestamp: %s //      ", StringUtils.center(block.timestamp, 30)));
                prev.append(String.format("// Previous: %s //      ", StringUtils.center(block.shortPrevious(), 31)));
                sig.append(String.format("// Signature: %s //  →  ", StringUtils.center(block.shortSignature(), 30)));
                transactions.append(String.format("// Transactions: %s //     ", StringUtils.center(block.shortTransactions(), 27)));
                merkles.append(String.format("// Merkle Root: %s //     ", StringUtils.center(block.shortMerkle(), 28)));
            }
            String header = StringUtils.leftPad("", 47, "/");
            System.out.println(StringUtils.repeat(header, "      ", blockchain.size()));
            System.out.println(blockNo);
            System.out.println(times);
            System.out.println(prev);
            System.out.println(sig + " [next] ");
            System.out.println(transactions);
            System.out.println(merkles);
            System.out.println(StringUtils.repeat(header, "     ", blockchain.size()));
        }

        public static void ascii(){
            System.out.println("___.   .__                 __          .__           .__        ");
            System.out.println("\\_ |__ |  |   ____   ____ |  | __ ____ |  |__ _____  |__| ____  ");
            System.out.println(" | __ \\|  |  /  _ \\_/ ___\\|  |/ // ___\\|  |  \\\\__  \\ |  |/    \\");
            System.out.println(" | \\_\\ \\  |_(  <_> )  \\___|    <\\  \\___|   Y  \\/ __ \\|  |   |  \\");
            System.out.println(" |___  /____/\\____/ \\___  >__|_ \\\\___  >___|  (____  /__|___|  /");
            System.out.println("     \\/                 \\/     \\/    \\/     \\/     \\/        \\/ \n");
        }
    }
}