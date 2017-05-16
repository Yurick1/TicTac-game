package gui;

import game.Game;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow extends JFrame {	
	private JButton buttonNewGame;
	private JButton buttonExit;
	private JPanel controlPanel;
	private MenuWindow menu;
	private Game gameField;
	
	public GameWindow(int size, boolean playerX, MenuWindow menu) throws HeadlessException	//исключение - подсказка в автоматически созданном классе, унаследованном от JFrame 
	{
		super("Крестики-нолики");
		this.menu = menu;	//Окно, к которому следует обращаться при вызове меню	
		super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		super.setBounds(this.menu.getX()+100-50*size, this.menu.getY()+100-50*size, 100*size, 100*size);
		super.setResizable(false);
		super.setLayout(new BorderLayout());
			
		this.buttonNewGame = new JButton("Новая игра");
		this.buttonExit = new JButton("Выход");
		this.controlPanel = new JPanel();
		
		this.controlPanel.setLayout(new GridLayout());
		this.controlPanel.add(this.buttonNewGame, BorderLayout.LINE_START);
		this.controlPanel.add(this.buttonExit, BorderLayout.LINE_END);
		this.gameField = new Game(size, playerX, this.menu);
		super.add(controlPanel, BorderLayout.SOUTH);
		super.add(this.gameField, BorderLayout.CENTER);
		
		this.buttonNewGame.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						setVisible(false);
						dispose();
						menu.setBounds(getX()+getWidth()/2-100, getY()+getHeight()/2-100, 200, 200);
						menu.setVisible(true);
					}
				});
		this.buttonExit.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						System.exit(0);
					}
				});
		
		super.setVisible(true);
		//Первый шаг в игре обязательно после инициализации всего!
		this.gameField.firstStep();		
	}
}
