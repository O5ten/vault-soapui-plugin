package se.osten.vault.common;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;

public class AppRole {
    private String roleId;
    private String secretId;

    public AppRole(String roleId, String secretId) {
        this.roleId = roleId;
        this.secretId = secretId;
    }

    public Vault authenticate(Vault vault, VaultConfig vaultConfig) {
        String token = "";
        try {
            token = vault.auth()
                    .loginByAppRole(AuthBackend.AppRole.getApiPath(), roleId, secretId)
                    .getAuthClientToken();
            System.out.println("Authenticated to Vault");
        } catch(VaultException e) {
            System.out.println("Unable to login to Vault " + e);
        }
        return new Vault(vaultConfig.token(token));
    }
}
