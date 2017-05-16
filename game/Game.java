package game;

import java.awt.GridLayout;
import javax.swing.*;


public final class Game extends JPanel
{
	GameButton[][] gameFieldButtons;
	private boolean playerX;
	private gui.MenuWindow menu;
	AI comp;

	public Game(int size, boolean playerX, gui.MenuWindow menu)
	{
		comp = new AI(playerX, size, this);
		this.menu = menu;
		this.playerX = playerX;
		//Заполнение поля
		super.setLayout(new GridLayout(size+1, size+1));
		this.gameFieldButtons = new GameButton[size][size];
		char xScale='0';
		char yScale='1';
		for (int i = 0; i<size+1; i++)
			for (int j = 0; j<size+1; j++)
				if (i == 0)
				{
					super.add(new JLabel("<html><p style=\"font-size: "+10*size+"\">"+xScale+"</p></html>", JLabel.CENTER));					
					xScale++;
				}
				else if (j == 0)
				{
					super.add(new JLabel("<html><p style=\"font-size: "+10*size+"\">"+yScale+"</p></html>", JLabel.CENTER));
					yScale++;
				}
				else
					super.add(this.gameFieldButtons[i-1][j-1] = new GameButton(this));
		//Обработка нажатий на кнопки поля (ходы игрока)
		for (int i=0; i<size; i++)
			for (int j=0; j<size; j++)
				this.gameFieldButtons[j][i].addActionListener(new GameButtonListener(this.gameFieldButtons[j][i], playerX));
	}
	
	//Первый ход компа, если игрок - О
	public void firstStep()
	{
		if (!this.playerX)
			this.comp.compTurnAI();		
	}	
		
	//Проверка ничьей. Используется только после проверки победы
	boolean isDraw()
	{
		for (int i=0; i<this.gameFieldButtons.length; i++)
			for (int j=0; j<this.gameFieldButtons.length; j++)
				if (this.gameFieldButtons[i][j].getVal() == ButtonsVals.EMPTY)
					return false;
		this.menu.stop.callDialog(gui.StopGameLabels.DRAW);
		return true;
	}
	
	//Проверка победы
	boolean isWin(ButtonsVals ChekingDot) {
		int buffHorizontal = 0;
		int buffVertical = 0;
		int buffMainDiagonal = 0;
		int buffSideDiagonal = 0;
		int dotsToWin = this.gameFieldButtons.length;
		for (int i=0; i<this.gameFieldButtons.length; i++)
		{
			for (int j=0; j<this.gameFieldButtons.length; j++)
			{
				if (this.gameFieldButtons[i][j].getVal() == ChekingDot)
					buffHorizontal++;
				else
					buffHorizontal=0;
				
				if (this.gameFieldButtons[j][i].getVal() == ChekingDot)
					buffVertical++;
				else
					buffVertical=0;
				
				if (this.gameFieldButtons[i][j].getVal() == ChekingDot && i==j)
					buffMainDiagonal++;
				
				if (this.gameFieldButtons[i][j].getVal() == ChekingDot && i==this.gameFieldButtons.length-j-1)
					buffSideDiagonal++;
				
				if (buffVertical==dotsToWin || buffHorizontal==dotsToWin || buffMainDiagonal==dotsToWin || 
						buffSideDiagonal==dotsToWin)
				{
					if (this.playerX && ChekingDot==ButtonsVals.X || !this.playerX && ChekingDot==ButtonsVals.O)
						this.menu.stop.callDialog(gui.StopGameLabels.WIN);
					else
						this.menu.stop.callDialog(gui.StopGameLabels.LOST);
					return true;
				}
			}
			buffHorizontal=0;
			buffVertical=0;
		}
		return false;
	}
}