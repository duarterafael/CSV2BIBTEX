
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {

	private static JFrame frame;
	private static BufferedImage image;
	static JTextField textfield1, textfield2, textfield3;
	private static Button fileChooserButton, exportButtom;
	private static JFileChooser chooser;

	private static String getFileExtension(File file) {
		String extension = "";

		try {
			if (file != null && file.exists()) {
				String name = file.getName();
				extension = name.substring(name.lastIndexOf("."));
			}
		} catch (Exception e) {
			extension = "";
		}

		return extension;

	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

					frame = new JFrame("Spring CSV to Ris");

					frame.getContentPane().setLayout(new FlowLayout());
					textfield1 = new JTextField("Select a CSV Spring export file", 30);
					textfield1.setEnabled(false);
					frame.getContentPane().add(textfield1);

					fileChooserButton = new Button("Select");
					frame.getContentPane().add(fileChooserButton);
					fileChooserButton.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							chooser = new JFileChooser();
							textfield1.setText("Select a CSV Spring export file");
							int returnVal = chooser.showOpenDialog(null);
							if (returnVal == JFileChooser.APPROVE_OPTION) {
								textfield1.setText(chooser.getSelectedFile().getPath());
								exportButtom.setEnabled(true);
							}
						}

					});

					exportButtom = new Button("Export");
					frame.getContentPane().add(exportButtom);
					exportButtom.setEnabled(false);
					exportButtom.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							if (!getFileExtension(chooser.getSelectedFile()).equalsIgnoreCase(".CSV")) {
								JOptionPane.showMessageDialog(frame, "Only CSV files can be converted!",
										"Error Message", JOptionPane.ERROR_MESSAGE);
							}else {
								try {
									Csv2Ris.export(chooser.getSelectedFile());
								} catch (IOException e1) {
									JOptionPane.showMessageDialog(frame, e1.getMessage(),
											"Error Message", JOptionPane.ERROR_MESSAGE);
									e1.printStackTrace();
								} catch (InterruptedException e1) {
									JOptionPane.showMessageDialog(frame, e1.getMessage(),
											"Error Message", JOptionPane.ERROR_MESSAGE);
									e1.printStackTrace();
								}
							}
						}

					});
					frame.pack();
					frame.setVisible(true);

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}
}
