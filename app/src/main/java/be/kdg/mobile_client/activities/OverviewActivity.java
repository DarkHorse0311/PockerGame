package be.kdg.mobile_client.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.List;

import javax.inject.Inject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import be.kdg.mobile_client.R;
import be.kdg.mobile_client.adapters.RoomRecyclerAdapter;
import be.kdg.mobile_client.model.Room;
import be.kdg.mobile_client.services.GameService;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * This activity is used to display all the rooms as cards.
 */
public class OverviewActivity extends BaseActivity {
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.lvUser) RecyclerView lvRoom;
    @Inject GameService gameService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getControllerComponent().inject(this);
        setContentView(R.layout.activity_overview);
        ButterKnife.bind(this);
        addEventHandlers();
        getRooms();
    }

    private void addEventHandlers() {
        btnBack.setOnClickListener(e -> navigateTo(MenuActivity.class));
    }

    @SuppressLint("CheckResult")
    private void getRooms() {
        gameService.getRooms().observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::initializeAdapter, error -> Log.e("OverviewActivity", error.getMessage()));
    }

    /**
     * Initializes the rooms adapter to show all the rooms that
     * were retrieved from the game-service back-end.
     *
     * @param rooms The rooms that need to be used by the adapter.
     */
    private void initializeAdapter(List<Room> rooms) {
        RoomRecyclerAdapter roomAdapter = new RoomRecyclerAdapter(this, rooms);
        lvRoom.setAdapter(roomAdapter);
        lvRoom.setLayoutManager(new LinearLayoutManager(this));
    }
}
