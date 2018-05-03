package com.jf2978;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Set;

public class Transaction {

    // Static Variables
    private static int total = 0; // number of transactions generated, to be used within our hash to differentiate duplicates (think CTR Mode of operation)

    //Instance Variables
    String id; // unique id hash for this transaction
    public PublicKey sender;
    public PublicKey recipient;
    public float value; // amount to send
    public byte[] signature; // digital signature to verify

    public Set<TransactionInput> inputs; // previous transactions of sender
    public Set<TransactionOutput> outputs; // resulting transaction outputs (including change)

    public Transaction(PublicKey from, PublicKey to, float val, Set<TransactionInput> in){
        sender = from;
        recipient = to;
        value = val;
        inputs = in;
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

        // Filter through TransactionInputs + assign values to transaction outputs if in Blockchain unspent map
        System.out.println("Searching for unspent Transaction Inputs ...");
        for(TransactionInput input : inputs){
            input.UTXO = SimpleBlockChain.UTXOs.get(input.id);
        }

        // check if transaction inputs sum to a value large enough to process transaction amount
        float sum = getInputsValue();
        if(sum < value) {
            System.out.printf("Available input (%f) too low for amount (%f)\n", sum, value);
            return false;
        }

        // Create new TransactionOutputs
        float change = sum - value;
        outputs.add(new TransactionOutput(recipient, value, id));
        if(change > 0){
            outputs.add(new TransactionOutput(sender, change, id));
        }
        return true;

        // Update Unspent Transactions Map

    }

    //returns sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            total += i.UTXO.value;
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
                "Transaction Count: " + total;
    }
}