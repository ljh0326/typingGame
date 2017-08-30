package TypingGame;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TypingGameGui3 extends Frame {

	// ������ ũ��
	final int FRAME_WIDTH = 400;
	final int FRAME_HEIGHT = 300;

	// ȭ��ũ��
	final int SCREEN_WIDTH;
	final int SCREEN_HEIGHT;

	int speed = 500; // �ܾ �������� �ӵ�
	int interval = 2 * 1000; // ���ο� �ܾ ������ ����

	int level = 0;
	int score = 0;
	int life = 3;

	boolean isPlaying = false;

	// �ܾ �����ϴ� Ŭ����
	WordGenerator wg = null;
	WordDropper wm = null;

	FontMetrics fm; // ȭ�鿡�� ���� ���̸� ���ϴµ� ���

	String[] data = { "�¿�", "����", "����", "ȿ��", "����", "����", "Ƽ�Ĵ�", "���", "����ī" };

	Vector words = new Vector();

	TextField tf = new TextField();
	Panel pScore = new Panel(new GridLayout(1, 3));
	Label lbLevel = new Label("Level :" + level, Label.CENTER);
	Label lbScore = new Label("Score: " + score, Label.CENTER);
	Label lbLife = new Label("Life:" + life, Label.CENTER);
	MyCanvas screen = new MyCanvas();

	TypingGameGui3() {
		this("Typing game ver2.0");
	}

	TypingGameGui3(String title) {
		super(title);

		// �гο� ���� ���� ����ǥ��
		pScore.setBackground(Color.YELLOW);
		pScore.add(lbLevel);
		pScore.add(lbScore);
		pScore.add(lbLife);
		add(pScore, "North");
		add(screen, "Center");
		add(tf, "South");

		// �̺�Ʈ �ڵ鷯��ü�� �����ؼ�
		MyEventHandler handler = new MyEventHandler();
		// ������ �����ʿ�
		addWindowListener(handler);
		// �ؽ�Ʈ�ʵ� �׼Ǹ����ʿ� ����
		tf.addActionListener(handler);

		// ũ�⸦ �������ְ�
		setBounds(500, 200, FRAME_WIDTH, FRAME_HEIGHT);
		// ������ ũ�� ���� �Ұ���
		setResizable(false);
		setVisible(true);

		SCREEN_WIDTH = screen.getWidth();
		SCREEN_HEIGHT = screen.getHeight();
		fm = getFontMetrics(getFont());
	}

	// �θ�� ��ũ���� �ٽñ׸���.
	public void repaint() {
		super.repaint();
		screen.repaint();
	}

	// ������ �Լ�
	public void delay(int millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// isPlaying�� true�� �ٲٰ� �����带 �����Ѵ�.
	public void start() {
		isPlaying = true;

		wg = new WordGenerator();
		wg.start();

		wm = new WordDropper();
		wm.start();
	}

	public static void main(String[] args) {
		TypingGameGui3 win = new TypingGameGui3();
		win.start();
	}

	// �ܾ���� ����߸��� ������
	class WordDropper extends Thread {
		public void run() {
			outer:
			// �������ε��� �ݺ��ϰ�
			while (isPlaying) {
				// ���ǵ带 ������ ��Ų��.
				delay(speed);
				// 1. words�� ����� ��� �ܾ�(Word�ν��Ͻ�)�� y���� ���ܸ�ŭ ������Ų��.
				for (int i = 0; i < words.size(); i++) {
					Word tmp = (Word)words.get(i);

					tmp.y += tmp.step;

					// y�� ��ũ�� �����ٳ����� ���� �ܾ üũ
					if (tmp.y >= SCREEN_HEIGHT) {
						tmp.y = SCREEN_HEIGHT;
						words.remove(tmp);
						life--;
						lbLife.setText("Life :" + life);
						break;
					}

					// �������� �ٴ޸� ��������
					if (life <= 0) {
						isPlaying = false;
						break outer;
					}
				}
				repaint();
			}
		}
	}
	
	
	class WordGenerator extends Thread {
		public void run() {
			while (isPlaying) {
				// 1. ���ڿ� �迭 data�� ������ ��Ҹ� ���
				int pick = (int) (Math.random() * data.length);
				// 2. words�� �����Ѵ�.
				words.add(new Word(data[pick]));
				// 3. �ν��Ͻ� ���� interval�� �ð���ŭ �ð������� �д�.
				delay(interval);
			}
		}
	}
	
	class MyCanvas extends Canvas {

		// �׷��Ƚ��� �޾Ƽ� ȭ���� �����ϰ� ���ش�.
		public void clear() {
			Graphics g = getGraphics();
			g.clearRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		}

		// apint�Լ� �������̵�
		public void paint(Graphics g) {
			clear();

			// �ؽ�Ʈ�� ȭ�鿡 ���
			for (int i = 0; i < words.size(); i++) {
				Word tmp = (Word) words.get(i);
				g.drawString(tmp.word, tmp.x, tmp.y);
			}
		}
	}
	class Word {
		String word = "";
		int x = 0;
		int y = 0;
		int step = 5;

		Word(String word) {
			this(word, 10);
		}

		Word(String word, int step) {
			this.word = word;
			this.step = step;

			int strWidth = fm.stringWidth(word);

			x = (int) (Math.random() * SCREEN_WIDTH);

			if (x + strWidth >= SCREEN_WIDTH)
				x = SCREEN_WIDTH - strWidth;
		}

		public String toString() {
			return word;
		}
	}

	class MyEventHandler extends WindowAdapter implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			while (isPlaying) {
				String input = tf.getText().trim();
				tf.setText("");

				if(!isPlaying) return;
				
				for (int i = 0; i < words.size(); i++) {
					 Word tmp = (Word)words.get(i); 
					
					if(input.equals(tmp.word)) {
						words.remove(i);
						score += input.length() * 50;
						lbScore.setText("Score:"+score); 
                        Toolkit.getDefaultToolkit().beep(); 
					}
				}
				repaint();
			}
			
		}
		public void windowClosing(WindowEvent e) {
			e.getWindow().setVisible(false);
			e.getWindow().dispose();
			System.exit(0);
		}
	}
}
