package se.osten.vault.common;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;

import java.util.Map;

public class VaultClient {
    private Vault vault;
    private VaultConfig vaultConfig;

    public void init(String serverLocation) {
        try {
            vaultConfig = new VaultConfig()
                    .address(serverLocation)
                    .build();
            vault = new Vault(vaultConfig);
        } catch(VaultException e) {
            throw new VaultPluginException("Failed to initiate Vault client @\""
                    + serverLocation + "\"", e);
        }
    }

    public boolean authenticateWithAppRole(String roleId, String secretId) {
        try {
            vault = new AppRole(roleId, secretId).authenticate(vault, vaultConfig);
            return true;
        } catch(VaultException e) {
            throw new VaultPluginException("Unable to authenticate to Vault with AppRole", e);
        }
    }

    public boolean authenticateWithGithub(String githubToken) {
        try {
            vault = new Github(githubToken).authenticate(vault, vaultConfig);
            System.out.println("Authenticated to Vault with Github");
            return true;
        } catch(VaultException e) {
            throw new VaultPluginException("Unable to authenticate to Vault with Github", e);
        }
    }

    public Map<String, String> read(String path) {
        Map<String, String> data = null;
        try {
            data = vault.logical().read(path).getData();
            System.out.println(data);
        } catch(VaultException e) {
            throw new VaultPluginException("Unable to read from Vault @\"" + path + "\"", e);
        }
        return data;
    }
}
