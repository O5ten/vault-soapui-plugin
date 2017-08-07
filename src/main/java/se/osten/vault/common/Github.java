package se.osten.vault.common;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;

public class Github {
    private String githubToken;

    public Github(String gitubToken) {
        this.githubToken = githubToken;
    }

    public Vault authenticate(Vault vault, VaultConfig vaultConfig) throws VaultException {
        String token = vault.auth()
                .loginByGithub(githubToken)
                .getAuthClientToken();
        return new Vault(vaultConfig.token(token));
    }
}
