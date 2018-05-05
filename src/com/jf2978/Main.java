package com.jf2978;

import com.google.gson.GsonBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class Main {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        SimpleBlockChain sbc = new SimpleBlockChain();

        // Create wallets for Alice + Bob
        Wallet alice = new Wallet();
        System.out.println("Wallet created for Alice!");
        System.out.println("Public Key: " + alice.eK);
        Wallet bob = new Wallet();
        System.out.println("Wallet created for Bob!");
        System.out.println("Public Key: " + bob.eK);

        // Manually create Genesis Transaction + coinbase wallet
        Wallet coinbase = new Wallet();
        Transaction genesisTransaction = new Transaction(coinbase.eK, alice.eK, 100f, null);
        genesisTransaction.sign(coinbase.dK);
        genesisTransaction.id = "0";
        TransactionOutput out = new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.id);
        genesisTransaction.outputs.add(out);
        SimpleBlockChain.UTXOs.put(coinbase.eK, genesisTransaction.outputs);

        // Create Genesis block
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        SimpleBlockChain.add(genesis);

        // Check balance, create a transaction from Alice -> Bob + sign
        System.out.println("Alice Balance: " + alice.balance());

        // Print Blockchain JSON
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(sbc));
    }
}