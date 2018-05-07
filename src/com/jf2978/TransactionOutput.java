package com.jf2978;

import com.google.gson.GsonBuilder;

import java.security.PublicKey;

/** =====
 * The TransactionOutput class represents any previously-processed transactions to be used as either inputs or outputs
 * of a new transaction.
 *
 * @author jf2978
 */
public class TransactionOutput{

    // Instance Variables
    public String id; // TX hash
    public String parentId; // TX hash that produced this output
    public float value; // specified output amount for this
    public PublicKey recipient; // recipient of the amount specified

    // #####
    // CONSTRUCTOR(S)
    // #####

    /** =====
     * Transaction constructor to build this Transaction object using the given Transaction inputs, addresses
     * and amount.
     *
     * @param to Public key of recipient
     * @param val Amount to be sent
     * @param parent Transaction Id that produced this output
     */
    public TransactionOutput(PublicKey to, float val, String parent){
        recipient = to;
        value = val;
        parentId = parent;
        id = hash();
    }

    /** {@inheritDoc} */
    public String toString(){
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    // #####
    // HELPER METHODS
    // #####

    /** =====
     * Generates cryptographic (one-way) hash for the current transaction output using the Secure Hash Algorithm (512)
     * Note, this aims to provide data integrity and origin authenticity before signing as well.
     *
     * @return Hex string output of the hash function
     */
    private String hash(){
        return Utility.SHA512(this.simplify());
    }

    /** =====
     * Represents this transaction output as a String, used to compress the object information needed for
     * hashing and signing
     *
     * @return String representation of this transaction output
     */
    private String simplify(){
        return Utility.getStringFromKey(recipient) + Float.toString(value) + parentId;
    }
}