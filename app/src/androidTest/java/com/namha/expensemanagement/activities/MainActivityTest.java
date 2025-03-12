package com.namha.expensemanagement.activities;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.namha.expensemanagement.R;
import com.namha.expensemanagement.ui.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testChatbotButtonClick() {
        onView(withId(R.id.fabChatbot)).perform(click());
    }

    @Test
    public void testMenuItemClick() {
        onView(withId(R.id.tvHome)).perform(click());
        onView(withId(R.id.tvHistory)).perform(click());
        onView(withId(R.id.tvReport)).perform(click());
        onView(withId(R.id.tvUpdate)).perform(click());
        onView(withId(R.id.tvSetting)).perform(click());
    }
}
