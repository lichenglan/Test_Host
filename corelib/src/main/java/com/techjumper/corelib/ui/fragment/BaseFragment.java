package com.techjumper.corelib.ui.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.techjumper.corelib.ui.activity.BaseFragmentActivity;
import com.techjumper.corelib.utils.UI;

import butterknife.ButterKnife;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/12
 * * * * * * * * * * * * * * * * * * * * * * *
 **/

public abstract class BaseFragment extends Fragment {

    public static final String SAVE_STATE_BUNDLE = "save_state_bundle";
    private final static String SAVED_STATE_SHOULD_SAVE = "saved_state_should_save";


    protected BaseFragment mThis;
    protected View mViewRoot;
    protected UI mUi;
    protected SparseArray<Parcelable> mSavedState;
    private Animation mAnimation;
//    private Handler mHandler = new Handler();

    private Bundle mSaveStateBundle;
    /**
     * 用来控制被回收时是否应该回调restoreState()与saveState()
     * false不回调, true回调
     */
    private boolean mShouldSaveAndRestore;

    public BaseFragment() {
        if (getArguments() == null)
            setArguments(new Bundle());
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThis = this;
        restoreState();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        mViewRoot = inflateView(inflater, savedInstanceState);
        if (mViewRoot == null) {
            mViewRoot = new View(getContext());
        }
        mUi = UI.create(mViewRoot);
        ButterKnife.bind(this, mViewRoot);

        initData(savedInstanceState);
        initView(savedInstanceState);
        mViewRoot.post(() -> onViewInited(savedInstanceState));
        return mViewRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mSavedState != null)
            view.restoreHierarchyState(mSavedState);
    }

    protected <T extends View> T find(int id) {
        return mUi.findById(id);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAnimation != null) {
            mAnimation.cancel();
        }
        saveState();
        ButterKnife.unbind(this);
        if (getView() != null) {
            mSavedState = new SparseArray<>();
            getView().saveHierarchyState(mSavedState);
        }
    }

    protected abstract View inflateView(LayoutInflater inflater, Bundle savedInstanceState);

    protected abstract void initData(Bundle savedInstanceState);

    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 界面初始化完毕,此时控件已获得宽高,可以弹对话框等等
     */
    protected abstract void onViewInited(Bundle savedInstanceState);

    /**
     * @return 返回一个独一无二的用来标识这个Fragment身份的字符串
     */
    public String getFragmentSignature() {
        return getClass().getName();
    }

    /**
     * 界面回收时保存数据
     */
    protected void onSaveState(Bundle saveBundle) {

    }

    /**
     * 界面回收时保存数据,但不能包含View数据
     */
    protected void onSaveStateNoView(Bundle saveBundle) {

    }

    /**
     * 当调用了show()和hide()的回调
     */
    public void onShowAndHideHint(boolean isShow) {
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
    }

    public void startAnimation(int animRes, boolean show) {
        if (mAnimation != null) mAnimation.cancel();
        if (mViewRoot == null) return;
        mAnimation = AnimationUtils.loadAnimation(getActivity(), animRes);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                FragmentActivity activity = getActivity();
//                if (activity != null && !activity.isFinishing()) {
//                    Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(getFragmentSignature());
//                    if (fragment != null) {
//                        mHandler.postDelayed(() -> {
//                            activity.getSupportFragmentManager().beginTransaction().show(fragment).commitAllowingStateLoss();
//                        }, 1000);
//                    }
//                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                if (show) return;
//                FragmentActivity activity = getActivity();
//                if (activity != null && !activity.isFinishing()) {
//                    Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(getFragmentSignature());
//                    if (fragment != null) {
//                        activity.getSupportFragmentManager().beginTransaction().hide(fragment).commitAllowingStateLoss();
//                    }
//                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mViewRoot.post(() -> mViewRoot.startAnimation(mAnimation));
    }


    private void saveState() {
        if (mSaveStateBundle == null) {
            if (getArguments() == null) return;
            mSaveStateBundle = getArguments().getBundle(SAVE_STATE_BUNDLE);
        }

        if (mSaveStateBundle == null) {
            mSaveStateBundle = new Bundle();
            getArguments().putBundle(SAVE_STATE_BUNDLE, mSaveStateBundle);
        }
        mSaveStateBundle.putBoolean(SAVED_STATE_SHOULD_SAVE, mShouldSaveAndRestore);
        if (!mShouldSaveAndRestore) return;
        if (getView() != null) onSaveState(mSaveStateBundle);
        else onSaveStateNoView(mSaveStateBundle);

    }

    private void restoreState() {
        Bundle args = getArguments();
        if (args == null) return;

        mSaveStateBundle = args.getBundle(SAVE_STATE_BUNDLE);
        if (mSaveStateBundle == null) return;

        mShouldSaveAndRestore = mSaveStateBundle.getBoolean(SAVED_STATE_SHOULD_SAVE, false);
        if (!mShouldSaveAndRestore) mSaveStateBundle = null;
    }

    public boolean hasRestoreData() {
        return mSaveStateBundle != null;
    }

    public Bundle getRestoreData() {
        return mSaveStateBundle;
    }

    public BaseFragmentActivity getBaseFragmentActivity() {
        BaseFragmentActivity activity = null;
        try {
            activity = (BaseFragmentActivity) getActivity();
        } catch (Exception ignored) {
        }
        return activity;
    }

    /**
     * 设置当页面被回收时,是否保存数据
     */
    public void shouldSave(boolean should) {
        mShouldSaveAndRestore = should;
    }

}
