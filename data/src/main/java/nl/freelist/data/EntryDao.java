package nl.freelist.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;
import nl.freelist.data.dto.DataEntry;
import nl.freelist.data.dto.DataEntryExtra;

@Dao
public interface EntryDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(DataEntry dataEntry);

  @Update
  void update(DataEntry dataEntry);

  @Delete
  void delete(DataEntry dataEntry);

  @Query("DELETE FROM DataEntry")
  void deleteAllEntries();

  @Query("SELECT * FROM DataEntry ORDER BY duration ASC")
  List<DataEntry> getAllEntries();

  @Query(
      "SELECT id FROM DataEntry\n"
          + "WHERE DataEntry.parentId = :id;\n"
  )
  List<Integer> getAllDirectChildrenIdsForParent(int id);

  @Query(
      "WITH RECURSIVE ChildrenCTE(x) AS (\n"
          + "  SELECT  DataEntry.id\n"
          + "  FROM DataEntry\n"
          + "  WHERE DataEntry.id = :id\n"
          + "  UNION ALL\n"
          + "  SELECT  DataEntry.id\n"
          + "  FROM    DataEntry, ChildrenCTE cte\n"
          + "          WHERE DataEntry.parentId=cte.x LIMIT 10000\n"
          + ") \n"
          + "SELECT *\n"
          + ", (\n"
          + "\tSELECT count(*) FROM DataEntry\n"
          + "\tWHERE DataEntry.id != :id\n"
          + "\tAND DataEntry.id IN\n"
          + "\t(select * from ChildrenCTE)\n"
          + "\tAND DataEntry.id NOT IN\n"
          + "\t(select parentId from DataEntry)\n"
          + "\t) AS childrenCount\n"
          + ", (\n"
          + "\tSELECT sum(duration) FROM DataEntry\n"
          + "\tWHERE DataEntry.id != :id\n"
          + "\tAND DataEntry.id IN\n"
          + "\t(select * from ChildrenCTE)\n"
          + "\tAND DataEntry.id NOT IN\n"
          + "\t(select parentId from DataEntry)\n"
          + "\t) AS childrenDuration\n"
          + "FROM DataEntry\n"
          + "WHERE DataEntry.id = :id;")
  DataEntryExtra getDataEntryExtra(int id);

}
