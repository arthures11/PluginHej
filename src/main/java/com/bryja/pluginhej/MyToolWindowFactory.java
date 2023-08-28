package com.bryja.pluginhej;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowContentUiType;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.sun.istack.NotNull;

import javax.swing.*;

public class MyToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("witamy po lewej stronie!"));

//        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
//        Content content = contentFactory.createContent(panel, "", false);
//        toolWindow.getContentManager().addContent(content);
//
//        JList<String> fileList = new JList<>(listProjectFiles(project));
//        fileList.addListSelectionListener(e -> openSelectedFile(project, fileList.getSelectedValue()));
//        VirtualFileManager.getInstance().addVirtualFileListener(new MyFileChangeListener(fileList, project));
//        panel.add(new JScrollPane(fileList));

        JList<String> fileList = new JList<>(listProjectFiles(project));
        fileList.addListSelectionListener(e -> openSelectedFile(project, fileList.getSelectedValue()));
        panel.add(new JScrollPane(fileList));

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private String[] listProjectFiles(Project project) {
        VirtualFile[] files = project.getBaseDir().getChildren();
        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }
        return fileNames;
    }

    private void openSelectedFile(Project project, String fileName) {
        VirtualFile file = project.getBaseDir().findChild(fileName);
        if (file != null) {
            FileEditorManager.getInstance(project).openFile(file, true);
        }
    }


}

class MyFileChangeListener extends VirtualFileAdapter {
    private final JList<String> fileList;

    private final Project project;
    public MyFileChangeListener(JList<String> fileList, Project project) {
        this.fileList = fileList;
        this.project=project;
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        fileList.setListData(listProjectFiles(project));
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        fileList.setListData(listProjectFiles(project));
    }

    private String[] listProjectFiles(Project project) {
        VirtualFile[] files = project.getBaseDir().getChildren();
        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }
        return fileNames;
    }


}