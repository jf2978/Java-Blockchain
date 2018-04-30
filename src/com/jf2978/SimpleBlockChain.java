package com.jf2978;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SimpleBlockChain<T> {

    private List<Block> blockchain;
    private String lastHash;

    // INNER CLASS - Block
    public class Block {
        private String signature; // aims to provide data integrity, origin authenticity + non-repudiation
        private String prev; // dependence on the previous block signature guarantees no tampering on previous blocks
        private String timestamp;
        private T data;

        public Block(T data, String prevHash){
            this.data = data;
            this.prev = prevHash;
            this.timestamp = LocalDateTime.now().toString();
            this.signature = hash();
        }

        private String hash(){
            return Utility.SHA512(prev+timestamp+data);
        }
    }

    public SimpleBlockChain(){
        blockchain = new ArrayList<>();
        lastHash = "0";
    }

    public Block add(T data) {
        Block newBlock = new Block(data, lastHash);
        blockchain.add(newBlock);
        lastHash = newBlock.hash();
        return newBlock;
    }
}

