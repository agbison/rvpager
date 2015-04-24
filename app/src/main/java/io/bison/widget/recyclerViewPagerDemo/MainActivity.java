
package io.bison.widget.recyclerViewPagerDemo;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {
    private final String ARG_SELECTED_LAYOUT_ID = "selectedLayoutId";

    private final int DEFAULT_LAYOUT = R.layout.layout_list;

    private int mSelectedLayoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rv);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        mSelectedLayoutId = DEFAULT_LAYOUT;
        if (savedInstanceState != null) {
            mSelectedLayoutId = savedInstanceState.getInt(ARG_SELECTED_LAYOUT_ID);
        }

        addLayoutTab(
                actionBar, R.layout.layout_list, R.mipmap.ic_launcher, "list");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_SELECTED_LAYOUT_ID, mSelectedLayoutId);
    }

    private void addLayoutTab(ActionBar actionBar, int layoutId, int iconId, String tag) {
        ActionBar.Tab tab = actionBar.newTab()
                .setText("")
                .setIcon(iconId)
                .setTabListener(new TabListener(layoutId, tag));
        actionBar.addTab(tab, layoutId == mSelectedLayoutId);
    }

    public class TabListener implements ActionBar.TabListener {
        private RVLayoutFragment mFragment;
        private final int mLayoutId;
        private final String mTag;

        public TabListener(int layoutId, String tag) {
            mLayoutId = layoutId;
            mTag = tag;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            mFragment = (RVLayoutFragment) getSupportFragmentManager().findFragmentByTag(mTag);
            if (mFragment == null) {
                mFragment = (RVLayoutFragment) RVLayoutFragment.newInstance(mLayoutId);
                ft.add(R.id.content, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }

            mSelectedLayoutId = mFragment.getLayoutId();
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }
    }

}
