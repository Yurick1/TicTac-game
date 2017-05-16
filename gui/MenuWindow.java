package gui;

import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.*;

public final class MenuWindow extends JFrame {
	private JComboBox cbCharapters;
	private JComboBox cbMapSizes;
	private JButton buttonStartGame;
	private JButton buttonExit;
	private String[] charapters = new String[3];
	private String[] mapSizes = new String[8];
	private Random rand;
	private boolean playerX;
	GameWindow game;
	public StopGameDialog stop;
	
	public MenuWindow() throws HeadlessException 
	{
		super.setTitle("Новая игра");
		super.setBounds(300, 300, 200, 200);
		super.setResizable(false);
		super.setLayout(new GridLayout(4, 2));
		super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		this.charapters[0] = new String("Крестики");
		this.charapters[1] = new String("Нолики");
		this.charapters[2] = new String("Случайный");
		for (int i=3; i<10; i++)
			this.mapSizes[i-3] = new String(i+"x"+i);
		this.mapSizes[7] = new String("Случайный");
		this.cbCharapters = new JComboBox(this.charapters);
		this.cbMapSizes = new JComboBox(this.mapSizes);
		this.buttonStartGame = new JButton("Начать");
		this.buttonExit = new JButton("Выйти");
		this.rand = new Random();
		this.stop = new StopGameDialog(this);
		
		super.add(new JLabel("Кем играть:"));
		super.add(this.cbCharapters);
		super.add(new JLabel("Размер поля:"));
		super.add(this.cbMapSizes);
		super.add(new JLabel());
		super.add(new JLabel());
		super.add(this.buttonStartGame);
		super.add(this.buttonExit);
		
		this.buttonStartGame.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				switch (cbCharapters.getSelectedIndex())
				{
				case 0:
					playerX = true;
					break;
				case 1:
					playerX = false;
					break;
				case 2:
					playerX = rand.nextBoolean();
					break;
				}
				
				if (cbMapSizes.getSelectedIndex() == mapSizes.length-1)
					newGame(rand.nextInt(7)+3, playerX);
				else
					newGame(cbMapSizes.getSelectedIndex() + 3, playerX);
				setVisible(false);
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
	}
	
	private void newGame(int size, boolean playerX)
	{
		this.game = new GameWindow(size, playerX, this);
	}
}