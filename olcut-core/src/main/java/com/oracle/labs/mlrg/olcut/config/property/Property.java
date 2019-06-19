package com.oracle.labs.mlrg.olcut.config.property;

import java.io.Serializable;

/**
 * Tag interface for the types extracted from a configuration file.
 *
 * Property implementations should be immutable and final.
 */
public interface Property extends Serializable {
    public Property copy();
}
