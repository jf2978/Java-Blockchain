package com.jf2978;

import com.google.gson.GsonBuilder;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** =====
 * The Transaction class represents a payment between two parties within the P2P network as described
 * in the Bitcoin whitepaper - with a sender, recipient, value, hash for these fields, signature slot
 * for the sender to generate and Transaction Inputs/Outputs
 *
 * @author jf2978
 */
public class Transaction {

    // Static Variables
    private static int total = 0; // number of transactions generated, to be used within our hash to differentiate duplicates (think CTR Mode of operation)

    //Instance Variables
    String id; // TX hash for this transaction
    public PublicKey sender;
    public PublicKey recipient;
    public float value; // amount to send
    public byte[] signature; // digital signature to verify

    public Set<TransactionOutput> inputs; // previous transaction outputs of sender (to be spent for this transaction)
    public Set<TransactionOutput> outputs; // resulting transaction outputs (including change)

    // #####
    // CONSTRUCTOR(S)
    // #####

    /** =====
     * Transaction constructor to build this Transaction object using the given Transaction inputs, addresses
     * and amount.
     *
     * @param from Public key of sender
     * @param to Public key of recipient
     * @param val Amount to be sent
     * @param in Set of unspent transaction inputs to be (entirely) spent
     */
    public Transaction(PublicKey from, PublicKey to, float val, Set<TransactionOutput> in){
        sender = from;
        recipient = to;
        value = val;
        inputs = in;
        outputs = new HashSet<>();
        id = hash();
    }

    /** =====
     * Transaction constructor utilized to build internal transaction objects that don't require inputs. This
     * was intended to be used for a hardcoded coinbase transactions
     *
     * @param hash Hardcoded TX hash
     * @param from Public key of sender
     * @param to Public key of recipient
     * @param val Amount to be sent
     */
    protected Transaction(String hash, PublicKey from, PublicKey to, float val){
        id = hash;
        sender = from;
        recipient = to;
        value = val;
        outputs = new HashSet<>();
        outputs.add(new TransactionOutput(recipient, value, id));
    }



    // #####
    // PUBLIC METHODS
    // #####

    /** {@inheritDoc} */
    public String toString(){
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    /** =====
     * Generates digital signature of this transaction's id (hash)
     *
     * @param dK Private key of the sender
     */
    public void sign(PrivateKey dK){
        signature = Utility.ECDSASignature(id, dK);
    }

    /** =====
     * Verifies this transaction's digital signature
     *
     * @return Verification result
     */
    public boolean verify(){
        return Utility.verifyECDSASignature(id, sender, signature);
    }

    /** =====
     * Processes the current transaction (ensuring an up-to-date UTXO map entry) by verifying its digital signature,
     * checking the transaction inputs are valid, generating the transaction outputs and updating the blockchain
     * accordingly.
     *
     * @return Processing result (transaction invalid if false)
     */
    public boolean process(){

        // Check Unspent transactions map
        Map<PublicKey, Set<TransactionOutput>> UTXOs = Main.SimpleBlockChain.UTXOs;

        // Verify the signature of this transaction
        if(!verify()){
            System.out.println("Failed to verify transaction signature...");
            return false;
        }

        // Check that inputs correspond with what's in the blockchain
        if(!UTXOs.get(sender).containsAll(inputs)){
            System.out.println("Transaction inputs are invalid...");
            return false;
        }

        // check if inputs sum to a value large enough to process transaction amount
        // TODO: float fee = Utility.calculateFee(value);
        float sum = getInputsValue();
        if(sum < value) {
            System.out.printf("Available input (%f) too low for amount (%f)\n", sum, value);
            return false;
        }

        // Generate TransactionOutput(s)
        float change = sum - value;
        outputs.add(new TransactionOutput(recipient, value, id));

        // Potentially do something with transaction fee here
        if(change > 0){
            outputs.add(new TransactionOutput(sender, change, id));
        }

        // Remove spent Transaction Outputs from UTXOs map
        UTXOs.get(sender).removeAll(inputs);

        // Update blockchain with this set (and initializes entry if DNE for appropriate key)
        for(TransactionOutput output : outputs){
            UTXOs.putIfAbsent(output.recipient, new HashSet<>());
            UTXOs.get(output.recipient).add(output);
        }

        return true;
    }

    // #####
    // HELPER METHODS
    // #####

    /** =====
     * Represents this transaction as a String, used to compress the object information needed for hashing and signing
     *
     * @return String representation of this transaction
     */
    private String simplify(){
        return Utility.getStringFromKey(sender) + Utility.getStringFromKey(recipient) + Float.toString(value) + total;
    }

    /** =====
     * Generates cryptographic (one-way) hash for the current transaction using the Secure Hash Algorithm (512)
     * Note, this aims to provide data integrity and origin authenticity before signing as well.
     *
     * @return Hex string output of the hash function
     */
    private String hash(){
        total++;
        return Utility.SHA512(this.simplify());
    }

    /** =====
     * Returns the sum of transaction input values
     *
     * @return Total spendable amount
     */
    private float getInputsValue() {
        float total = 0;
        for(TransactionOutput i : inputs) {
            total += i.value;
        }
        return total;
    }
}