package be.kdg.mobile_client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import be.kdg.mobile_client.R;
import be.kdg.mobile_client.adapters.UserRecyclerAdapter;
import be.kdg.mobile_client.model.User;
import be.kdg.mobile_client.services.CallbackWrapper;
import be.kdg.mobile_client.services.UserService;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UserSearchActivity extends BaseActivity {
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.etSearch) EditText etSearch;
    @BindView(R.id.lvUser) RecyclerView lvUser;
    @Inject UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getControllerComponent().inject(this);
        setContentView(R.layout.activity_usersearch);
        ButterKnife.bind(this);
        addEventHandlers();
        getUsers();
    }

    private void addEventHandlers() {
        btnBack.setOnClickListener(e -> {
            Intent intent = new Intent(this, FriendsActivity.class);
            startActivity(intent);
        });
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (event == null || !event.isShiftPressed()) {
                    getUsersByName(etSearch.getText().toString());
                }
            }
            return false;
        });
    }

    /**
     * Gets all the users for the user micro-service and puts them into
     * a list adapter.
     */
    private void getUsers() {
        userService.getUser().enqueue(new CallbackWrapper<>((throwable, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                initializeAdapter(Arrays.asList(response.body()));
            } else {
                Toast.makeText(getApplicationContext(), "Error getting rooms", Toast.LENGTH_LONG).show();
            }
        }));
    }

    /**
     * Gets all the users that have the searched string inside their name
     * and puts them into a list adapter.
     *
     * @param name The search string.
     */
    private void getUsersByName(String name) {
        userService.getUserByName(name).enqueue(new CallbackWrapper<>((throwable, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                initializeAdapter(Arrays.asList(response.body()));
            } else {
                Toast.makeText(getApplicationContext(), "Error getting rooms", Toast.LENGTH_LONG).show();
            }
        }));
    }

    /**
     * Initializes the rooms adapter to show all the rooms that
     * were retrieved from the game-service back-end.
     *
     * @param users The users that need to be used by the adapter.
     */
    private void initializeAdapter(List<User> users) {
        UserRecyclerAdapter userAdapter = new UserRecyclerAdapter(users);
        lvUser.setAdapter(userAdapter);
        lvUser.setLayoutManager(new LinearLayoutManager(this));
    }
}
