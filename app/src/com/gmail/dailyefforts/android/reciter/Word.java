package com.gmail.dailyefforts.android.reciter;

import java.util.Locale;

import android.util.SparseArray;

public class Word {
	private String word;
	private String meaning;
	private String sample;
	private int id;

	public Word(int id, String word, String meaning) {
		this(id, word, meaning, null);
	}

	public Word(int id, String word, String meaning, String sample) {
		this.id = id;
		this.word = word;
		this.meaning = meaning;
		this.sample = sample;
	}

	public String getWord() {
		return word;
	}

	public int getId() {
		return id;
	}

	public String getSample() {
		return sample;
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
		return String.format(Locale.getDefault(), "[%d, %s, %s, %s]", id, word,
				meaning, sample);
	}
}
