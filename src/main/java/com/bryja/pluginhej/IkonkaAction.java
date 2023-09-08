package com.bryja.pluginhej;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.EditorTextField;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IkonkaAction extends AnAction {

    public SimpleToolWindowPanel panel ;

    public IkonkaAction() {
    }

    public IkonkaAction(SimpleToolWindowPanel panel) {
            super("the icon", "dodaj plik", AllIcons.General.Add);
            this.panel=panel;
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            Project pr = e.getProject();
            createTxtFile(pr);



                String baseDir = pr.getBasePath();

                File baseDirectory = new File(baseDir);
                String targetFileName = "plik.txt";

                String filePath = findFile(baseDirectory, targetFileName);


                Path path = Paths.get(filePath);
                 //   Path filePath = Paths.get(txtFile.getPath());
                    //  String fileContent = null;

                    //fileContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
                    try {
                        EditorTextField editorTextField = new EditorTextField(Files.readString(path));
                        panel.setContent(editorTextField);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }



            }


    public static String findFile(File directory, String targetFileName) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String filePath = findFile(file, targetFileName);
                    if (filePath != null) {
                        return filePath;
                    }
                } else if (file.getName().equals(targetFileName)) {
                    return file.getAbsolutePath();
                }
            }
        }

        return null;
    }
    void createTxtFile(Project project) {

      //  VirtualFile baseDir = project.getBaseDir();

       // VirtualFile txtFile = null;
        try {
         //   txtFile = baseDir.createChildData(this, "plik.txt");
        //    FileDocumentManager.getInstance().saveDocument(FileDocumentManager.getInstance().getDocument(txtFile));


            String dir = project.getBasePath();
            List<String> command = new ArrayList<>();
            command.add("ruby");
            command.add("C:\\Users\\arthur\\AppData\\Local\\JetBrains\\RubyMine2023.2\\tmp\\script.rb");
            command.add(dir);
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process pr = processBuilder.start();
            pr.waitFor(5000, TimeUnit.MILLISECONDS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


//        try {
//            VirtualFile baseDir = project.getBaseDir();
//
//            VirtualFile txtFile = baseDir.createChildData(this, "plik.txt");
//            FileDocumentManager.getInstance().saveDocument(FileDocumentManager.getInstance().getDocument(txtFile));
//
//            FileUtil.writeToFile(new File(txtFile.getPath()), "jakis tekst z pliku");
//
//            VfsUtil.markDirtyAndRefresh(true, true, true, baseDir);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
