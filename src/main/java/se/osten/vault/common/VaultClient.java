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
            System.out.println("Connected to Vault");
        } catch(VaultException e) {
           System.out.println("Failed to connect to Vault " + e);
        }
    }

    public void authenticateWithAppRole(String roleId, String secretId) {
        vault = new AppRole(roleId, secretId).authenticate(vault, vaultConfig);
    }

    public void authenticateWithGithub() {

    }

    public Map<String, String> read(String path) {
        Map<String, String> data = null;
        try {
            data = vault.logical().read(path).getData();
            System.out.println(data);
        } catch(VaultException e) {
            System.out.println("Unable to read from Vault @ path "
                    + path + " " + e);
        }
        return data;
    }
}
