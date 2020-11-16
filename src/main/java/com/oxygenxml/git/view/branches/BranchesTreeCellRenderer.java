package com.oxygenxml.git.view.branches;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.eclipse.jgit.lib.Constants;

import com.oxygenxml.git.constants.Icons;
import com.oxygenxml.git.view.history.RoundedLineBorder;
import com.oxygenxml.git.view.renderer.RendererUtil;
import com.oxygenxml.git.view.renderer.RenderingInfo;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.util.ColorTheme;

/**
 * Renderer for the nodes icon in the branches tree, based on the path to the
 * node.
 * 
 * @author Bogdan Draghici
 *
 */
public class BranchesTreeCellRenderer extends DefaultTreeCellRenderer {
  /**
   * Default selection color.
   */
  private final Color defaultSelectionColor = getBackgroundSelectionColor();
  /**
   * Default border color for selection.
   */
  private final Color defaultBorderSelectionColor = new Color(102,167,232);
  /**
   * Tells us if the context menu is showing.
   */
  private BooleanSupplier isContextMenuShowing;
  /**
   * Supplies the current branch.
   */
  private Supplier<String> currentBranchNameSupplier;
  
  /**
   * Constructor.
   * 
   * @param isContextMenuShowing Tells us if the context menu is showing.
   * @param currentBranchNameSupplier Gives us the current branch name.
   */
  public BranchesTreeCellRenderer(
      BooleanSupplier isContextMenuShowing,
      Supplier<String> currentBranchNameSupplier) {
    this.isContextMenuShowing = isContextMenuShowing;
    this.currentBranchNameSupplier = currentBranchNameSupplier;
  }

  /**
   * @see DefaultTreeCellRenderer.getTreeCellRendererComponent(JTree, Object, boolean, boolean, boolean, int, boolean)
   */
  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
      int row, boolean hasFocus) {

    JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    
    Icon icon = null;
    String text = "";
    String path = value.toString();
    if (((DefaultMutableTreeNode) value).getParent() == null) {
      icon = Icons.getIcon(Icons.LOCAL_REPO);
    } else {
      RenderingInfo renderingInfo = RendererUtil.getRenderingInfo(path);
      if (renderingInfo != null) {
        icon = renderingInfo.getIcon();
        text = renderingInfo.getTooltip();
      }
    }

    if (label != null) {
      label.setIcon(icon);
      if (!text.isEmpty()) {
        label.setText(text);
      }
      
      Font font = label.getFont();
      label.setFont(font.deriveFont(Font.PLAIN));
      label.setBorder(new EmptyBorder(0, 5, 0, 0));
      if (path.equals(Constants.R_HEADS + currentBranchNameSupplier.get())) {
        // Mark the current branch
        label.setFont(font.deriveFont(Font.BOLD));
        label.setBorder(new RoundedLineBorder(label.getForeground(), 1, 8, true));
      }
      
      // Active/inactive table selection
      if (sel) {
        if (tree.hasFocus()) {
          ColorTheme colorTheme = PluginWorkspaceProvider.getPluginWorkspace().getColorTheme();
          setBorderSelectionColor(colorTheme.isDarkTheme() ? defaultSelectionColor 
              : defaultBorderSelectionColor);
          setBackgroundSelectionColor(defaultSelectionColor);
        } else if (!isContextMenuShowing.getAsBoolean()) {
          setBorderSelectionColor(RendererUtil.getInactiveSelectionColor(tree, defaultSelectionColor));
          setBackgroundSelectionColor(RendererUtil.getInactiveSelectionColor(tree, defaultSelectionColor));
        }
      }
    }

    return label;
  }
  
  /**
   * Paints the node, and in case it is also selected, take care not to draw the dashed rectangle border.
   * @see com.oxygenxml.git.view.branches.BranchesTreeCellRenderer.paint(Graphics)
   */
  @Override
  public void paint(Graphics g) {
    if (selected) {
      g.setColor(getBackgroundSelectionColor());
      g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
      hasFocus = false;
      super.paint(g);
      paintBorder(g, 0, 0, getWidth(), getHeight());      
    } else {
      super.paint(g);
    }
  }
  /**
   * Paints the border for the selected node.
   * @param g
   * @param x
   * @param y
   * @param width
   * @param height
   */
  private void paintBorder(Graphics g, int x, int y, int w, int h) {
    Color bsColor = getBorderSelectionColor();
    if (bsColor != null && selected) {
        g.setColor(bsColor);
        g.drawRect(x, y, w - 1, h - 1);
    }
  }
}
