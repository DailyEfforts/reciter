package com.gmail.dailyefforts.android.reviwer;

public class Word {
	private String word;
	private String meaning;

	public Word(String word, String meaning) {
		super();
		this.word = word;
		this.meaning = meaning;
	}

	public String getWord() {
		return word;
	}

	public String getMeaning() {
		return meaning;
	}

	@Override
	public String toString() {
		return "Word [word=" + word + ", meaning=" + meaning + "]";
	}
}
