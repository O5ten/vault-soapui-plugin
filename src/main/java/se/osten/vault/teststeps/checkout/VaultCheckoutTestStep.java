package se.osten.vault.teststeps.checkout;

import com.bettercloud.vault.VaultException;
import com.eviware.soapui.SoapUI;
import com.eviware.soapui.config.TestStepConfig;
import com.eviware.soapui.impl.wsdl.MutableTestPropertyHolder;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestRunContext;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStepResult;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStepWithProperties;
import com.eviware.soapui.model.support.DefaultTestStepProperty;
import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestProperty;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.plugins.auto.PluginTestStep;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.types.StringToObjectMap;
import com.eviware.soapui.support.xml.XmlObjectConfigurationBuilder;
import com.eviware.soapui.support.xml.XmlObjectConfigurationReader;
import se.osten.vault.common.AuthBackend;
import se.osten.vault.common.VaultClient;
import se.osten.vault.common.VaultPluginException;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

@PluginTestStep(typeName = "VaultCheckoutTestStep", name = "Vault Checkout TestStep",
        description = "Checkout credentials from Vault",
        iconPath = "se/osten/vault/teststeps/checkout/vault-checkout.jpg")
public class VaultCheckoutTestStep extends WsdlTestStepWithProperties {

    private String serverLocation = "";
    private String secretId = "";
    private String roleId = "";
    private String githubToken = "";
    private AuthBackend authBackend = AuthBackend.AppRole;
    private static boolean actionGroupAdded = false;
    private String vaultSecret = "";

    public VaultCheckoutTestStep(WsdlTestCase testCase, TestStepConfig config, boolean forLoadTest) {
        super(testCase, config, true, true);
        if (!actionGroupAdded) {
            SoapUI.getActionRegistry().addActionGroup(new VaultCheckoutTestStepActionGroup());
            actionGroupAdded = true;
        }
        readConfig(config);
    }

    @Override
    public ImageIcon getIcon() {
        return UISupport.createImageIcon("se/osten/vault/teststeps/checkout/vault-checkout.jpg");
    }

    private void readConfig(TestStepConfig config) {
        if (config != null) {
            XmlObjectConfigurationReader reader = new XmlObjectConfigurationReader(config.getConfig());
            this.serverLocation = reader.readString("serverLocation", "http://localhost:8200");
            this.vaultSecret = reader.readString("vaultSecret", "");
            this.authBackend = AuthBackend.valueOf(reader.readString("authBackend", AuthBackend.AppRole.name()));
            this.secretId = reader.readString("secretId", "");
            this.roleId = reader.readString("roleId", "");
            this.githubToken = reader.readString("githubToken", "");
        }
    }

    public void updateConfig() {
        XmlObjectConfigurationBuilder builder = new XmlObjectConfigurationBuilder();
        builder.add("serverLocation", this.serverLocation);
        builder.add("vaultSecret", this.vaultSecret);
        builder.add("authBackend", this.authBackend.name());
        builder.add("roleId", this.roleId);
        builder.add("secretId", this.secretId);
        builder.add("githubToken", this.githubToken);
        getConfig().setConfig(builder.finish());
    }

    private boolean settingExists(String settingId, String key) {
        return settingId.equalsIgnoreCase(key);
    }

    public VaultCheckoutTestStepResult run(TestCaseRunner testCaseRunner, TestCaseRunContext testCaseRunContext) {
        VaultCheckoutTestStepResult result = new VaultCheckoutTestStepResult(this);
        Map<String, String> keychain = new HashMap<String, String>();
        try {
            VaultClient vaultClient = new VaultClient();
            vaultClient.init(serverLocation);
            if (authenticate(vaultClient)) {
                keychain.putAll(vaultClient.read(this.vaultSecret));
            }
            for(String key : keychain.keySet()){
                String value = keychain.get(key);
                DefaultTestStepProperty prop = new DefaultTestStepProperty(key, this);
                prop.setValue(value);
                prop.setIsReadOnly(true);
                this.addProperty(prop, true);
            }
            result.setKeyChain(keychain);
            result.setStatus(TestStepResult.TestStepStatus.OK);
        } catch (VaultPluginException e) {
            result.setError(e);
            result.setStatus(TestStepResult.TestStepStatus.FAILED);
        }
        return result;
    }

    private boolean authenticate(VaultClient vaultClient) {
        switch (getAuthBackend()) {
            case AppRole:
                return vaultClient.authenticateWithAppRole(roleId, secretId);
            case GitHub:
                return vaultClient.authenticateWithGithub(githubToken);
            default:
                throw new UnsupportedOperationException("Backend " + getAuthBackend() + " not implemented yet.");
        }
    }

    public String getServerLocation() {
        return this.serverLocation;
    }

    public String getVaultSecret() {
        return this.vaultSecret;
    }

    public AuthBackend getAuthBackend() {
        return this.authBackend;
    }

    public void setAuthBackend(AuthBackend authBackend) {
        this.authBackend = authBackend;
    }

    public void setServerLocation(String serverLocation) {
        this.serverLocation = serverLocation;
    }

    @Override
    public boolean hasEditor() {
        return true;
    }

    public String getSecretId() {
        return secretId;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getGithubToken() {
        return githubToken;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public void setGithubToken(String githubToken) {
        this.githubToken = githubToken;
    }

    public VaultCheckoutTestStepResult runOnce() {
        return run(new WsdlTestCaseRunner(
                        this.getTestCase(),
                        new StringToObjectMap()),
                new WsdlTestRunContext(this));
    }

    public void setVaultSecret(String vaultSecret) {
        this.vaultSecret = vaultSecret;
    }
}
