package nl.juraji.biliomi.config.core;

import javax.enterprise.inject.Vetoed;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
@Vetoed // Do not inject, use the producer instead
public class YamlCoreSettings {
    private USBiliomi biliomi;

    public USBiliomi getBiliomi() {
        return biliomi;
    }

    public void setBiliomi(USBiliomi biliomi) {
        this.biliomi = biliomi;
    }
}
