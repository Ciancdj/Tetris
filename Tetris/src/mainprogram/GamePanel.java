package mainprogram;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
/**
* @author Cianc
* @version 创建时间：2018年9月16日 上午8:41:40
* @ClassName GamePanel
* @Description 生成游戏的图层
*/
public class GamePanel extends JPanel implements Runnable{
	
	// 表示可移动模块的类型
	private int style;
	private int styleOne;
	private int styleTwo;
	private int styleFour;
	private int styleFive;
	private int styleSix;
	// 判断是否结束
	private boolean isEnd = false;
	// 表示可移动方格的位置
	private int x[] = new int[4];
	private int y[] = new int[4];
	// 表示下一步将要移动的位置
	private int nextX[] = new int[4];
	private int nextY[] = new int[4];
	// 是否有可控制模块
	private boolean isControl = false;
	// 是否已经执行过一次键盘事件
	private boolean isLock = false;
	// 创建随机对象
	private Random rand = new Random();
	/**
	 * 初始化代码
	 */
	public GamePanel(){
		super.setBounds(0, 0, 600, 800);
		GameInformation.init(super.getHeight(), super.getWidth());
		for(int row = 0; row < GameInformation.NUM_ROW; row++) 
			for(int col = 0; col < GameInformation.NUM_COL; col++) 
				GameInformation.checkerBoard[row][col] = false;
	}
	
	/**
	 * 多线程运行函数
	 */
	@Override
	public void run() {
		/*
		 * true 处添加胜败条件
		 */
		while (!isEnd) {
			// 若没有可控制模块则随机创建模块	
			if (!isControl) {
				createBlock();
				isControl = true;
				for (int index = 0; index < 4; index++) {
					nextX[index] = x[index];
					nextY[index] = y[index];
					}
			}
			for (int index = 0; index < 4; index++) {
				nextY[index]++;
			}
			if (isMove(nextX,nextY)) {
				/*
				 * 若IsMove为true则表示该可移动方块下一步可移动
				 */
				for (int index = 0; index < 4; index++) {
					y[index] = nextY[index];
				}
			}
			else {
				/* 
				 * 若是可移动方块往下一步会触碰到其他的方块
				 * 则表示可移动方块达到可固定区域
				 * 这时候将可移动方块放置棋盘
				 */
				for (int index = 0; index < 4; index++) {
					GameInformation.checkerBoard[y[index]][x[index]] = true;
				}
				isControl = false;
			}
			try {
				Thread.sleep(750);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			scanFill();
			this.repaint();
			isLock = true;
			addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (isLock) {
						switch(e.getKeyCode()) {
						// 当按下上箭头
						case KeyEvent.VK_UP: {
							changeBlock();
							break;
						}
						// 当按下下箭头
						case KeyEvent.VK_DOWN: {
							//执行两次，每次往下一格
							for (int i = 0; i < 2; i++) {
								for (int index = 0; index < 4; index++) {
									nextY[index] = y[index] + 1;
									// 判断在往下移动后是否可移动 
								}
								if (isMove(nextX,nextY)) {
									/*
									 * 表示接下来可以往下移动两格 
									 */
									for (int index = 0; index < 4; index++) {
										y[index] = nextY[index];
									}
								}
								else {
									/*
									 * 表示已经移动到最下端或着下端有物体占位
									 * 将下一步位置归回原位
									 */
									for (int index = 0; index < 4; index++) {
										nextX[index] = x[index];
										nextY[index] = y[index];
									}
									break;
								}
							}
							break;
						}
						// 当按下左箭头
						case KeyEvent.VK_LEFT: {
							for (int index = 0; index < 4; index++) {
								nextX[index] = x[index] - 1;
							}
							// 判断在往左移动后是否可移动
							if (isMove(nextX,nextY)) {
								/*
								 * 表示接下来可以往左移动 
								 */
								for (int index = 0; index < 4; index++) {
									x[index] = nextX[index];
								}
							}
							else {
								/*
								 * 表示已经移动到最左端或着左端有物体占位
								 * 将下一步位置归回原位
								 */
								for (int index = 0; index < 4; index++) {
									nextX[index] = x[index];
									nextY[index] = y[index];
								}
							}
							break;
						}
						// 当按下右箭头
						case KeyEvent.VK_RIGHT: {
							for (int index = 0; index < 4; index++) {
								nextX[index] = x[index] + 1;
								// 判断在往左移动后是否可移动
							}
							if (isMove(nextX,nextY)) {
								/*
								 * 表示接下来可以往左移动 
								 */
								for (int index = 0; index < 4; index++) {
									x[index] = nextX[index];
								}
							}
							else {
								/*
								 * 表示已经移动到最左端或着左端有物体占位
								 * 将下一步位置归回原位
								 */
								for (int index = 0; index < 4; index++) {
									nextX[index] = x[index];
									nextY[index] = y[index];
									}
								}
							break;
							}
						}
					}
					isLock = false;
				}
			});
		}
	}
	
	/**
	 * 扫描棋盘中是否有已满的一行或着棋盘是否高于了第四行
	 * 若是高于第四行程序结束
	 * 若是已满则执行条件判断代码
	 * 若是没有满则无视
	 */
	private void scanFill() {
		// 统计目前得分得次数
		int numGetPoint = 0;
		// 自棋盘的底端开始检查
		for (int index = GameInformation.NUM_ROW - 1; 
				index >= GameInformation.GAME_OVER_HEIGHT; index--) {
			if(index == GameInformation.GAME_OVER_HEIGHT) {
				for (int i = 0; i < GameInformation.NUM_COL; i++) {
					if(GameInformation.checkerBoard[index][i]) {
						// 以下打开游戏失败函数
						isEnd = true;
						Frame.gameOverImage();
					}
				}
			}
			else {
				/* 
				 * 从每行的开头和杰为进行检查
				 * 如果有一个在棋盘中为 false 则跳出循环
				 */
				int left = 0;
				int right = GameInformation.NUM_COL - 1;
				while (left <= right) {
					if (!(GameInformation.checkerBoard[index][left]
							&& GameInformation.checkerBoard[index][right]))
						break;
					else { left++;right--;}
				}
				// 这时表示本行的方块为满格的状态，这时候进行清空
				if (left > right) {
					numGetPoint++;
					for (int i = index; i > 0; i--) 
						for (int j = 0; j < GameInformation.NUM_COL; j++) {
							GameInformation.checkerBoard[i][j] = 
									GameInformation.checkerBoard[i - 1][j];
							GameInformation.checkerBoard[i - 1][j] = false;
						}
				}
			}
		}
		// 以下为得分函数执行
		if (numGetPoint != 0) {
			getPoint(numGetPoint);
		}
	}
	
	/**
	 * 得分函数
	 * @param numGetPoint
	 */
	private void getPoint(int numGetPoint) {
		GameInformation.allPoint += numGetPoint *
				GameInformation.GET_POINT;
		Frame.changePointList();
	}

	/**
	 * 判断若是可变方块经过变化后是否在棋盘上有空位
	 * @param col
	 * @param row
	 * @return boolean
	 */
	private boolean isMove(int col[],int row[]) {
		for (int index = 0; index < 4; index++) {
			// 判断是否可移动方格超出左右与下边界
			if (row[index] >= GameInformation.NUM_ROW - 1 || 
					(col[index]<0 || col[index]>=GameInformation.NUM_COL))
				return false;
			// 判断可移动方格的下一步移动位置是否有占位的情况
			if (GameInformation.checkerBoard[row[index]][col[index]]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 制图
	 * @param g
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.red);
		// 绘制以固定的所有的方块
		for (int row = 0; row < GameInformation.NUM_ROW; row++) 
			for (int col = 0; col < GameInformation.NUM_COL; col++) 
				if(GameInformation.checkerBoard[row][col]) 
					g.fillRect(col * GameInformation.lenCol, 
							row * GameInformation.lenRow, 
							GameInformation.lenCol, GameInformation.lenRow);
		// 绘制可动方块
		for (int index = 0; index < 4; index++) {
			g.fillRect(x[index] * GameInformation.lenCol, 
					y[index] * GameInformation.lenRow, 
					GameInformation.lenCol, GameInformation.lenRow);
		}
	}
	
	/**
	 * 当棋盘中没有可以移动的俄罗斯方块后
	 * 随机创建新的方块
	 */
	private void createBlock() {
		switch(rand.nextInt(11) + 1) {
		case 1: {
			x[0] = GameInformation.NUM_COL/2 - 1;
			x[1] = x[2] = x[3] = GameInformation.NUM_COL/2;
			y[0] = y[1] = 0;
			y[2] = 1;
			y[3] = 2;
			style = 1;
			styleOne = 1;
			break;
		}
		case 2: {
			x[0] = x[1] = GameInformation.NUM_COL/2 - 1;
			x[2] = GameInformation.NUM_COL/2;
			x[3] = GameInformation.NUM_COL/2 + 1;
			y[0] = 1;
			y[1] = y[2] = y[3] = 2;
			style = 2;
			styleTwo = 1;
			break;
		}
		case 3: {
			x[1] = x[2] = x[3] = GameInformation.NUM_COL/2;
			x[0] = GameInformation.NUM_COL/2 - 1;
			y[3] = 0;
			y[2] = 1;
			y[1] = y[0] = 2;
			style = 2;
			styleTwo = 2;
			break;
		}
		case 4: {
			x[0] = x[1] = GameInformation.NUM_COL/2 - 1;
			x[2] = GameInformation.NUM_COL/2;
			x[3] = GameInformation.NUM_COL/2 + 1;
			y[1] = y[2] = y[3] = 1;
			y[0] = 2;
			style = 1;
			styleOne = 2;
			break;
		}
		case 5: {
			x[0] = x[2] = GameInformation.NUM_COL/2 - 1;
			x[1] = x[3] = GameInformation.NUM_COL/2;
			y[0] = y[1] = 1;
			y[2] = y[3] = 2;
			style = 3;
			break;
		}
		case 6: {
			x[0] = x[1] = x[2] = x[3] = GameInformation.NUM_COL/2;
			y[0] = 0;
			y[1] = 1;
			y[2] = 2;
			y[3] = 3;
			style = 4;
			styleFour = 1;
			break;
		}
		case 7: {
			x[0] = GameInformation.NUM_COL/2 - 2;
			x[1] = GameInformation.NUM_COL/2 - 1;
			x[2] = GameInformation.NUM_COL/2;
			x[3] = GameInformation.NUM_COL/2 + 1;
			y[0] = y[1] = y[2] = y[3] = 2;
			style = 4;
			styleFour = 2;
			break;
		}
		case 8: {
			x[0] = x[1] = GameInformation.NUM_COL/2;
			x[2] = x[3] = GameInformation.NUM_COL/2 - 1;
			y[0] = 0;
			y[1] = y[2] = 1;
			y[3] = 2;
			style = 5;
			styleFive = 1;
			break;
		}
		case 9: {
			x[3] = GameInformation.NUM_COL/2 - 1;
			x[1] = x[2] = GameInformation.NUM_COL/2;
			x[0] = GameInformation.NUM_COL/2 + 1;
			y[2] = y[3] = 1;
			y[1] = y[0] = 2;
			style = 5;
			styleFive = 2;
			break;
		}
		case 10: {
			x[0] = x[1] = GameInformation.NUM_COL/2;
			x[2] = x[3] = GameInformation.NUM_COL/2 - 1;
			y[0] = 2;
			y[1] = y[2] = 1;
			y[3] = 0;
			style = 6;
			styleSix = 1;
			break;
		}
		case 11: {
			x[0] = GameInformation.NUM_COL/2 + 1;
			x[1] = x[2] = GameInformation.NUM_COL/2;
			x[3] = GameInformation.NUM_COL/2 - 1;
			y[0] = y[2] = 1;
			y[1] = y[3] = 2;
			style = 6;
			styleSix = 2;
			break;
		}
		}
	}
	
	/**
	 * 改变可移动方块的模样
	 */
	private void changeBlock() {
		// 判断目前的可移动模块的类型属于哪一个类别
		int tempStyle = 0;
		switch(style) {
		case 1: {
			tempStyle = styleOne;
			// 单纯的用于计数，若是可变则强制跳出，否者不变
			for (int i = 0; i < 4; i++) {
				tempStyle++;
				if (tempStyle > 4) {
					tempStyle -= 4;
				}
				if(tempStyle == 1) {
					nextX[0] -= 1;
					nextX[2] += 1;
					nextX[3] += 2;
					nextY[1] -= 1;
					nextY[3] += 1;
					// 判断在变化之后是否会有超出和占位的情况
					if (isMove(nextX,nextY)) {
						// 表示可以进行变化
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// 表示在变化后会出现错误
						continue;
					}
				}
				else if(tempStyle == 2) {
					nextX[1] -= 1;
					nextX[3] += 1;
					nextY[0] += 1;
					nextY[2] -= 1;
					nextY[3] -= 2;
					// 判断在变化之后是否会有超出和占位的情况
					if (isMove(nextX,nextY)) {
						// 表示可以进行变化
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// 表示在变化后会出现错误
						continue;
					}
				}
				else if(tempStyle == 3) {
					nextX[0] += 1;
					nextX[2] -= 1;
					nextX[3] -= 2;
					nextY[1] += 1;
					nextY[3] -= 1;
					// 判断在变化之后是否会有超出和占位的情况
					if (isMove(nextX,nextY)) {
						// 表示可以进行变化
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// 表示在变化后会出现错误
						continue;
					}
				}
				else {
					nextX[1] += 1;
					nextX[3] -= 1;
					nextY[0] -= 1;
					nextY[2] += 1;
					nextY[3] += 2;
					// 判断在变化之后是否会有超出和占位的情况
					if (isMove(nextX,nextY)) {
						// 表示可以进行变化
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// 表示在变化后会出现错误
						continue;
					}
				}
			}
			styleOne = tempStyle;
			break;
		}
		case 2: {
			tempStyle = styleTwo;
			// 单纯的用于计数，若是可变则强制跳出，否者不变
			for (int i = 0; i < 4; i++) {
				tempStyle++;
				if (tempStyle > 4) {
					tempStyle -= 4;
				}
				if(tempStyle == 1) {
					nextX[0] -= 1;
					nextX[2] += 1;
					nextX[3] += 2;
					nextY[1] += 1;
					nextY[3] -= 1;
					// 判断在变化之后是否会有超出和占位的情况
					if (isMove(nextX,nextY)) {
						// 表示可以进行变化
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// 表示在变化后会出现错误
						continue;
					}
				}
				else if(tempStyle == 2) {
					nextX[1] += 1;
					nextX[3] -= 1;
					nextY[0] += 1;
					nextY[2] -= 1;
					nextY[3] -= 2;
					// 判断在变化之后是否会有超出和占位的情况
					if (isMove(nextX,nextY)) {
						// 表示可以进行变化
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// 表示在变化后会出现错误
						continue;
					}
				}
				else if(tempStyle == 3) {
					nextX[0] += 1;
					nextX[2] -= 1;
					nextX[3] -= 2;
					nextY[1] -= 1;
					nextY[3] += 1;
					// 判断在变化之后是否会有超出和占位的情况
					if (isMove(nextX,nextY)) {
						// 表示可以进行变化
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// 表示在变化后会出现错误
						continue;
					}
				}
				else {
					nextX[1] -= 1;
					nextX[3] += 1;
					nextY[0] -= 1;
					nextY[2] += 1;
					nextY[3] += 2;
					// 判断在变化之后是否会有超出和占位的情况
					if (isMove(nextX,nextY)) {
						// 表示可以进行变化
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// 表示在变化后会出现错误
						continue;
					}
				}
			}
			styleTwo = tempStyle;
			break;
		}
		case 4: {
			tempStyle = styleFour;
			if (tempStyle == 1) {
				nextX[0] -= 1;
				nextX[2] += 1;
				nextX[3] += 2;
				nextY[0] += 1;
				nextY[2] -= 1;
				nextY[3] -= 2;
				// 判断变化后是否会出现占位，超格的情况
				if (isMove(nextX,nextY)){
					// 表示可以进行变化
					for (int index = 0; index < 4; index++) {
						y[index] = nextY[index];
						x[index] = nextX[index];
						styleFour = 2;
					}
				}
				else {
					// 将下一步的变化回归
					for (int index = 0; index < 4; index++) {
						nextY[index] = y[index];
						nextX[index] = x[index];
					}
				}
			}
			else {
				nextX[0] += 1;
				nextX[2] -= 1;
				nextX[3] -= 2;
				nextY[0] -= 1;
				nextY[2] += 1;
				nextY[3] += 2;
				// 判断变化后是否会出现占位，超格的情况
				if (isMove(nextX,nextY)){
					// 表示可以进行变化
					for (int index = 0; index < 4; index++) {
						y[index] = nextY[index];
						x[index] = nextX[index];
						styleFour = 1;
					}
				}
				else {
					// 将下一步的变化回归
					for (int index = 0; index < 4; index++) {
						nextY[index] = y[index];
						nextX[index] = x[index];
					}
				}
			}
			break;
		}
		case 5: {
			tempStyle = styleFive;
			if (tempStyle == 1) {
				nextX[0] += 1;
				nextX[2] += 1;
				nextY[0] += 1;
				nextY[2] -= 1;
				nextY[3] -= 2;
				// 判断变化后是否会出现占位，超格的情况
				if (isMove(nextX,nextY)){
					// 表示可以进行变化
					for (int index = 0; index < 4; index++) {
						y[index] = nextY[index];
						x[index] = nextX[index];
						styleFive = 2;
					}
				}
				else {
					// 将下一步的变化回归
					for (int index = 0; index < 4; index++) {
						nextY[index] = y[index];
						nextX[index] = x[index];
					}
				}
			}
			else {
				nextX[0] -= 1;
				nextX[2] -= 1;
				nextY[0] -= 1;
				nextY[2] += 1;
				nextY[3] += 2;
				// 判断变化后是否会出现占位，超格的情况
				if (isMove(nextX,nextY)){
					// 表示可以进行变化
					for (int index = 0; index < 4; index++) {
						y[index] = nextY[index];
						x[index] = nextX[index];
						styleFive = 1;
					}
				}
				else {
					// 将下一步的变化回归
					for (int index = 0; index < 4; index++) {
						nextY[index] = y[index];
						nextX[index] = x[index];
					}
				}
			}
			break;
		}
		case 6: {
			tempStyle = styleSix;
			if (tempStyle == 1) {
				nextX[0] += 1;
				nextX[2] += 1;
				nextY[0] -= 2;
				nextY[2] -= 1;
				nextY[3] += 1;
				// 判断变化后是否会出现占位，超格的情况
				if (isMove(nextX,nextY)){
					// 表示可以进行变化
					for (int index = 0; index < 4; index++) {
						y[index] = nextY[index];
						x[index] = nextX[index];
						styleSix = 2;
					}
				}
				else {
					// 将下一步的变化回归
					for (int index = 0; index < 4; index++) {
						nextY[index] = y[index];
						nextX[index] = x[index];
					}
				}
			}
			else {
				nextX[0] -= 1;
				nextX[2] -= 1;
				nextY[0] += 2;
				nextY[2] += 1;
				nextY[3] -= 1;
				// 判断变化后是否会出现占位，超格的情况
				if (isMove(nextX,nextY)){
					// 表示可以进行变化
					for (int index = 0; index < 4; index++) {
						y[index] = nextY[index];
						x[index] = nextX[index];
						styleSix = 1;
					}
				}
				else {
					// 将下一步的变化回归
					for (int index = 0; index < 4; index++) {
						nextY[index] = y[index];
						nextX[index] = x[index];
					}
				}
			}
			break;
		}
		}
	}

}
