package nl.freelist.freelist;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface EntryDao {

  @Insert
  void insert(Entry entry);

  @Update
  void update(Entry entry);

  @Delete
  void delete(Entry entry);

  @Query("DELETE FROM entry")
  void deleteAllEntries();

  @Query("SELECT * FROM entry ORDER BY duration DESC")
  LiveData<List<Entry>> getAllEntries();

}
