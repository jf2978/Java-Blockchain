package com.jf2978;

import java.security.PublicKey;

public class TransactionOutput{

    // Instance Variables
    public String id; // TX hash
    public String parentId; // TX hash that produced this output
    public float value; // specified output amount for this
    public PublicKey recipient; // recipient of the amount specified

    // Constructor(s)
    public TransactionOutput(PublicKey from, float val, String parent){
        recipient = from;
        value = val;
        parentId = parent;
        id = hash();
    }

    // Checks if current TransactionOutput is associated with the given public key
    public boolean isMine(PublicKey key){
        return key == recipient;
    }

    private String hash(){
        return Utility.SHA512(this.simplify());
    }

    // Method for compressing TransactionOutput information as String (for TXO hash)
    private String simplify(){
        return Utility.getStringFromKey(recipient) + Float.toString(value) + parentId;
    }

    // Method for returning human-readable form of this transaction output
    public String toString(){
        return "Origin TX Hash: " + parentId + '\n' +
                "To: " + Utility.getStringFromKey(recipient) + '\n' +
                "Amount: " + Float.toString(value) + '\n' +
                "TX Hash: " + id;
    }
}