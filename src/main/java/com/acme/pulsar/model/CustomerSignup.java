package com.acme.pulsar.model;

public record CustomerSignup(String text, Block... blocks) {

	public static CustomerSignup from(UserSignup signup) {
		String fullName = "%s %s".formatted(signup.firstName(), signup.lastName());
		String text = "New customer signup (%s)".formatted(fullName);
		String markdown = ":tada: *New customer signup* :tada:\n\t\t%s (%s)".formatted(fullName, signup.email());
		return new CustomerSignup(text, Block.withMarkdown(markdown));
	}

	public record Block(String type, Text text) {
		public static Block withMarkdown(String markdown) {
			return new Block("section", new Text("mrkdwn", markdown));
		}
	}

	public record Text(String type, String text) {
	}
}
