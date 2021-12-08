package com.oxygenxml.git.service;

import java.net.URL;
import java.util.function.Supplier;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;

import com.oxygenxml.git.utils.RepoUtil;
import com.oxygenxml.git.view.event.GitEventInfo;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.listeners.WSEditorListener;

/**
 * A cache intended to avoid reading the file system too often.
 * @author alex_jitianu
 */
public class StatusCache {
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(StatusCache.class);
  
  
  private GitStatus cache = null;
  private Supplier<Git> statusComputer;
  
  public StatusCache(GitListeners listeners, Supplier<Git> statusComputer) {
    this.statusComputer = statusComputer;
    listeners.addGitListener(new GitEventAdapter() {
      @Override
      public void operationSuccessfullyEnded(GitEventInfo info) {
        resetCache();
      }
    });
  }
  
  /**
   * @return Cached status.
   */
  public synchronized GitStatus getStatus() {
    if (cache == null) {
      cache = new GitStatusCommand(statusComputer).getStatus();
    }
    return cache;
  }

  public synchronized void resetCache() {
    cache = null;
  }

  public void installEditorsHook(PluginWorkspace pluginWorkspace) {
    pluginWorkspace.addEditorChangeListener(
        new WSEditorChangeListener() {
          @Override
          public void editorOpened(final URL editorLocation) {
            addEditorSaveHook(pluginWorkspace.getEditorAccess(editorLocation, PluginWorkspace.MAIN_EDITING_AREA));
          }
        },
        PluginWorkspace.MAIN_EDITING_AREA);
  }

  /**
   * Adds a hook to refresh the models if the editor is part of the Git working copy.
   * 
   * @param editorLocation Editor to check.
   */
  private void addEditorSaveHook(WSEditor editorAccess) {
    if (editorAccess != null) {
      editorAccess.addEditorListener(new WSEditorListener() {
        @Override
        public void editorSaved(int operationType) {
          if (RepoUtil.isFileFromRepository(editorAccess.getEditorLocation())) {
            resetCache();
          }
        }
      });
    }
  }
}
