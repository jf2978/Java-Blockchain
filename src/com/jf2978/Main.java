package com.jf2978;

import com.google.gson.GsonBuilder;

public class Main {

    public static void main(String[] args) {
        SimpleBlockChain<String> sbc = new SimpleBlockChain<>();

        sbc.add("First Block");
        sbc.add("Second Block");
        sbc.add("Third Block");

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(sbc);
        System.out.println(blockchainJson);
    }
}