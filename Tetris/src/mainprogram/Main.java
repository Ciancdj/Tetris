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
	 * 初始化界面，并调用主调用程序.
	 */
	public Frame(){
		super("俄罗斯方块");
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
	 * 开始界面.
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
				// TODO 自动生成的方法存根
				mainPanel.removeAll();
				initGame();
			}
			
		});
		leftPanel.add(gameStartButton);
		mainPanel.add(leftPanel);
		sideNav(0);
	}
	
	/**
	 * 游戏侧边的提示栏
	 */
	private static void sideNav(int getPoint) {
		JPanel sidePanel = new JPanel();
		sidePanel.setBounds(600,0,400,800);
		sidePanel.setBackground(Color.gray);
		sidePanel.setLayout(null);
		textField = new JTextField(Integer.toString(getPoint));
		JTextArea textArea = new JTextArea("       Welcome to the Tetris\n"
				+ "     ← Move Left\n"
				+ "     → Move Right\n"
				+ "     ↑ Switch Style\n"
				+ "     ↓ Speed Down");
		// 得分框区域
		textField.setBounds(0, 0, 400, 200);
		textField.setFont(new Font("Arial",Font.BOLD,100));
		textField.setHorizontalAlignment(JLabel.CENTER);
		textField.setEditable(false);
		textField.setBackground(Color.gray);
		// 文本区域
		textArea.setBounds(0, 550, 400, 350);
		textArea.setFont(new Font("Arial",Font.ITALIC,30));
		textArea.setEditable(false);
		textArea.setBackground(Color.gray);
		sidePanel.add(textField);
		sidePanel.add(textArea);
		mainPanel.add(sidePanel);
	}
	
	/**
	 * 调用成绩读取程序然后进行判断
	 * 最后将成绩列表返回
	 * @param scoreIn
	 * @return
	 */
	private static Score[] scoreBoard(Score scoreIn) {
		Score[] score = scoreRead();
		int scoreTemp;
		String dateTemp;
		//排序完后与传入的数值进行比较
		if (score[score.length - 1].score < scoreIn.score) {
			//比较结果有大于的，把最小的进行覆盖
			score[score.length - 1] = scoreIn;
			//覆盖后进行写入 
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
	 * 对成绩进行读取
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
	 * 用来对成绩进行写入
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
	 * 游戏结束界面
	 */
	public static void gameOverImage() {
		mainPanel.removeAll();
		JPanel overPanel = new JPanel();
		String scoreString = null;
		Date date = new Date();
		// 界面初始化
		overPanel.setBounds(0, 0, 600, 800);
		overPanel.setBackground(Color.white);
		overPanel.setLayout(null);
		// 本次数据记载
		Score scoreIn = new Score();
		DateFormat df = DateFormat.getDateTimeInstance();
		scoreIn.score = Integer.valueOf(textField.getText());
		scoreIn.date = df.format(date);
		// 数据榜初始化
		Score score[] = scoreBoard(scoreIn);
		// GameOver字样
		JTextField[] overTextField = new JTextField[12];
		overTextField[0] = new JTextField("Game Over!",15);
		overTextField[1] = new JTextField("-> ScoreBoard <- ");
		// 为积分榜中得各个字符串定高
		int height = 125;
		// 将列表中得所有得数字转换为字符串
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
				// TODO 自动生成的方法存根
				// 这里跳转至游戏页面,然后进行初始化
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
	 * 改变文本框中的得分
	 */
	public static void changePointList() {
		textField.setText(
				Integer.toString(GameInformation.allPoint));
	}
	
	/**
	 *  对游戏进行初始化然后调用游戏界面
	 */
	private static void initGame() {
		mainPanel.updateUI();
		gamePanel = new GamePanel();
		mainPanel.add(gamePanel);
		gamePanel.requestFocus();
		sideNav(0);
		// 创建多线程
		threadGame = new Thread(gamePanel);
		threadGame.start();
	}
	
}

class GameInformation {
/**
 * @author Cianc
 * @version 1.0
 */
	// 棋盘的行数
	static int NUM_ROW = 20;
	// 棋盘的列数
    static int NUM_COL = 13;
    // 每一次消行的得分
 	static int GET_POINT = 10;
	// 程序结束高度
	static int GAME_OVER_HEIGHT = 4;
    // 棋盘一行占有的长度
	static int lenRow;
	// 棋盘一列占有的长度
	static int lenCol;
	// 记录游戏得分并
	static int allPoint = 0;
	// 棋盘初始化
	static boolean[][] checkerBoard = new boolean[NUM_ROW][NUM_COL];
	/**
	 * 初始化数值
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
