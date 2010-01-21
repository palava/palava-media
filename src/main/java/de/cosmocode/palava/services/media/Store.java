package de.cosmocode.palava.services.media;

import java.io.InputStream;

public interface Store {

    String store(InputStream stream);
    
    InputStream read(String identifier);
    
}
