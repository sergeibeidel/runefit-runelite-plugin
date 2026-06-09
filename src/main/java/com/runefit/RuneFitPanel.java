package com.runefit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

final class RuneFitPanel extends PluginPanel
{
	private final JButton generateButton = new JButton("Generate Link Code");
	private final JTextField codeField = new JTextField();
	private final JLabel characterLabel = new JLabel("No character linked yet");
	private final JTextArea statusArea = new JTextArea();

	RuneFitPanel(Runnable generateAction)
	{
		setLayout(new BorderLayout());

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		content.setBackground(ColorScheme.DARK_GRAY_COLOR);

		JTextArea instructions = textArea(
			"Log into the OSRS character you want to link, then generate a single-use code and paste it into RuneFit."
		);

		codeField.setEditable(false);
		codeField.setVisible(false);
		codeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		codeField.setToolTipText("Select and copy this code into RuneFit.");

		statusArea.setLineWrap(true);
		statusArea.setWrapStyleWord(true);
		statusArea.setEditable(false);
		statusArea.setFocusable(false);
		statusArea.setOpaque(false);
		statusArea.setVisible(false);

		generateButton.addActionListener(event -> generateAction.run());
		generateButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

		content.add(instructions);
		content.add(Box.createRigidArea(new Dimension(0, 12)));
		content.add(characterLabel);
		content.add(Box.createRigidArea(new Dimension(0, 8)));
		content.add(codeField);
		content.add(Box.createRigidArea(new Dimension(0, 8)));
		content.add(statusArea);
		content.add(Box.createRigidArea(new Dimension(0, 12)));
		content.add(generateButton);

		add(content, BorderLayout.NORTH);
	}

	void showLoading()
	{
		SwingUtilities.invokeLater(() ->
		{
			generateButton.setEnabled(false);
			codeField.setVisible(false);
			statusArea.setText("Requesting a link code...");
			statusArea.setVisible(true);
		});
	}

	void showCode(LinkCodeResponse response)
	{
		SwingUtilities.invokeLater(() ->
		{
			generateButton.setEnabled(true);
			characterLabel.setText("Character: " + response.getCharacterName());
			codeField.setText(response.getLinkCode());
			codeField.setVisible(true);
			statusArea.setText("Paste this code into RuneFit within ten minutes. It can only be used once.");
			statusArea.setVisible(true);
			revalidate();
			repaint();
		});
	}

	void showError(String message)
	{
		SwingUtilities.invokeLater(() ->
		{
			generateButton.setEnabled(true);
			codeField.setVisible(false);
			statusArea.setText(message);
			statusArea.setVisible(true);
			revalidate();
			repaint();
		});
	}

	private JTextArea textArea(String text)
	{
		JTextArea area = new JTextArea(text);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEditable(false);
		area.setFocusable(false);
		area.setOpaque(false);
		return area;
	}
}
