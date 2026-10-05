package com.iexceed.webcontainer.authentication.azureadb2c;


import java.io.Serializable;

public class AADPasswordResetException extends Exception implements Serializable {
    private static final long serialVersionUID = 4715216804778192433L;

    public AADPasswordResetException(String message){
        super(message);
    }
}