package game;

//import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;

//������ �������� ����. ������������ � ������ Game
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
	 * � ������� �� �������, ������������ �� �����, � ��������� �� ������ JPanel � ����������� ��������� ������, � ������.
	 * ��� ������ ������ ����� � ���������� �� JButton. ������� paintComponent � repaint �������� � ����,
	 * ��� ������ �������������� ��������� ��� ��� ��������� ����, � ��� ����� ������-�� ����� �������� �������
	 * �������� �������������� ��� ��������� ������ ����. ��� ��� ������� ����, �����, ��� ��� �������, ����� ������ 
	 * ������� � HTML � CSS: ��� ����� �� ��� ������ �������� ������ 'X' ��� 'O'. ������� ������,
	 * ����������� �������, ������, ������ - �������.
	 * ������� ��������� ��� ��� ������ �� Java 2.
	 */
	void drawButton(boolean  playerTurn, boolean playerX)
	{
		/*
		 * ������� ����� ����� Color � getRed(), getGreen(), getBlue(), �� �������� ����� ������ � CSS:
		 * <p style="color:rgb(int_red, int_green, int_blue)">Some Text</p>,
		 * ��� int_someColorComponent - ��������������� ������.
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