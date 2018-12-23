package nl.freelist.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import nl.freelist.freelist.R;
import nl.freelist.presentationConstants.ActivityConstants;
import nl.freelist.recyclerviewAdapters.FreelistEntryAdapter;
import nl.freelist.viewModelPerActivity.NavigateFreelistActivityViewModel;

public class NavigateFreelistActivity extends AppCompatActivity {

  private NavigateFreelistActivityViewModel navigateFreelistActivityViewModel;
  private FreelistEntryAdapter adapter;
  private RecyclerView recyclerView;

  public NavigateFreelistActivity() {
  }

  @Override
  protected void onResume() {
    super.onResume();
    updateRecyclerView();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_calendar);

    navigateFreelistActivityViewModel = ViewModelProviders.of(this)
        .get(NavigateFreelistActivityViewModel.class);

    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setHasFixedSize(true);

    adapter = new FreelistEntryAdapter();

    recyclerView.setAdapter(adapter);

    FloatingActionButton buttonAddEntry = findViewById(R.id.button_add_entry);
    buttonAddEntry.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent(NavigateFreelistActivity.this, AddEditEntryActivity.class);
            intent.putExtra(
                ActivityConstants.EXTRA_REQUEST_TYPE_ADD, ActivityConstants.ADD_ENTRY_REQUEST);
            startActivityForResult(intent, ActivityConstants.ADD_ENTRY_REQUEST);
          }
        });

    new ItemTouchHelper(
        new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
          @Override
          public boolean onMove(
              @NonNull RecyclerView recyclerView,
              @NonNull RecyclerView.ViewHolder viewHolder,
              @NonNull RecyclerView.ViewHolder viewHolder1) {
            // drag and drop functionality
            return false;
          }

          @Override
          public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            // viewHolder.getAdapterPosition() //where did we swipe?
            navigateFreelistActivityViewModel.delete(
                adapter.getEntryAt(viewHolder.getAdapterPosition()));
            Toast.makeText(NavigateFreelistActivity.this, "DataEntry deleted", Toast.LENGTH_SHORT)
                .show();
          }
        })
        .attachToRecyclerView(recyclerView);
  }

  private void updateRecyclerView() {
    navigateFreelistActivityViewModel
        .getAllEntries()
        .subscribe(
            entries -> {
              // update RecyclerView
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      adapter.setEntries(entries);
                      Toast.makeText(
                          NavigateFreelistActivity.this,
                          "navigateFreelistActivityViewModel refreshed!",
                          Toast.LENGTH_SHORT)
                          .show();
                    }
                  });
            });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == ActivityConstants.ADD_ENTRY_REQUEST && resultCode == RESULT_OK) {
      Toast.makeText(
          this,
          "DataEntry " + data.getStringExtra(ActivityConstants.EXTRA_TITLE) + " saved!",
          Toast.LENGTH_SHORT)
          .show();
      updateRecyclerView();
    } else if (requestCode == ActivityConstants.ADD_ENTRY_REQUEST && resultCode != RESULT_OK) {
      Toast.makeText(this, "DataEntry not saved.", Toast.LENGTH_SHORT).show();
    } else if (requestCode == ActivityConstants.EDIT_ENTRY_REQUEST && resultCode == RESULT_OK) {
      Toast.makeText(
          this,
          "DataEntry " + data.getStringExtra(ActivityConstants.EXTRA_TITLE) + " edited!",
          Toast.LENGTH_SHORT)
          .show();
      updateRecyclerView();
    } else if (requestCode == ActivityConstants.EDIT_ENTRY_REQUEST && resultCode != RESULT_OK) {
      Toast.makeText(
          this,
          "DataEntry " + data.getStringExtra(ActivityConstants.EXTRA_TITLE) + " not edited!",
          Toast.LENGTH_SHORT)
          .show();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.delete_all_entries:
        navigateFreelistActivityViewModel.deleteAllEntries();
        Toast.makeText(this, "All entries deleted", Toast.LENGTH_SHORT).show();
        return true;
      case R.id.settings:
        Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NavigateFreelistActivity.this, SettingsActivity.class);
        startActivity(intent);
        return true;
      case R.id.undo:
        Toast.makeText(this, "Undo selected", Toast.LENGTH_SHORT).show();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
