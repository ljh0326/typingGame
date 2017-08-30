package TypingGame;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TypingGameGui extends Frame {

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
//	final int MAX_LAVEL;
	
	boolean isPlaying = false;

	// �ܾ �����ϴ� Ŭ����
	WordGenerator wg = null;
	WordDropper wm = null;
	VirusThread vt = null;
	
	FontMetrics fm; // ȭ�鿡�� ���� ���̸� ���ϴµ� ���
//	ThreadGroup virusGrp = new ThreadGroup("")
	
	String[] data = { "�¿�", "����", "����", "ȿ��", "����", "����", "Ƽ�Ĵ�", "���", "����ī" };

	Vector words = new Vector();

	TextField tf = new TextField();
	Panel pScore = new Panel(new GridLayout(1, 3));
	Label lbLevel = new Label("Level :" + level, Label.CENTER);
	Label lbScore = new Label("Score: " + score, Label.CENTER);
	Label lbLife = new Label("Life:" + life, Label.CENTER);
	MyCanvas screen = new MyCanvas();

	TypingGameGui() {
		this("Typing game ver2.0");
	}

	TypingGameGui(String title) {
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
		
		//1. WordGenerator ������ ����
		wg = new WordGenerator();
		wg.start();

		//2. WordDropper ������ ����
		wm = new WordDropper();
		wm.start();
	}

	public static void main(String[] args) {
		TypingGameGui win = new TypingGameGui();
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
					Word tmp = (Word) words.get(i);

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
				
				boolean isVirus = ((int)(Math.random()*10)+1) /10 != 0;
				// 2. words�� �����Ѵ�.
				words.add(new Word(data[pick], isVirus));
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
				g.setColor(tmp.color);
				g.drawString(tmp.word, tmp.x, tmp.y);
			}
		}
	}

	class VirusThread extends Thread{
		public void run() {
			int rand = (int)(Math.random()*5);
			
			int oldValue = 0;
			int virusTime = 10 * 1000;

			switch(rand) {
//			   1. rand�� ���� 0�̸�, virusTime���� speed�� ���� �������� ���δ�.
			case 0: 
				speed /= 2;
				break;
//             2. rand�� ���� 1�̸�, virusTime���� interval�� ���� �������� ���δ�.
			case 1: 
				interval /= 2;
				break;
//             3. rand�� ���� 2�̸�, virusTime���� speed�� ���� �� ��� ���δ�.
			case 2:
				speed *= 2;
				break;
//             4. rand�� ���� 3�̸�, virusTime���� interval�� ���� �� ��� ���δ�.
			case 3:
				interval *= 2;
				break;
//             5. rand�� ���� 4�̸�, ȭ���� ��� �ܾ ���ش�.
			case 4:
				words.clear();
				break;
			}
			delay(virusTime);
		}
		
	}
	class Word {
		String word = "";
		int x = 0;
		int y = 0;
		int step = 5;

		//�⺻�÷�
		Color color = Color.BLACK;
		boolean isVirus = false;
		
		//�ܾ �ִٸ� ������ 10���� ���̷��� false�� ����
		Word(String word) {
			this(word, 10, false);
		}
		
		Word(String word, boolean isVirus){
			this(word, 10, isVirus);
		}

		Word(String word, int step, boolean isVirus) {
			this.word = word;
			this.step = step;
			this.isVirus = isVirus;
			
			//���� ���̷������ �ܾ�� ��������
			System.out.println(isVirus);
			if(isVirus)color = Color.RED;
			
			//
			int strWidth = fm.stringWidth(word);
			
			//����
			x = (int) (Math.random() * SCREEN_WIDTH);

			//ȭ�� �ʰ��ϴ°��� �����Ǹ� 
			if (x + strWidth >= SCREEN_WIDTH)
				x = SCREEN_WIDTH - strWidth;
		}

		public String toString() {
			return word;
		}
	}

	class MyEventHandler extends WindowAdapter implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			//�ؽ�Ʈ�ʵ忡 �ִ� ���� ������� �޾ƿ´�.
			String input = tf.getText().trim();
			//�ؽ�Ʈ�ʵ带 ��ĭ���� ������ش�.
			tf.setText("");

			System.out.println(input);
			
			//�������� �ƴ϶�� ����
			if (!isPlaying)
				return;

			//���Ϳ� ����ִ� ��� �ܾ ���ɴ�.
			for (int i = 0; i < words.size(); i++) {
				//i��° �׸��� �ް�
				Word tmp = (Word) words.get(i);

				//����ڰ� �Է��� �ܾ�� i��° �ܾ ������
				if (input.equals(tmp.word)) {
					//�ܾ����
					words.remove(i);
					//���� �߰� �ܾ���� * 50
					score += input.length() * 50;
					//���� label�� ����
					lbScore.setText("Score:" + score);
					//���Ҹ� ����
					Toolkit.getDefaultToolkit().beep();
					
					//���� ���̷������ ���̷��� ������ ����
					if(tmp.isVirus) {
						vt = new VirusThread();
						vt.start();
					}

					break;
				}
			}
			repaint();

		}

		public void windowClosing(WindowEvent e) {
			e.getWindow().setVisible(false);
			e.getWindow().dispose();
			System.exit(0);
		}
	}
}
