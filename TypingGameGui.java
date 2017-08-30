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

	int score = 0;
	int life = 3;
	int curLevel = 0;
	final int MAX_LEVEL;

	boolean isPlaying = false;
	boolean isTrap = false;
	// �ܾ �����ϴ� Ŭ����
	WordGenerator wg = null;
	WordDropper wm = null;
	VirusThread vt = null;

	FontMetrics fm; // ȭ�鿡�� ���� ���̸� ���ϴµ� ���
	ThreadGroup virusGrp = new ThreadGroup("virus");

	String[][] data = { { "�¿�", "����", "����", "ȿ��", "����", "����", "Ƽ�Ĵ�", "���", "����ī" },
			{ "�¿�", "����", "����", "ȿ��", "����", "����", "Ƽ�Ĵ�", "���", "����ī" },
			{ "�¿�", "����", "����", "ȿ��", "����", "����", "Ƽ�Ĵ�", "���", "����ī" },
			{ "�¿�", "����", "����", "ȿ��", "����", "����", "Ƽ�Ĵ�", "���", "����ī" }, };

	Vector words = new Vector();

	final Level[] LEVEL = { new Level(500, 2000, 1000, data[0]), new Level(250, 1500, 2000, data[1]),
			new Level(120, 1000, 3000, data[2]), new Level(100, 500, 4000, data[3]) };

	TextField tf = new TextField();
	Panel pScore = new Panel(new GridLayout(1, 3));
	Label lbLevel = new Label("Level :" + curLevel, Label.CENTER);
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
		MAX_LEVEL = LEVEL.length - 1;
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
		showLevel(0);
		isPlaying = true;

		// 1. WordGenerator ������ ����
		wg = new WordGenerator();
		wg.start();

		// 2. WordDropper ������ ����
		wm = new WordDropper();
		wm.start();
	}

	public Level getLevel(int level) {
		if (level > MAX_LEVEL)
			level = MAX_LEVEL;
		if (level < 0)
			level = 0;

		return LEVEL[level];
	}

	public boolean levelUpCheck() {
		// 1. ���� ������ �´� Level�ν��Ͻ��� ���´�.(getLevel()���)
		Level lv = getLevel(curLevel);

		// 2. ���� ����(score)�� ���������� �Ѿ ����(levelUpScore)��
		// �Ǵ����� ���θ� �Ǵ��ؼ� true �Ǵ� false�� ��ȯ�Ѵ�.
		if (score >= lv.levelUpScore)
			return true;

		return false;

	}

	public synchronized int getCurLevel() {
		return curLevel;
	}

	public synchronized void levelUp() {
//		  1. ��� ���̷����� ���߰� �Ѵ�.(interrupt()���)
		virusGrp.interrupt();
//        2. curLevel�� ���� ������Ű��,
		curLevel++;
//        curLevel�� Level�ν��Ͻ��� ���´�.(getLevel()���)
		Level lv = getLevel(curLevel);
//          3. lbLevel�� ������ ���ο� �������� �°� �����Ѵ�.
		lbLevel.setText(curLevel + "");
//          4. words�� ����.
		words.clear();
//          5. screen�� �����.
		screen.clear();
//          6. ���ο� ������ ȭ�鿡 �����ش�.(showLevel()���)
		showLevel(curLevel);
//          7. speed�� interval�� ���� 2���� ���� Level�ν��Ͻ��� ������ �����Ѵ�.
		speed = lv.speed; interval = lv.interval;

	}

	public void showLevel(int level) {
		String tmp = "Level" + level;
		showTitle(tmp, 1 * 1000);
	}

	public void showTitle(String title, int time) {
		Graphics g = screen.getGraphics();

		Font titleFont = new Font("Serif", Font.BOLD, 20);
		g.setFont(titleFont);

		FontMetrics fm = getFontMetrics(titleFont);
		int width = fm.stringWidth(title);

		g.drawString(title, (SCREEN_WIDTH - width) / 2, SCREEN_HEIGHT / 2);
		delay(time);
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
						showTitle("Game Over", 0);

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

				String[] data = LEVEL[getCurLevel()].data;

				// 1. ���ڿ� �迭 data�� ������ ��Ҹ� ���
				int pick = (int) (Math.random() * data.length);
				// 5�� ������ �ѹ� �� ������ 
				boolean isVirus = ((int)(Math.random() * 5) + 1) / 5 != 0;
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

	class VirusThread extends Thread {
		public VirusThread(ThreadGroup group, String name) {
			super(group, name);
		}
		
		public void run() {
			int rand = (int) (Math.random() * 6);

			int oldValue = 0;
			int virusTime = 10 * 1000;

			switch (rand) {
			// 1. rand�� ���� 0�̸�, virusTime���� speed�� ���� �������� ���δ�.
			case 0:
				speed /= 2;
				break;
			// 2. rand�� ���� 1�̸�, virusTime���� interval�� ���� �������� ���δ�.
			case 1:
				interval /= 2;
				break;
			// 3. rand�� ���� 2�̸�, virusTime���� speed�� ���� �� ��� ���δ�.
			case 2:
				speed *= 2;
				break;
			// 4. rand�� ���� 3�̸�, virusTime���� interval�� ���� �� ��� ���δ�.
			case 3:
				interval *= 2;
				break;
			// 5. rand�� ���� 4�̸�, ȭ���� ��� �ܾ ���ش�.
			case 4:
				words.clear();
				break;
			// 6. rand�� ���� 5�̸�, ������ 3ȸ��
			case 5:
				life += 3;
				break;
//			// 7. rand�� ���� 6�̸� ���� ����ȭ ����
//			case 6:
//				isTrap = true;
//				break;
			}
			delay(virusTime);
		}

	}

	//���� Ŭ����
	class Level {
		int speed;
		int interval;
		int levelUpScore;
		String[] data;

		Level(int speed, int interval, int levelUpScore, String[] data) {
			this.speed = speed;
			this.interval = interval;
			this.levelUpScore = levelUpScore;
			this.data = data;
		}
	}

	class Word {
		String word = "";
		int x = 0;
		int y = 0;
		int step = 5;

		// �⺻�÷�
		Color color = Color.BLACK;
		
//		if(isTrap) {
//			color = Color.WHITE;
//			delay(5000);
//			isTrap = false;
//		}
		
		boolean isVirus = false;

		// �ܾ �ִٸ� ������ 10���� ���̷��� false�� ����
		Word(String word) {
			this(word, 10, false);
		}

		Word(String word, boolean isVirus) {
			this(word, 10, isVirus);
		}

		Word(String word, int step, boolean isVirus) {
			this.word = word;
			this.step = step;
			this.isVirus = isVirus;

			// ���� ���̷������ �ܾ�� ��������
			System.out.println(isVirus);
			if (isVirus)
				color = Color.RED;

			int strWidth = fm.stringWidth(word);

			// ����
			x = (int) (Math.random() * SCREEN_WIDTH);

			// ȭ�� �ʰ��ϴ°��� �����Ǹ�
			if (x + strWidth >= SCREEN_WIDTH)
				x = SCREEN_WIDTH - strWidth;
		}

		public String toString() {
			return word;
		}
	}

	class MyEventHandler extends WindowAdapter implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// �ؽ�Ʈ�ʵ忡 �ִ� ���� ������� �޾ƿ´�.
			String input = tf.getText().trim();
			// �ؽ�Ʈ�ʵ带 ��ĭ���� ������ش�.
			tf.setText("");

			System.out.println(input);

			// �������� �ƴ϶�� ����
			if (!isPlaying)
				return;

			// ���Ϳ� ����ִ� ��� �ܾ ���ɴ�.
			for (int i = 0; i < words.size(); i++) {
				// i��° �׸��� �ް�
				Word tmp = (Word) words.get(i);

				// ����ڰ� �Է��� �ܾ�� i��° �ܾ ������
				if (input.equals(tmp.word)) {
					// �ܾ����
					words.remove(i);
					// ���� �߰� �ܾ���� * 50
					score += input.length() * 50;
					// ���� label�� ����
					lbScore.setText("Score:" + score);
					// ���Ҹ� ����
					Toolkit.getDefaultToolkit().beep();

					// ���� ������ �ƽ����ƴϰ� ���� ���� �����̵Ǹ�
					if(curLevel != MAX_LEVEL && levelUpCheck()) {
						levelUp();
					} else {
					//�ƴѵ� ���̷����� ���̷��������� �׷� ����
						if (tmp.isVirus) {
							new VirusThread(virusGrp,"virus").start();
		
						}
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
