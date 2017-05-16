package game;

//import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;

//Кнопка игрового поля. Используется в классе Game
final class GameButton extends JButton
{
	ButtonsVals val;
	Graphics graph;	
	Game game;
	
	GameButton(Game game)
	{
		super();
		this.val = ButtonsVals.EMPTY;
		
		this.game = game;
	} 
	
	/* 
	 * В отличии от примера, разобранного на уроке, я использую не просто JPanel и определение координат кликов, а кнопки.
	 * Уже вторую неделю бьюсь с рисованием на JButton. Обычный paintComponent и repaint приводят к тому,
	 * что кнопка перерисовается непонятно чем при наведении мыши, а при клике почему-то очень странным образом
	 * пытаются перерисоваться все остальные кнопки поля. Так как времени мало, делаю, как мне кажется, очень глупый 
	 * костыль с HTML и CSS: при клике на всю кнопку рисуется символ 'X' или 'O'. Символы кнопок,
	 * заполненных игроком, черные, компом - красные.
	 * Надеюсь подробнее все это понять на Java 2.
	 */
	void drawButton(boolean  playerTurn, boolean playerX)
	{
		/*
		 * Пытался через класс Color и getRed(), getGreen(), getBlue(), но возникло много ошибок в CSS:
		 * <p style="color:rgb(int_red, int_green, int_blue)">Some Text</p>,
		 * где int_someColorComponent - соответствующий геттер.
		 */
		String buttonColor;
		if (playerTurn)
		{
			buttonColor = "black";
			this.val = this.setVal(playerX);
		}
		else
		{
			buttonColor = "red";
			this.val = this.setVal(!playerX);
		}
		//super.setText("<html><p color=\""+buttonColor+"\" style=\"font-size: "+super.getHeight()+"\">"+this.val+"</p></html>");
		super.setText("<html><p color=\""+buttonColor+"\" style=\"font-size: "+super.getHeight()+"\">"+this.val+"</p></html>");
		
		if (!this.game.isWin(this.getVal()) && !this.game.isDraw() && playerTurn)
			this.game.comp.compTurnAI();
	}
	
	protected static ButtonsVals setVal(boolean isX)
	{
		if (isX)
			return ButtonsVals.X;
		else
			return ButtonsVals.O;
	}

	ButtonsVals getVal() {
		return val;
	}
}