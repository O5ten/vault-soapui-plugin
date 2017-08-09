package se.osten.vault.teststeps.checkout;

import com.eviware.soapui.impl.wsdl.panels.teststeps.support.PropertyHolderTable;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.components.JEditorStatusBarWithProgress;
import com.eviware.soapui.support.components.JXToolBar;
import com.eviware.soapui.support.components.SimpleBindingForm;
import com.eviware.soapui.ui.support.ModelItemDesktopPanel;
import com.jgoodies.binding.PresentationModel;
import se.osten.vault.common.AuthBackend;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static se.osten.vault.common.AuthBackend.*;

/**
 * Simple DesktopPanel that provides a basic UI for configuring the EMailTestStep. Should perhaps be improved with
 * a "Test" button and a log panel.
 */
public class VaultCheckoutTestStepDesktopPanel extends ModelItemDesktopPanel<VaultCheckoutTestStep> implements ActionListener {
    private PresentationModel<VaultCheckoutTestStep> pm;
    private SimpleBindingForm form;
    private JComboBox authComboBox;
    private JTextField serverLocationTextField;
    private JTextField roleIdField;
    private JPasswordField secretIdField;
    private JEditorStatusBarWithProgress statusBar;
    private CardLayout cards;
    private JPanel cardPanel;
    private JPasswordField personalTokenField;
    private PropertyHolderTable propertiesTable;
    private JTextField vaultSecretField;

    public VaultCheckoutTestStepDesktopPanel(VaultCheckoutTestStep modelItem) {
        super(modelItem);
        this.pm = new PresentationModel<VaultCheckoutTestStep>(getModelItem());
        this.form = new SimpleBindingForm(pm);
        this.cards = new CardLayout();
        buildUI();
    }

    private void buildUI() {
        addToolbar();
        buildStatusLabel();
        addServerLocation();
        addVaultSecret();
        addCredentialProperties();
        addAuthBackend();
        addBackendOptions();

        add(new JScrollPane(form.getPanel()), BorderLayout.CENTER);
        setPreferredSize(new Dimension(500, 300));
    }

    private void addVaultSecret() {
        this.vaultSecretField = this.form.appendTextField("vaultSecret", "Vault Secret", "http://vault-server.tld/v1");
        this.vaultSecretField.setText(this.getModelItem().getRoleId());
        this.vaultSecretField.addActionListener(this);
    }

    private void addCredentialProperties() {
        this.propertiesTable = new PropertyHolderTable(getModelItem());
        this.form.append(propertiesTable);
    }

    private void addToolbar() {
        JXToolBar toolbar = UISupport.createToolbar();
        toolbar.add(UISupport.createActionButton(new SubmitAction(), true));
        this.form.append(toolbar);
    }

    private void buildStatusLabel() {
        this.statusBar = new JEditorStatusBarWithProgress();
        this.statusBar.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        this.form.append(this.statusBar);
    }

    public JEditorStatusBarWithProgress getStatusBar() {
        return this.statusBar;
    }

    private void addBackendOptions() {
        this.form.appendSeparator();
        this.cardPanel = new JPanel(cards);
        cardPanel.add(getAppRoleBackendOptions(), AppRole.name());
        cardPanel.add(getGitHubBackendOptions(), GitHub.name());
        this.form.append(cardPanel);
        this.authComboBox.addActionListener(this);
    }

    private JPanel getAppRoleBackendOptions(){
        SimpleBindingForm appRoleForm = new SimpleBindingForm(pm);
        this.roleIdField = appRoleForm.appendTextField("roleId", "Role Id", "");
        this.roleIdField.setText(this.getModelItem().getRoleId());
        this.roleIdField.addActionListener(this);
        this.secretIdField = appRoleForm.appendPasswordField("secretId", "Secret Id", "Secret Vault Id to identify the user");
        this.secretIdField.setText(this.getModelItem().getSecretId());
        this.secretIdField.addActionListener(this);
        return appRoleForm.getPanel();
    }

    private void addAuthBackend() {
        this.form.appendSeparator();
        this.authComboBox = this.form.appendComboBox("Auth Backend", values(), "Authentication backend");
        this.authComboBox.addActionListener(this);
    }

    private void addServerLocation() {
        this.form.appendHeading("Server Configuration");
        this.serverLocationTextField = this.form.appendTextField("serverLocation", "Server Location", "http://vault-server.tld/v1");
        this.serverLocationTextField.addActionListener(this);
    }

    @Override
    public boolean release() {
        return super.release();
    }

    public JPanel getGitHubBackendOptions() {
        SimpleBindingForm gitHubForm = new SimpleBindingForm(pm);
        gitHubForm.append(new JLabel("Enter GitHub Personalized Token"));
        this.personalTokenField = gitHubForm.appendPasswordField("Token","personalized token from github organization or account");
        return gitHubForm.getPanel();
    }

    private class SubmitAction extends AbstractAction {
        public SubmitAction() {
            this.putValue("SmallIcon", UISupport.createImageIcon("/submit_request.gif"));
            this.putValue("ShortDescription", "Submit request to vault and extract credentials (Alt-Enter)");
            this.putValue("AcceleratorKey", UISupport.getKeyStroke("alt ENTER"));
        }

        public void actionPerformed(ActionEvent e) {
            VaultCheckoutTestStepDesktopPanel.this.statusBar.setIndeterminate(false);
            VaultCheckoutTestStepDesktopPanel.this.statusBar.setInfo("Running");
            getModelItem().runOnce();
            VaultCheckoutTestStepDesktopPanel.this.statusBar.setInfo("Credentials extracted");
        }
    }

    public void actionPerformed(ActionEvent e) {
        AuthBackend backend = (AuthBackend) this.authComboBox.getSelectedItem();
        getModelItem().setServerLocation(this.serverLocationTextField.getText());
        getModelItem().setAuthBackend(backend);
        cards.show(cardPanel, backend.name());
        getModelItem().setRoleId(roleIdField.getText());
        getModelItem().setSecretId(new String(secretIdField.getPassword()));
        getModelItem().setGithubToken(new String(this.personalTokenField.getPassword()));
        getModelItem().setVaultSecret(vaultSecretField.getText());
    }
}
