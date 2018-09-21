package mainprogram;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.text.DateFormat;

class Score implements Serializable{
	static final long serialVerisionUID = 3056L;
	String date;
	int score;
}

class Frame extends JFrame{
/**
* @author Cianc
* @version 1.0
*/	
	private Container mainContainer;
	private static GamePanel gamePanel;
	private static JTextField textField;
	public static Thread threadGame;
	public static JPanel mainPanel;
	/**
	 * ��ʼ�����棬�����������ó���.
	 */
	public Frame(){
		super("����˹����");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(400, 100, 1000, 800);
		mainContainer = this.getContentPane();
		mainPanel = new JPanel();
		mainPanel.setBackground(Color.black);
		mainPanel.setLayout(null);
		startImage();
		mainContainer.add(mainPanel);
		this.setVisible(true);
	}
	
	/**
	 * ��ʼ����.
	 */
	private static void startImage() {
		mainPanel.updateUI();
		JPanel leftPanel = new JPanel();
		leftPanel.setBounds(0, 0, 600, 800);
		leftPanel.setBackground(Color.white);
		leftPanel.setLayout(null);
		Button gameStartButton = new Button("Game Start");
		gameStartButton.setFont(new Font("Arial",Font.BOLD,70));
	 	gameStartButton.setBounds(50,250,500,200);
		gameStartButton.setBackground(Color.white);
		gameStartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO �Զ����ɵķ������
				mainPanel.removeAll();
				initGame();
			}
			
		});
		leftPanel.add(gameStartButton);
		mainPanel.add(leftPanel);
		sideNav(0);
	}
	
	/**
	 * ��Ϸ��ߵ���ʾ��
	 */
	private static void sideNav(int getPoint) {
		JPanel sidePanel = new JPanel();
		sidePanel.setBounds(600,0,400,800);
		sidePanel.setBackground(Color.gray);
		sidePanel.setLayout(null);
		textField = new JTextField(Integer.toString(getPoint));
		JTextArea textArea = new JTextArea("       Welcome to the Tetris\n"
				+ "     �� Move Left\n"
				+ "     �� Move Right\n"
				+ "     �� Switch Style\n"
				+ "     �� Speed Down");
		// �÷ֿ�����
		textField.setBounds(0, 0, 400, 200);
		textField.setFont(new Font("Arial",Font.BOLD,100));
		textField.setHorizontalAlignment(JLabel.CENTER);
		textField.setEditable(false);
		textField.setBackground(Color.gray);
		// �ı�����
		textArea.setBounds(0, 550, 400, 350);
		textArea.setFont(new Font("Arial",Font.ITALIC,30));
		textArea.setEditable(false);
		textArea.setBackground(Color.gray);
		sidePanel.add(textField);
		sidePanel.add(textArea);
		mainPanel.add(sidePanel);
	}
	
	/**
	 * ���óɼ���ȡ����Ȼ������ж�
	 * ��󽫳ɼ��б���
	 * @param scoreIn
	 * @return
	 */
	private static Score[] scoreBoard(Score scoreIn) {
		Score[] score = scoreRead();
		int scoreTemp;
		String dateTemp;
		//��������봫�����ֵ���бȽ�
		if (score[score.length - 1].score < scoreIn.score) {
			//�ȽϽ���д��ڵģ�����С�Ľ��и���
			score[score.length - 1] = scoreIn;
			//���Ǻ����д�� 
			for (int index = 0; index < score.length; index++) 
				for (int j = 0; j < score.length - index - 1; j++) 
					if (score[j].score < score[j + 1].score) {
						scoreTemp = score[j].score;
						dateTemp = score[j].date;
						score[j].score = score[j + 1].score;
						score[j].date = score[j + 1].date;
						score[j + 1].score = scoreTemp;
						score[j + 1].date = dateTemp;
					}
			scoreWrite(score);
		}
		return score;
	}
	
	/**
	 * �Գɼ����ж�ȡ
	 * @return score[]
	 */
	private static Score[] scoreRead() {
		Score[] score = null;
		boolean judge = false;
		try {
			ObjectInputStream scoreFile = new ObjectInputStream(
					new FileInputStream("ScoreObject.dat"));
			score = (Score[])(scoreFile.readObject());
			scoreFile.close();
		} catch(FileNotFoundException e) {
			score = new Score[10];
			for (int index = 0; index < 10; index++) {
				score[index] = new Score();
				score[index].score = 0;
				score[index].date = "null";
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			return score;
		} 
	}
	
	/**
	 * �����Գɼ�����д��
	 * @param score
	 */
	private static void scoreWrite(Score[] score) {
		try {
			ObjectOutputStream scoreFile = new ObjectOutputStream(
					new FileOutputStream("ScoreObject.dat"));
			for (int index = 0; index < score.length; index++) {
				System.out.println(score[index].score + " " + score[index].date );
			}
			scoreFile.writeObject(score);
			scoreFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��Ϸ��������
	 */
	public static void gameOverImage() {
		mainPanel.removeAll();
		JPanel overPanel = new JPanel();
		String scoreString = null;
		Date date = new Date();
		// �����ʼ��
		overPanel.setBounds(0, 0, 600, 800);
		overPanel.setBackground(Color.white);
		overPanel.setLayout(null);
		// �������ݼ���
		Score scoreIn = new Score();
		DateFormat df = DateFormat.getDateTimeInstance();
		scoreIn.score = Integer.valueOf(textField.getText());
		scoreIn.date = df.format(date);
		// ���ݰ��ʼ��
		Score score[] = scoreBoard(scoreIn);
		// GameOver����
		JTextField[] overTextField = new JTextField[12];
		overTextField[0] = new JTextField("Game Over!",15);
		overTextField[1] = new JTextField("-> ScoreBoard <- ");
		// Ϊ���ְ��еø����ַ�������
		int height = 125;
		// ���б��е����е�����ת��Ϊ�ַ���
		for (int index = 2, j = 0; index < overTextField.length;
				index++, j++) {
			scoreString = Integer.toString((j+1)) + " .      " +
					Integer.toString(score[j].score) + "   " +
					score[j].date;
			overTextField[index] = new JTextField(scoreString);
			overTextField[index].setFont(
					new Font("Arial",Font.ITALIC,20));
			overTextField[index].setBounds(0, height, 600, 25);
			height += 35;
		}
		overTextField[0].setBounds(0, 0, 600, 50);
		overTextField[0].setFont(new Font("Arial",Font.ITALIC,50));	
		overTextField[1].setBounds(0, 60, 600, 30);
		overTextField[1].setFont(new Font("Arial",Font.ITALIC,30));	
		for (int index = 0; index < overTextField.length; index++) {
			overTextField[index].setEditable(false);
			overTextField[index].setBackground(Color.white);
			overTextField[index].setBorder(new EmptyBorder(0,0,0,0));
			overTextField[index].setHorizontalAlignment(JLabel.CENTER);	
			overPanel.add(overTextField[index]);
		}
		JButton reStartButton = new JButton("Re Star!");
		reStartButton.setFont(new Font("Arial",Font.ITALIC,50));
		reStartButton.setBounds(0, 600, 600, 50);
		reStartButton.setBackground(Color.WHITE);
		reStartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO �Զ����ɵķ������
				// ������ת����Ϸҳ��,Ȼ����г�ʼ��
				mainPanel.removeAll();
				startImage();
			}
		});
		overPanel.add(reStartButton);
		mainPanel.add(overPanel);
		sideNav(GameInformation.allPoint);
		mainPanel.updateUI();
	}
	
	/**
	 * �ı��ı����еĵ÷�
	 */
	public static void changePointList() {
		textField.setText(
				Integer.toString(GameInformation.allPoint));
	}
	
	/**
	 *  ����Ϸ���г�ʼ��Ȼ�������Ϸ����
	 */
	private static void initGame() {
		mainPanel.updateUI();
		gamePanel = new GamePanel();
		mainPanel.add(gamePanel);
		gamePanel.requestFocus();
		sideNav(0);
		// �������߳�
		threadGame = new Thread(gamePanel);
		threadGame.start();
	}
	
}

class GameInformation {
/**
 * @author Cianc
 * @version 1.0
 */
	// ���̵�����
	static int NUM_ROW = 20;
	// ���̵�����
    static int NUM_COL = 13;
    // ÿһ�����еĵ÷�
 	static int GET_POINT = 10;
	// ��������߶�
	static int GAME_OVER_HEIGHT = 4;
    // ����һ��ռ�еĳ���
	static int lenRow;
	// ����һ��ռ�еĳ���
	static int lenCol;
	// ��¼��Ϸ�÷ֲ�
	static int allPoint = 0;
	// ���̳�ʼ��
	static boolean[][] checkerBoard = new boolean[NUM_ROW][NUM_COL];
	/**
	 * ��ʼ����ֵ
	 * @param height
	 * @param width
	 */
	static void init(int height,int width) {
		lenRow = height/NUM_ROW;
		lenCol = width/NUM_COL;
	}
}

public class Main {
/**
* @author Cianc
* @version 1.0
*/
	public static void main(String args[]) {
		new Frame();
	}
}
