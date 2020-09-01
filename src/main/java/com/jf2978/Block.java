package com.jf2978;

import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** =====
 * The Block class represents an individual block that makes up our decentralized network. As outlined in the Bitcoin
 * whitepaper, each block contains a public reference to the previous block, a list of transactions, a timestamp and its
 * merkleroot (for verification purposes)
 *
 * @author jf2978
 */
public class Block {

    // Instance variables
    public String signature; // aims to provide data integrity, origin authenticity + non-repudiation
    public String previousHash; // dependence on the previous block signature guarantees no tampering on previous blocks
    public String merkleRoot; // hash tree root for verifying transaction list
    public List<Transaction> transactions;
    public String timestamp;
    private int nonce;

    // #####
    // CONSTRUCTOR(S)
    // #####

    /** =====
     * Block constructor to build this Block object using a reference to the previous block
     *
     * @param prev Previous block id
     */
    public Block(String prev){
        previousHash = prev;
        transactions = new ArrayList<>();
        timestamp = LocalDateTime.now().toString();
        nonce = new SecureRandom().nextInt();
        signature = this.hash();
    }

    // #####
    // PUBLIC METHODS
    // #####

    /** {@inheritDoc} */
    public String toString(){
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    public String shortSignature(){
        int length = signature.length();
        return signature.substring(0,5) + signature.substring(length/2 - 2, length/2 + 2) + signature.substring(length - 5, length);
    }

    public String shortPrevious(){
        if(previousHash.length() == 1){
            return previousHash;
        }
        int length = previousHash.length();
        return previousHash.substring(0,5) + previousHash.substring(length/2 - 2, length/2 + 2) + previousHash.substring(length - 5, length);
    }

    public String shortMerkle(){
        if(merkleRoot.length() == 1){
            return merkleRoot;
        }
        int length = merkleRoot.length();
        return merkleRoot.substring(0,5) + merkleRoot.substring(length/2 - 2, length/2 + 2) + merkleRoot.substring(length - 5, length);
    }

    public String shortTransactions(){
        StringBuilder sb = new StringBuilder();
        sb.append("< ");
        for(Transaction tx : transactions){
            if(sb.length() > 15){
                sb.append("...");
                break;
            }
            String id = tx.id.length() == 1 ? "" : "[" + tx.id.substring(0,5) + "]";
            sb.append(id);
        }
        sb.append(" >");
        return sb.toString();
    }
    /** =====
     * "Mines" current block. That is, using CPU power to continuously produce hash signatures that match
     * the blockchain's current difficulty - Proof of Work
     *
     * @param diff Mining difficulty and number of zeroes our target hash value starts with
     */
    public void mine(int diff){
        merkleRoot = Utility.getMerkleRoot(transactions);
        String target = StringUtils.leftPad("", diff, '0'); // Appends diff * ('0') to ""
        LocalDateTime start = LocalDateTime.now();

        System.out.println("Mining Block...");
        while(!signature.substring(0, diff).equals(target)) {
            nonce++;
            timestamp = LocalDateTime.now().toString();
            signature = hash();
        }

        LocalDateTime end = LocalDateTime.now();
        System.out.println("Mining Success: " + Duration.between(start,end).toString());
    }

    /** =====
     * Adds transaction to this block
     *
     * @param transaction Transaction to be added to the list
     */
    public boolean addTransaction(Transaction transaction){
        // Valid Transaction check
        if(transaction == null || transaction.outputs == null){ return false; }

        // Check if not genesis block
        if(!previousHash.equals("0")){
            if(!transaction.process()) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        return true;
    }

    // #####
    // HELPER METHODS
    // #####

    /** =====
     * Generates cryptographic (one-way) hash for the current transaction using the Secure Hash Algorithm (512)
     * Note, this aims to provide data integrity and origin authenticity before signing as well.
     *
     * @return Hex string output of the hash function
     */
    protected String hash() {
        return Utility.SHA512(previousHash + timestamp + merkleRoot + Integer.toString(nonce));
    }
}