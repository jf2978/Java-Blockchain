package com.jf2978;

import com.google.gson.GsonBuilder;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** =====
 * The Wallet class represents a particular individual interacting with the blockchain. Each user is entirely
 * identified by their public key (i.e. their "wallet address") and verified via their digital signature generated
 * by their private key
 *
 * @author jf2978
 */
public class Wallet {

    // Instance variables
    public PublicKey eK; // *Anybody can pay* to your wallet via your public (encrypt) key - share your public key
    public PrivateKey dK; // *Nobody can use* your wallet via your private (decrypt) key - sign with your private key

    // #####
    // CONSTRUCTOR(S)
    // #####

    /** =====
     * Constructs a new Wallet by generating key pair
     */
    public Wallet(){
        keyGen();
    }

    // #####
    // PUBLIC METHODS
    // #####

    /** {@inheritDoc} */
    public String toString(){
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    /** =====
     * Checks the balance of the current wallet by iterating through associated unspent transaction outputs
     *
     * @return Current balance
     */
    public float balance(){
        // Check Unspent transactions for this public key
        Set<TransactionOutput> UTXOs = Main.SimpleBlockChain.UTXOs.get(eK);
        if(UTXOs == null){
            return 0f;
        }

        // For each Transaction Output, add its value to balance
        float balance = 0f;
        for(TransactionOutput output : UTXOs){
            balance += output.value;
        }
        return balance;
    }

    /** =====
     * Creates a transaction for the specified amount from this wallet address to another's
     *
     * @param to Public key of recipient
     * @param value Amount to send
     * @return Signed transaction from this wallet address
     */
    public Transaction send(PublicKey to, float value){

        // Check if balance is large enough
        if(balance() < value){
            System.out.println("Insufficient funds");
            return null;
        }

        // Gather enough UTXOs to be used as "inputs" for this TX
        Set<TransactionOutput> inputs = getInputs(value);

        // Generate, sign and return new Transaction object
        Transaction transaction = new Transaction(eK, to, value, inputs);
        transaction.sign(dK);

        return transaction;
    }

    // #####
    // HELPER METHODS
    // #####

    /** =====
     * Generates a pair of public and private keys using the Elliptic-Curve Digital Signature Algorithm provided
     * by the BouncyCastle API
     */
    private void keyGen(){
        try{
            // Set up Key Generator
            KeyPairGenerator kg = KeyPairGenerator.getInstance("ECDSA", "BC"); // Generates key pair based on algorithm given
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG"); // Cryptographically strong RNG based on SHA1 algorithm
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp521r1");

            // Generate keys
            kg.initialize(ecSpec, sr);
            KeyPair keyPair = kg.generateKeyPair();

            // Assign to wallet Instance variables
            eK = keyPair.getPublic();
            dK = keyPair.getPrivate();
        }
        catch(NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e){
            System.out.println(e.getMessage());
        }
    }

    /** =====
     * Obtains a set of transaction outputs to be used for a specific amount
     *
     * @param goal Amount looking to spend
     * @return Set of spendable transaction outputs
     */
    protected Set<TransactionOutput> getInputs(float goal){
        float current = 0;
        Set<TransactionOutput> result = new HashSet<>();
        Iterator<TransactionOutput> spendable = Main.SimpleBlockChain.UTXOs.get(this.eK).iterator();

        // Iterate through UTXOs for this public key until we have enough
        while(current <= goal && spendable.hasNext()) {
            TransactionOutput next = spendable.next();
            result.add(next);
            current += next.value;
        }
        return result;
    }

    /** =====
     * Obtains all transaction outputs associated to this wallet address
     *
     * @return Set of all spendable transaction outputs
     */
    protected Set<TransactionOutput> getAllInputs(){
        return Main.SimpleBlockChain.UTXOs.get(this.eK);
    }
}