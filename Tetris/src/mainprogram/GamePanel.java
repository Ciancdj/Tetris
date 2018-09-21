package mainprogram;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
/**
* @author Cianc
* @version ����ʱ�䣺2018��9��16�� ����8:41:40
* @ClassName GamePanel
* @Description ������Ϸ��ͼ��
*/
public class GamePanel extends JPanel implements Runnable{
	
	// ��ʾ���ƶ�ģ�������
	private int style;
	private int styleOne;
	private int styleTwo;
	private int styleFour;
	private int styleFive;
	private int styleSix;
	// �ж��Ƿ����
	private boolean isEnd = false;
	// ��ʾ���ƶ������λ��
	private int x[] = new int[4];
	private int y[] = new int[4];
	// ��ʾ��һ����Ҫ�ƶ���λ��
	private int nextX[] = new int[4];
	private int nextY[] = new int[4];
	// �Ƿ��пɿ���ģ��
	private boolean isControl = false;
	// �Ƿ��Ѿ�ִ�й�һ�μ����¼�
	private boolean isLock = false;
	// �����������
	private Random rand = new Random();
	/**
	 * ��ʼ������
	 */
	public GamePanel(){
		super.setBounds(0, 0, 600, 800);
		GameInformation.init(super.getHeight(), super.getWidth());
		for(int row = 0; row < GameInformation.NUM_ROW; row++) 
			for(int col = 0; col < GameInformation.NUM_COL; col++) 
				GameInformation.checkerBoard[row][col] = false;
	}
	
	/**
	 * ���߳����к���
	 */
	@Override
	public void run() {
		/*
		 * true �����ʤ������
		 */
		while (!isEnd) {
			// ��û�пɿ���ģ�����������ģ��	
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
				 * ��IsMoveΪtrue���ʾ�ÿ��ƶ�������һ�����ƶ�
				 */
				for (int index = 0; index < 4; index++) {
					y[index] = nextY[index];
				}
			}
			else {
				/* 
				 * ���ǿ��ƶ���������һ���ᴥ���������ķ���
				 * ���ʾ���ƶ�����ﵽ�ɹ̶�����
				 * ��ʱ�򽫿��ƶ������������
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
						// �������ϼ�ͷ
						case KeyEvent.VK_UP: {
							changeBlock();
							break;
						}
						// �������¼�ͷ
						case KeyEvent.VK_DOWN: {
							//ִ�����Σ�ÿ������һ��
							for (int i = 0; i < 2; i++) {
								for (int index = 0; index < 4; index++) {
									nextY[index] = y[index] + 1;
									// �ж��������ƶ����Ƿ���ƶ� 
								}
								if (isMove(nextX,nextY)) {
									/*
									 * ��ʾ���������������ƶ����� 
									 */
									for (int index = 0; index < 4; index++) {
										y[index] = nextY[index];
									}
								}
								else {
									/*
									 * ��ʾ�Ѿ��ƶ������¶˻����¶�������ռλ
									 * ����һ��λ�ù��ԭλ
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
						// ���������ͷ
						case KeyEvent.VK_LEFT: {
							for (int index = 0; index < 4; index++) {
								nextX[index] = x[index] - 1;
							}
							// �ж��������ƶ����Ƿ���ƶ�
							if (isMove(nextX,nextY)) {
								/*
								 * ��ʾ���������������ƶ� 
								 */
								for (int index = 0; index < 4; index++) {
									x[index] = nextX[index];
								}
							}
							else {
								/*
								 * ��ʾ�Ѿ��ƶ�������˻������������ռλ
								 * ����һ��λ�ù��ԭλ
								 */
								for (int index = 0; index < 4; index++) {
									nextX[index] = x[index];
									nextY[index] = y[index];
								}
							}
							break;
						}
						// �������Ҽ�ͷ
						case KeyEvent.VK_RIGHT: {
							for (int index = 0; index < 4; index++) {
								nextX[index] = x[index] + 1;
								// �ж��������ƶ����Ƿ���ƶ�
							}
							if (isMove(nextX,nextY)) {
								/*
								 * ��ʾ���������������ƶ� 
								 */
								for (int index = 0; index < 4; index++) {
									x[index] = nextX[index];
								}
							}
							else {
								/*
								 * ��ʾ�Ѿ��ƶ�������˻������������ռλ
								 * ����һ��λ�ù��ԭλ
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
	 * ɨ���������Ƿ���������һ�л��������Ƿ�����˵�����
	 * ���Ǹ��ڵ����г������
	 * ����������ִ�������жϴ���
	 * ����û����������
	 */
	private void scanFill() {
		// ͳ��Ŀǰ�÷ֵô���
		int numGetPoint = 0;
		// �����̵ĵ׶˿�ʼ���
		for (int index = GameInformation.NUM_ROW - 1; 
				index >= GameInformation.GAME_OVER_HEIGHT; index--) {
			if(index == GameInformation.GAME_OVER_HEIGHT) {
				for (int i = 0; i < GameInformation.NUM_COL; i++) {
					if(GameInformation.checkerBoard[index][i]) {
						// ���´���Ϸʧ�ܺ���
						isEnd = true;
						Frame.gameOverImage();
					}
				}
			}
			else {
				/* 
				 * ��ÿ�еĿ�ͷ�ͽ�Ϊ���м��
				 * �����һ����������Ϊ false ������ѭ��
				 */
				int left = 0;
				int right = GameInformation.NUM_COL - 1;
				while (left <= right) {
					if (!(GameInformation.checkerBoard[index][left]
							&& GameInformation.checkerBoard[index][right]))
						break;
					else { left++;right--;}
				}
				// ��ʱ��ʾ���еķ���Ϊ�����״̬����ʱ��������
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
		// ����Ϊ�÷ֺ���ִ��
		if (numGetPoint != 0) {
			getPoint(numGetPoint);
		}
	}
	
	/**
	 * �÷ֺ���
	 * @param numGetPoint
	 */
	private void getPoint(int numGetPoint) {
		GameInformation.allPoint += numGetPoint *
				GameInformation.GET_POINT;
		Frame.changePointList();
	}

	/**
	 * �ж����ǿɱ䷽�龭���仯���Ƿ����������п�λ
	 * @param col
	 * @param row
	 * @return boolean
	 */
	private boolean isMove(int col[],int row[]) {
		for (int index = 0; index < 4; index++) {
			// �ж��Ƿ���ƶ����񳬳��������±߽�
			if (row[index] >= GameInformation.NUM_ROW - 1 || 
					(col[index]<0 || col[index]>=GameInformation.NUM_COL))
				return false;
			// �жϿ��ƶ��������һ���ƶ�λ���Ƿ���ռλ�����
			if (GameInformation.checkerBoard[row[index]][col[index]]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ��ͼ
	 * @param g
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.red);
		// �����Թ̶������еķ���
		for (int row = 0; row < GameInformation.NUM_ROW; row++) 
			for (int col = 0; col < GameInformation.NUM_COL; col++) 
				if(GameInformation.checkerBoard[row][col]) 
					g.fillRect(col * GameInformation.lenCol, 
							row * GameInformation.lenRow, 
							GameInformation.lenCol, GameInformation.lenRow);
		// ���ƿɶ�����
		for (int index = 0; index < 4; index++) {
			g.fillRect(x[index] * GameInformation.lenCol, 
					y[index] * GameInformation.lenRow, 
					GameInformation.lenCol, GameInformation.lenRow);
		}
	}
	
	/**
	 * ��������û�п����ƶ��Ķ���˹�����
	 * ��������µķ���
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
	 * �ı���ƶ������ģ��
	 */
	private void changeBlock() {
		// �ж�Ŀǰ�Ŀ��ƶ�ģ�������������һ�����
		int tempStyle = 0;
		switch(style) {
		case 1: {
			tempStyle = styleOne;
			// ���������ڼ��������ǿɱ���ǿ�����������߲���
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
					// �ж��ڱ仯֮���Ƿ���г�����ռλ�����
					if (isMove(nextX,nextY)) {
						// ��ʾ���Խ��б仯
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// ��ʾ�ڱ仯�����ִ���
						continue;
					}
				}
				else if(tempStyle == 2) {
					nextX[1] -= 1;
					nextX[3] += 1;
					nextY[0] += 1;
					nextY[2] -= 1;
					nextY[3] -= 2;
					// �ж��ڱ仯֮���Ƿ���г�����ռλ�����
					if (isMove(nextX,nextY)) {
						// ��ʾ���Խ��б仯
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// ��ʾ�ڱ仯�����ִ���
						continue;
					}
				}
				else if(tempStyle == 3) {
					nextX[0] += 1;
					nextX[2] -= 1;
					nextX[3] -= 2;
					nextY[1] += 1;
					nextY[3] -= 1;
					// �ж��ڱ仯֮���Ƿ���г�����ռλ�����
					if (isMove(nextX,nextY)) {
						// ��ʾ���Խ��б仯
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// ��ʾ�ڱ仯�����ִ���
						continue;
					}
				}
				else {
					nextX[1] += 1;
					nextX[3] -= 1;
					nextY[0] -= 1;
					nextY[2] += 1;
					nextY[3] += 2;
					// �ж��ڱ仯֮���Ƿ���г�����ռλ�����
					if (isMove(nextX,nextY)) {
						// ��ʾ���Խ��б仯
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// ��ʾ�ڱ仯�����ִ���
						continue;
					}
				}
			}
			styleOne = tempStyle;
			break;
		}
		case 2: {
			tempStyle = styleTwo;
			// ���������ڼ��������ǿɱ���ǿ�����������߲���
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
					// �ж��ڱ仯֮���Ƿ���г�����ռλ�����
					if (isMove(nextX,nextY)) {
						// ��ʾ���Խ��б仯
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// ��ʾ�ڱ仯�����ִ���
						continue;
					}
				}
				else if(tempStyle == 2) {
					nextX[1] += 1;
					nextX[3] -= 1;
					nextY[0] += 1;
					nextY[2] -= 1;
					nextY[3] -= 2;
					// �ж��ڱ仯֮���Ƿ���г�����ռλ�����
					if (isMove(nextX,nextY)) {
						// ��ʾ���Խ��б仯
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// ��ʾ�ڱ仯�����ִ���
						continue;
					}
				}
				else if(tempStyle == 3) {
					nextX[0] += 1;
					nextX[2] -= 1;
					nextX[3] -= 2;
					nextY[1] -= 1;
					nextY[3] += 1;
					// �ж��ڱ仯֮���Ƿ���г�����ռλ�����
					if (isMove(nextX,nextY)) {
						// ��ʾ���Խ��б仯
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// ��ʾ�ڱ仯�����ִ���
						continue;
					}
				}
				else {
					nextX[1] -= 1;
					nextX[3] += 1;
					nextY[0] -= 1;
					nextY[2] += 1;
					nextY[3] += 2;
					// �ж��ڱ仯֮���Ƿ���г�����ռλ�����
					if (isMove(nextX,nextY)) {
						// ��ʾ���Խ��б仯
						for (int index = 0; index < 4; index++) {
							y[index] = nextY[index];
							x[index] = nextX[index];
						}
						break;
					}
					else {
					// ��ʾ�ڱ仯�����ִ���
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
				// �жϱ仯���Ƿ�����ռλ����������
				if (isMove(nextX,nextY)){
					// ��ʾ���Խ��б仯
					for (int index = 0; index < 4; index++) {
						y[index] = nextY[index];
						x[index] = nextX[index];
						styleFour = 2;
					}
				}
				else {
					// ����һ���ı仯�ع�
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
				// �жϱ仯���Ƿ�����ռλ����������
				if (isMove(nextX,nextY)){
					// ��ʾ���Խ��б仯
					for (int index = 0; index < 4; index++) {
						y[index] = nextY[index];
						x[index] = nextX[index];
						styleFour = 1;
					}
				}
				else {
					// ����һ���ı仯�ع�
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
				// �жϱ仯���Ƿ�����ռλ����������
				if (isMove(nextX,nextY)){
					// ��ʾ���Խ��б仯
					for (int index = 0; index < 4; index++) {
						y[index] = nextY[index];
						x[index] = nextX[index];
						styleFive = 2;
					}
				}
				else {
					// ����һ���ı仯�ع�
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
				// �жϱ仯���Ƿ�����ռλ����������
				if (isMove(nextX,nextY)){
					// ��ʾ���Խ��б仯
					for (int index = 0; index < 4; index++) {
						y[index] = nextY[index];
						x[index] = nextX[index];
						styleFive = 1;
					}
				}
				else {
					// ����һ���ı仯�ع�
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
				// �жϱ仯���Ƿ�����ռλ����������
				if (isMove(nextX,nextY)){
					// ��ʾ���Խ��б仯
					for (int index = 0; index < 4; index++) {
						y[index] = nextY[index];
						x[index] = nextX[index];
						styleSix = 2;
					}
				}
				else {
					// ����һ���ı仯�ع�
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
				// �жϱ仯���Ƿ�����ռλ����������
				if (isMove(nextX,nextY)){
					// ��ʾ���Խ��б仯
					for (int index = 0; index < 4; index++) {
						y[index] = nextY[index];
						x[index] = nextX[index];
						styleSix = 1;
					}
				}
				else {
					// ����һ���ı仯�ع�
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
