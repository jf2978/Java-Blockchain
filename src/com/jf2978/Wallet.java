package com.jf2978;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {

    // Instance variables
    public PublicKey eK; // *Anybody can pay* to your wallet via your public (encrypt) key - share your public key
    public PrivateKey dK; // *Nobody can use* your wallet via your private (decrypt) key - sign with your private key

    // Constructor(s)
    public Wallet(){
        keyGen();
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
}