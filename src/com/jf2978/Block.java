package com.jf2978;

import java.time.LocalDateTime;

public class Block<T> {
    private String hash;
    private String prevHash;
    private T data;
    private LocalDateTime timestamp;

   /* ===
        CONSTRUCTOR(S)
    */
    public Block (T data, String prevHash){
        this.data = data;
        this.prevHash = prevHash;
        this.timestamp = LocalDateTime.now();
    }

    /* ===
        PUBLIC METHODS
     */
    @Override
    public int hashCode(){
        return 0;
    }

    @Override
    public boolean equals(Object obj){
        return false;
    }

    /* ===
        PRIVATE METHODS
     */

    private String hash(){
        return this.hash;
    }
}
