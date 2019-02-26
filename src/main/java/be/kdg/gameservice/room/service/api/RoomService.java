package be.kdg.gameservice.room.service.api;

import be.kdg.gameservice.room.exception.RoomException;
import be.kdg.gameservice.room.model.GameRules;
import be.kdg.gameservice.room.model.PrivateRoom;
import be.kdg.gameservice.room.model.Room;
import be.kdg.gameservice.round.exception.RoundException;
import be.kdg.gameservice.round.model.Round;

import java.util.List;

public interface RoomService {
    Round startNewRoundForRoom(int roomId) throws RoomException;

    Round getCurrentRound(int roomId) throws RoomException;

    List<Room> getRooms();

    Room getRoom(int roomId) throws RoomException;

    Room getRoomByName(String roomName);

    Room addRoom(String name, GameRules gameRules);

    Room changeRoom(int roomId, Room room) throws RoomException;

    void deleteRoom(int id) throws RoomException;

    int checkChips(int roomId, int userChips) throws RoomException;

    void enoughRoundPlayers(int roomId) throws RoomException, RoundException;
}
