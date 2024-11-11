import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GUI extends JFrame implements ActionListener {
    // Deklarasi tombol
    private JButton enableCmdButton;
    private JButton disableCmdButton;
    private JButton enableTaskManagerButton;
    private JButton disableTaskManagerButton;
    private JButton enableRegistryButton;
    private JButton disableRegistryButton;
    private JButton exitButton;
    private JButton scanTrojanButton;
    private JList<String> resultList;
    private DefaultListModel<String> listModel;
    private ArrayList<File> detectedFiles;

    public GUI() {
        // Mengatur judul JFrame
        setTitle("CyberSecurity -- by AnchorV1");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Membuat tombol
        enableCmdButton = new JButton("Enable CMD");
        disableCmdButton = new JButton("Disable CMD");
        enableTaskManagerButton = new JButton("Enable Task Manager");
        disableTaskManagerButton = new JButton("Disable Task Manager");
        enableRegistryButton = new JButton("Enable Regedit");
        disableRegistryButton = new JButton("Disable Regedit");
        scanTrojanButton = new JButton("Scan for Trojans");
        exitButton = new JButton("Exit");

        // Membuat JList dan model untuk hasil scan
        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(resultList);

        // Menambahkan ActionListener pada tombol
        enableCmdButton.addActionListener(this);
        disableCmdButton.addActionListener(this);
        enableTaskManagerButton.addActionListener(this);
        disableTaskManagerButton.addActionListener(this);
        enableRegistryButton.addActionListener(this);
        disableRegistryButton.addActionListener(this);
        scanTrojanButton.addActionListener(e -> scanForTrojans());
        exitButton.addActionListener(e -> System.exit(0));

        // Menambahkan Listener untuk klik item pada JList
        resultList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedIndex = resultList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        File selectedFile = detectedFiles.get(selectedIndex);
                        showFileOptions(selectedFile);
                    }
                }
            }
        });

        // Menambahkan tombol dan JList ke JFrame
        add(enableCmdButton);
        add(disableCmdButton);
        add(enableTaskManagerButton);
        add(disableTaskManagerButton);
        add(enableRegistryButton);
        add(disableRegistryButton);
        add(scanTrojanButton);
        add(exitButton);
        add(listScrollPane);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = "";
        if (e.getSource() == enableCmdButton) {
            command = "enable_cmd";
        } else if (e.getSource() == disableCmdButton) {
            command = "disable_cmd";
        } else if (e.getSource() == enableTaskManagerButton) {
            command = "enable_task_manager";
        } else if (e.getSource() == disableTaskManagerButton) {
            command = "disable_task_manager";
        } else if (e.getSource() == enableRegistryButton) {
            command = "enable_registry";
        } else if (e.getSource() == disableRegistryButton) {
            command = "disable_registry";
        }

        // Memanggil Python script
        if (!command.isEmpty()) {
            callPythonScript(command);
        }
    }

    // Fungsi untuk melakukan scan trojan menggunakan Python script
    private void scanForTrojans() {
        listModel.clear();
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", "functions.py", "scan_trojans");
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();

            if (line != null && !line.equals("No threats detected")) {
                String[] files = line.split(";");
                for (String file : files) {
                    listModel.addElement(file);
                }
                JOptionPane.showMessageDialog(this, "Scan Complete. Trojan files detected.", "Scan Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Scan Complete. No threats detected.", "Scan Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during scan.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void callPythonScript(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", "functions.py", command);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                JOptionPane.showMessageDialog(this, line, "Output", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error calling Python script", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Menampilkan opsi untuk file yang dipilih
    private void showFileOptions(File file) {
        String[] options = {"Delete File", "Go to File Location", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Pilih tindakan untuk file:\n" + file.getAbsolutePath(),
                "File Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (choice) {
            case 0 -> deleteFile(file);
            case 1 -> openFileLocation(file);
            default -> System.out.println("No action selected.");
        }
    }

    // Menghapus file yang dipilih
    private void deleteFile(File file) {
        if (file.delete()) {
            JOptionPane.showMessageDialog(this, "File berhasil dihapus.", "Success", JOptionPane.INFORMATION_MESSAGE);
            listModel.removeElement(file.getName());
            detectedFiles.remove(file);
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menghapus file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Membuka lokasi file di File Explorer
    private void openFileLocation(File file) {
        try {
            Desktop.getDesktop().open(file.getParentFile());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal membuka lokasi file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new GUI();
    }
}
