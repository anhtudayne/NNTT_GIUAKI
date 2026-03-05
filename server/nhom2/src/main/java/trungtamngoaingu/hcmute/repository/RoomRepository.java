package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.Room;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
  @Query("SELECT ro FROM Room ro")
  List<Room> myGetAll();
}
