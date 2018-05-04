package com.jf2978;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Wallet {

    // Instance variables
    public PublicKey eK; // *Anybody can pay* to your wallet via your public (encrypt) key - share your public key
    public PrivateKey dK; // *Nobody can use* your wallet via your private (decrypt) key - sign with your private key

    // Static variables
    public static Set<TransactionOutput> UTXOs; // Unspent transaction outputs associated to this public key

    // Constructor(s)
    public Wallet(){
        keyGen();
        UTXOs = SimpleBlockChain.UTXOs.get(eK);
    }

    // Generates pair of public/private keys using the Elliptic-Curve Algorithm
    public void keyGen(){
        try{
            // Set up Key Generator
            // ECDSA = Elliptic Curve Digital Signature Algorithm, BC = BouncyCastle (provider)
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

    public float balance(){
        float balance = 0;
        for(TransactionOutput output : UTXOs){
            balance += output.value;
        }
        System.out.println("Current Balance: " + balance);
        return balance;
    }

    public Transaction send(PublicKey to, float value){

        // Check if balance is large enough
        if(balance() < value){
            System.out.println("Insufficient funds!");
            return null;
        }

        // Gather enough UTXOs to be used as "inputs" for this TX
        float input = 0;
        Set<TransactionOutput> inputs = new HashSet<>();
        Iterator<TransactionOutput> it = UTXOs.iterator();
        while(it.hasNext() && input < value){
            TransactionOutput next = it.next();
            inputs.add(next);
            input += next.value;
        }
        System.out.printf("Sending %f using TX Inputs: %s", value, inputs);

        // Update UTXOs set accordingly
        UTXOs.removeAll(inputs);

        // Generate, sign and return new Transaction object
        Transaction transaction = new Transaction(eK, to, value, inputs);
        transaction.sign(dK);

        return transaction;
    }
}