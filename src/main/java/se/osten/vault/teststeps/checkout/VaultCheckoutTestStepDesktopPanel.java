package se.osten.vault.teststeps.checkout;

import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.support.components.SimpleBindingForm;
import com.eviware.soapui.ui.support.ModelItemDesktopPanel;
import com.google.common.collect.Sets;
import com.jgoodies.binding.PresentationModel;
import se.osten.vault.common.AuthBackend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

/**
 * Simple DesktopPanel that provides a basic UI for configuring the EMailTestStep. Should perhaps be improved with
 * a "Test" button and a log panel.
 */
public class VaultCheckoutTestStepDesktopPanel extends ModelItemDesktopPanel<VaultCheckoutTestStep> implements ActionListener {
    private PresentationModel<VaultCheckoutTestStep> pm;
    private SimpleBindingForm form;
    private JComboBox authComboBox;
    private JTextField serverLocationTextField;
    private JPasswordField secretIdField;

    public VaultCheckoutTestStepDesktopPanel(VaultCheckoutTestStep modelItem) {
        super(modelItem);
        this.pm = new PresentationModel<VaultCheckoutTestStep>(getModelItem());
        this.form = new SimpleBindingForm(pm);
        buildUI();
    }

    private void buildUI() {
        addServerLocation();
        authBackend();
        addSecretId();
        add(new JScrollPane(form.getPanel()), BorderLayout.CENTER);
        setPreferredSize(new Dimension(500, 300));
    }

    private void addSecretId() {
        this.form.appendSeparator();
        this.secretIdField = this.form.appendPasswordField("Secret Id", "Secret Vault Id to identify the user");
        this.secretIdField.setText(this.getModelItem().getSecretId());
        this.authComboBox.addActionListener(this);
    }

    private void authBackend() {
        this.form.appendSeparator();
        this.authComboBox = this.form.appendComboBox("Auth Backend", AuthBackend.values(), "Authentication backend");
        this.authComboBox.addActionListener(this);
    }

    private void addServerLocation() {
        this.form.appendSeparator();
        this.form.appendHeading("Server Configuration");
        this.serverLocationTextField = this.form.appendTextField("serverLocation", "Server Location", "http://vault-server.tld/v1");
        this.serverLocationTextField.addActionListener(this);
    }

    private Set<String> getTestStepNamesWithinRange() {
        VaultCheckoutTestStep repeatStep = getModelItem();
        WsdlTestCase testCase = repeatStep.getTestCase();
        Set<String> testStepNames = testCase.getTestSteps().keySet();
        Set<String> testStepNamesWithinRange = Sets.newLinkedHashSet();
        int targetTestStepIndex = testCase.getTestStepIndexByName(repeatStep.getName());

        for (String testStepName : testStepNames) {
            int testStepIndex = testCase.getTestStepIndexByName(testStepName);
            if (testStepIndex < targetTestStepIndex) {
                testStepNamesWithinRange.add(testStepName);
            }
        }
        return testStepNamesWithinRange;
    }

    @Override
    public boolean release() {
        return super.release();
    }

    public void actionPerformed(ActionEvent e) {
        getModelItem().setServerLocation(this.serverLocationTextField.getText());
        getModelItem().setAuthBackend((AuthBackend)this.authComboBox.getSelectedItem());
        getModelItem().setSecretId(new String(secretIdField.getPassword()));
    }
}
