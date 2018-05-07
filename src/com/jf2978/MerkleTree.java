package com.jf2978;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/** =====
 * The MerkleTree class is intended to be used as a data structure to enable succinct cryptographic verification
 * of transactions as the merkle root contains the integrity of all signatures from its base and any one transaction
 * can be verified in O(d) time if the location of the leaf is known.
 *
 * @author jf2978
 */
public class MerkleTree {

    // Instance variables
    private Node root;
    private List<String> leaves;

    // #####
    // CONSTRUCTOR(S)
    // #####

    /** =====
     * Merkle (Hash) Tree constructor that builds the tree from bottom up (leaves → root)
     *
     * @param signatures Public key of sender
     */
    public MerkleTree(List<String> signatures){
        leaves = signatures;
        constructTree(leaves);
    }

    /** =====
     * Inner Node class for Merkle Tree
     */
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

    // #####
    // PUBLIC METHODS
    // #####

    /** {@inheritDoc} */
    public String toString(){
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    /** =====
     * Returns the current tree root
     *
     * @return Tree root node
     */
    public Node getRoot(){
        return this.root;
    }

    /** =====
     * Returns tree leaves
     *
     * @return List of signatures (leaves)
     */
    public List<String> getLeaves(){
        return this.leaves;
    }

    // #####
    // HELPER METHODS
    // #####

    /** =====
     * Wrapper method for validity checks and constructing both base and internal nodes of the tree
     *
     * @param signatures list of transaction signatures
     */
    private void constructTree(List<String> signatures) {
        // If no signatures present throw exception
        if(signatures.size() == 0){
            throw new IllegalArgumentException("Must provide a transaction signature to construct Merkle Tree");
        }

        // If only one transaction, return as root node
        if(signatures.size() == 1){
            root = new Node(Utility.SHA512(signatures.get(0)));
        }

        List<Node> parents = constructBase(signatures);
        root = constructInternal(parents);
    }

    /** =====
     * Constructs the first level of merkle tree from signature list
     *
     * @param signatures List of transaction signatures
     * @return Base level of nodes in the tree
     */
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
    /** =====
     * Recursively constructs every level of the tree above the base
     *
     * @param children List of current child nodes
     * @return Merkle tree root
     */
    private Node constructInternal(List<Node> children){

        // Base case: root found
        if(children.size() == 1){
            return children.get(0);
        }

        // Generate parents
        boolean odd = children.size() % 2 != 0;
        for(int i = 1; i < children.size(); i += 2){
            Node left = children.get(i-1);
            Node right = children.get(i);
            Node parent = new Node(Utility.SHA512(left.hash + right.hash), left, right);
            children.add(parent);
        }

        // If the number of nodes is odd, "inherit" the remaining child node (no hash needed)
        if(odd){
            children.add(children.get(children.size() - 1));
        }
        return constructInternal(children);
    }
}