package se.osten.vault.teststeps.checkout;

import com.eviware.soapui.impl.EmptyPanelBuilder;
import com.eviware.soapui.plugins.auto.PluginPanelBuilder;
import com.eviware.soapui.ui.desktop.DesktopPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginPanelBuilder(targetModelItem = VaultCheckoutTestStep.class)
public class VaultCheckoutTestStepPanelBuilder extends EmptyPanelBuilder<VaultCheckoutTestStep> {
    Logger logger = LoggerFactory.getLogger(VaultCheckoutTestStepPanelBuilder.class);

    public VaultCheckoutTestStepPanelBuilder(){
        logger.info("================> Registered plugin panel builder for VaultCheckoutTestStep");
    }

    @Override
    public DesktopPanel buildDesktopPanel(VaultCheckoutTestStep modelItem) {
        return new VaultCheckoutTestStepDesktopPanel(modelItem);
    }

    @Override
    public boolean hasDesktopPanel() {
        return true;
    }
}
