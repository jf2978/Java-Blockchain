package com.jf2978;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;

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

    public Transaction(PublicKey from, PublicKey to, float val, Set<TransactionOutput> in){
        sender = from;
        recipient = to;
        value = val;
        inputs = in;
        outputs = new HashSet<>();
        id = hash();
    }

    // Generates cryptographic (unique) id for this transaction using SHA-512 hash
    private String hash(){
        total++;
        return Utility.SHA512(this.simplify());
    }

    // Generates signature of the hash of this transaction (providing data integrity)
    public void sign(PrivateKey dK){
        signature = Utility.ECDSASignature(dK, id);
    }

    // Verifies transaction signature
    public boolean verify(){
        return Utility.verifyECDSASignature(sender, this.simplify(), signature);
    }

    public boolean process(){

        // Verify the signature of this transaction
        if(!verify()){
            System.out.println("Failed to verify transaction signature...");
            return false;
        }

        // Check that inputs are all UTXOs for this sender
        if(!inputs.containsAll(SimpleBlockChain.UTXOs.get(sender))){
            System.out.println("Transaction inputs are invalid...");
            return false;
        }

        // check if inputs sum to a value large enough to process transaction amount
        float sum = getInputsValue();
        if(sum < value) {
            System.out.printf("Available input (%f) too low for amount (%f)\n", sum, value);
            return false;
        }

        // Generate TransactionOutput(s)
        float change = sum - value;
        outputs.add(new TransactionOutput(recipient, value, id));
        if(change > 0){
            outputs.add(new TransactionOutput(sender, change, id));
        }

        // Update UTXOs map by removing used TransactonOutputs + adding newly generated ones
        SimpleBlockChain.UTXOs.get(sender).removeAll(inputs);
        SimpleBlockChain.UTXOs.get(sender).addAll(outputs);

        return true;
    }

    // returns sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for(TransactionOutput i : inputs) {
            total += i.value;
        }
        return total;
    }

    // Method for compressing Transaction information as String (for signing and verifying)
    private String simplify(){
        return Utility.getStringFromKey(sender) + Utility.getStringFromKey(recipient) + Float.toString(value) + total;
    }

    // Method for returning human-readable form of this transaction
    public String toString(){
        return "From: " + Utility.getStringFromKey(sender) + '\n' +
                "To: " + Utility.getStringFromKey(recipient) + '\n' +
                "Amount: " + Float.toString(value) + '\n' +
                "Transaction Count: " + total + '\n' +
                "TX Hash: " + id;
    }
}