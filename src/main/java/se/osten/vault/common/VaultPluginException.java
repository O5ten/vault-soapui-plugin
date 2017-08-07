package se.osten.vault.common;

public class VaultPluginException {
    public VaultPluginException(String msg, Throwable e) {
        System.out.println(msg);
        System.out.println(e.toString());
    }
}
