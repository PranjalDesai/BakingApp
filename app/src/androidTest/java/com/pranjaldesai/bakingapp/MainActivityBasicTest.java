package com.pranjaldesai.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityBasicTest {

    private IdlingResource mIdlingResource;

    @Rule public ActivityTestRule<MainActivity> mainActivityActivityTestRule= new ActivityTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mainActivityActivityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);

    }

    @Test
    public void MainActivityRecyclerViewIsDisplayed(){
        onView(withId(R.id.recipe_list)).check(matches(isDisplayed()));
    }

    @Test
    public void clickRecyclerViewItemOpensRecipeStepListActivity(){
        onView(withId(R.id.recipe_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.nested_scrollView)).check(matches(isDisplayed()));
    }


    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
