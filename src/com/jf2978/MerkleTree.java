package com.jf2978;

import java.util.ArrayList;
import java.util.List;

public class MerkleTree {

    // Instance variables
    private Node root;
    private List<String> leaves;

    // Constructor(s)
    public MerkleTree(List<String> signatures){
        leaves = signatures;
        constructTree(leaves);
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

        public Node(String h, Node l, Node r){
            hash = h;
            left = l;
            right = r;
        }
    }

    // Wrapper method to initially construct a merkle tree from a list of signatures
    private void constructTree(List<String> signatures) {
        /*if (signatures.size() <= 1) {
            throw new IllegalArgumentException("Must be at least two signatures to construct a Merkle tree");
        }*/

        List<Node> parents = constructBase(signatures);
        root = constructInternal(parents);
    }

    // Constructs the first level of tree -> returns first set of parent nodes
    private List<Node> constructBase(List<String> signatures){
        boolean odd = signatures.size() % 2 != 0;
        List<Node> parents = odd ? new ArrayList<>(signatures.size() / 2 + 1): new ArrayList<>(signatures.size() / 2);

        // Create leaves + construct immediate parents
        for(int i = 1; i < signatures.size(); i += 2){
            Node left = new Node(signatures.get(i-1));
            Node right = new Node(signatures.get(i));
            Node parent = new Node(Utility.SHA512(signatures.get(i-1) + signatures.get(i)), left, right);
            parents.add(parent);
        }

        // If the number of nodes is odd, "inherit" the remaining child node
        if(odd){
            Node n = new Node(signatures.get(signatures.size() - 1));
            parents.add(n);
        }

        return parents;
    }

    // Constructs every subsequent level of the tree recursively -> returns root
    private Node constructInternal(List<Node> parents){

        // Base case: merkle root found
        if(parents.size() == 1){
            return parents.get(0);
        }

        boolean odd = parents.size() % 2 != 0;

        // Generate immediate parents
        for(int i = 1; i < parents.size(); i += 2){
            Node left = parents.get(i-1);
            Node right = parents.get(i);
            Node parent = new Node(Utility.SHA512(left.hash + right.hash), left, right);
            parents.add(parent);
        }

        // If the number of nodes is odd, "inherit" the remaining child node (no hash needed)
        if(odd){
            parents.add(parents.get(parents.size() - 1));
        }

        return constructInternal(parents);
    }

    public Node getRoot(){
        return this.root;
    }

    public List<String> getLeaves(){
        return this.leaves;
    }
}
