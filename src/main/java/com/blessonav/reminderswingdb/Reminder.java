package com.blessonav.reminderswingdb;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reminder {

  private static final Logger LOGGER = LoggerFactory.getLogger(Reminder.class);

  private long id = -1L;
  private String name;
  boolean deleteinitated=false;
  private String text="";
  public Timer getTimer() {
	return timer;
}

public void setTimer() {
	if(timer==null)
	{	timer = new Timer(1000, new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            long runningTime = date.getTime()-System.currentTimeMillis();
	            if(runningTime==0)
	            {	
	            	   JOptionPane.showMessageDialog(null, name, "Reminder", JOptionPane.WARNING_MESSAGE);
	            	  	try {
							wait(1);
						} catch (InterruptedException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
	            	  
	            	if(!deleteinitated && !vrepeat )
						try {
							deleteinitated=true;
							JOptionPane.showMessageDialog(null, "Deleting Reminder "+name,name,  JOptionPane.WARNING_MESSAGE);      	  	
							
							delete(id);	
							
							//deleteinitated=true;
						} catch (SQLException e1) {
							LOGGER.error("Deleting reminder : "+name+" failed" );
						}
					else
	            	{
						JOptionPane.showMessageDialog(null, "Updating Reminder "+name+" to next day", name, JOptionPane.WARNING_MESSAGE);      	  	
						
						Calendar c = Calendar.getInstance(); 
		            	c.setTime(date); 
		            	c.add(Calendar.DATE, 1);
		            	date = c.getTime();	
		            	runningTime = date.getTime()-System.currentTimeMillis();
	            	}
	            		
	            		
	            }
	            else if( runningTime<0)
	            {	if(vrepeat) 
	            	{
	            	while(runningTime<0) {
	            	JOptionPane.showMessageDialog(null, "Updating Reminder "+name+" to next day as time passed",name,  JOptionPane.WARNING_MESSAGE);      	  	
					
	            	Calendar c = Calendar.getInstance(); 
	            	c.setTime(date); 
	            	c.add(Calendar.DATE, 1);
	            	date = c.getTime();	
	            	runningTime = date.getTime()-System.currentTimeMillis();
	            	}
	            	}
	            else if(!deleteinitated)
	            {
	            	deleteinitated=true;
	            	   JOptionPane.showMessageDialog(null, name, "Reminder", JOptionPane.WARNING_MESSAGE);
	            	JOptionPane.showMessageDialog(null, "Deleting non repeating reminder."+"\n Refresh to update GUI",name , JOptionPane.WARNING_MESSAGE);      	  	
	            	try {
						//JOptionPane.showMessageDialog(null, name, "Deleting Reminder "+name, JOptionPane.WARNING_MESSAGE);      	  	
						
						delete(id);			
						
					} catch (SQLException e1) {
						LOGGER.error("Deleting reminder : "+name+" failed" );
					}	
	            }
	            
	            }	
	            Duration duration = Duration.ofMillis(runningTime);
	            long hours = duration.toHours();
	            duration = duration.minusHours(hours);
	            long minutes = duration.toMinutes();
	            duration = duration.minusMinutes(minutes);
	            long millis = duration.toMillis();
	            long seconds = millis / 1000;
	            millis -= (seconds * 1000);
	            
	            text=String.format(" - %02d:%02d:%02d", hours, minutes, seconds);
	        }
	    });
	if(!timer.isRunning()) 
	 timer.start();
}}


private Date date;
  private boolean datepresent;
  private Timer timer;
  public boolean isDatepresent() {
	return datepresent;
}
  
  
 private boolean vrepeat;
public boolean isVrepeat() {
	return vrepeat;
}

public void setVrepeat(boolean vrepeat) {
	this.vrepeat = vrepeat;
}

public void setDatepresent(boolean datepresent) {
	this.datepresent = datepresent;
}

public Date getDate() {
	return date;
}

public void setDate(Date date) {
	this.date = date;
}

public void delete() throws SQLException {
    if (id == -1) {
      // Can throw an exception
    } else {
      Reminder.LOGGER.debug("Deleting reminder: {}", this);
      final String sql = "DELETE FROM reminders WHERE id = ?";
      try (Connection connection = DbHelper.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setLong(1, id);
        pstmt.execute();
        id = -1;
      }
    }
  }

public void delete(long id) throws SQLException {
    if (id == -1) {
      // Can throw an exception
    } else {
      LOGGER.debug("Deleting reminder statically: {}");
      final String sql = "DELETE FROM reminders WHERE id = ?";
      try (Connection connection = DbHelper.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setLong(1, id);
        pstmt.execute();
        id = -1;
      }
    }
    if(timer.isRunning()) 
   	 {timer.stop();
   	  timer=null;
   	 }
   	 }
  /*public String getReminders() {
    return reminders;
  }*/

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void save(boolean flag) throws SQLException {
    try (Connection connection = DbHelper.getConnection()) {
      if (id == -1) {
        Reminder.LOGGER.debug("Adding new reminder: {}", this);
        final String sql = "INSERT INTO reminders(name, dateselected,timeselected,datepresent,repeat) VALUES(?, ?,?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
          pstmt.setString(1, name);
          if(flag)
        	  pstmt.setDate(2, new java.sql.Date(date.getTime()));
          else
        	  pstmt.setDate(2, null);
          pstmt.setTime(3, new java.sql.Time(date.getTime()));
          pstmt.setBoolean(4, flag);
          pstmt.setBoolean(5, vrepeat);
          pstmt.execute();

          try (final ResultSet rs = pstmt.getGeneratedKeys()) {
            rs.next();
            id = rs.getLong(1);
          }
        }
      } else {
        Reminder.LOGGER.debug("Updating existing reminder: {}", this);
        final String sql = "UPDATE reminders SET name = ?, dateselected = ?,timeselected=?,datepresent=?,repeat=? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
          pstmt.setString(1, name);
          if(flag)
        	  pstmt.setDate(2, new java.sql.Date(date.getTime()));
          else
        	  pstmt.setDate(2, null);
          pstmt.setTime(3, new java.sql.Time(date.getTime()));
          pstmt.setBoolean(4, flag);
          pstmt.setBoolean(5, vrepeat);
          pstmt.setLong(6, id);
          pstmt.execute();
        }
      }
    }
  }

/*  public void setReminders(final String reminders) {
    this.reminders = reminders;
  }*/

  public void setId(final long id) {
    this.id = id;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    final StringBuilder formatted = new StringBuilder();
    if (id == -1) {
      formatted.append("[No Id] ");
    } else {
      formatted.append("[").append(id).append("] ");
    }

    if (name == null) {
      formatted.append("no name");
    } else {
      formatted.append(name);
    }
    if (text == null) {
        formatted.append("no text");
      } else {
    	 // setTimer();
          long runningTime = date.getTime()-System.currentTimeMillis();
    	  while(runningTime<0) {
          	//JOptionPane.showMessageDialog(null, name, "Updating Reminder "+name+" to next day as time passed", JOptionPane.WARNING_MESSAGE);      	  	
				
          	Calendar c = Calendar.getInstance(); 
          	c.setTime(date); 
          	c.add(Calendar.DATE, 1);
          	date = c.getTime();	
          	runningTime = date.getTime()-System.currentTimeMillis();
          	}
    	  Duration duration = Duration.ofMillis(runningTime);
          long hours = duration.toHours();
          duration = duration.minusHours(hours);
          long minutes = duration.toMinutes();
          duration = duration.minusMinutes(minutes);
          long millis = duration.toMillis();
          long seconds = millis / 1000;
          millis -= (seconds * 1000);
          
          text=String.format(" - %02d:%02d:%02d", hours, minutes, seconds);
        formatted.append(text);
      }

    return formatted.toString();
  }

}
