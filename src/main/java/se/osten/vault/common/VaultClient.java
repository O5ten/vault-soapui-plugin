package se.osten.vault.common;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;

import java.util.Map;

public class VaultClient {
    private Vault vault;
    private VaultConfig vaultConfig;
    private boolean authenticated = false;

    public void init(String serverLocation) {
        try {
            vaultConfig = new VaultConfig()
                    .address(serverLocation)
                    .build();
            vault = new Vault(vaultConfig);
            System.out.println("Initiated Vault client @\"" + serverLocation + "\"");
        } catch(VaultException e) {
            new VaultPluginException("Failed to initiate Vault client @\""
                    + serverLocation + "\"", e);
        }
    }

    public void authenticateWithAppRole(String roleId, String secretId) {
        try {
            vault = new AppRole(roleId, secretId).authenticate(vault, vaultConfig);
            authenticated = true;
            System.out.println("Authenticated to Vault with AppRole");
        } catch(VaultException e) {
            authenticated = false;
            new VaultPluginException("Unable to authenticate to Vault with AppRole", e);
        }
    }

    public void authenticateWithGithub(String githubToken) {
        try {
            vault = new Github(githubToken).authenticate(vault, vaultConfig);
            authenticated = true;
            System.out.println("Authenticated to Vault with Github");
        } catch(VaultException e) {
            authenticated = false;
            new VaultPluginException("Unable to authenticate to Vault with Github", e);
        }
    }

    public Map<String, String> read(String path) {
        Map<String, String> data = null;
        try {
            data = vault.logical().read(path).getData();
            System.out.println(data);
        } catch(VaultException e) {
            new VaultPluginException("Unable to read from Vault @\"" + path + "\"", e);
        }
        return data;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
