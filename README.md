ExpandableButtonMenu
==================

ExpandableButtonMenu is an Android library which implements an expandable button that can be used as a substitute of a fixed size menu.
It is a Foursquare like button that expands into three buttons.

![Example Image][3]

Including in your project
-------------------------

The library is available on Maven Central

    dependencies {
        compile 'co.lemonlabs:expandable-button-menu:1.0.0'
    }


Usage
-----

The library supports Android 2.3+.

Just include this view to your root layout (current only RelativeLayout is supported):

    <lt.lemonlabs.android.expandablebuttonmenu.ExpandableMenuOverlay
        android:id="@+id/button_menu"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:src="@drawable/circle_home"
    />

Custom attributes can be used for easy customization. Add this to your layout declaration:

    xmlns:ebm="http://schemas.android.com/apk/res-auto"

and you can use these attributes

    ebm:dimAmount="0.8"        // Screen dim amount when menu is expanded
    ebm:mainButtonSize="0.25"  // Main button size in % of screen height
    ebm:otherButtonSize="0.22" // Expanded menu button size in % of screen height
    ebm:distanceY="0.17"       // Distance between expanded and collapsed button in screen % of screen height
    ebm:distanceX="0.28"       // Distance between expanded button in % of screen width
    ebm:bottomPad="0.02"       // Button padding in % of screen height

    // Button drawable and text resources
    ebm:closeButtonSrc="@drawable/circle_close"
    ebm:leftButtonSrc="@drawable/circle_1"
    ebm:midButtonSrc="@drawable/circle_2"
    ebm:rightButtonSrc="@drawable/circle_3"
    ebm:leftButtonText="@string/action_left"
    ebm:midButtonText="@string/action_mid"
    ebm:rightButtonText="@string/action_right"

In your activity/fragment add the callbacks:

    menuOverlay = (ExpandableMenuOverlay) findViewById(R.id.button_menu);
    menuOverlay.setOnMenuButtonClickListener(new ExpandableButtonMenu.OnMenuButtonClick() {
        @Override
        public void onClick(ExpandableButtonMenu.MenuButton action) {
            switch (action) {
                case MID:
                // do stuff and dismiss
                menuOverlay.getButtonMenu().toggle();
                break;
                case LEFT:
                // do stuff
                break;
                case RIGHT:
                // do stuff
                break;
            }
        }
    });

Acknowledgements
--------------------
* Thanks to Rockmelt android app for inspiration. Unfortunately, their app is no longer [available][1].
* [NineOldDroids][2] for allowing back porting the library.

Developed By
--------------------
[Lemon Labs][4]- <team@lemonlabs.lt>

License
-----------

    Copyright 2013 Lemon Labs

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[1]: http://allthingsd.com/20130802/yahoo-paid-60m-to-70m-for-rockmelt-will-dump-browser-and-use-tech-to-better-deliver-its-media-and-mobile-properties
[2]: https://github.com/JakeWharton/NineOldAndroids">https://github.com/JakeWharton/NineOldAndroids/
[3]: https://raw.github.com/lemonlabs/ExpandableButtonMenu/master/images/demo.gif
[4]: http://www.lemonlabs.co
=======
