package com.jf2978;

public class TransactionInput{

    public String id; // Reference to previous TransactionOutput
    public TransactionOutput UTXO; // Unspent Transaction Output (UTXO)

    public TransactionInput(String transactionOutputId) {
        this.id = transactionOutputId;
    }
}