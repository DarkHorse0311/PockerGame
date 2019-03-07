package be.kdg.mobile_client.viewmodels;

import android.annotation.SuppressLint;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import be.kdg.mobile_client.model.Act;
import be.kdg.mobile_client.model.ActType;
import be.kdg.mobile_client.model.Player;
import be.kdg.mobile_client.model.Room;
import be.kdg.mobile_client.model.Round;
import be.kdg.mobile_client.repos.RoomRepository;
import be.kdg.mobile_client.repos.RoundRepository;
import lombok.Getter;
import lombok.Setter;

/**
 * The main viewmodel for joining a room and fetching its state.
 * It works similarly to a Presenter in the MVP pattern.
 * The LiveData variables are directly linked and accessible from the layout.
 * When the LiveData variables change, the bound data in the layout is subsequently changed.
 */
@SuppressLint("CheckResult")
public class RoomViewModel extends ViewModel {
    private final RoomRepository roomRepo;
    private final RoundRepository roundRepo;
    @Getter MutableLiveData<Room> room = new MutableLiveData<>();
    @Getter MutableLiveData<Round> round = new MutableLiveData<>();
    @Getter MutableLiveData<Player> player = new MutableLiveData<>();
    @Getter MutableLiveData<String> toast = new MutableLiveData<>();
    @Getter MutableLiveData<Boolean> myTurn = new MutableLiveData<>();
    @Getter @Setter MutableLiveData<Integer> seekBarValue = new MutableLiveData<>();

    @Inject
    public RoomViewModel(RoomRepository roomRepo, RoundRepository roundRepo) {
        this.roomRepo = roomRepo;
        this.roundRepo = roundRepo;
    }

    /**
     * Setup initial room connection
     */
    public void init(int roomId) {
        roomRepo.findById(roomId)
                .subscribe(next -> {
                    if (playerCapReached(next)) return;
                    room.setValue(next);
                    roomRepo.listenOnRoomUpdate(roomId).subscribe(room::postValue, this::notifyUser);
                    roundRepo.listenOnRoundUpdate(roomId).subscribe(value -> {
                        round.postValue(value);
                        updateRoomPlayers(value);
                        updatePlayer(value);
                        checkTurnByBlinds(value);
                    }, this::notifyUser);
                    //TODO: initializeWinnerConnection();
                    roomRepo.joinRoom(roomId).subscribe(player::postValue, this::notifyUser);
                    roomRepo.listenOnActUpdate(roomId).subscribe(this::onNewAct, this::notifyUser);
                }, this::notifyUser);
    }

    private void updateRoomPlayers(Round rnd) {
        Room tempRoom = room.getValue();
        tempRoom.setPlayersInRoom(rnd.getPlayersInRound());
        room.setValue(tempRoom);
    }

    private void updatePlayer(Round rnd) {
        rnd.getPlayersInRound().forEach(otherPlayer -> {
            if (otherPlayer.getUserId().equals(player.getValue().getUserId())) {
                player.postValue(otherPlayer);
            }
        });
    }

    /**
     * Check if its my turn
     */
    private void onNewAct(Act act) {
        myTurn.setValue(false);
        if (act.getNextUserId().equals(player.getValue().getUserId())) {
            myTurn.setValue(true);
        } else {
            checkTurnByBlinds(round.getValue());
        }
    }

    /**
     * Check if its my turn by looking at the blinds
     */
    private void checkTurnByBlinds(Round rnd) {
        if (rnd == null) return;
        int nextPlayerIndex = rnd.getButton() >= rnd.getPlayersInRound().size() - 1 ? 0 : rnd.getButton() + 1;
        if (rnd.getPlayersInRound().get(nextPlayerIndex).getUserId().equals(player.getValue().getUserId())) {
            myTurn.setValue(true);
        }
    }

    private boolean playerCapReached(Room newRoom) {
        return newRoom.getPlayersInRoom().size() >= newRoom.getGameRules().getMaxPlayerCount();
    }

    public boolean cardExists(int index) {
        return round.getValue() != null &&
                round.getValue().getCards() != null &&
                round.getValue().getCards().get(index) != null;
    }

    /**
     * This method is called when the user clicks on an act
     */
    public void onAct(ActType actType) {
        Player me = player.getValue();
        Round rnd = round.getValue();
        int roomId = room.getValue().getId();
        Act act = new Act(rnd.getId(), me.getUserId(), me.getId(), roomId, actType,
                rnd.getCurrentPhase(), 0, 0, "");
        if (actType == ActType.RAISE || actType == ActType.CALL) {
            final int bet = seekBarValue.getValue();
            act.setBet(bet);
            act.setTotalBet(bet);
        }
        roundRepo.addAct(act).subscribe(e -> seekBarValue.setValue(0),  this::notifyUser);
    }

    /**
     * Update the toast livedata variable, RoomActivity listens on this variable and shows a toast
     */
    public void notifyUser(Throwable throwable) {
        toast.postValue(throwable.getMessage());
    }

    public void leaveRoom() {
        roomRepo.leaveRoom(room.getValue().getId()).subscribe(e -> {}, this::notifyUser);
    }
}
