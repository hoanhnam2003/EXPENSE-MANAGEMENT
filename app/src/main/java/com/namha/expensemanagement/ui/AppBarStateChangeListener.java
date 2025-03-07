package com.namha.expensemanagement.ui;

import com.google.android.material.appbar.AppBarLayout;

public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
    public enum State {
        EXPANDED, COLLAPSED, IDLE
    }

    private int dScrollRange = 0;
    private int lastOffset = -1;
    private State mCurrentState = State.IDLE;
    private int currentScrollRange = -1;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (lastOffset == verticalOffset) {
            return;
        }
        if (currentScrollRange != appBarLayout.getTotalScrollRange()) {
            dScrollRange = Math.abs(appBarLayout.getTotalScrollRange() - currentScrollRange);
        }
        lastOffset = verticalOffset;
        if (currentScrollRange == -1) {
            currentScrollRange = appBarLayout.getTotalScrollRange();
        }
        if (verticalOffset == 0) {
            if (mCurrentState != State.EXPANDED) {
                mCurrentState = State.EXPANDED;
                onStateChanged(appBarLayout, State.EXPANDED);
                updateCurrentScrollRange(appBarLayout);
            }
        } else if (Math.abs(verticalOffset) >= currentScrollRange) {
            if (mCurrentState != State.COLLAPSED) {
                mCurrentState = State.COLLAPSED;
                onStateChanged(appBarLayout, State.COLLAPSED);
                updateCurrentScrollRange(appBarLayout);
            }
        } else {
            if (mCurrentState != State.IDLE) {
                mCurrentState = State.IDLE;
                onStateChanged(appBarLayout, State.IDLE);
            }
            updateCurrentScrollRange(appBarLayout);
        }
    }

    private void updateCurrentScrollRange(AppBarLayout appBar) {
        try {
            if (appBar != null) {
                currentScrollRange = appBar.getTotalScrollRange();
            }
        } catch (Exception e) {
            currentScrollRange = -1;
        }
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
}