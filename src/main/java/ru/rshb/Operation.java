package ru.rshb;

public enum Operation {
	CREATE("-c", "create.ftl"),
	ALTER("-a", "alter.ftl"),
	DELETE("-d", "delete.ftl");

	private String key;

	private String templateName;

	Operation(String key, String templateName) {
		this.key = key;
		this.templateName = templateName;
	}

	public static Operation map(String input) {
		for(Operation o : Operation.values()) {
			if(o.is(input)) return o;
		}

		return CREATE;
	}

	public boolean is(String val) {
		return this.name().equalsIgnoreCase(val);
	}

	public String getTemplateName() {
		return this.templateName;
	}
}
