package com.example.journaldebord.exceptions;


/**
 * Exception used when there is a problem while login or when a user register for the first time
 */
public class LoginException extends Exception{

    private static final long serialVersionUID = 6878364983674394167L;

    /**
     * Constructs a <code>LoginException</code> with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public LoginException(String s) {
        super(s);
    }

}
