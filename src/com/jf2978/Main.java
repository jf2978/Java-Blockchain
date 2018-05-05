package com.jf2978;

import com.google.gson.GsonBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class Main {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        SimpleBlockChain sbc = new SimpleBlockChain();

        // Create wallets
        Wallet alice = new Wallet();
        System.out.println("Alice Public Key: " + alice.eK);
        Wallet bob = new Wallet();
        System.out.println("Bob Public Key: " + bob.eK);
        Wallet coinbase = new Wallet();
        System.out.println("Coinbase Public Key: " + coinbase.eK);

        // Create genesis transaction to release funds into the blockchain
        Transaction genesisTransaction = new Transaction(coinbase.eK, alice.eK, 100f);
        genesisTransaction.sign(coinbase.dK);
        SimpleBlockChain.UTXOs.put(alice.eK, genesisTransaction.outputs);
        System.out.println("Alice Unspent Transaction Outputs: \n" + SimpleBlockChain.UTXOs.get(alice.eK));

        // Create + mine Genesis block
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        SimpleBlockChain.add(genesis);

        // Check balance, create a transaction from Alice -> Bob + sign
        System.out.println("Alice Balance: " + alice.balance());

        // Print Blockchain JSON
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(SimpleBlockChain.blockchain));
    }
}