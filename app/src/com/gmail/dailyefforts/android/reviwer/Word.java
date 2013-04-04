package com.gmail.dailyefforts.android.reviwer;

import android.util.SparseArray;

public class Word {
	private String word;
	private String meaning;
	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String WORD_MEANING_SPLIT = "@";

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

	private static SparseArray<Word> map = new SparseArray<Word>();

	public static SparseArray<Word> getMap() {
		return map;
	}

	@Override
	public String toString() {
		return "Word [word=" + word + ", meaning=" + meaning + "]";
	}
}
