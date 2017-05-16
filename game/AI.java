package game;

import java.util.Random;

final class AI {
	//****************************************
	//false, если отладочный вывод в консоль не требуется.
	private final boolean DEBUG_OUTPUT = false;
	//****************************************
	private boolean playerX;
	private int size;
	private Game game;
	private Random rand = new Random();
	//Есть ли смысл проверять диагонали
	private boolean checkMainDiag=true;
	private boolean checkSideDiag=true;
	//Количество заполненных диагоналей центра массива с четной размерностью. Максимум наобходимо 2.
	private int filledCenterDiagonals = 0;
	
	AI(boolean playerX, int size, Game game)
	{
		this.playerX = playerX;
		this.size = size;
		this.game = game;
	}
	
	//"Умный" ход компа. Все проверки и действия в данной функции последовательны. При выполнении действия дальнейший проверки не проводятся.
	/*TODO: если свободные две боковых перпендикулярные линии и точка между ними, то в перую очередь надо ставить туда,
	а не просто в первый же свободный угол.
	TODO: если размерность поля четная и игрок решил вообще не заполнять центральный квардатик, то надо просто в горизонтальный или
	вертикальный ряд поставить две точки компа, а не заполнять его полностью. */
	public void compTurnAI()
	{
		//System.out.println("НОВЫЙ ХОД");
		ButtonsVals compDot, playerDot;

		compDot = GameButton.setVal(!playerX);
		playerDot = GameButton.setVal(playerX);

		// 0. Свободен ли центр (если размерность нечетная)
		if (this.size%2!=0 &&
				this.game.gameFieldButtons[this.size/2][this.size/2].getVal() == ButtonsVals.EMPTY)
		{
			this.game.gameFieldButtons[this.size/2][this.size/2].drawButton(false, this.playerX);
			debugOutput(this.size/2, this.size/2, DebugOutputTypes.CENTER, LineTypes.NO_ONE, false);
			return;
		}

		// 1. Свободен ли центр (если размерность четная). Ставится по диагонали от занятой и на одной из двух оставшихся.
		//Как-то тут не спасает xor...
		if (this.size % 2 == 0 && (fillCenter(this.size - 1, this.size - 1, this.size, this.size, playerDot)
				|| fillCenter(this.size - 1, this.size, this.size, this.size - 1, playerDot)
				|| fillCenter(this.size, this.size, this.size - 1, this.size - 1, playerDot)
				|| fillCenter(this.size, this.size - 1, this.size - 1, this.size, playerDot)))
			return;

		// 2. Выигрывает ли игрок или комп.
		//Самая сложная часть алгоритма: определение опасности для игрока и нападение или опасности для компа и защиты.
		if (protectionAndAttack(compDot, playerDot))
			return;

		// 3. Если центр занят и никто не выигрывает, ставим точку в первый же свободный угол
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

		// 4. Рандомный ход
		this.compTurnRandom();
	}
	
	//Заполняет центр массива с четной размерностью.
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
	
	/*Определяет, надо ли защищаться или намеренно атаковать, и вызывает функцию, отвечающую за действия атаки или защиты
	Конечно, эту функцию еще стоит доделать. В данном варианте она готова в плане функционала, а не в аккуратности кода
	и оптимизированности алгоритма. Думаю, некоторые действия, которые выполняются отдельно, 
	можно выполнять в одном и том же участке кода. Например, проверку диагоналей и столбцов запихнуть в один цикл
	или избавиться от "массива опасности".*/
	private boolean protectionAndAttack(ButtonsVals compDot, ButtonsVals playerDot)
	{
		//Количество точек игрока и компа на диагоналях
		int playerDotsInMainDiagonal=0;
		int compDotsInMainDiagonal=0;
		int playerDotsInSideDiagonal=0;
		int compDotsInSideDiagonal=0;
		//Массив количеств точек игрока и компа на каждой линии и столбце.
		//Первая строка массива - строки игрового массива, вторая - столбцы.
		//Каждый элемент строки данного массива - количество точек в линии игрового массива.
		int[][] playerDotsInLine = new int[size][2];
		int[][] compDotsInLine = new int[size][2];
		//"Массив опасности". Принцип такой же, кк и в массиве количества точек. 
		//0 - не определено (линия пустая), 1 - да (есть точки ТОЛЬКО игрока или ТОЛЬКО компа), 2 - нет (есть точки и игрока, и компа).
		int[][] playerDangerLine = new int[size][2];
		int[][] compDangerLine = new int[size][2];
		// Определяет тип линии, в котой будет делать ход.
		LineTypes type = LineTypes.NO_ONE;
		
		//ЛИНИИ
		for (int i = 0; i < this.size; i++)
			for (int j = 0; j < this.size; j++)
			{
				//СТРОКИ
				//проверяем строку, если 0 (точек до этого не было) или 1 (есть/была опасность) в "массиве опасности" компа или игрока
				if (playerDangerLine[i][0]!=2 || compDangerLine[i][0]!=2)
				{
					//подсчитываем, сколько точек игрока и компа на линии
					if (this.game.gameFieldButtons[i][j].getVal() == playerDot)
						playerDotsInLine[i][0]++;	//каждый элемент массива количества точек, обозначающий строку, ++
					else if (this.game.gameFieldButtons[i][j].getVal() == compDot)
						compDotsInLine[i][0]++;
					//если есть точки и компа, и игрока, то опасности нет, ставим 2 (безопасная линия)
					//в каждый элемент массива опасности, обозначающий строку
					if (playerDotsInLine[i][0]>0 && compDotsInLine[i][0]>0)
					{
						playerDangerLine[i][0]=2;
						compDangerLine[i][0]=2;					
					}
					//если есть точки компа, но нет точек игрока, ставим 1 (опасность есть) игроку
					else if (playerDotsInLine[i][0]==0 && compDotsInLine[i][0]>1)
						playerDangerLine[i][0]=1;
					//аналогично
					else if (playerDotsInLine[i][0]>1 && compDotsInLine[i][0]==0)
						compDangerLine[i][0]=1;
					//если вообще никаких точек на линии нет, ставим обоим 0
					else
					{
						playerDangerLine[i][0]=0;
						compDangerLine[i][0]=0;
					}
				}
				//СТОБЦЫ (аналогично строкам)
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
		
		//ДИАГОНАЛИ
		for (int i = 0; i < this.size; i++)
			for (int j = 0; j < this.size; j++) 
			{
				//если есть смысл проверять диагональ (до этого была каким-то чудом пустой или занята точками ТОЛЬКО компа или ТОЛЬКО игрока)
				if (this.checkMainDiag)
				{
					if (this.game.gameFieldButtons[i][i].getVal() == playerDot)
						playerDotsInMainDiagonal++;
					else if (this.game.gameFieldButtons[i][i].getVal() == compDot)
						compDotsInMainDiagonal++;
					//если есть точки и компа, и игрока, то нет смысла дальше с ней что-то делать.
					if (playerDotsInMainDiagonal>0 && compDotsInMainDiagonal>0)
						this.checkMainDiag=false;
				}
				//аналогично
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
		//ОТЛАДОЧНЫЙ ВЫВОД
		if (DEBUG_OUTPUT) 
		{
			System.out.println("Точки игрока");
			for (int j = 0; j < 2; j++) 
			{
				for (int i = 0; i < this.size; i++)
					System.out.print(playerDotsInLine[i][j]);
				System.out.println();
			}
			System.out.println("Точки компа");
			for (int j = 0; j < 2; j++) 
			{
				for (int i = 0; i < this.size; i++)
					System.out.print(compDotsInLine[i][j]);
				System.out.println();
			}
			System.out.println("Опасность игрока");
			for (int j = 0; j < 2; j++) 
			{
				for (int i = 0; i < this.size; i++)
					System.out.print(playerDangerLine[i][j]);
				System.out.println();
			}
			System.out.println("Опасность компа");
			for (int j = 0; j < 2; j++) 
			{
				for (int i = 0; i < this.size; i++)
					System.out.print(compDangerLine[i][j]);
				System.out.println();
			}
			System.out.println();
		}
		//после всех проверок обнуляем пассив количества точек, так как в проверках происходит только инкремент.
		//ни в коем случае не обнуляем "массив опасности"!!!
		for (int i=0; i< this.size; i++)
			for (int j=0; j<2; j++)
			{
				playerDotsInLine[i][j]=0;
				compDotsInLine[i][j]=0;
			}
		
		//ЛИНИИ
		for (int i=0; i<size; i++)	//номер линии (элемент строки в таблице количества точек)
			for (int j=0; j<2; j++)	//номер строки таблицы - строки или столбы игрового массива 
			{
				//если для компа есть опасность, защищаем линию
				if (compDangerLine[i][j]==1)
				{
					if (j==1)
						type=LineTypes.VERTICAL;
					else
						type=LineTypes.HORISONTAL;
					//заранее говорим, что больше опасности нет
					compDangerLine[i][j]=2;
					return lineAction(type, i, true);
				}
				//Нападаем на линию, аналогично
				if (playerDangerLine[i][j]==1)
				{
					if (j==1)
						type=LineTypes.VERTICAL;
					else
						type=LineTypes.HORISONTAL;
					//заранее говорим, что больше опасности нет
					playerDangerLine[i][j]=2;
					return lineAction(type, i, false);
				}
			}
		
		//ДИАГОНАЛИ
		//Если надо защищаться (только главная диагональ)...
		if (this.checkMainDiag && playerDotsInMainDiagonal>=2)
			return lineAction(LineTypes.MAIN_DIAG, 0, true);
		//Если надо защищаться (только побочная диагональ)...
		if (this.checkSideDiag && playerDotsInSideDiagonal>=2)
			return lineAction(LineTypes.SIDE_DIAG, 0, true);
		//Если надо нападать (только главная диагональ)...
		if (this.checkMainDiag && compDotsInMainDiagonal>=2)
			return lineAction(LineTypes.MAIN_DIAG, 0, false);		
		//Если надо нападать (только побочная диагональ)...
		if (this.checkSideDiag && compDotsInSideDiagonal>=2)
			return lineAction(LineTypes.SIDE_DIAG, 0, false);
		
		//если никаких действий не предпринемали
		//if (type==LineTypes.NO_ONE)
		return false;		
	}
	
	//Действия с линиями или диагоналями
	private boolean lineAction(LineTypes type, int line, boolean isProtection)
	{
		//ДИАГОНАЛИ
		if (type==LineTypes.MAIN_DIAG || type==LineTypes.SIDE_DIAG)
		{
			//проверяем весь массив
			for (int i = 0; i < this.size; i++)
				for (int j = 0; j < this.size; j++)
					//если точка поля пустая и лежит на нужной диагонали
					if (this.game.gameFieldButtons[i][j].getVal() == ButtonsVals.EMPTY
							&& ((i == j && type == LineTypes.MAIN_DIAG) || (i==this.size-j-1 && type == LineTypes.SIDE_DIAG))) 
					{
						//ставим там свою точку
						this.game.gameFieldButtons[i][j].drawButton(false, this.playerX);
						debugOutput(i, j, DebugOutputTypes.BLOCK_WIN, type, isProtection);
						return true;
					}
		}
		else
		{
			//ЛИНИИ. Аналогично диагоналям: если точка пустая и лежит на нужной линии, заполняем ее
			for (int i = 0; i < this.size; i++)
			{
				//СТОЛБЦЫ
				if (this.game.gameFieldButtons[i][line].getVal()==ButtonsVals.EMPTY && type==LineTypes.VERTICAL)
				{
					this.game.gameFieldButtons[i][line].drawButton(false, this.playerX);
					debugOutput(i, line, DebugOutputTypes.BLOCK_WIN, type, isProtection);
					return true;
				}
				//СТРОКИ
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
	
	//Рандомный ход компа
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
	
	//Отладочный вывод в консоль
	private void debugOutput(int i, int j, DebugOutputTypes actionType, LineTypes type, boolean isProtection)
	{
		if (DEBUG_OUTPUT)
		{
			switch (actionType)
			{
			case CENTER:
				System.out.print("Ставлю в центр");
				break;
			case BLOCK_WIN:
				if (!isProtection)
					System.out.print("Нападаю на ");
				else
					System.out.print("Защищаю ");
				switch (type)
				{
				case HORISONTAL:
					System.out.print("горизонтальную линию ");
					break;
				case VERTICAL:
					System.out.print("вертикальную линию ");
					break;
				case MAIN_DIAG:
					System.out.print("главную диагональ ");
					break;
				case SIDE_DIAG:
					System.out.print("побочную диагональ ");
					break;
				default:						
				}
				break;
			case ANGLE:
				System.out.print("Ставлю в угол ");
				break;
			case RANDOM:
				System.out.print("Ставлю рандомно ");
				break;
			}
			System.out.println("(" + (i + 1) + ", " + (j + 1) + ").");
		}
	}
}
