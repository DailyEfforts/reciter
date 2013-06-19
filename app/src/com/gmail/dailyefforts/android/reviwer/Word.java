package com.gmail.dailyefforts.android.reviwer;

import java.util.Locale;

import android.util.SparseArray;

public class Word {
	private String word;
	private String meaning;
	private int id;

	public Word(int id, String word, String meaning) {
		this.id = id;
		this.word = word;
		this.meaning = meaning;
	}

	public String getWord() {
		return word;
	}

	public int getId() {
		return id;
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
		return String.format(Locale.getDefault(), "[%d, %s, %s]", id, word,
				meaning);
	}
}
