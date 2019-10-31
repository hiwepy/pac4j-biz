package org.pac4j.core.ext.exception;

import org.pac4j.core.exception.TechnicalException;

/**
 * 
 * @author vladimir.stankovic
 *
 * Aug 4, 2016
 */
public class MethodNotSupportedException extends TechnicalException {
    private static final long serialVersionUID = 3705043083010304496L;

    public MethodNotSupportedException(String msg) {
        super(msg);
    }
}
