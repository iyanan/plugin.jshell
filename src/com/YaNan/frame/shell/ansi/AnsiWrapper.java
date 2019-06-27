package com.YaNan.frame.shell.ansi;

public class AnsiWrapper {
	public static interface COLOR{
		final static int FONT_BLACK = 40;
		final static int FONT_RED = 41;
		final static int FONT_GREEN = 42;
		final static int FONT_YELLOW = 43;
		final static int FONT_BLUE = 44;
		final static int FONT_PURPLE = 45;
		final static int FONT_WETGREEN = 46;
		final static int FONT_WHITE = 47;
		
		final static int BACKGROUND_BLACK = 30;
		final static int BACKGROUND_RED = 31;
		final static int BACKGROUND_GREEN = 32;
		final static int BACKGROUND_YELLOW = 33;
		final static int BACKGROUND_BLUE = 34;
		final static int BACKGROUND_PURPLE = 35;
		final static int BACKGROUND_WETGREEN = 36;
		final static int BACKGROUND_WHITE = 37;
		
	}
	private StringBuilder temp;
	public AnsiWrapper() {
		temp = new StringBuilder("\33[");
	}
	public AnsiWrapper wrapperColor(int color) {
		temp.append(color+";");
		return this;
	}
	public AnsiWrapper wrapperEnd() {
		temp.append("\33[0m");
		return this;
	}
	public AnsiWrapper wrapperFlash() {
		temp.append("5m");
		return this;
	}
	public AnsiWrapper wrapperHighLight() {
		temp.append("1m");
		return this;
	}
	public AnsiWrapper wrapperContent(String content) {
		temp.append(content);
		return this;
	}
	public static AnsiWrapper newWrapper() {
		return new AnsiWrapper();
	}
	public String toString() {
		return temp.toString();
	}
}
