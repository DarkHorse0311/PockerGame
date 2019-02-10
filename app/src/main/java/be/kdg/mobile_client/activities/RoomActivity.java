package be.kdg.mobile_client.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import be.kdg.mobile_client.R;
import be.kdg.mobile_client.fragments.ChatFragment;
import be.kdg.mobile_client.services.SharedPrefService;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity of an individual room.
 * This room contains a fragment in which the chat messages are processed.
 */
public class RoomActivity extends BaseActivity {
    @BindView(R.id.btnShowChat) Button btnShowChat;
    @BindView(R.id.llFragment) LinearLayout llFragment;
    @Inject FragmentManager fragmentManager;
    @Inject SharedPrefService sharedPrefService;
    private Fragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getControllerComponent().inject(this);
        checkIfAuthorized(sharedPrefService);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        ButterKnife.bind(this);
        chatFragment = fragmentManager.findFragmentByTag(getString(R.string.chat_fragment_tag));
        hideFragment(chatFragment); // initially hide the chatfragment
        handleShowChatButton();
    }

    private void handleShowChatButton() {
        btnShowChat.setOnClickListener(e -> {
            if (chatFragment != null && chatFragment.isHidden()) {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left)
                        .show(chatFragment)
                        .commit();
            } else {
                hideFragment(chatFragment);
            }
        });
    }

    private void hideFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left)
                .hide(fragment)
                .commit();
    }
}
