package com.techjumper.corelib.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.SparseArray;

import com.techjumper.corelib.R;
import com.techjumper.corelib.entity.BaseFragmentActivitySaveEntity;
import com.techjumper.corelib.mvp.interfaces.IBaseActivityPresenter;
import com.techjumper.corelib.ui.fragment.BaseFragment;
import com.techjumper.corelib.utils.common.AcHelper;
import com.techjumper.corelib.utils.window.KeyboardUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/14
 * * * * * * * * * * * * * * * * * * * * * * *
 **/

/**
 * 封装了 Fragment 的切换<br/>
 * 直接调用 {@link #switchFragment(int, BaseFragment, boolean)} 即可
 */
public abstract class BaseFragmentActivity<P extends IBaseActivityPresenter> extends BaseViewActivity<P> implements FragmentManager.OnBackStackChangedListener {

    /**
     * 所有Fragment tag的集合
     * key: container id
     */
    private Set<String> mFragmentTags;
    /**
     * 当前Fragment的tag
     * key: containerId
     */
    private SparseArray<String> mCurrFragmentTags;

    /**
     * Fragment可以同时添加多个,这里只保存一个支持返回栈的containerId
     */
    private int mPrimaryContainer;
    /**
     * 保存一个未添加入返回栈的Tag;
     */
    private String mNoStackFragmentTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (savedInstanceState == null) return;

        try {
            BaseFragmentActivitySaveEntity entity = savedInstanceState.getParcelable(BaseFragmentActivitySaveEntity.ENTITY_KEY);
            mPrimaryContainer = entity.getContainerId();
            mNoStackFragmentTag = entity.getNoStackTag();
            String[] allFragmentTags = entity.getAllFragmentTags();
            Set<String> tagSet = getOrInitFragmentTags();
            tagSet.clear();
            Collections.addAll(tagSet, allFragmentTags);
            ArrayList<BaseFragmentActivitySaveEntity.IntKeyStringValue> keyValueList = entity.getKeyValue();
            SparseArray<String> currTags = getOrInitCurrFragmentTags();
            currTags.clear();
            for (BaseFragmentActivitySaveEntity.IntKeyStringValue keyValue : keyValueList) {
                currTags.put(keyValue.getKey(), keyValue.getTag());
            }
            FragmentManager fManager = getSupportFragmentManager();
            FragmentTransaction ft = fManager.beginTransaction();
            for (String tag : tagSet) {
                hideFragmentFromTagNoCommit(fManager, ft, tag);
            }
            for (BaseFragmentActivitySaveEntity.IntKeyStringValue keyValue : keyValueList) {
                Fragment fragment = fManager.findFragmentByTag(keyValue.getTag());
                if (fragment == null || !(fragment instanceof BaseFragment)) continue;
                showOrAddFragmentNoCommit(fManager, ft, keyValue, (BaseFragment) fragment, false);
            }
            ft.commitAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Set<String> tagSet = getOrInitFragmentTags();
        String[] allTags = new String[tagSet.size()];
        Iterator<String> it = tagSet.iterator();
        for (int i = 0; it.hasNext(); i++) {
            allTags[i] = it.next();
        }

        ArrayList<BaseFragmentActivitySaveEntity.IntKeyStringValue> keyValueList = new ArrayList<>();
        SparseArray<String> currFragmentTags = getOrInitCurrFragmentTags();
        for (int i = 0; i < currFragmentTags.size(); i++) {
            BaseFragmentActivitySaveEntity.IntKeyStringValue keyValue
                    = new BaseFragmentActivitySaveEntity.IntKeyStringValue();
            keyValue.setKey(currFragmentTags.keyAt(i));
            keyValue.setTag(currFragmentTags.valueAt(i));
            keyValueList.add(keyValue);
        }

        BaseFragmentActivitySaveEntity entity = new BaseFragmentActivitySaveEntity();
        entity.setAllFragmentTags(allTags);
        entity.setKeyValue(keyValueList);
        entity.setContainerId(mPrimaryContainer);
        entity.setNoStackTag(mNoStackFragmentTag);

        outState.putParcelable(BaseFragmentActivitySaveEntity.ENTITY_KEY, entity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

    protected void switchFragment(int containerId, BaseFragment fragment, boolean addToBackStack) {
        switchFragment(containerId, fragment, addToBackStack, true);
    }

    protected void switchFragment(int containerId, BaseFragment fragment, boolean addToBackStack, boolean anim) {
        addNewFragmentToSet(fragment);
        String lastFragmentTag = getCurrFragmentTag(containerId);
        setCurrFragmentTag(containerId, fragment);

        if (mPrimaryContainer == 0) mPrimaryContainer = containerId;

        FragmentManager fManager = getSupportFragmentManager();
        FragmentTransaction ft = fManager.beginTransaction();

        hideFragmentFromTagNoCommit(fManager, ft, lastFragmentTag, anim);
        showOrAddFragmentNoCommit(fManager, ft, containerId, fragment, addToBackStack, anim);
        ft.commitAllowingStateLoss();
    }

//    protected void replaceFragment(int containerId, BaseFragment fragment) {
//        addNewFragmentToSet(fragment);
//        setCurrFragmentTag(containerId, fragment);
//        mNoStackFragmentTag = getFragmentSignature(fragment);
//        getSupportFragmentManager().beginTransaction().replace(containerId, fragment).commitAllowingStateLoss();
//    }
//

    @SuppressLint("CommitTransaction")
    private void showOrAddFragmentNoCommit(FragmentManager fManager, FragmentTransaction ft
            , int containerId, BaseFragment fragment, boolean addToBackStack, boolean anim) {
        if (anim) {
            ft.setCustomAnimations(AcHelper.defaultStartAnimEnter
                    , AcHelper.defaultStartAnimExit
                    , AcHelper.defaultBackAnimEnter
                    , AcHelper.defaultBackAnimExit);
        } else {
            ft.setCustomAnimations(0, 0, 0, 0);
        }
        Fragment lastFragment = fManager.findFragmentByTag(getFragmentSignature(fragment));
        if (lastFragment != null) {
            ft.show(lastFragment);
            BaseFragment baseFragment = (BaseFragment) lastFragment;
            baseFragment.onShowAndHideHint(true);
            if (anim) {
                baseFragment.startAnimation(AcHelper.defaultStartAnimEnter, true);
            }
        } else {
            ft.add(containerId, fragment, getFragmentSignature(fragment));
            if (addToBackStack) {
                ft.addToBackStack(getFragmentSignature(fragment));
            } else if (TextUtils.isEmpty(mNoStackFragmentTag)) {
                mNoStackFragmentTag = getFragmentSignature(fragment);
            }
        }

    }

    @SuppressLint("CommitTransaction")
    private void showOrAddFragmentNoCommit(FragmentManager fManager, FragmentTransaction ft
            , BaseFragmentActivitySaveEntity.IntKeyStringValue keyValue, BaseFragment fragment, boolean addToBackStack) {
        ft.setCustomAnimations(0, 0, 0, 0);
        Fragment lastFragment = fManager.findFragmentByTag(keyValue.getTag());
        if (lastFragment != null) {
            ft.show(lastFragment);
        } else {
            ft.add(keyValue.getKey(), fragment, keyValue.getTag());
            if (addToBackStack) ft.addToBackStack(keyValue.getTag());
        }

    }

    @SuppressLint("CommitTransaction")
    private void hideFragmentFromTagNoCommit(FragmentManager fManager, FragmentTransaction ft, String tag) {
        hideFragmentFromTagNoCommit(fManager, ft, tag, false);
    }

    private void hideFragmentFromTagNoCommit(FragmentManager fManager, FragmentTransaction ft, String tag, boolean anim) {
        if (TextUtils.isEmpty(tag)) return;
        Fragment lastFragment = fManager.findFragmentByTag(tag);
        if (lastFragment == null) return;
        ft.hide(lastFragment);
        BaseFragment baseFragment = (BaseFragment) lastFragment;
        baseFragment.onShowAndHideHint(false);
        if (!anim) return;
        baseFragment.startAnimation(AcHelper.defaultStartAnimExit, false);

    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        boolean canContinue = true;
        try {
            if (!fm.popBackStackImmediate()) {
                AcHelper.finish(this);
                return;
            }
        } catch (Exception ignored) {
            canContinue = false;
        }
        if (!canContinue) return;

        BaseFragment fragment = null;
        int count = fm.getBackStackEntryCount();
        if (count > 0) {
            FragmentManager.BackStackEntry backEntry = fm.getBackStackEntryAt(count - 1);
            fragment = (BaseFragment) fm.findFragmentByTag(backEntry.getName());
        } else if (!TextUtils.isEmpty(mNoStackFragmentTag)) {
            fragment = (BaseFragment) fm.findFragmentByTag(mNoStackFragmentTag);
        }
        if (fragment != null) {
            fragment.startAnimation(AcHelper.defaultBackAnimEnter, true);
            fragment.onShowAndHideHint(true);
        }

    }

    public void removeFragment(BaseFragment fragment) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragment.getFragmentSignature());
        if (f != null) {
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }

    private void addNewFragmentToSet(BaseFragment fragment) {
        Set<String> set = getOrInitFragmentTags();
        set.add(getFragmentSignature(fragment));
    }

    private void setCurrFragmentTag(int containerId, BaseFragment fragment) {
        SparseArray<String> fragmentTags = getOrInitCurrFragmentTags();
        fragmentTags.put(containerId, getFragmentSignature(fragment));
    }

    private void updateCurrFragmentTag() {
        if (mPrimaryContainer == 0) return;
        SparseArray<String> fragmentTags = getOrInitCurrFragmentTags();
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count <= 0) {
            fragmentTags.put(mPrimaryContainer, mNoStackFragmentTag);
            return;
        }
        String name = getSupportFragmentManager().getBackStackEntryAt(count - 1).getName();
        fragmentTags.put(mPrimaryContainer, name);
    }

    private String getCurrFragmentTag(int containerId) {
        return getOrInitCurrFragmentTags().get(containerId);
    }

    private String getFragmentSignature(BaseFragment fragment) {
        return fragment.getFragmentSignature();
    }

    private Set<String> getOrInitFragmentTags() {
        if (mFragmentTags == null) mFragmentTags = new HashSet<>();
        return mFragmentTags;
    }

    private SparseArray<String> getOrInitCurrFragmentTags() {
        if (mCurrFragmentTags == null) mCurrFragmentTags = new SparseArray<>();
        return mCurrFragmentTags;
    }


    @Override
    public void onBackStackChanged() {
        updateCurrFragmentTag();
    }

}
