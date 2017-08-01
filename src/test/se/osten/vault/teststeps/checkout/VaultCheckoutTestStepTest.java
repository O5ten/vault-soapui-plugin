package se.osten.vault.teststeps.checkout;

import com.eviware.soapui.config.TestStepConfig;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import groovy.transform.ASTTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class VaultCheckoutTestStepTest {

    VaultCheckoutTestStep testStep;

    @Before
    public void setup() {
        testStep = new VaultCheckoutTestStep(mock(WsdlTestCase.class), mock(com.eviware.soapui.config.TestStepConfig.class), true);
    }

    @Test
    public void shouldCreateTestStep() {

    }

}
