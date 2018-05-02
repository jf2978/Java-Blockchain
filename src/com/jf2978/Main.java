package com.jf2978;

import com.google.gson.GsonBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class Main {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        // Create wallets for both Alice and Bob
        Wallet alice = new Wallet();
        Wallet bob = new Wallet();

        // Create a transaction from Alice to Bob + sign (disregarding transaction history for now)
        Transaction transaction = new Transaction(alice.eK, bob.eK, 5, null);
        transaction.sign(alice.dK);

        // Verify signature
        if(transaction.verify(alice.eK)){
            System.out.println(" -- Transaction Verified -- ");
        }


        /*
        SimpleBlockChain<String> sbc = new SimpleBlockChain<>();

        // Intialize genesis block
        Block gen = new Block<>("Genesis Block", "0");
        gen.mine(SimpleBlockChain.difficulty);
        sbc.add(gen);
        System.out.println("First Block mined!");

        // Intialize second block
        Block second = new Block<>("Second Block", gen.getSignature());
        second.mine(SimpleBlockChain.difficulty);
        sbc.add(second);
        System.out.println("Second Block mined!");

        // Intialize third block
        Block third = new Block<>("Third Block", second.getSignature());
        third.mine(SimpleBlockChain.difficulty);
        sbc.add(third);
        System.out.println("Third Block mined!");

        // Verify blockchain
        if(sbc.isValid()){
            System.out.println("-- Blockchain Verified --");
        }
        // Print Blockchain JSON
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(sbc));*/
    }
}