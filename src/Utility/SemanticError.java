package Utility;

import AST.Location;

public class SemanticError extends Error {
    public SemanticError(Location location, String message) {
        super(location.toString() + message);
    }
}
