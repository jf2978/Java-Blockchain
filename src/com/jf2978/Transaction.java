package com.jf2978;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

    // Inner Class - TransactionInput
    public class TransactionInput{

    }
    // Inner Class - TransactionOutput
    public class TransactionOutput{

    }

    // Static Variables
    private static int total = 0; // number of transactions generated, to be used within our hash to differentiate duplicates

    //Instance Variables
    String id; // unique id hash for this transaction
    public PublicKey sender; // from:
    public PublicKey recipient; // to:
    public double value; // amount to send
    public byte[] signature; // digital signature to verify

    public List<TransactionInput> input = new ArrayList<>(); // previous transactions of sender (to ensure has funds to send)
    public List<TransactionOutput> output = new ArrayList<>();

    public Transaction(PublicKey from, PublicKey to, double val, ArrayList<TransactionInput> in){
        sender = from;
        recipient = to;
        value = val;
        input = in;
        id = hash();
    }

    // Generates cryptographic (unique) id for this transaction using SHA-512 hash
    private String hash(){
        total++;
        return Utility.SHA512(Utility.getStringFromKey(sender) + Utility.getStringFromKey(recipient) + Double.toString(value) + total);
    }
}
