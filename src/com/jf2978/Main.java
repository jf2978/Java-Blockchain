package com.jf2978;

import com.google.gson.GsonBuilder;

public class Main {

    public static void main(String[] args) {
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
        System.out.println("First Block mined!");

        // Verify blockchain
        if(sbc.isValid()){
            System.out.println("-- Blockchain Verified --");
        }
        // Print Blockchain JSON
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(sbc));
    }
}