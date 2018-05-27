package com.blessonav.reminderswingdb;


import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  
  
  
  
  
  
  public static void main(final String[] args) {
	    DbHelper.getInstance().init();
	    DbHelper.getInstance().registerShutdownHook();

	    SwingUtilities.invokeLater(new Runnable() {
	      @Override
	      public void run() {
	        Main.LOGGER.debug("Starting application");

	        final Application app = new Application();
	        app.setTitle("Simple Java Database Swing Application");
	        app.setSize(800, 600);
	        app.setLocationRelativeTo(null);
	        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        app.setVisible(true);
	      }
	    });
	  }
	}


  /*public static void main(final String[] args) {
	    DbHelper.getInstance().init();
	    Reminder c=new 	Reminder();
	    c.setName("BLESSON");
	    c.setReminders("blessonavt@gmail.com");
	   
	    try {
	    	 c.save();
	    	for(Reminder reminder:RemindersHelper.getInstance().getReminders())
	    	{
	    		LOGGER.debug("	>> [{}] [{}] [{}]", reminder.getId(), reminder.getName(),reminder.getReminders());;
	    		
	    	}
	    		
	    	
	    	c.save();
			Connection con=DbHelper.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT * FROM reminders");
			while(rs.next())
			{
			LOGGER.debug("	>> [{}] [{}] [{}]", new Object[] {rs.getInt("id"),rs.getString("name"),rs.getString("reminders")});
		}
			
		
		
	} catch (SQLException e) {
		LOGGER.error("Failed",e);
	}
    DbHelper.getInstance().close();
    LOGGER.info("Done");
    DbHelper.getInstance().registerShutdownHook();

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        Main.LOGGER.debug("Starting application");

        final Application app = new Application();
        app.setTitle("Simple Java Database Swing Application");
        app.setSize(800, 600);
        app.setLocationRelativeTo(null);
        app.setDefaultCloseOperation(Application.EXIT_ON_CLOSE);
        app.setVisible(true);

        // app.addWindowListener(new WindowAdapter() {
        // @Override
        // public void windowClosing(WindowEvent e) {
        // Main.LOGGER.info("Done");
        // DbHelper.getInstance().close();
        // }
        // });
      }
    });
  }}*/

