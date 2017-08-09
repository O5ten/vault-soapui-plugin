package se.osten.vault.teststeps.checkout;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.config.TestStepConfig;
import com.eviware.soapui.impl.rest.support.XmlBeansRestParamsTestPropertyHolder;
import com.eviware.soapui.impl.wsdl.MutableTestPropertyHolder;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestRunContext;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStepResult;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStepWithProperties;
import com.eviware.soapui.model.ModelItem;
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
import org.apache.xmlbeans.SchemaType;
import se.osten.vault.common.AuthBackend;
import se.osten.vault.common.VaultClient;
import org.apache.commons.lang.NotImplementedException;

import javax.swing.*;
import javax.xml.namespace.QName;
import java.util.List;

@PluginTestStep(typeName = "VaultCheckoutTestStep", name = "Vault Checkout TestStep",
        description = "Checkout credentials from Vault",
        iconPath = "se/osten/vault/teststeps/checkout/vault-checkout.jpg")
public class VaultCheckoutTestStep extends WsdlTestStepWithProperties implements MutableTestPropertyHolder {

    private String serverLocation = "http://localhost:8200";
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
            this.serverLocation = reader.readString("severLocation", "http://localhost:8200");
            this.vaultSecret = reader.readString("vaultSecret","");
            this.authBackend = AuthBackend.valueOf(reader.readString("authBackend", AuthBackend.AppRole.name()));
            this.secretId = reader.readString("secretId", "");
            this.roleId = reader.readString("roleId", "");
        }
    }

    private void updateConfig() {
        XmlObjectConfigurationBuilder builder = new XmlObjectConfigurationBuilder();
        builder.add("serverLocation", this.serverLocation);
        builder.add("vaultSecret", this.vaultSecret);
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
        if(authenticate(vaultClient)) {
            vaultClient.read(this.vaultSecret);
        }
        result.setStatus(TestStepResult.TestStepStatus.OK);
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

    public void runOnce() {
        run(new WsdlTestCaseRunner(
                this.getTestCase(),
                new StringToObjectMap()),
                new WsdlTestRunContext(this));
    }

    public TestProperty addProperty(String s) {
        DefaultTestStepProperty property = new DefaultTestStepProperty(s, this);
        this.addProperty(property);
        return property;
    }

    public TestProperty removeProperty(String s) {
        return this.getProperties().remove(s);
    }

    public boolean renameProperty(String s, String s1) {
        TestProperty testProperty = getProperties().get(s);
        if(testProperty != null){
            getProperties().remove(s);
            getProperties().put(s1, testProperty);
            return true;
        }
        return false;
    }

    public void setVaultSecret(String vaultSecret) {
        this.vaultSecret = vaultSecret;
    }
}
