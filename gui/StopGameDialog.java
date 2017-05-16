package gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class StopGameDialog extends JDialog {
	private JLabel label;
	private JButton continueButton;
	private MenuWindow menu;

	public StopGameDialog(MenuWindow menu) throws HeadlessException
	{
		super(menu);
		this.menu = menu;
		//TODO: расположение по центру
		//super.setBounds(menu.game.getX()+menu.game.getWidth()/2-10, menu.game.getY()+menu.game.getHeight()/2-50, 20, 100);
		super.setSize(20, 100);
		super.setResizable(false);
		super.setModal(true);
		super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		super.setTitle("Игра завершена");
		super.setLayout(new BorderLayout());
		this.continueButton = new JButton("Далее");
		super.add(this.continueButton, BorderLayout.SOUTH);

		this.continueButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				menu.game.setVisible(false);
				menu.game.dispose();
				label.setVisible(false);
				//label.disable();
				setVisible(false);
				dispose();
				menu.setBounds(getX()+getWidth()/2-100, getY()+getHeight()/2-100, 200, 200);
				menu.setVisible(true);
			}
		});
	}
	
	public void callDialog(StopGameLabels labelVal)
	{
		switch (labelVal)
		{
		case DRAW:
			this.label = new JLabel("<html><p style=\"font-size:120%\">Ничья</p></html>", JLabel.CENTER);
			break;
		case LOST:
			this.label = new JLabel("<html><p style=\"font-size:120%\">Игра окончена</p></html>", JLabel.CENTER);
			break;
		case WIN:
			this.label = new JLabel("<html><p style=\"font-size:120%\">Вы выиграли!</p></html>", JLabel.CENTER);
			break;
		}
		super.setBounds(menu.game.getX()+menu.game.getWidth()/2-50, menu.game.getY()+menu.game.getHeight()/2-50, 20, 100);
		super.add(this.label, BorderLayout.CENTER);
		super.setVisible(true);		 
	}
}