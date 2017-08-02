package se.osten.vault.teststeps.checkout;

import com.eviware.soapui.config.SettingConfig;
import com.eviware.soapui.config.SettingsConfig;
import com.eviware.soapui.config.TestStepConfig;
import com.eviware.soapui.config.impl.TestStepConfigImpl;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestRunContext;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.support.types.StringToObjectMap;
import com.eviware.soapui.support.xml.XmlObjectConfigurationBuilder;
import com.google.common.collect.Lists;
import com.sun.java.xml.ns.jaxRpc.ri.config.impl.SchemaTypeImpl;
import groovy.transform.ASTTest;
import org.apache.xmlbeans.SchemaType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import se.osten.vault.common.AuthBackend;

import static org.mockito.Mockito.*;

public class VaultCheckoutTestStepTest {

    VaultCheckoutTestStep testStep;
    private TestStepConfig config;

    @Before
    public void setup() {
        XmlObjectConfigurationBuilder builder = new XmlObjectConfigurationBuilder();
        builder.add("serverLocation", "http://localhost:8200/v1");
        builder.add("authBackend", AuthBackend.AppRole.name());
        builder.add("secretId", "secret");
        builder.add("settingsList", new String[]{});
        TestStepConfig config = mock(TestStepConfig.class);
        SettingsConfig settingsConfig = mock(SettingsConfig.class);
        when(settingsConfig.getSettingList()).thenReturn(Lists.<SettingConfig>newArrayList());
        when(config.getConfig()).thenReturn(builder.finish());
        when(config.getSettings()).thenReturn(settingsConfig);
        this.config = config;
        testStep = new VaultCheckoutTestStep(
                mock(WsdlTestCase.class),
                config,
                true);
    }

    @Test
    public void shouldExecuteTestStep() {
        testStep.runOnce();
    }
}
