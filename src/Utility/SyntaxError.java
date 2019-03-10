package Utility;

import AST.Location;

public class SyntaxError extends Error {
    public SyntaxError(Location location, String message) {
        super(location.toString() + message);
    }
}
