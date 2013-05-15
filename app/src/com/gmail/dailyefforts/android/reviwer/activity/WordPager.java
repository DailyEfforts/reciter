package com.gmail.dailyefforts.android.reviwer.activity;

import java.util.Locale;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;
import com.gmail.dailyefforts.android.reviwer.word.Word;

public class WordPager extends FragmentActivity implements OnInitListener,
		OnClickListener {

	private static final String TAG = WordPager.class.getSimpleName();

	private String mWord;
	private String mMeaning;

	private static SparseArray<Word> map;

	private DBA dba;

	private String mAddToBook;

	private String mRmFromBook;

	private TextToSpeech mTts;

	private static ViewPager mViewPager;

	private SharedPreferences mSharedPref;

	private static Animation mAnimation;

	private PageHandler mHandler;

	private ImageButton imgBtnPlay;

	private boolean isSpeaking;

	private int mRate;

	private static int mGapTime;

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (menu != null) {
			menu.clear();
		}

		getMenuInflater().inflate(R.menu.action, menu);

		if (dba == null || menu == null) {
			return false;
		}

		MenuItem star = menu.findItem(R.id.menu_star);
		if (star != null) {
			if (dba.getStar(mWord) <= 0) {
				star.setIcon(android.R.drawable.star_off);
				star.setTitle(R.string.add_to_word_book);
			} else {
				star.setIcon(android.R.drawable.star_on);
				star.setTitle(R.string.remove_from_word_book);
			}
		}

		if (Debuger.DEBUG) {
			Log.d(TAG, "onPrepareOptionsMenu()");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_read:
			isSpeaking = !isSpeaking;
			invalidateOptionsMenu();
			return true;
		case R.id.menu_star:
			if (dba == null) {
				return false;
			}
			if (dba.getStar(mWord) <= 0) {
				dba.star(mWord);
				toast(String.format(mAddToBook, mWord));
				invalidateOptionsMenu();
			} else {
				dba.unStar(mWord);
				toast(String.format(mRmFromBook, mWord));
				invalidateOptionsMenu();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private static boolean playing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_word_pager);

		mViewPager = (ViewPager) findViewById(R.id.word_pager);
		mAnimation = AnimationUtils.loadAnimation(this, R.anim.meaning_zoom_in);

		imgBtnPlay = (ImageButton) findViewById(R.id.ib_play);
		setProgressBarVisibility(true);

		getActionBar().setDisplayShowTitleEnabled(false);
		mSharedPref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		mGapTime = Integer.valueOf(mSharedPref.getString(getResources()
				.getString(R.string.pref_key_slide_show_time_gap),
				Config.DEFAULT_TIME_GAP));

		dba = DBA.getInstance(getApplicationContext());

		Resources res = getResources();

		map = Word.getMap();

		if (map == null || map.size() <= 0) {
			return;
		}

		mRate = (Window.PROGRESS_END - Window.PROGRESS_START) / map.size();

		mAddToBook = String.valueOf(res.getText(R.string.tip_add_to_word_book));
		mRmFromBook = String.valueOf(res
				.getText(R.string.tip_remove_from_word_book));

		mTts = new TextToSpeech(this, this);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		WordPagerAdapter adapter = new WordPagerAdapter(
				getSupportFragmentManager());

		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(0);
		mWord = map.get(0).getWord();
		mMeaning = map.get(0).getMeaning();
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {
						mWord = map.get(position).getWord();
						mMeaning = map.get(position).getMeaning();

						if (Debuger.DEBUG) {
							Log.d(TAG, "onPageSelected() w: " + mWord + ", m: "
									+ mMeaning);
							Log.d(TAG, "position: " + position + ", size: "
									+ map.size());
						}

						if (position == map.size() - 1) {
							setProgress(Window.PROGRESS_END);
						} else {
							setProgress(mRate * (position + 1));
						}

						invalidateOptionsMenu();
						readIt(mWord);
					}

				});
		mHandler = new PageHandler();

		if (imgBtnPlay != null) {
			imgBtnPlay.setOnClickListener(this);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_play:
			if (imgBtnPlay != null) {
				if (playing) {
					// stop playing
					imgBtnPlay
							.setImageResource(android.R.drawable.ic_media_play);
					if (mHandler != null) {
						mHandler.removeMessages(PageHandler.MSG_PAGE_NEXT);
					}
				} else {
					// start playing
					imgBtnPlay
							.setImageResource(android.R.drawable.ic_media_pause);
					readIt(mWord);
					if (mHandler != null) {
						mHandler.sendMessageDelayed(Message.obtain(mHandler,
								PageHandler.MSG_PAGE_NEXT), mGapTime * 1000);
					}
				}
				playing = !playing;
			}
			break;
		}
	}

	public static class PageHandler extends Handler {
		public static final int MSG_PAGE_PREVIOUS = 0;
		public static final int MSG_PAGE_NEXT = 1;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_PAGE_NEXT:
				if (mViewPager != null) {
					if (Debuger.DEBUG) {
						Log.d(TAG, "handleMessage() current item: "
								+ mViewPager.getCurrentItem());
					}

					int currentItem = mViewPager.getCurrentItem();
					if (currentItem < map.size() - 1) {
						sendMessageDelayed(
								Message.obtain(this, PageHandler.MSG_PAGE_NEXT),
								mGapTime * 1000);
						mViewPager.setCurrentItem(currentItem + 1);
					} else {
						removeMessages(MSG_PAGE_NEXT);
					}
				}

				break;
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		playing = false;
		isSpeaking = false;
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mHandler != null) {
			mHandler.removeMessages(PageHandler.MSG_PAGE_NEXT);
		}
	}

	private class WordPagerAdapter extends FragmentPagerAdapter {

		public WordPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {
			Fragment fragment = new WordFragment();
			Bundle args = new Bundle();
			args.putInt(WordFragment.INDEX, index);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return map.size();
		}

	}

	public static class WordFragment extends Fragment {
		public static final String INDEX = "index";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_word, container,
					false);
			if (view != null) {
				TextView word = (TextView) view.findViewById(R.id.tv_word);
				TextView meaning = (TextView) view
						.findViewById(R.id.tv_meaning);
				if (word != null && meaning != null) {
					int idx = getArguments().getInt(INDEX);
					if (idx < map.size()) {
						String w = map.get(idx).getWord();
						String m = map.get(idx).getMeaning();
						word.setText(w);
						meaning.setText(m);

						if (mAnimation != null) {
							meaning.startAnimation(mAnimation);
						}
					}
				}
			}

			return view;
		}
	}

	private void readIt(final String word) {
		if (mTts != null && isSpeaking) {
			mTts.speak(word, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	private void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	int mWordCounter = 0;

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			// Set preferred language to US english.
			// Note that a language may not be available, and the result will
			// indicate this.
			int result = -1;
			if (Config.CURRENT_LANGUAGE.equals(Config.LANG_FR)) {
				result = mTts.setLanguage(Locale.FRANCE);
			} else {
				result = mTts.setLanguage(Locale.ENGLISH);
			}
			// Try this someday for some interesting results.
			// int result mTts.setLanguage(Locale.FRANCE);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Lanuage data is missing or the language is not supported.
				Log.e(TAG, "Language is not available.");
			} else {

			}
		} else {
			// Initialization failed.
			Log.e(TAG, "Could not initialize TextToSpeech.");
		}
	}

}
