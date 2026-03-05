package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Room;
import trungtamngoaingu.hcmute.repository.RoomRepository;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    public List<Room> getAllRooms() {
        return roomRepository.myGetAll();
    }

    public Optional<Room> getRoomById(Integer id) {
        return roomRepository.findById(id);
    }

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room updateRoom(Integer id, Room room) {
        if (roomRepository.existsById(id)) {
            room.setRoomId(id);
            return roomRepository.save(room);
        }
        return null;
    }

    public void deleteRoom(Integer id) {
        roomRepository.deleteById(id);
    }
}
