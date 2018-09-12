package com.sneyder.cryptotracker.ui.main;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.sneyder.cryptotracker.R;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

  @Rule
  public ActivityTestRule<MainActivity> mActivityTestRule =
      new ActivityTestRule<>(MainActivity.class);

  @Test
  public void mainActivityTest() {
    // Added a sleep statement to match the app's execution delay.
    // The recommended way to handle such scenarios is to use Espresso idling resources:
    // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
    try {
      Thread.sleep(60000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    ViewInteraction appCompatImageView = onView(
        allOf(withId(R.id.toggleFavoriteImageView),
            childAtPosition(
                childAtPosition(
                    withId(R.id.cryptoCurrenciesRecyclerView),
                    0),
                4),
            isDisplayed()));
    appCompatImageView.perform(click());

    ViewInteraction recyclerView = onView(
        allOf(withId(R.id.cryptoCurrenciesRecyclerView),
            childAtPosition(
                withId(R.id.swipeRefreshCurrencies),
                0)));
    recyclerView.perform(actionOnItemAtPosition(0, click()));

    // Added a sleep statement to match the app's execution delay.
    // The recommended way to handle such scenarios is to use Espresso idling resources:
    // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
    try {
      Thread.sleep(3559166);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    ViewInteraction switch_ = onView(
        allOf(withId(R.id.priceAboveSwitch),
            childAtPosition(
                childAtPosition(
                    withClassName(is("android.support.v7.widget.CardView")),
                    0),
                2),
            isDisplayed()));
    switch_.perform(click());

    ViewInteraction appCompatEditText = onView(
        allOf(withId(R.id.priceAboveEditText),
            childAtPosition(
                childAtPosition(
                    withClassName(is("android.support.v7.widget.CardView")),
                    0),
                4),
            isDisplayed()));
    appCompatEditText.perform(replaceText("12000"), closeSoftKeyboard());

    pressBack();

    ViewInteraction appCompatSpinner = onView(
        allOf(withId(R.id.currencySpinner),
            childAtPosition(
                childAtPosition(
                    withClassName(is("android.widget.LinearLayout")),
                    0),
                3)));
    appCompatSpinner.perform(scrollTo(), click());

    DataInteraction appCompatTextView = onData(anything())
        .inAdapterView(childAtPosition(
            withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
            0))
        .atPosition(3);
    appCompatTextView.perform(click());

    ViewInteraction appCompatImageButton = onView(
        allOf(withContentDescription("Navigate up"),
            childAtPosition(
                allOf(withId(R.id.toolbar),
                    childAtPosition(
                        withClassName(is("android.support.constraint.ConstraintLayout")),
                        0)),
                0)));
    appCompatImageButton.perform(scrollTo(), click());

    // Added a sleep statement to match the app's execution delay.
    // The recommended way to handle such scenarios is to use Espresso idling resources:
    // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
    try {
      Thread.sleep(3562017);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    ViewInteraction appCompatSpinner2 = onView(
        allOf(withId(R.id.currencySpinner),
            childAtPosition(
                childAtPosition(
                    withId(R.id.containerLayout),
                    0),
                1),
            isDisplayed()));
    appCompatSpinner2.perform(click());

    DataInteraction appCompatTextView2 = onData(anything())
        .inAdapterView(childAtPosition(
            withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
            0))
        .atPosition(11);
    appCompatTextView2.perform(click());

    ViewInteraction appCompatSpinner3 = onView(
        allOf(withId(R.id.percentChangeSpinner),
            childAtPosition(
                childAtPosition(
                    withId(R.id.containerLayout),
                    0),
                2),
            isDisplayed()));
    appCompatSpinner3.perform(click());

    DataInteraction appCompatTextView3 = onData(anything())
        .inAdapterView(childAtPosition(
            withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
            0))
        .atPosition(1);
    appCompatTextView3.perform(click());

    ViewInteraction customTextView = onView(
        allOf(withId(R.id.sortByTextView), withText("Market Cap(DESC)"),
            childAtPosition(
                allOf(withId(R.id.sortByTextViewContainer),
                    childAtPosition(
                        withClassName(is("android.support.constraint.ConstraintLayout")),
                        0)),
                0),
            isDisplayed()));
    customTextView.perform(click());

    DataInteraction appCompatCheckedTextView = onData(anything())
        .inAdapterView(allOf(withId(R.id.select_dialog_listview),
            childAtPosition(
                withId(R.id.contentPanel),
                0)))
        .atPosition(2);
    appCompatCheckedTextView.perform(click());

    // Added a sleep statement to match the app's execution delay.
    // The recommended way to handle such scenarios is to use Espresso idling resources:
    // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    ViewInteraction appCompatImageButton2 = onView(
        allOf(withContentDescription("Open navigation drawer"),
            childAtPosition(
                allOf(withId(R.id.toolbar),
                    childAtPosition(
                        withClassName(is("android.support.design.widget.AppBarLayout")),
                        0)),
                1),
            isDisplayed()));
    appCompatImageButton2.perform(click());
  }

  private static Matcher<View> childAtPosition(
      final Matcher<View> parentMatcher, final int position) {

    return new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup && parentMatcher.matches(parent)
            && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }
}
