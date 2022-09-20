package chiefarug.mods.markcraft.parser;

public class Reader {

	private final String text;
	private int index;
	private final int length;

	public Reader(String t) {
		text = t;
		index = -1;
		length = t.length();
	}

	public char current() {
		return text.charAt(index);
	}

	public boolean hasNext() {
		return index + 1 < length;
	}

	public boolean isLast() {
		return !hasNext();
	}

	public void move(int i) {
		index += i;
	}

	public char next() {
		move(1);
		return current();
	}

	public char peek(int i) {
		return text.charAt(index + i);
	}

	@Override
	public String toString() {
		return "Reader[" + text + ", " + index + "]";
	}
}
