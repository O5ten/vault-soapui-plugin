package se.osten.vault.common;

public enum AuthBackend {
    AppRole("/auth/approle/login"),
    GitHub("/auth/github/login");

    private final String apiPath;

    AuthBackend(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getApiPath() {
        return apiPath;
    }

    @Override
    public String toString(){
        return this.name();
    }
}
