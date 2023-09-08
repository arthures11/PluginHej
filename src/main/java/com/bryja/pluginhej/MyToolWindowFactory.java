package com.bryja.pluginhej;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowContentUiType;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.sun.istack.NotNull;

import javax.swing.*;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MyToolWindowFactory implements ToolWindowFactory{

    private JTree tree;
    private DefaultMutableTreeNode root;

    private Project pr;
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        panel.add(new JLabel("witamy po lewej stronie!"));
//
//
//        JList<String> fileList = new JList<>(listProjectFiles(project));
//        fileList.addListSelectionListener(e -> openSelectedFile(project, fileList.getSelectedValue()));
//        panel.add(new JScrollPane(fileList));
//
//        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
//        Content content = contentFactory.createContent(panel, "", false);
//        toolWindow.getContentManager().addContent(content);


//v2
//        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(true, true);
//        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Pliczki:");
//        DefaultTreeModel treeModel = new DefaultTreeModel(root);
//
//        PsiDirectory baseDir = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
//        if (baseDir != null) {
//            processDirectory(baseDir, root);
//        }
//
//        JTree fileTree = new Tree(treeModel);
//        JScrollPane scrollPane = new JScrollPane(fileTree);
//        panel.setContent(scrollPane);
//
//        fileTree.addTreeSelectionListener(new TreeSelectionListener() {
//            @Override
//            public void valueChanged(TreeSelectionEvent e) {
//                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
//                if (selectedNode != null && selectedNode.isLeaf()) {
//                    String selectedFileName = selectedNode.getUserObject().toString();
//                    openSelectedFile(project, selectedFileName);
//                }
//            }
//        });
//
//
//
//        toolWindow.getContentManager().addContent(toolWindow.getContentManager().getFactory().createContent(panel, "", false));
//
//        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
//        Content content = contentFactory.createContent(panel, "", false);
//        toolWindow.getContentManager().addContent(content);



        //v3:


        pr = project;
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(true);

        VirtualFile baseDir = pr.getBaseDir();
         root = new DefaultMutableTreeNode(new TreeNodeData(baseDir.getName(), baseDir));

        populateTree(root, baseDir);

         tree = new JTree(new DefaultTreeModel(root));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        tree.setCellRenderer(renderer);

        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (selectedNode != null) {
                        TreeNodeData nodeData = (TreeNodeData) selectedNode.getUserObject();
                        VirtualFile selectedFile = nodeData.getVirtualFile();
                        if (selectedFile != null && !selectedFile.isDirectory()) {
                            FileEditorManager.getInstance(pr).openFile(selectedFile, true);
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tree);
       // panel.setContent(scrollPane);
//        EditorTextField asd = new EditorTextField("123123");
//        panel.setContent(asd);


        IkonkaAction ikonkaAction = new IkonkaAction(panel);
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("MyToolbar", new DefaultActionGroup(ikonkaAction), true);
        panel.setToolbar(actionToolbar.getComponent());


        //stara ikonka bez klasy
//        AnAction myAction = new AnAction("the icon", "add file", AllIcons.General.Information) {
//            @Override
//            public void actionPerformed(@NotNull AnActionEvent e) {
//                createTxtFile(pr);
//
//
//
//                VirtualFile baseDir = project.getBaseDir();
//
//                VirtualFile txtFile = baseDir.findChild("plik.txt");
//                if (txtFile != null && txtFile.exists()) {
//                    Path filePath = Paths.get(txtFile.getPath());
//                    String fileContent = null;
//                    try {
//
//                        fileContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
//                        EditorTextField editorTextField = new EditorTextField(fileContent);
//                        panel.setContent(editorTextField);
//
//                    } catch (IOException a) {
//                        throw new RuntimeException(a);
//                    }
//                }
//
//
//            }
//        };


        toolWindow.getComponent().add(panel);
        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileAdapter() {
            @Override
            public void contentsChanged(VirtualFileEvent event) {
                updateTree();
            }

            @Override
            public void fileCreated(VirtualFileEvent event) {
                updateTree();
            }

            @Override
            public void fileDeleted(VirtualFileEvent event) {
                updateTree();
            }
        });

    }


    void createTxtFile(Project project) {

     //   VirtualFile baseDir = project.getBaseDir();

      //  VirtualFile txtFile = null;
        try {
         //   txtFile = baseDir.createChildData(this, "plik.txt");
       //     FileDocumentManager.getInstance().saveDocument(FileDocumentManager.getInstance().getDocument(txtFile));


            String dir = project.getBasePath();
            List<String> command = new ArrayList<>();
            command.add("ruby");
            command.add("C:\\Users\\arthur\\AppData\\Local\\JetBrains\\RubyMine2023.2\\tmp\\script.rb");
            command.add(dir);
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();
        } catch (IOException e) {
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

    void createTxtFileTest(Project project, String s) {
        try {
            VirtualFile baseDir = project.getBaseDir();

            VirtualFile txtFile = baseDir.createChildData(this, "asdasdasd.txt");
            FileDocumentManager.getInstance().saveDocument(FileDocumentManager.getInstance().getDocument(txtFile));

            FileUtil.writeToFile(new File(txtFile.getPath()), s);

            VfsUtil.markDirtyAndRefresh(true, true, true, baseDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTree() {
        root.removeAllChildren();
        VirtualFile baseDir = pr.getBaseDir();
        populateTree(root, baseDir);
        ((DefaultTreeModel) tree.getModel()).reload(root);
    }

    private void populateTree(DefaultMutableTreeNode parent, VirtualFile virtualFile) {
        for (VirtualFile child : virtualFile.getChildren()) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new TreeNodeData(child.getName(), child));
            parent.add(childNode);
            if (child.isDirectory()) {
                populateTree(childNode, child);
            }
        }
    }
    private void processDirectory(PsiDirectory directory, DefaultMutableTreeNode parentNode) {
        for (PsiFile file : directory.getFiles()) {
            DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file.getName());
            parentNode.add(fileNode);
        }

        for (PsiDirectory subDirectory : directory.getSubdirectories()) {
            DefaultMutableTreeNode directoryNode = new DefaultMutableTreeNode(subDirectory.getName());
            parentNode.add(directoryNode);
            processDirectory(subDirectory, directoryNode); // Recursively process subdirectories
        }
    }

//stare
    void displayText(Project project, JPanel panel) {
        VirtualFile baseDir = project.getBaseDir();

        VirtualFile txtFile = baseDir.findChild("plik.txt");
        if (txtFile != null && txtFile.exists()) {
            Path filePath = Paths.get(txtFile.getPath());
            String fileContent = null;
            try {
                fileContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
                JLabel label = new JLabel("<html><body style='padding: 10px;'>"+fileContent+"</body></html>");
                panel.remove(label);
                label.setOpaque(true);
                label.setBackground(Color.WHITE);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));

              //  panel.add(label);
               // panel.revalidate();
               // panel.repaint();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }
    private String readTextFromFile(VirtualFile file) {
        try {
            return new String(file.contentsToByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void openSelectedFile(Project project, String fileName) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(project.getBasePath(), fileName));
        if (virtualFile != null) {
            FileEditorManager.getInstance(project).openFile(virtualFile, true);
        }
    }
    private String[] listProjectFiles(Project project) {
        VirtualFile[] files = project.getBaseDir().getChildren();
        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }
        return fileNames;
    }

//    private void openSelectedFile(Project project, String fileName) {
//        VirtualFile file = project.getBaseDir().findChild(fileName);
//        if (file != null) {
//            FileEditorManager.getInstance(project).openFile(file, true);
//        }
//    }


}

  class TreeNodeData {
    private final String name;
    private final VirtualFile virtualFile;

    public TreeNodeData(String name, VirtualFile virtualFile) {
        this.name = name;
        this.virtualFile = virtualFile;
    }

    public String getName() {
        return name;
    }

    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    @Override
    public String toString() {
        return name;
    }
}