package com.oxygenxml.git.view.dialog;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.oxygenxml.git.constants.Icons;
import com.oxygenxml.git.constants.UIConstants;
import com.oxygenxml.git.service.GitAccess;
import com.oxygenxml.git.translator.Tags;
import com.oxygenxml.git.translator.Translator;
import com.oxygenxml.git.view.util.UIUtil;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

public class BranchSwitchConfirmationDialog extends OKOtherAndCancelDialog {

  /**
   * Question icon right inset.
   */
  private static final int QUESTION_ICON_RIGHT_INSET = 10;

  /**
   * The translator.
   */
  private static final Translator TRANSLATOR = Translator.getInstance();

  /**
   * Dialog title.
   */
  private static final String TITLE = TRANSLATOR.getTranslation(Tags.SWITCH_BRANCH);

  /**
   * Text to be written on the button in case the answer to the question is OK.
   */
  private static final String OK_BUTTON_NAME = TRANSLATOR.getTranslation(Tags.STASH_CHANGES);

  /**
   * Text to be written on the button in case the answer to the question is "Other"/option 2.
   */
  private static final String OTHER_BUTTON_NAME = TRANSLATOR.getTranslation(Tags.MOVE_CHANGES);

  /**
   * Text to be written on the button in case the answer to the question is negative.
   */
  private static final String CANCEL_BUTTON_NAME = TRANSLATOR.getTranslation(Tags.CANCEL);


  /**
   * Constructor.
   * 
   * @param newBranch The branch to set.
   */
  public BranchSwitchConfirmationDialog(String newBranch) {
    super(
            PluginWorkspaceProvider.getPluginWorkspace() != null ?
                    (JFrame) PluginWorkspaceProvider.getPluginWorkspace().getParentFrame() : null,
            TITLE,
            true);

    JPanel panel = getContentPanel();
    GridBagConstraints gbc = new GridBagConstraints();

    // Icon
    String iconPath = Icons.QUESTION_ICON;
    JLabel iconLabel = new JLabel();
    Icon questionIcon = Icons.getIcon(iconPath);
    if (questionIcon != null) {
      iconLabel.setIcon(questionIcon);
    }
    gbc.insets = new Insets(
            UIConstants.COMPONENT_TOP_PADDING,
            UIConstants.COMPONENT_LEFT_PADDING,
            UIConstants.COMPONENT_BOTTOM_PADDING,
            QUESTION_ICON_RIGHT_INSET);
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 0;
    gbc.weighty = 0;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 2;
    panel.add(iconLabel, gbc);

    JTextArea textArea = UIUtil.createMessageArea("");
    textArea.setDocument(new FileStatusDialog.CustomWrapDocument());
    textArea.setLineWrap(false);
    textArea.setText(
        MessageFormat.format(
            TRANSLATOR.getTranslation(Tags.UNCOMMITTED_CHANGES_WHEN_SWITCHING_BRANCHES),
            GitAccess.getInstance().getBranchInfo().getBranchName(),
            newBranch));
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridx = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    panel.add(textArea, gbc);

    this.setButtonText(getOKButton(), OK_BUTTON_NAME);
    this.setButtonText(getOtherButton(), OTHER_BUTTON_NAME);
    this.setButtonText(getCancelButton(), CANCEL_BUTTON_NAME);

    setResizable(false);
    pack();

    if (PluginWorkspaceProvider.getPluginWorkspace() != null) {
      setLocationRelativeTo((JFrame) PluginWorkspaceProvider.getPluginWorkspace().getParentFrame());
    }
  }

}
