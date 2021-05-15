package ru.rshb;

public class CustomException extends RuntimeException {

	private final String msg;

	public CustomException(String text) {
		super();
		this.msg = text;
	}

	public CustomException(String text, Throwable ex) {
		super(ex);
		this.msg = text;
	}

	public void print() {
		System.out.println(msg + " | " + getMessage());
	}

}
