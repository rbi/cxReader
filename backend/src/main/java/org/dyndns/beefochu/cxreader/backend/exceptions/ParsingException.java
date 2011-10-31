package org.dyndns.beefochu.cxreader.backend.exceptions;

public class ParsingException extends Exception {
    private static final long serialVersionUID = 1L;

    public ParsingException() {
        
    }
    
    public ParsingException(Exception ex) {
        super(ex);
    }
}
