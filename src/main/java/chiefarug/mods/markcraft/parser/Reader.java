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
		return this.text.charAt(this.index);
	}

	public boolean hasNext() {
		return (this.index + 1) < this.length;
	}

	public char next() {
		this.index++;
		return this.current();
	}

	public char peekNext() {
		return this.text.charAt(this.index + 1);
	}

	public boolean incrementIfNext(char c) {
		if (this.hasNext() && this.peekNext() == c) {
			this.next();
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Reader[" + text + ", " + index + "]";
	}
}
