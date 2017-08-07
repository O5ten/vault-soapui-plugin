package se.osten.vault.teststeps.checkout;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.config.TestStepConfig;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestRunContext;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStepResult;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStepWithProperties;
import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.plugins.auto.PluginTestStep;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.types.StringToObjectMap;
import com.eviware.soapui.support.xml.XmlObjectConfigurationBuilder;
import com.eviware.soapui.support.xml.XmlObjectConfigurationReader;
import se.osten.vault.common.AuthBackend;
import se.osten.vault.common.VaultClient;
import org.apache.commons.lang.NotImplementedException;

import javax.swing.*;

@PluginTestStep(typeName = "VaultCheckoutTestStep", name = "Vault Checkout TestStep",
        description = "Checkout credentials from Vault",
        iconPath = "se/osten/vault/teststeps/checkout/vault-checkout.jpg")
public class VaultCheckoutTestStep extends WsdlTestStepWithProperties {

    private String serverLocation = "http://localhost:8200";
    private String[] vaultPaths = {"secret/testDB"}; //read from config file
    private String secretId = "";
    private String roleId = "";
    private String githubToken = "";
    private AuthBackend authBackend = AuthBackend.AppRole;
    private boolean isAuthenticated = false;

    private static boolean actionGroupAdded = false;

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
            this.serverLocation = reader.readString("severLocation", "http://localhost:8200");
            this.authBackend = AuthBackend.valueOf(reader.readString("authBackend", "AppRole"));
            this.secretId = reader.readString("secretId", "");
            this.roleId = reader.readString("roleId", "");
        }
    }

    private void updateConfig() {
        XmlObjectConfigurationBuilder builder = new XmlObjectConfigurationBuilder();
        builder.add("serverLocation", this.serverLocation);
        builder.add("authBackend", this.authBackend.name());
        builder.add("roleId", this.roleId);
        builder.add("secretId", this.secretId);
        getConfig().setConfig(builder.finish());
    }

    private boolean settingExists(String settingId, String key) {
        return settingId.equalsIgnoreCase(key);
    }

    public TestStepResult run(TestCaseRunner testCaseRunner, TestCaseRunContext testCaseRunContext) {
        WsdlTestStepResult result = new WsdlTestStepResult(this);

        VaultClient vaultClient = new VaultClient();
        vaultClient.init(serverLocation);

        this.isAuthenticated = authenticate(vaultClient);
        if(this.isAuthenticated) {
            for(String vaultPath : vaultPaths) {
                vaultClient.read(vaultPath);
            }
        }
        result.setStatus(TestStepResult.TestStepStatus.OK);
        return result;
    }

    private boolean authenticate(VaultClient vaultClient) {
        if(this.isAuthenticated) {
            switch (getAuthBackend()) {
                case AppRole:
                    return vaultClient.authenticateWithAppRole(roleId, secretId);
                case GitHub:
                    return vaultClient.authenticateWithGithub(githubToken);
                default:
                    throw new NotImplementedException("Backend " + getAuthBackend() + " not implemented yet.");
            }
        }
        return isAuthenticated;
    }

    public String getServerLocation() {
        return this.serverLocation;
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

    public void runOnce() {
        run(new WsdlTestCaseRunner(
                this.getTestCase(),
                new StringToObjectMap()),
                new WsdlTestRunContext(this));
    }
}
