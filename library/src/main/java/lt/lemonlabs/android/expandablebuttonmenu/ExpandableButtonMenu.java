/*
 * Copyright (C) 2013 Lemon Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lt.lemonlabs.android.expandablebuttonmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

@SuppressWarnings("ConstantConditions")
public class ExpandableButtonMenu extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = "ExpandableButtonMenu";

    public enum MenuButton {
        MID, LEFT, RIGHT
    }

    /**
     * DEFAULT BUTTON SIZE AND DISTANCE VALUES
     */

    private static final float DEFAULT_BOTTOM_PADDING = 0.05f;
    private static final float DEFAULT_MAIN_BUTTON_SIZE = 0.25f;
    private static final float DEFAULT_OTHER_BUTTON_SIZE = 0.2f;
    private static final float DEFAULT_BUTTON_DISTANCE_Y = 0.15f;
    private static final float DEFAULT_BUTTON_DISTANCE_X = 0.27f;


    /**
     * Screen metrics
     */
    protected int sWidth;
    protected int sHeight;

    private ExpandableMenuOverlay mParent;

    private View mOverlay;

    private View mMidContainer;
    private View mRightContainer;
    private View mLeftContainer;

    private ImageButton mCloseBtn;
    private ImageButton mMidBtn;
    private ImageButton mRightBtn;
    private ImageButton mLeftBtn;

    private TextView mMidText;
    private TextView mRightText;
    private TextView mLeftText;

    /**
     * Flag indicating that the menu is expanded or collapsed
     */
    private boolean mExpanded;

    /**
     * Flag indicating if clicking anywhere on the screen collapses the menu
     */
    private boolean mAllowOverlayClose = true;

    /**
     * Flag indicating that menu is being animated
     */
    private boolean mAnimating;


    /**
     * Menu button position variables in % of screen width or height
     */
    protected float bottomPadding = DEFAULT_BOTTOM_PADDING;
    protected float mainButtonSize = DEFAULT_MAIN_BUTTON_SIZE;
    protected float otherButtonSize = DEFAULT_OTHER_BUTTON_SIZE;
    protected float buttonDistanceY = DEFAULT_BUTTON_DISTANCE_Y;
    protected float buttonDistanceX = DEFAULT_BUTTON_DISTANCE_X;

    /**
     * Button click interface. Use setOnMenuButtonClickListener() to
     * register callbacks
     */
    private OnMenuButtonClick mListener;

    public ExpandableButtonMenu(Context context) {
        this(context, null, 0);
    }

    public ExpandableButtonMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableButtonMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate();
        parseAttributes(attrs);
        setViewLayoutParams();
        calculateAnimationProportions();
    }


    /**
     * Set a top level overlay that acts as a proxy to this view. In a
     * a current implementation the content of the expandable menu is
     * set to a dialog. This allows the control over native screen dim
     * and keyboard key callbacks.
     *
     * @param parent
     */
    public void setButtonMenuParentOverlay(ExpandableMenuOverlay parent) {
        mParent = parent;
    }

    /**
     * Set a callback on menu button clicks
     *
     * @param listener
     */
    public void setOnMenuButtonClickListener(OnMenuButtonClick listener) {
        mListener = listener;
    }

    /**
     * Returns the menu button container. The first child of the container is
     * a TextView, the second - an ImageButton
     *
     * @param button one of {@link MenuButton#LEFT}, {@link MenuButton#MID}, {@link MenuButton#RIGHT}
     */
    public View getMenuButton(MenuButton button) {
        switch (button) {
            case MID:
                return mMidContainer;
            case LEFT:
                return mLeftContainer;
            case RIGHT:
                return mRightContainer;
        }
        return null;
    }

    /**
     * Set text appearance for button text views
     *
     * @param appearanceResource
     */
    public void setMenuTextAppearance(int appearanceResource) {
        mLeftText.setTextAppearance(getContext(), appearanceResource);
        mMidText.setTextAppearance(getContext(), appearanceResource);
        mRightText.setTextAppearance(getContext(), appearanceResource);
    }

    /**
     * Set image resource for a menu button
     *
     * @param button
     * @param imageResource
     */
    public void setMenuButtonImage(MenuButton button, int imageResource) {
        setMenuButtonImage(button, getResources().getDrawable(imageResource));
    }

    /**
     * Set image drawable for a menu button
     *
     * @param button
     * @param drawable
     */
    public void setMenuButtonImage(MenuButton button, Drawable drawable) {
        switch (button) {
            case MID:
                mMidBtn.setImageDrawable(drawable);
                break;
            case LEFT:
                mLeftBtn.setImageDrawable(drawable);
                break;
            case RIGHT:
                mRightBtn.setImageDrawable(drawable);
                break;
        }
    }

    /**
     * Set string resource displayed under a menu button
     *
     * @param button
     * @param stringResource
     */
    public void setMenuButtonText(MenuButton button, int stringResource) {
        setMenuButtonText(button, getContext().getString(stringResource));
    }

    /**
     * Set text displayed under a menu button
     *
     * @param button
     * @param text
     */
    public void setMenuButtonText(MenuButton button, String text) {
        switch (button) {
            case MID:
                mMidText.setText(text);
                break;
            case LEFT:
                mLeftText.setText(text);
                break;
            case RIGHT:
                mRightText.setText(text);
                break;
        }
    }

    public void setAllowOverlayClose(boolean allow) {
        mAllowOverlayClose = allow;
    }

    public void setAnimating(boolean isAnimating) { mAnimating = isAnimating;}

    public boolean isExpanded() {
        return mExpanded;
    }

    public boolean isAllowOverlayClose() {return mAllowOverlayClose; }

    public float getMainButtonSize() {
        return mainButtonSize;
    }

    public float getBottomPadding() {
        return bottomPadding;
    }

    public float getOtherButtonSize() {
        return otherButtonSize;
    }

    public float getTranslationY() {
        return TRANSLATION_Y;
    }

    public float getTranslationX() {
        return TRANSLATION_X;
    }


    /**
     * Toggle the expandable menu button, expanding or collapsing it
     */
    public void toggle() {
        if (!mAnimating) {
            mAnimating = true;
            if (mExpanded) {
                animateCollapse();
            } else {
                animateExpand();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ebm__menu_overlay) {
            if (mExpanded && mAllowOverlayClose) toggle();
        } else if (id == R.id.ebm__menu_left_image) {
            if (mListener != null) mListener.onClick(MenuButton.LEFT);
        } else if (id == R.id.ebm__menu_middle_image) {
            if (mListener != null) mListener.onClick(MenuButton.MID);
        } else if (id == R.id.ebm__menu_right_image) {
            if (mListener != null) mListener.onClick(MenuButton.RIGHT);
        } else if (id == R.id.ebm__menu_close_image) {
            toggle();
        }
    }


    /**
     * Inflates the view
     */
    private void inflate() {
        ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.ebm__menu, this, true);

        mOverlay = findViewById(R.id.ebm__menu_overlay);

        mMidContainer = findViewById(R.id.ebm__menu_middle_container);
        mLeftContainer = findViewById(R.id.ebm__menu_left_container);
        mRightContainer = findViewById(R.id.ebm__menu_right_container);

        mMidText = (TextView) findViewById(R.id.ebm__menu_middle_text);
        mLeftText = (TextView) findViewById(R.id.ebm__menu_left_text);
        mRightText = (TextView) findViewById(R.id.ebm__menu_right_text);

        mCloseBtn = (ImageButton) findViewById(R.id.ebm__menu_close_image);
        mMidBtn = (ImageButton) findViewById(R.id.ebm__menu_middle_image);
        mRightBtn = (ImageButton) findViewById(R.id.ebm__menu_right_image);
        mLeftBtn = (ImageButton) findViewById(R.id.ebm__menu_left_image);

        sWidth = ScreenHelper.getScreenWidth(getContext());
        sHeight = ScreenHelper.getScreenHeight(getContext());

        mMidBtn.setEnabled(false);
        mRightBtn.setEnabled(false);
        mLeftBtn.setEnabled(false);

        mCloseBtn.setOnClickListener(this);
        mMidBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);
        mLeftBtn.setOnClickListener(this);
        mOverlay.setOnClickListener(this);
    }

    /**
     * Parses custom XML attributes
     *
     * @param attrs
     */
    private void parseAttributes(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ExpandableMenuOverlay, 0, 0);
            try {
                // button metrics
                mainButtonSize = a.getFloat(R.styleable.ExpandableMenuOverlay_mainButtonSize, DEFAULT_MAIN_BUTTON_SIZE);
                otherButtonSize = a.getFloat(R.styleable.ExpandableMenuOverlay_otherButtonSize, DEFAULT_OTHER_BUTTON_SIZE);
                bottomPadding = a.getFloat(R.styleable.ExpandableMenuOverlay_bottomPad, DEFAULT_BOTTOM_PADDING);
                buttonDistanceY = a.getFloat(R.styleable.ExpandableMenuOverlay_distanceY, DEFAULT_BUTTON_DISTANCE_Y);
                buttonDistanceX = a.getFloat(R.styleable.ExpandableMenuOverlay_distanceX, DEFAULT_BUTTON_DISTANCE_X);

                // button resources
                mCloseBtn.setBackgroundResource(a.getResourceId(R.styleable.ExpandableMenuOverlay_closeButtonSrc, 0));
                mLeftBtn.setBackgroundResource(a.getResourceId(R.styleable.ExpandableMenuOverlay_leftButtonSrc, 0));
                mRightBtn.setBackgroundResource(a.getResourceId(R.styleable.ExpandableMenuOverlay_rightButtonSrc, 0));
                mMidBtn.setBackgroundResource(a.getResourceId(R.styleable.ExpandableMenuOverlay_midButtonSrc, 0));

                // button text
                mLeftText.setText(a.getResourceId(R.styleable.ExpandableMenuOverlay_leftButtonText, R.string.empty));
                mRightText.setText(a.getResourceId(R.styleable.ExpandableMenuOverlay_rightButtonText, R.string.empty));
                mMidText.setText(a.getResourceId(R.styleable.ExpandableMenuOverlay_midButtonText, R.string.empty));

            } finally {
                a.recycle();
            }
        }
    }

    /**
     * Initialized the layout of menu buttons. Sets button sizes and distances between them
     * by a % of screen width or height accordingly.
     * Some extra padding between buttons is added by default to avoid intersections.
     */
    private void setViewLayoutParams() {
        // Some extra margin to center other buttons in the center of the main button
        final int EXTRA_MARGIN = (int) (sWidth * (mainButtonSize - otherButtonSize) / 2);


        Log.d(TAG, "otherButton: " + otherButtonSize);
        Log.d(TAG, "mainButton: " + mainButtonSize);

        RelativeLayout.LayoutParams rParams = (LayoutParams) mCloseBtn.getLayoutParams();
        rParams.width = (int) (sWidth * otherButtonSize);
        rParams.height = (int) (sWidth * otherButtonSize);
        rParams.setMargins(0, 0, 0, (int) (sHeight * bottomPadding + EXTRA_MARGIN));

        rParams = (LayoutParams) mMidContainer.getLayoutParams();
        rParams.setMargins(0, 0, 0, (int) (sHeight * bottomPadding + EXTRA_MARGIN));
        rParams = (LayoutParams) mRightContainer.getLayoutParams();
        rParams.setMargins(0, 0, 0, (int) (sHeight * bottomPadding + EXTRA_MARGIN));
        rParams = (LayoutParams) mLeftContainer.getLayoutParams();
        rParams.setMargins(0, 0, 0, (int) (sHeight * bottomPadding + EXTRA_MARGIN));

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mMidBtn.getLayoutParams();
        params.width = (int) (sWidth * otherButtonSize );
        params.height = (int) (sWidth * otherButtonSize );

        params = (LinearLayout.LayoutParams) mRightBtn.getLayoutParams();
        params.width = (int) (sWidth * otherButtonSize );
        params.height = (int) (sWidth * otherButtonSize);

        params = (LinearLayout.LayoutParams) mLeftBtn.getLayoutParams();
        params.width = (int) (sWidth * otherButtonSize );
        params.height = (int) (sWidth * otherButtonSize );
    }


    /**
     * ANIMATION DEFINITIONS
     */

    /**
     * We don't use AnimatorSet so we have our own counter to see whether all animations have ended
     */
    private volatile byte ANIMATION_COUNTER;

    /**
     * Collapse and expand animation duration
     */
    private static final int ANIMATION_DURATION = 300;

    /**
     * Used interpolators
     */
    private static final float INTERPOLATOR_WEIGHT = 3.0f;
    private AnticipateInterpolator anticipation;
    private OvershootInterpolator overshoot;


    /**
     * Translation in Y axis of all three menu buttons
     */
    protected float TRANSLATION_Y;

    /**
     * Translation in X axis for left and right buttons
     */
    protected float TRANSLATION_X;

    /**
     * Initialized animation properties
     */
    private void calculateAnimationProportions() {
        TRANSLATION_Y = sHeight * buttonDistanceY;
        TRANSLATION_X = sWidth * buttonDistanceX;

        anticipation = new AnticipateInterpolator(INTERPOLATOR_WEIGHT);
        overshoot = new OvershootInterpolator(INTERPOLATOR_WEIGHT);
    }

    /**
     * Start expand animation
     */
    private void animateExpand() {
        mCloseBtn.setVisibility(View.VISIBLE);
        mMidContainer.setVisibility(View.VISIBLE);
        mRightContainer.setVisibility(View.VISIBLE);
        mLeftContainer.setVisibility(View.VISIBLE);

        setButtonsVisibleForPreHC();

        ANIMATION_COUNTER = 0;

        ViewPropertyAnimator.animate(mMidContainer).setDuration(ANIMATION_DURATION).translationYBy(-TRANSLATION_Y).setInterpolator(overshoot).setListener(ON_EXPAND_COLLAPSE_LISTENER);
        ViewPropertyAnimator.animate(mRightContainer).setDuration(ANIMATION_DURATION).translationYBy(-TRANSLATION_Y).translationXBy(TRANSLATION_X).setInterpolator(overshoot).setListener(ON_EXPAND_COLLAPSE_LISTENER);
        ViewPropertyAnimator.animate(mLeftContainer).setDuration(ANIMATION_DURATION).translationYBy(-TRANSLATION_Y).translationXBy(-TRANSLATION_X).setInterpolator(overshoot).setListener(ON_EXPAND_COLLAPSE_LISTENER);
    }

    /**
     * Start collapse animation
     */
    private void animateCollapse() {
        mCloseBtn.setVisibility(View.VISIBLE);

        ANIMATION_COUNTER = 0;

        ViewPropertyAnimator.animate(mMidContainer).setDuration(ANIMATION_DURATION).translationYBy(TRANSLATION_Y).setInterpolator(anticipation).setListener(ON_EXPAND_COLLAPSE_LISTENER);
        ViewPropertyAnimator.animate(mRightContainer).setDuration(ANIMATION_DURATION).translationYBy(TRANSLATION_Y).translationXBy(-TRANSLATION_X).setInterpolator(anticipation).setListener(ON_EXPAND_COLLAPSE_LISTENER);
        ViewPropertyAnimator.animate(mLeftContainer).setDuration(ANIMATION_DURATION).translationYBy(TRANSLATION_Y).translationXBy(TRANSLATION_X).setInterpolator(anticipation).setListener(ON_EXPAND_COLLAPSE_LISTENER);

    }

    /**
     * Manually invalidate views for pre-Honeycomb devices
     */
    private void invalidateViewsForPreHC() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

            final int EXTRA_BOTTOM_MARGIN = (int) (sWidth * (mainButtonSize - otherButtonSize) / 2);
            if (mExpanded) {

                ViewHelper.setAlpha(mMidContainer, 0f);
                ViewHelper.setAlpha(mRightContainer, 0f);
                ViewHelper.setAlpha(mLeftContainer, 0f);

                ViewPropertyAnimator.animate(mMidContainer).setDuration(0).translationYBy(-TRANSLATION_Y);
                ViewPropertyAnimator.animate(mRightContainer).setDuration(0).translationYBy(-TRANSLATION_Y).translationXBy(TRANSLATION_X);
                ViewPropertyAnimator.animate(mLeftContainer).setDuration(0).translationYBy(-TRANSLATION_Y).translationXBy(-TRANSLATION_X);

                RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, (int) (sHeight * bottomPadding + EXTRA_BOTTOM_MARGIN));
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                mMidContainer.setLayoutParams(params);
                mRightContainer.setLayoutParams(params);
                mLeftContainer.setLayoutParams(params);

            } else {

                final int CENTER_RIGHT_POSITION = mMidContainer.getRight();
                final int EXTRA_MARGIN = mLeftContainer.getLeft() - (int) (CENTER_RIGHT_POSITION - TRANSLATION_X);

                ViewPropertyAnimator.animate(mMidContainer).setDuration(0).translationYBy(TRANSLATION_Y);
                ViewPropertyAnimator.animate(mRightContainer).setDuration(0).translationYBy(TRANSLATION_Y).translationXBy(-TRANSLATION_X);
                ViewPropertyAnimator.animate(mLeftContainer).setDuration(0).translationYBy(TRANSLATION_Y).translationXBy(TRANSLATION_X);

                RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.setMargins(0, 0, 0, (int) (sHeight * bottomPadding + TRANSLATION_Y + EXTRA_BOTTOM_MARGIN));
                mMidContainer.setLayoutParams(params);

                params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.RIGHT_OF, mMidContainer.getId());
                params.setMargins(EXTRA_MARGIN, 0, 0, (int) (sHeight * bottomPadding + TRANSLATION_Y + EXTRA_BOTTOM_MARGIN));
                mRightContainer.setLayoutParams(params);

                params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.LEFT_OF, mMidContainer.getId());
                params.setMargins(0, 0, EXTRA_MARGIN, (int) (sHeight * bottomPadding + TRANSLATION_Y + EXTRA_BOTTOM_MARGIN));
                mLeftContainer.setLayoutParams(params);

            }
        }
    }

    /**
     * Hide views for pre-Honeycomb devices
     */
    private void setButtonsVisibleForPreHC() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            ViewHelper.setAlpha(mMidContainer, 1.0f);
            ViewHelper.setAlpha(mLeftContainer, 1.0f);
            ViewHelper.setAlpha(mRightContainer, 1.0f);
        }
    }

    /**
     * Listener for expand and collapse animations
     */
    private Animator.AnimatorListener ON_EXPAND_COLLAPSE_LISTENER = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            if (mCloseBtn.isEnabled())
                mCloseBtn.setEnabled(false);
            if (mOverlay.isEnabled())
                mOverlay.setEnabled(false);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            ANIMATION_COUNTER++;
            if (ANIMATION_COUNTER == 1 && mExpanded)
                mParent.showInitButton();

            if (ANIMATION_COUNTER == 3) {

                if (mExpanded) {
                    mCloseBtn.setVisibility(View.GONE);
                    mMidContainer.setVisibility(View.GONE);
                    mRightContainer.setVisibility(View.GONE);
                    mLeftContainer.setVisibility(View.GONE);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mParent.dismiss();
                            mParent.mDismissing = false;
                        }
                    }, 75);
                }

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        invalidateViewsForPreHC();
                        mAnimating = false;
                        mExpanded = !mExpanded;
                    }
                }, 50);


                mCloseBtn.setEnabled(true);
                mMidBtn.setEnabled(true);
                mRightBtn.setEnabled(true);
                mLeftBtn.setEnabled(true);
                mOverlay.setEnabled(true);
            }

        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    /**
     * Button click callback interface
     */
    public interface OnMenuButtonClick {
        public void onClick(MenuButton action);
    }


}

