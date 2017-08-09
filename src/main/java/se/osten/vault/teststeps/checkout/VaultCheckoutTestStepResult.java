package se.osten.vault.teststeps.checkout;

import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStep;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStepResult;

import java.util.Map;

public class VaultCheckoutTestStepResult extends WsdlTestStepResult{
    private Map<String, String> keyChain;

    public VaultCheckoutTestStepResult(WsdlTestStep testStep) {
        super(testStep);
    }

    public Map<String, String> getKeyChain() {
        return keyChain;
    }

    public void setKeyChain(Map<String, String> keyChain) {
        this.keyChain = keyChain;
    }
}
