
package com.gmail.dailyefforts.reciter.test.drag;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import android.content.ClipData;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.gmail.dailyefforts.reciter.Config;
import com.gmail.dailyefforts.reciter.Word;
import com.gmail.dailyefforts.reciter.test.AbstractTestActivity;
import com.gmail.dailyefforts.reviwer.R;

public class LearningActivity extends AbstractTestActivity implements
        OnDragListener, OnClickListener {

    private static final String TAG = LearningActivity.class
            .getSimpleName();

    private Button mBtnCurrentWord;

    private int mDbCount;

    private Button mBtnArrowLeft;

    private Button mBtnArrowRight;

    private ViewFlipper mFlipper;

    private TextView mMeaning;

    private static ArrayList<String> mWrongWordList = new ArrayList<String>();
    private MyHandler mHandler;

    private Button mBtnShow;

    private Animation mAnimaScale;

    private TextView mSample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        mAnimaScale = AnimationUtils.loadAnimation(this, R.anim.scale);

        mHandler = new MyHandler(this);

        mBtnCurrentWord = (Button) findViewById(R.id.btn_word);

        mBtnArrowLeft = (Button) findViewById(R.id.btn_previous);
        mBtnArrowRight = (Button) findViewById(R.id.btn_next);
        mBtnShow = (Button) findViewById(R.id.btn_show);

        mFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        mMeaning = (TextView) findViewById(R.id.tv_meaning);
        mSample = (TextView) findViewById(R.id.tv_sample);

        mBtnArrowLeft.setOnClickListener(this);
        mBtnArrowRight.setOnClickListener(this);
        mBtnShow.setOnClickListener(this);

        mBtnCurrentWord.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        if (Config.DEBUG) {
                            Log.d(TAG, "onTouch() ACTION_DOWN");
                        }
                        ClipData dragData = ClipData.newPlainText("label", "text");
                        DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
                        v.clearAnimation();
                        v.startDrag(dragData, shadowBuilder, v, 0);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (Config.DEBUG) {
                            Log.d(TAG, "onTouch() ACTION_UP");
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }

        });

        mBtnCurrentWord.setOnDragListener(this);

        mDbCount = mDba.getCount();

        if (savedInstanceState == null) {
            if (mWrongWordList != null) {
                mWrongWordList.clear();
            }
        }

        Random random = new Random();
        int optNum = 4;

        mTestCases.clear();

        for (int i = 0; i < mWordList.size(); i++) {
            TestCase testCase = new TestCase();

            Word w = mWordList.get(i);

            if (w == null) {
                continue;
            }

            testCase.wordIdx = w.getId();
            int id = w.getId();
            if (Config.DEBUG) {
                Log.d(TAG, "onCreate() id: " + id);
            }
            arrList.clear();
            while (arrList.size() <= optNum) {
                int tmp = random.nextInt(mDbCount);
                if (tmp != 0 && tmp != id && !arrList.contains(tmp)) {
                    arrList.add(tmp);
                }
            }

            int answerIdx = random.nextInt(optNum);

            for (int j = 0; j < optNum; j++) {
                if (j == answerIdx) {
                    arrList.set(j, id);
                }

                switch (j) {
                    case 0:
                        testCase.topLeftIdx = arrList.get(j);
                        break;
                    case 1:
                        testCase.topRightIdx = arrList.get(j);
                        break;
                    case 2:
                        testCase.bottomLeftIdx = arrList.get(j);
                        break;
                    case 3:
                        testCase.bottomRightIdx = arrList.get(j);
                        break;
                }
            }

            mTestCases.add(testCase);

            if (Config.DEBUG) {
                Log.d(TAG,
                        "onCreate() test case-" + i + ", "
                                + testCase.toString());
            }
        }
        buildTestCase();
        mBtnShow.startAnimation(mAnimaScale);
    }

    private static ArrayList<TestCase> mTestCases = new ArrayList<LearningActivity.TestCase>();

    private class TestCase {
        public int wordIdx;
        public int topLeftIdx;
        public int topRightIdx;
        public int bottomLeftIdx;
        public int bottomRightIdx;

        @Override
        public String toString() {
            return String.format(Locale.getDefault(),
                    "testcase: %d, %d, %d, %d, %d", wordIdx, topLeftIdx,
                    topRightIdx, bottomLeftIdx, bottomRightIdx);
        }
    }

    private boolean mBingo;

    private static ArrayList<Integer> arrList = new ArrayList<Integer>();

    @Override
    protected void buildTestCase() {
        super.buildTestCase();
        if (mWordIdx >= mWordList.size()) {
            finish();
            return;
        }

        mBingo = false;

        TestCase testCase = mTestCases.get(mWordIdx);

        if (mBtnCurrentWord.getVisibility() != View.VISIBLE) {
            mBtnCurrentWord.setVisibility(View.VISIBLE);
        }

        int idxInDb = testCase.wordIdx;

        Word curentWord = mDba.getWordByIdx(idxInDb);

        mWord = curentWord.getWord();
        mBtnCurrentWord.setText(mWord);
        mMeaning.setText(curentWord.getMeaning());
        String sample = curentWord.getSample();
        if (sample != null) {
            if (Config.CURRENT_BOOK_NAME == Config.BOOK_NAME_LITERATURE) {
                String[] pair = sample.split("--");
                if (pair != null && pair.length == 2) {
                    sample = "《" + pair[0] + "》\n" + pair[1].trim();
                }
            }
            mSample.setText(sample);
        }

        if (mWordIdx == 0) {
            mBtnArrowLeft.setEnabled(false);
            mBtnArrowLeft.setAlpha(0.1F);
        } else {
            mBtnArrowLeft.setEnabled(true);
            mBtnArrowLeft.setAlpha(1.0F);
        }

        if (hasNext()) {
            mBtnArrowRight.setText(R.string.next);
        } else {
            mBtnArrowRight.setText(R.string.done);
        }

        mBtnShow.setText(R.string.show);

        invalidateOptionsMenu();
        mFlipper.setDisplayedChild(0);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        if (Config.DEBUG) {
            Log.d(TAG, "onDrag() event.getAction(): " + event.getAction());
        }
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:

                if (v.getId() == mBtnCurrentWord.getId()) {
                    mBtnCurrentWord.clearAnimation();
                    mBtnCurrentWord.setVisibility(View.INVISIBLE);
                }
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DROP:
                if (v.getId() == mBtnCurrentWord.getId()) {
                    mBtnCurrentWord.setVisibility(View.VISIBLE);
                } else {
                    // judge(v);
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                if (v.getId() == mBtnCurrentWord.getId()) {
                    if (!mBingo) {
                        mBtnCurrentWord.setVisibility(View.VISIBLE);
                    }
                } else {
                    v.clearAnimation();
                }
                break;
        }
        if (v.getId() == mBtnCurrentWord.getId()) {
            return false;
        } else {
            return true;
        }
    }

    private static class MyHandler extends Handler {

        public static final int MSG_FORWOARD = 0;

        private WeakReference<LearningActivity> mRef;

        public MyHandler(LearningActivity activity) {
            mRef = new WeakReference<LearningActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FORWOARD:
                    final LearningActivity activity = mRef.get();
                    if (activity != null) {
                        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
                        activity.mFlipper.setAnimation(anim);
                        activity.mFlipper.showNext();
                        if (activity.mFlipper.getDisplayedChild() == 0) {
                            activity.mBtnShow.setText(R.string.show);
                        } else {
                            activity.mBtnShow.setText(R.string.hide);
                        }
                    }
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous:
                backward();
                break;
            case R.id.btn_next:
                forward();
                break;
            case R.id.btn_show:
                mHandler.sendEmptyMessageDelayed(MyHandler.MSG_FORWOARD, 200);
                v.clearAnimation();
                break;
        }
    }

}
