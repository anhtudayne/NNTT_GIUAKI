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

    // --- CÁC HÀM XỬ LÝ DỮ LIỆU BẰNG STREAM API ---

    /**
     * LỌC DANH SÁCH PHÒNG THEO TRẠNG THÁI (STREAM API)
     * - Các Stream API phù hợp với việc Filter dữ liệu ngay tại Code cấp Service (Backend).
     * - Lambda `room -> room.getStatus() == targetStatus` hoạt động như một Predicate Function, 
     *   trả về boolean (true/false) để giữ lại hoặc bỏ đi Data trong ống truyền.
     */
    public List<Room> getRoomsByStatus(String statusStr) {
        List<Room> allRooms = roomRepository.myGetAll();
        try {
            Room.Status targetStatus = Room.Status.valueOf(statusStr);
            return allRooms.stream()
                    .filter(room -> room.getStatus() == targetStatus)
                    .toList();
        } catch (IllegalArgumentException e) {
            return List.of(); 
        }
    }

    /**
     * LỌC PHÒNG HỌC THEO SỨC CHỨA TỐI THIỂU (STREAM API)
     * - Hàm filter nhận vào `minCapacity` và so sánh >=. Nó loại các phòng quá nhỏ ra khỏi luồng,
     *   nhường chỗ cho kết quả là Set Map các phòng có số lượng ghế học trên mức yêu cầu.
     * - `toList()`: Collector để kết nối luồng dữ liệu Stream Data về lại dạng List để nhúng trả API.
     */
    public List<Room> getRoomsByMinCapacity(Integer minCapacity) {
        List<Room> allRooms = roomRepository.myGetAll();
        if (minCapacity == null) return allRooms;
        
        return allRooms.stream()
                .filter(room -> room.getCapacity() >= minCapacity)
                .toList();
    }
}
