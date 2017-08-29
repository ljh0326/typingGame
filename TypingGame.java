package TypingGame;

import java.awt.*;
import java.util.*;

public class TypingGame{

	ArrayList words = new ArrayList();
	String[] data = { "�¿�", "����", "����", "ȿ��", "����", "����", "Ƽ�Ĵ�", "���", "����ī" };
	int interval = 2 * 1000; // 2��

	// ����
	int life = 3;
	static int score = 0;

	// �ܾ �����ϴ� Ŭ����
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
			//1. �ݺ����� �̿��ؼ� ����ڰ� �Է��� �ܾ words�� �ִ��� Ȯ���Ѵ�
			//2. ������ ��Ҹ��� �����ϰ� words���� �����Ѵ�.
			//3. �������� ����ؼ� ������Ų��.
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
				// 1. ���ڿ� �迭 data�� ������ ��Ҹ� ���
				int pick = (int) (Math.random() * data.length);
				// 2. words�� �����Ѵ�.
				words.add(new Word(data[pick]));
				// 3. �ν��Ͻ� ���� interval�� �ð���ŭ �ð������� �д�.
				delay(interval);
			}
		}
	}

	class WordDropper extends Thread {
		public void run() {

			// 5. �ݺ����� �̿��ؼ� 1~4�� �۾��� �ݺ��Ѵ�.
			while (true) {
				// 1. words�� ����� ��� �ܾ�(Word�ν��Ͻ�)�� y���� 1 ���ҽ�Ų��.
				for (int i = 0; i < words.size(); i++) {
					Word word = (Word) words.get(i);
					word.y -= 1;
					// 2. y�� ���� 0���� �۰ų� ������, words���� �ܾ �����ϰ� life�� 1 ���ҽ�Ų��.
					if (word.y <= 0) {
						words.remove(i);
						life -= 1;
					}
					// 3. life�� ���� 0�� �Ǹ� life�� ������ ����ϰ� ������ �����Ѵ�.
					if (life == 0) {
						System.out.println(score);
						System.exit(0);
					}
					// 4. 1�ʰ� �ð��� ������Ų��.(delay()���)
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
