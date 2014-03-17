package com.gmail.dailyefforts.android.reciter;

import java.util.Locale;

import android.util.SparseArray;

public class Word {
	private int mId;
	private String mWord;
	private String mMeaning;
	private String mSample;

	public Word(int id, String word, String meaning) {
		this(id, word, meaning, null);
	}

	public Word(int id, String word, String meaning, String sample) {
		this.mId = id;
		this.mWord = word;
		this.mMeaning = meaning;
		this.mSample = sample;
	}

	public String getWord() {
		return mWord;
	}

	public int getId() {
		return mId;
	}

	public String getSample() {
		return mSample;
	}

	public String getMeaning() {
		return mMeaning;
	}

	private static SparseArray<Word> sMap = new SparseArray<Word>();

	public static SparseArray<Word> getMap() {
		return sMap;
	}

	@Override
	public String toString() {
		return String.format(Locale.getDefault(), "[%d, %s, %s, %s]", mId,
				mWord, mMeaning, mSample);
	}
}
