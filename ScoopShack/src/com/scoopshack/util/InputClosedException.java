package com.scoopshack.util;

/** Thrown when the input stream ends (e.g. Ctrl+D) so the program can exit cleanly. */
public class InputClosedException extends RuntimeException {
    public InputClosedException() {
        super("Input stream closed");
    }
}
