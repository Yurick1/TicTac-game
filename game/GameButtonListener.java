package game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Обработчик нажатия на кнопку игрового поля. Используется в классе Game
final class GameButtonListener implements ActionListener
{
	private GameButton button;
	private boolean playerX;
	
	public GameButtonListener(GameButton button, boolean playerX)
	{
		this.playerX = playerX;
		this.button = button;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (this.button.getVal() == ButtonsVals.EMPTY)
		{
			this.button.drawButton(true, this.playerX);
			return;
		}
		else
			return;
	}	
}