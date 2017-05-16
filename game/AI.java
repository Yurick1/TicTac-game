package game;

import java.util.Random;

final class AI {
	//****************************************
	//false, ���� ���������� ����� � ������� �� ���������.
	private final boolean DEBUG_OUTPUT = false;
	//****************************************
	private boolean playerX;
	private int size;
	private Game game;
	private Random rand = new Random();
	//���� �� ����� ��������� ���������
	private boolean checkMainDiag=true;
	private boolean checkSideDiag=true;
	//���������� ����������� ���������� ������ ������� � ������ ������������. �������� ���������� 2.
	private int filledCenterDiagonals = 0;
	
	AI(boolean playerX, int size, Game game)
	{
		this.playerX = playerX;
		this.size = size;
		this.game = game;
	}
	
	//"�����" ��� �����. ��� �������� � �������� � ������ ������� ���������������. ��� ���������� �������� ���������� �������� �� ����������.
	/*TODO: ���� ��������� ��� ������� ���������������� ����� � ����� ����� ����, �� � ����� ������� ���� ������� ����,
	� �� ������ � ������ �� ��������� ����.
	TODO: ���� ����������� ���� ������ � ����� ����� ������ �� ��������� ����������� ���������, �� ���� ������ � �������������� ���
	������������ ��� ��������� ��� ����� �����, � �� ��������� ��� ���������. */
	public void compTurnAI()
	{
		//System.out.println("����� ���");
		ButtonsVals compDot, playerDot;

		compDot = GameButton.setVal(!playerX);
		playerDot = GameButton.setVal(playerX);

		// 0. �������� �� ����� (���� ����������� ��������)
		if (this.size%2!=0 &&
				this.game.gameFieldButtons[this.size/2][this.size/2].getVal() == ButtonsVals.EMPTY)
		{
			this.game.gameFieldButtons[this.size/2][this.size/2].drawButton(false, this.playerX);
			debugOutput(this.size/2, this.size/2, DebugOutputTypes.CENTER, LineTypes.NO_ONE, false);
			return;
		}

		// 1. �������� �� ����� (���� ����������� ������). �������� �� ��������� �� ������� � �� ����� �� ���� ����������.
		//���-�� ��� �� ������� xor...
		if (this.size % 2 == 0 && (fillCenter(this.size - 1, this.size - 1, this.size, this.size, playerDot)
				|| fillCenter(this.size - 1, this.size, this.size, this.size - 1, playerDot)
				|| fillCenter(this.size, this.size, this.size - 1, this.size - 1, playerDot)
				|| fillCenter(this.size, this.size - 1, this.size - 1, this.size, playerDot)))
			return;

		// 2. ���������� �� ����� ��� ����.
		//����� ������� ����� ���������: ����������� ��������� ��� ������ � ��������� ��� ��������� ��� ����� � ������.
		if (protectionAndAttack(compDot, playerDot))
			return;

		// 3. ���� ����� ����� � ����� �� ����������, ������ ����� � ������ �� ��������� ����
		for (int i = 0; i < this.size; i += this.size - 1)
			for (int j = 0; j < this.size; j += this.size - 1)
			{
				if (this.game.gameFieldButtons[i][j].getVal() == ButtonsVals.EMPTY)
				{
					debugOutput(i, j, DebugOutputTypes.ANGLE, LineTypes.NO_ONE, false);
					this.game.gameFieldButtons[i][j].drawButton(false, this.playerX);
					return;
				}
			}

		// 4. ��������� ���
		this.compTurnRandom();
	}
	
	//��������� ����� ������� � ������ ������������.
	private boolean fillCenter(int x0, int y0, int x1, int y1, ButtonsVals playerDot)
	{
		if ((this.game.gameFieldButtons[x1/2][y1/2].getVal() == ButtonsVals.EMPTY
				&& this.game.gameFieldButtons[x0/2][y0/2].getVal()==playerDot)
				|| (this.game.gameFieldButtons[x1/2][y1/2].getVal()==ButtonsVals.EMPTY && this.filledCenterDiagonals<2))
		{
			this.game.gameFieldButtons[x1/2][y1/2].drawButton(false, this.playerX);
			debugOutput(x1/2, y1/2, DebugOutputTypes.CENTER, LineTypes.NO_ONE, false);
			this.filledCenterDiagonals++;
			//System.out.println("this.filledCenterDiagonals = "+this.filledCenterDiagonals);
			return true;
		}
		return false;
	}
	
	/*����������, ���� �� ���������� ��� ��������� ���������, � �������� �������, ���������� �� �������� ����� ��� ������
	�������, ��� ������� ��� ����� ��������. � ������ �������� ��� ������ � ����� �����������, � �� � ������������ ����
	� ������������������ ���������. �����, ��������� ��������, ������� ����������� ��������, 
	����� ��������� � ����� � ��� �� ������� ����. ��������, �������� ���������� � �������� ��������� � ���� ����
	��� ���������� �� "������� ���������".*/
	private boolean protectionAndAttack(ButtonsVals compDot, ButtonsVals playerDot)
	{
		//���������� ����� ������ � ����� �� ����������
		int playerDotsInMainDiagonal=0;
		int compDotsInMainDiagonal=0;
		int playerDotsInSideDiagonal=0;
		int compDotsInSideDiagonal=0;
		//������ ��������� ����� ������ � ����� �� ������ ����� � �������.
		//������ ������ ������� - ������ �������� �������, ������ - �������.
		//������ ������� ������ ������� ������� - ���������� ����� � ����� �������� �������.
		int[][] playerDotsInLine = new int[size][2];
		int[][] compDotsInLine = new int[size][2];
		//"������ ���������". ������� ����� ��, �� � � ������� ���������� �����. 
		//0 - �� ���������� (����� ������), 1 - �� (���� ����� ������ ������ ��� ������ �����), 2 - ��� (���� ����� � ������, � �����).
		int[][] playerDangerLine = new int[size][2];
		int[][] compDangerLine = new int[size][2];
		// ���������� ��� �����, � ����� ����� ������ ���.
		LineTypes type = LineTypes.NO_ONE;
		
		//�����
		for (int i = 0; i < this.size; i++)
			for (int j = 0; j < this.size; j++)
			{
				//������
				//��������� ������, ���� 0 (����� �� ����� �� ����) ��� 1 (����/���� ���������) � "������� ���������" ����� ��� ������
				if (playerDangerLine[i][0]!=2 || compDangerLine[i][0]!=2)
				{
					//������������, ������� ����� ������ � ����� �� �����
					if (this.game.gameFieldButtons[i][j].getVal() == playerDot)
						playerDotsInLine[i][0]++;	//������ ������� ������� ���������� �����, ������������ ������, ++
					else if (this.game.gameFieldButtons[i][j].getVal() == compDot)
						compDotsInLine[i][0]++;
					//���� ���� ����� � �����, � ������, �� ��������� ���, ������ 2 (���������� �����)
					//� ������ ������� ������� ���������, ������������ ������
					if (playerDotsInLine[i][0]>0 && compDotsInLine[i][0]>0)
					{
						playerDangerLine[i][0]=2;
						compDangerLine[i][0]=2;					
					}
					//���� ���� ����� �����, �� ��� ����� ������, ������ 1 (��������� ����) ������
					else if (playerDotsInLine[i][0]==0 && compDotsInLine[i][0]>1)
						playerDangerLine[i][0]=1;
					//����������
					else if (playerDotsInLine[i][0]>1 && compDotsInLine[i][0]==0)
						compDangerLine[i][0]=1;
					//���� ������ ������� ����� �� ����� ���, ������ ����� 0
					else
					{
						playerDangerLine[i][0]=0;
						compDangerLine[i][0]=0;
					}
				}
				//������ (���������� �������)
				if (playerDangerLine[i][1]!=2 || compDangerLine[i][1]!=2)
				{
					if (this.game.gameFieldButtons[j][i].getVal() == playerDot)
						playerDotsInLine[i][1]++;
					else if (this.game.gameFieldButtons[j][i].getVal() == compDot)
						compDotsInLine[i][1]++;
					if (playerDotsInLine[i][1]>0 && compDotsInLine[i][1]>0)
					{
						playerDangerLine[i][1]=2;
						compDangerLine[i][1]=2;					
					}
					else if (playerDotsInLine[i][1]==0 && compDotsInLine[i][1]>1)
						playerDangerLine[i][1]=1;
					else if (playerDotsInLine[i][1]>1 && compDotsInLine[i][1]==0)
						compDangerLine[i][1]=1;
					else
					{
						playerDangerLine[i][1]=0;
						compDangerLine[i][1]=0;
					}
				}
			}
		
		//���������
		for (int i = 0; i < this.size; i++)
			for (int j = 0; j < this.size; j++) 
			{
				//���� ���� ����� ��������� ��������� (�� ����� ���� �����-�� ����� ������ ��� ������ ������� ������ ����� ��� ������ ������)
				if (this.checkMainDiag)
				{
					if (this.game.gameFieldButtons[i][i].getVal() == playerDot)
						playerDotsInMainDiagonal++;
					else if (this.game.gameFieldButtons[i][i].getVal() == compDot)
						compDotsInMainDiagonal++;
					//���� ���� ����� � �����, � ������, �� ��� ������ ������ � ��� ���-�� ������.
					if (playerDotsInMainDiagonal>0 && compDotsInMainDiagonal>0)
						this.checkMainDiag=false;
				}
				//����������
				if (this.checkSideDiag)
				{
					if (this.game.gameFieldButtons[this.size-j-1][j].getVal() == playerDot)
						playerDotsInSideDiagonal++;
					else if (this.game.gameFieldButtons[this.size-j-1][j].getVal() == compDot)
						compDotsInSideDiagonal++;
					if (playerDotsInSideDiagonal>0 && compDotsInSideDiagonal>0)
						this.checkSideDiag=false;
				}
			}
		//���������� �����
		if (DEBUG_OUTPUT) 
		{
			System.out.println("����� ������");
			for (int j = 0; j < 2; j++) 
			{
				for (int i = 0; i < this.size; i++)
					System.out.print(playerDotsInLine[i][j]);
				System.out.println();
			}
			System.out.println("����� �����");
			for (int j = 0; j < 2; j++) 
			{
				for (int i = 0; i < this.size; i++)
					System.out.print(compDotsInLine[i][j]);
				System.out.println();
			}
			System.out.println("��������� ������");
			for (int j = 0; j < 2; j++) 
			{
				for (int i = 0; i < this.size; i++)
					System.out.print(playerDangerLine[i][j]);
				System.out.println();
			}
			System.out.println("��������� �����");
			for (int j = 0; j < 2; j++) 
			{
				for (int i = 0; i < this.size; i++)
					System.out.print(compDangerLine[i][j]);
				System.out.println();
			}
			System.out.println();
		}
		//����� ���� �������� �������� ������ ���������� �����, ��� ��� � ��������� ���������� ������ ���������.
		//�� � ���� ������ �� �������� "������ ���������"!!!
		for (int i=0; i< this.size; i++)
			for (int j=0; j<2; j++)
			{
				playerDotsInLine[i][j]=0;
				compDotsInLine[i][j]=0;
			}
		
		//�����
		for (int i=0; i<size; i++)	//����� ����� (������� ������ � ������� ���������� �����)
			for (int j=0; j<2; j++)	//����� ������ ������� - ������ ��� ������ �������� ������� 
			{
				//���� ��� ����� ���� ���������, �������� �����
				if (compDangerLine[i][j]==1)
				{
					if (j==1)
						type=LineTypes.VERTICAL;
					else
						type=LineTypes.HORISONTAL;
					//������� �������, ��� ������ ��������� ���
					compDangerLine[i][j]=2;
					return lineAction(type, i, true);
				}
				//�������� �� �����, ����������
				if (playerDangerLine[i][j]==1)
				{
					if (j==1)
						type=LineTypes.VERTICAL;
					else
						type=LineTypes.HORISONTAL;
					//������� �������, ��� ������ ��������� ���
					playerDangerLine[i][j]=2;
					return lineAction(type, i, false);
				}
			}
		
		//���������
		//���� ���� ���������� (������ ������� ���������)...
		if (this.checkMainDiag && playerDotsInMainDiagonal>=2)
			return lineAction(LineTypes.MAIN_DIAG, 0, true);
		//���� ���� ���������� (������ �������� ���������)...
		if (this.checkSideDiag && playerDotsInSideDiagonal>=2)
			return lineAction(LineTypes.SIDE_DIAG, 0, true);
		//���� ���� �������� (������ ������� ���������)...
		if (this.checkMainDiag && compDotsInMainDiagonal>=2)
			return lineAction(LineTypes.MAIN_DIAG, 0, false);		
		//���� ���� �������� (������ �������� ���������)...
		if (this.checkSideDiag && compDotsInSideDiagonal>=2)
			return lineAction(LineTypes.SIDE_DIAG, 0, false);
		
		//���� ������� �������� �� �������������
		//if (type==LineTypes.NO_ONE)
		return false;		
	}
	
	//�������� � ������� ��� �����������
	private boolean lineAction(LineTypes type, int line, boolean isProtection)
	{
		//���������
		if (type==LineTypes.MAIN_DIAG || type==LineTypes.SIDE_DIAG)
		{
			//��������� ���� ������
			for (int i = 0; i < this.size; i++)
				for (int j = 0; j < this.size; j++)
					//���� ����� ���� ������ � ����� �� ������ ���������
					if (this.game.gameFieldButtons[i][j].getVal() == ButtonsVals.EMPTY
							&& ((i == j && type == LineTypes.MAIN_DIAG) || (i==this.size-j-1 && type == LineTypes.SIDE_DIAG))) 
					{
						//������ ��� ���� �����
						this.game.gameFieldButtons[i][j].drawButton(false, this.playerX);
						debugOutput(i, j, DebugOutputTypes.BLOCK_WIN, type, isProtection);
						return true;
					}
		}
		else
		{
			//�����. ���������� ����������: ���� ����� ������ � ����� �� ������ �����, ��������� ��
			for (int i = 0; i < this.size; i++)
			{
				//�������
				if (this.game.gameFieldButtons[i][line].getVal()==ButtonsVals.EMPTY && type==LineTypes.VERTICAL)
				{
					this.game.gameFieldButtons[i][line].drawButton(false, this.playerX);
					debugOutput(i, line, DebugOutputTypes.BLOCK_WIN, type, isProtection);
					return true;
				}
				//������
				else if (this.game.gameFieldButtons[line][i].getVal()==ButtonsVals.EMPTY && type==LineTypes.HORISONTAL)
				{
					this.game.gameFieldButtons[line][i].drawButton(false, this.playerX);
					debugOutput(i, line, DebugOutputTypes.BLOCK_WIN, type, isProtection);
					return true;
				}
			}
		}
		return false;
	}
	
	//��������� ��� �����
	void compTurnRandom()
	{
		int compCoordX=0;
		int compCoordY=0;
		do
		{
			compCoordX = this.rand.nextInt(this.size);
			compCoordY = this.rand.nextInt(this.size);
		} while (this.game.gameFieldButtons[compCoordX][compCoordY].getVal() != ButtonsVals.EMPTY);
		this.game.gameFieldButtons[compCoordX][compCoordY].drawButton(false, this.playerX);
		debugOutput(compCoordX, compCoordY, DebugOutputTypes.RANDOM, LineTypes.NO_ONE, false);
	}
	
	//���������� ����� � �������
	private void debugOutput(int i, int j, DebugOutputTypes actionType, LineTypes type, boolean isProtection)
	{
		if (DEBUG_OUTPUT)
		{
			switch (actionType)
			{
			case CENTER:
				System.out.print("������ � �����");
				break;
			case BLOCK_WIN:
				if (!isProtection)
					System.out.print("������� �� ");
				else
					System.out.print("������� ");
				switch (type)
				{
				case HORISONTAL:
					System.out.print("�������������� ����� ");
					break;
				case VERTICAL:
					System.out.print("������������ ����� ");
					break;
				case MAIN_DIAG:
					System.out.print("������� ��������� ");
					break;
				case SIDE_DIAG:
					System.out.print("�������� ��������� ");
					break;
				default:						
				}
				break;
			case ANGLE:
				System.out.print("������ � ���� ");
				break;
			case RANDOM:
				System.out.print("������ �������� ");
				break;
			}
			System.out.println("(" + (i + 1) + ", " + (j + 1) + ").");
		}
	}
}
