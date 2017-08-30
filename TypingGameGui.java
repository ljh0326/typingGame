package TypingGame;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TypingGameGui extends Frame {

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
//	final int MAX_LAVEL;
	
	boolean isPlaying = false;

	// 단어를 생성하는 클래스
	WordGenerator wg = null;
	WordDropper wm = null;
	VirusThread vt = null;
	
	FontMetrics fm; // 화면에서 글자 길이를 구하는데 사용
//	ThreadGroup virusGrp = new ThreadGroup("")
	
	String[] data = { "태연", "유리", "윤아", "효연", "수영", "서현", "티파니", "써니", "제시카" };

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
		
		//1. WordGenerator 쓰레드 가동
		wg = new WordGenerator();
		wg.start();

		//2. WordDropper 쓰레드 동작
		wm = new WordDropper();
		wm.start();
	}

	public static void main(String[] args) {
		TypingGameGui win = new TypingGameGui();
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
					Word tmp = (Word) words.get(i);

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
				
				boolean isVirus = ((int)(Math.random()*10)+1) /10 != 0;
				// 2. words에 저장한다.
				words.add(new Word(data[pick], isVirus));
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
//			   1. rand의 값이 0이면, virusTime동안 speed의 값을 절반으로 줄인다.
			case 0: 
				speed /= 2;
				break;
//             2. rand의 값이 1이면, virusTime동안 interval의 값을 절반으로 줄인다.
			case 1: 
				interval /= 2;
				break;
//             3. rand의 값이 2이면, virusTime동안 speed의 값을 두 배로 늘인다.
			case 2:
				speed *= 2;
				break;
//             4. rand의 값이 3이면, virusTime동안 interval의 값을 두 배로 늘인다.
			case 3:
				interval *= 2;
				break;
//             5. rand의 값이 4이면, 화면의 모든 단어를 없앤다.
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

		//기본컬러
		Color color = Color.BLACK;
		boolean isVirus = false;
		
		//단어만 있다면 스텝은 10으로 바이러스 false로 고정
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
			
			//만약 바이러스라면 단어색 빨강으로
			System.out.println(isVirus);
			if(isVirus)color = Color.RED;
			
			//
			int strWidth = fm.stringWidth(word);
			
			//랜덤
			x = (int) (Math.random() * SCREEN_WIDTH);

			//화면 초과하는곳에 생성되면 
			if (x + strWidth >= SCREEN_WIDTH)
				x = SCREEN_WIDTH - strWidth;
		}

		public String toString() {
			return word;
		}
	}

	class MyEventHandler extends WindowAdapter implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			//텍스트필드에 있는 값을 공백없이 받아온다.
			String input = tf.getText().trim();
			//텍스트필드를 빈칸으로 만들어준다.
			tf.setText("");

			System.out.println(input);
			
			//게임중이 아니라면 종료
			if (!isPlaying)
				return;

			//백터에 들어있는 모든 단어를 살핀다.
			for (int i = 0; i < words.size(); i++) {
				//i번째 항목을 받고
				Word tmp = (Word) words.get(i);

				//사용자가 입력한 단어와 i번째 단어가 같으면
				if (input.equals(tmp.word)) {
					//단어삭제
					words.remove(i);
					//점수 추가 단어길이 * 50
					score += input.length() * 50;
					//점수 label에 설정
					lbScore.setText("Score:" + score);
					//빕소리 나게
					Toolkit.getDefaultToolkit().beep();
					
					//만약 바이러스라면 바이러스 쓰레드 동작
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
