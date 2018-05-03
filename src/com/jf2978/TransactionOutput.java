package com.jf2978;

import java.security.PublicKey;

public class TransactionOutput{

    public String id;
    public PublicKey recipient; // recipient of the amount specified
    public float value;
    public String parentId; //

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
        return Utility.SHA512(Utility.getStringFromKey(recipient) + Float.toString(value) + parentId);
    }
}
