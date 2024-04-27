import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FileManager {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileManagerGUI());
    }

}

class FileManagerGUI extends JFrame {
    private JTextField currentDir;
    private JList<File> files;
    private File currentDirectory;
    private File[] directoryContents;
    private File sourceFile;
    private File destinationFile;

    public FileManagerGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("File Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        currentDirectory = new File(".");
        directoryContents = currentDirectory.listFiles();

        currentDir = new JTextField(20);
        currentDir.setText(currentDirectory.getAbsolutePath());
        currentDir.setEditable(false);

        files = new JList<>(directoryContents);
        files.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        files.setBackground(new Color(240, 240, 240));

        JButton changeDirButton = new JButton("Change Directory",new ImageIcon("replace.png"));
        changeDirButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Change Directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (chooser.showOpenDialog(FileManagerGUI.this) == JFileChooser.APPROVE_OPTION) {
                currentDirectory = chooser.getSelectedFile();
                currentDir.setText(currentDirectory.getAbsolutePath());
                updateFilesList();
            }
        });

        JButton copyButton = createStyledButton("Copy", "copy.png");
        copyButton.addActionListener(e -> sourceFile = files.getSelectedValue());

        JButton moveButton = createStyledButton("Move", "move.png");
        moveButton.addActionListener(e -> {
            destinationFile = currentDirectory;
            moveFile();
        });

        JButton pasteButton = createStyledButton("Paste", "paste.png");
        pasteButton.addActionListener(e -> {
            if (sourceFile != null && destinationFile != null) {
                copyFile(sourceFile, destinationFile);
                updateFilesList();
            }
        });

        JButton cutButton = createStyledButton("Cut", "cut.png");
        cutButton.addActionListener(e -> sourceFile = files.getSelectedValue());

        JButton deleteButton = createStyledButton("Delete", "delete.png");
        deleteButton.addActionListener(e -> {
            deleteFile();
            updateFilesList();
        });

        JButton renameButton = createStyledButton("Rename", "edit.png");
        renameButton.addActionListener(e -> {
            renameFile();
            updateFilesList();
        });

        JButton newFolderButton = createStyledButton("New Folder", "add-folder.png");
        newFolderButton.addActionListener(e -> {
            createNewFolder();
            updateFilesList();
        });

        JPanel panel = new JPanel();
        panel.add(currentDir);
        panel.add(changeDirButton);
        panel.add(copyButton);
        panel.add(moveButton);
        panel.add(pasteButton);
        panel.add(cutButton);
        panel.add(deleteButton);
        panel.add(renameButton);
        panel.add(newFolderButton);

        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(200, 220, 240));

        JScrollPane scrollPane = new JScrollPane(files);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        updateFilesList();
        setVisible(true);
    }

    private JButton createStyledButton(String text, String iconName) {
        JButton button = new JButton(text, new ImageIcon(iconName));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        return button;
    }

    private void updateFilesList() {
        directoryContents = currentDirectory.listFiles();
        if (directoryContents != null) {
            files.setListData(directoryContents);
        }
    }

    private void copyFile(File source, File destination) {
        try {
            Files.copy(source.toPath(), destination.toPath().resolve(source.getName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void moveFile() {
        if (sourceFile != null && destinationFile != null) {
            copyFile(sourceFile, destinationFile);
            deleteFile();
            sourceFile = null;
        }
    }

    private void deleteFile() {
        File fileToDelete = files.getSelectedValue();
        if (fileToDelete != null && fileToDelete.exists()) {
            if (fileToDelete.isDirectory()) {
                try {
                    Files.delete(fileToDelete.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                fileToDelete.delete();
            }
        }
    }

    private void renameFile() {
        File fileToRename = files.getSelectedValue();
        if (fileToRename != null && fileToRename.exists()) {
            String newName = JOptionPane.showInputDialog(this, "Enter new name:", fileToRename.getName());
            if (newName != null) {
                File newFile = new File(fileToRename.getParentFile(), newName);
                if (fileToRename.renameTo(newFile)) {
                    updateFilesList();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to rename the file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void createNewFolder() {
        String folderName = JOptionPane.showInputDialog(this, "Enter folder name:");
        if (folderName != null && !folderName.trim().isEmpty()) {
            File newFolder = new File(currentDirectory, folderName);
            if (newFolder.mkdir()) {
                updateFilesList();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create the new folder.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
