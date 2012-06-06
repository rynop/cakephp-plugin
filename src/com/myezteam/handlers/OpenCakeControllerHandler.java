/**
 * OpenCakeFileHandler.java
 * cakephp-plugin
 * 
 * Created by jeremy on Jun 4, 2012
 * DoApp, Inc. owns and reserves all rights to the intellectual
 * property and design of the following application.
 *
 * Copyright 2012 - All rights reserved.  Created by DoApp, Inc.
 */
package com.myezteam.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.myezteam.cakephp.util.CakePHPHelper;

/**
 * @author jeremy
 * 
 */
public class OpenCakeControllerHandler extends AbstractHandler
{

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
   * ExecutionEvent)
   */
  @Override
  public Object execute(ExecutionEvent executionEvent) throws ExecutionException
  {
    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    IFile selectedFile = CakePHPHelper.getSelectedFile(page);
    if (selectedFile != null)
    {
      IFile destinationFile = null;

      if (CakePHPHelper.isModel(selectedFile))
      {
        destinationFile = CakePHPHelper.getControllerFromModel(selectedFile);
      }
      else if (CakePHPHelper.isController(selectedFile))
      {
        // TODO: implement pop-up
      }
      else if (CakePHPHelper.isView(selectedFile))
      {
        destinationFile = CakePHPHelper.getControllerFromView(selectedFile);
      }
      try
      {
        if (destinationFile != null && destinationFile.exists())
        {
          IDE.openEditor(page, destinationFile);
        }
      }
      catch (CoreException e)
      {
        String clazz = destinationFile.getName();
        System.err.println("OpenCakeFile can not open file: " + clazz);
        e.printStackTrace();
      }
    }
    return null;
  }

}
