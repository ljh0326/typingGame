package TypingGame;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TypingGameGui3 extends Frame {

	// 프레임 크기
	final int FRAME_WIDTH = 400;
	final int FRAME_HEIGHT = 300;

	// 화면크기
	final int SCREEN_WIDTH;
	final int SCREEN_HEIGHT;

	int speed = 500; // 단어가 떨어지는 속도
	int interval = 2 * 1000; // 새로운 단어가 나오는 간격

	int level = 0;
	int score = 0;
	int life = 3;

	boolean isPlaying = false;

	// 단어를 생성하는 클래스
	WordGenerator wg = null;
	WordDropper wm = null;

	FontMetrics fm; // 화면에서 글자 길이를 구하는데 사용

	String[] data = { "태연", "유리", "윤아", "효연", "수영", "서현", "티파니", "써니", "제시카" };

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

		// 패널에 레벨 점수 생명표시
		pScore.setBackground(Color.YELLOW);
		pScore.add(lbLevel);
		pScore.add(lbScore);
		pScore.add(lbLife);
		add(pScore, "North");
		add(screen, "Center");
		add(tf, "South");

		// 이벤트 핸들러객체를 생성해서
		MyEventHandler handler = new MyEventHandler();
		// 윈도우 리스너와
		addWindowListener(handler);
		// 텍스트필드 액션리스너에 부착
		tf.addActionListener(handler);

		// 크기를 설정해주고
		setBounds(500, 200, FRAME_WIDTH, FRAME_HEIGHT);
		// 프레임 크기 변경 불가능
		setResizable(false);
		setVisible(true);

		SCREEN_WIDTH = screen.getWidth();
		SCREEN_HEIGHT = screen.getHeight();
		fm = getFontMetrics(getFont());
	}

	// 부모와 스크린을 다시그린다.
	public void repaint() {
		super.repaint();
		screen.repaint();
	}

	// 딜레이 함수
	public void delay(int millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// isPlaying을 true로 바꾸고 쓰레드를 시작한다.
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

	// 단어들을 떨어뜨리는 쓰레드
	class WordDropper extends Thread {
		public void run() {
			outer:
			// 게임중인동안 반복하고
			while (isPlaying) {
				// 스피드를 딜레이 시킨다.
				delay(speed);
				// 1. words에 저장된 모든 단어(Word인스턴스)의 y값을 스텝만큼 증가시킨다.
				for (int i = 0; i < words.size(); i++) {
					Word tmp = (Word)words.get(i);

					tmp.y += tmp.step;

					// y가 스크린 값보다높으면 받은 단어를 체크
					if (tmp.y >= SCREEN_HEIGHT) {
						tmp.y = SCREEN_HEIGHT;
						words.remove(tmp);
						life--;
						lbLife.setText("Life :" + life);
						break;
					}

					// 라이프가 다달면 게임종료
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
				// 1. 문자열 배열 data의 임의의 요소를 골라서
				int pick = (int) (Math.random() * data.length);
				// 2. words에 저장한다.
				words.add(new Word(data[pick]));
				// 3. 인스턴스 변수 interval의 시간만큼 시간간격을 둔다.
				delay(interval);
			}
		}
	}
	
	class MyCanvas extends Canvas {

		// 그래픽스를 받아서 화면을 깨끝하게 해준다.
		public void clear() {
			Graphics g = getGraphics();
			g.clearRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		}

		// apint함수 오버라이딩
		public void paint(Graphics g) {
			clear();

			// 텍스트를 화면에 출력
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
