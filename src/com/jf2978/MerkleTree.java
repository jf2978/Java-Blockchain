package com.jf2978;

import java.util.ArrayList;
import java.util.List;

public class MerkleTree {

    // Instance variables
    private Node root;
    private List<String> leaves;

    // Constructor(s)
    public MerkleTree(List<Transaction> transactions){

    }

    // Nested class (Merkle) Node
    static class Node{
        public String hash;
        public Node left;
        public Node right;

        public Node(String h){
            hash = h;
            left = null;
            right = null;
        }
    }

    // Constructs the first level of tree
    private void constructBase(List<String> signatures){
        List<Node> parents = new ArrayList<>(signatures.size() / 2);
        boolean odd = !(parents.size() % 2 == 0);

        // Create leaves + construct immediate parents
        for(int i = 1; i < signatures.size() - 1; i += 2){
            Node left = new Node(signatures.get(i-1));
            Node right = new Node(signatures.get(i-1));

            // generate parent nodes here...
        }
    }

    // Wrapper method to initially construct a merkle tree from a list of signatures
    private void constructTree(List<String> signatures) {
        if (signatures.size() <= 1) {
            throw new IllegalArgumentException("Must be at least two signatures to construct a Merkle tree");
        }
    }

    // Builds first (lowest) level of merkle tree: signatures
}
