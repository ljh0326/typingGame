package TypingGame;

import java.awt.*;
import java.util.*;

public class TypingGame{

	ArrayList words = new ArrayList();
	String[] data = { "태연", "유리", "윤아", "효연", "수영", "서현", "티파니", "써니", "제시카" };
	int interval = 2 * 1000; // 2초

	// 생명
	int life = 3;
	static int score = 0;

	// 단어를 생성하는 클래스
	WordGenerator wg = new WordGenerator();
	WordDropper wd = new WordDropper();

	public static void main(String[] args) {
		TypingGame game = new TypingGame();
		game.wg.start();
		game.wd.start();

		ArrayList words = game.words;
		
		while (true) {
			System.out.println("LIFE:" + game.life + "SCORE" + game.score);
			System.out.println(words);

			String prompt = ">>";
			System.out.println(prompt);

			Scanner sc = new Scanner(System.in);
			String input = sc.nextLine().trim();

			
			for (int i = 0; i < words.size(); i++) {
			//1. 반복문을 이용해서 사용자가 입력한 단어가 words에 있는지 확인한다
			//2. 있으면 삑소리가 나게하고 words에서 삭제한다.
			//3. 점수값을 계산해서 증가시킨다.
				if ((((Word)words.get(i)).word).equals(input)) {
					Toolkit.getDefaultToolkit().beep();
					score += (input.length() * 50 * ((Word) words.get(i)).y);
					words.remove(i);
				}
			}
		}
	}

	public void delay(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	class WordGenerator extends Thread {
		public void run() {
			while (true) {
				// 1. 문자열 배열 data의 임의의 요소를 골라서
				int pick = (int) (Math.random() * data.length);
				// 2. words에 저장한다.
				words.add(new Word(data[pick]));
				// 3. 인스턴스 변수 interval의 시간만큼 시간간격을 둔다.
				delay(interval);
			}
		}
	}

	class WordDropper extends Thread {
		public void run() {

			// 5. 반복문을 이용해서 1~4의 작업을 반복한다.
			while (true) {
				// 1. words에 저장된 모든 단어(Word인스턴스)의 y값을 1 감소시킨다.
				for (int i = 0; i < words.size(); i++) {
					Word word = (Word) words.get(i);
					word.y -= 1;
					// 2. y의 값이 0보다 작거나 같으면, words에서 단어를 제거하고 life를 1 감소시킨다.
					if (word.y <= 0) {
						words.remove(i);
						life -= 1;
					}
					// 3. life의 값이 0이 되면 life와 점수를 출력하고 게임을 종료한다.
					if (life == 0) {
						System.out.println(score);
						System.exit(0);
					}
					// 4. 1초간 시간을 지연시킨다.(delay()사용)
					delay(1000);
				}
			}
		}
	}

	class Word {
		String word = "";
		int y = 10;

		Word(String word) {
			this(word, 10);
		}

		Word(String word, int y) {
			this.word = word;
			this.y = y;
		}

		public String toString() {
			return word + "" + y;
		}
	}
}
