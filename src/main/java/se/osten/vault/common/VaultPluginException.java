package se.osten.vault.common;

public class VaultPluginException extends RuntimeException {
    public VaultPluginException(String msg, Throwable e) {
        super(msg, e);
    }
}
