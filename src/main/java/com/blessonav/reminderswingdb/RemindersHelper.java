package com.blessonav.reminderswingdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemindersHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemindersHelper.class);

  private static final RemindersHelper INSTANCE = new RemindersHelper();

  public static RemindersHelper getInstance() {
    return RemindersHelper.INSTANCE;
  }

  public List<Reminder> getReminders() throws SQLException {
    RemindersHelper.LOGGER.debug("Loading reminders");
     List<Reminder> reminders = new ArrayList<>();

    final String sql = "SELECT * FROM reminders ORDER BY id";
    try (Connection connection = DbHelper.getConnection();
        PreparedStatement psmt = connection.prepareStatement(sql);
        ResultSet rs = psmt.executeQuery()) {

      while (rs.next()) {
        final Reminder reminder = new Reminder();
        reminder.setId(rs.getLong("id"));
        reminder.setName(rs.getString("name"));
        reminder.setDatepresent(rs.getBoolean("datepresent"));
        reminder.setVrepeat(rs.getBoolean("repeat"));
        if(reminder.isDatepresent())
        	reminder.setDate(rs.getDate("dateselected"));
        else
        	reminder.setDate(new Date());
        Time t=rs.getTime("timeselected");
        Calendar now = Calendar.getInstance();
        now.setTime(reminder.getDate());
        now.set(Calendar.HOUR_OF_DAY, t.getHours());
        now.set(Calendar.MINUTE, t.getMinutes());
        now.set(Calendar.SECOND, t.getSeconds());
        reminder.setDate(now.getTime());
       /* int r=;
        reminder.getDate().setHours(5);
        reminder.getDate().setMinutes(t.());
        reminder.getDate().setSeconds();*/
        //reminder.setReminders(rs.getString("reminders"));
        reminders.add(reminder);
      }
    }
    final List<Reminder> remindersCopy = new ArrayList<>();
    for(Reminder reminder:reminders)
    {
    	if(reminder.isDatepresent() && (reminder.getDate().compareTo(new Date())<0))
    	 		reminder.delete();
    	else
    			remindersCopy.add(reminder);
  		
    }
    reminders=remindersCopy;

    RemindersHelper.LOGGER.debug("Loaded {} reminders", reminders.size());
    return reminders;
  }
}
