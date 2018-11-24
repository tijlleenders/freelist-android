package nl.freelist.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;
import nl.freelist.constants.ActivityConstants;
import nl.freelist.repository.ViewModelEntry;
import nl.freelist.userInterfaceHelpers.NumberPickerDuration;
import nl.freelist.database.Entry;
import nl.freelist.freelist.R;
import nl.freelist.userInterfaceHelpers.DateHelpers;
import nl.freelist.viewModels.TestViewModelActivityViewModel;

public class TestViewModelActivity extends AppCompatActivity
    implements DatePickerDialog.OnDateSetListener {

  private EditText editTextTitle;
  private EditText editTextDescription;
  private EditText editTextDueDate;
  private Button parentButton;
  private NumberPickerDuration numberPickerDuration;
  private TestViewModelActivityViewModel testViewModelActivityViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test_view_model);

    editTextTitle = findViewById(R.id.edit_text_title);
    editTextDescription = findViewById(R.id.edit_text_description);
    numberPickerDuration = findViewById(R.id.number_picker_duration);
    editTextDueDate = findViewById(R.id.edit_due_date);
    parentButton = findViewById(R.id.button_parent_change);

    numberPickerDuration.setMinValue(1);
    numberPickerDuration.setMaxValue(8); //todo: make configurable
    numberPickerDuration
        .setDisplayedValues(new String[]{"5m", "15m", "45m", "2h", "4h", "8h", "12h",
            "24h"}); //todo: make configurable, ie make custom subclass with functions to set with string or duration int?

    //Intent.ACTION_* fields are String constant.
    //You cannot use switch with String until JDK 7 android use JDK 6 or 5 to compile. So you can't use that method on Android
    //So using if else if :(
    Bundle bundle = getIntent().getExtras();
    testViewModelActivityViewModel = ViewModelProviders.of(this)
        .get(TestViewModelActivityViewModel.class);
    testViewModelActivityViewModel.getViewModelEntry(1).observe(this,
        new Observer<ViewModelEntry>() { //Todo: make returning viewModelEntry dynamic based on id
          @Override
          public void onChanged(@Nullable ViewModelEntry entry) {
            // do nothing; just necessary for testViewModelActivityViewModel to load data
            editTextTitle.setText(entry.getTitle());
          }
        });
    if (bundle.containsKey(ActivityConstants.EXTRA_REQUEST_TYPE_EDIT)) { //do edit setup
      parentButton.setText(bundle.getString(ActivityConstants.EXTRA_ENTRY_PARENT_ID));
      editTextTitle.setText(bundle.getString(ActivityConstants.EXTRA_ENTRY_TITLE));
      editTextDescription.setText(bundle.getString(ActivityConstants.EXTRA_ENTRY_DESCRIPTION));
      numberPickerDuration.setValue(numberPickerDuration.getNumberPickerPosition(
          bundle.getString(ActivityConstants.EXTRA_ENTRY_FORMATTED_DURATION)));
      editTextDueDate
          .setText(bundle.getString(ActivityConstants.EXTRA_ENTRY_FORMATTED_DATE, "01-01-2000"));

      getSupportActionBar()
          .setHomeAsUpIndicator(R.drawable.ic_close); //todo: move outside of if else if?
      setTitle("Edit existing");
    } else if (bundle.containsKey(ActivityConstants.EXTRA_REQUEST_TYPE_ADD)) { //do add setup

      //get current date as default for due date todo: move to DateHelpers class
      final Calendar c = Calendar.getInstance();

      //get info from viewModel
      //editTextTitle.setText(testViewModelActivityViewModel.getTitle());

      String currentDate = String.valueOf(c.get(Calendar.YEAR)) +
          "-" +
          (c.get(Calendar.MONTH) + 1) + //Android SDK says months are indexed starting at zero
          "-" +
          c.get(Calendar.DAY_OF_MONTH);
      editTextDueDate.setText(currentDate);

      getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
      setTitle("Add new");
    } //Todo: Add else if (bundle.containsKey(ActivityConstants.EXTRA_PARENT_CHANGED_KEY)) {}

    editTextDueDate.setFocusable(false); // setting android:inputType="none" in XML is not enough


  }

  private void saveEntry() {

    String title = editTextTitle.getText().toString();
    String description = editTextDescription.getText().toString();
    int duration = numberPickerDuration
        .getNumberPicker(numberPickerDuration.getValue()); //todo: replace by getDuration
    long date = DateHelpers.getDateFromString(editTextDueDate.getText().toString()).getTime();
    boolean isCompletedStatus = false;

    if (title.trim().isEmpty() || description.trim().isEmpty()) {
      boolean notificationBool = PreferenceManager.getDefaultSharedPreferences(this)
          .getBoolean("notifications", false);
      if (notificationBool) {
        Toast.makeText(this, "setting true", Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(this, "setting false", Toast.LENGTH_SHORT).show();
      }
      //Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
      return;
    }

    Entry entryToSave = new Entry(title, description, duration, date, isCompletedStatus);

    Bundle bundle = getIntent().getExtras();
    //Intent.ACTION_* fields are String constant.
    //You cannot use switch with String until JDK 7 android use JDK 6 or 5 to compile. So you can't use that method on Android
    //So using if else if :(

    if (bundle.containsKey(ActivityConstants.EXTRA_REQUEST_TYPE_EDIT)) {
      entryToSave.setId((Integer) bundle.get(ActivityConstants.EXTRA_ENTRY_ID));
//      testViewModelActivityViewModel.update(entryToSave);
      Toast.makeText(this, "Existing entry updated!", Toast.LENGTH_LONG).show();
    } else if (bundle.containsKey(ActivityConstants.EXTRA_REQUEST_TYPE_ADD)) {
//      testViewModelActivityViewModel.insert(entryToSave);
      Toast.makeText(this, "New entry saved!", Toast.LENGTH_LONG).show();
    }

    Intent data = new Intent();
    // todo: make distinction between add or update entry?
    data.putExtra(ActivityConstants.EXTRA_TITLE, title);
    setResult(RESULT_OK, data);
    finish();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.add_entry_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.save_entry:
        saveEntry();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void showDatePickerDialog(View v) {
    DialogFragment newFragment = new DatePickerFragment();
    newFragment.show(getSupportFragmentManager(), "datePicker");
  }

  @Override
  public void onDateSet(DatePicker view, int year, int month, int day) {
    // Do something with the date chosen by the user
    month += 1; //Android SDK says months are indexed starting at zero
    String toShow = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);
    editTextDueDate.setText(toShow);
  }

  public static class DatePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      // Use the current date as the default date in the picker
      Calendar c = Calendar.getInstance();

      // Parse content of EditText to start datePicker with the date in the EditText - if possible
      String editTextDateString = ((TestViewModelActivity) getActivity()).editTextDueDate.getText()
          .toString();
      Date convertedDate = DateHelpers.getDateFromString(editTextDateString);
      c.setTime(convertedDate);
      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH);
      int day = c.get(Calendar.DAY_OF_MONTH);

      // Create a new instance of DatePickerDialog and return it
      return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(),
          year, month, day);
    }

  }

}
