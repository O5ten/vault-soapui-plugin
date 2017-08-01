package se.osten.vault;

import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;
import com.eviware.soapui.support.UISupport;

@PluginConfiguration( groupId = "se.osten", name = "Vault Plugin", version = "1.0.0",
        autoDetect = true, description = "Provides teststeps that enables vault test-steps",
        infoUrl = "https://github.com/O5ten/vault-soapui-plugin")
public class PluginConfig extends PluginAdapter {
    public PluginConfig(){
        super();
        UISupport.addResourceClassLoader(getClass().getClassLoader());
    }
}