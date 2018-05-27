package com.blessonav.reminderswingdb;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.toedter.calendar.JDateChooser;

public class Application extends JFrame {

  private static final long serialVersionUID = 1386297934574206918L;

 // private JTextField idTextField;
  private JTextField nameTextField;
  private JScrollPane ReminderScrollPanel;
  //private JTextArea remindersTextArea;

  private DefaultListModel<Reminder> remindersListModel;
  private JList<Reminder> remindersList;
  private Timer timer;
  private JLabel label;
  private Action refreshAction;
  //private Action newAction;
  private Action saveAction;
  private Action deleteAction;
private boolean flag=true;
  private Reminder selected;
  private JSpinner timeSpinner = new JSpinner( new SpinnerDateModel() );
  JDateChooser  dateChooser= new JDateChooser();
  JCheckBox repeat = new JCheckBox("Repeat Reminding");
  public Application() {
    initActions();
    initMenu();
    initComponents();

    refreshData();
    createNew();
  }

  private JComponent createEditor() {
    final JPanel panel = new JPanel(new GridBagLayout());

    // Id
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.WEST;
    constraints.insets = new Insets(2, 2, 2, 2);
    panel.add(new JLabel("Remind me to "), constraints);
    //panel.add(new JLabel("Id"), constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.weightx = 1;
    constraints.insets = new Insets(2, 2, 2, 2);
    constraints.fill = GridBagConstraints.BOTH;
    nameTextField= new JTextField();
    panel.add(nameTextField, constraints);

    //idTextField = new JTextField();
    //idTextField.setEditable(false);
    //panel.add(idTextField, constraints);

    // Name
    constraints = new GridBagConstraints();
    constraints.gridy = 1;
    constraints.anchor = GridBagConstraints.WEST;
    constraints.insets = new Insets(2, 2, 2, 2);
    panel.add(new JLabel("Date "), constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 1;
   // constraints.weightx = 1;
    constraints.insets = new Insets(2, 2, 2, 2);
    constraints.fill = GridBagConstraints.BOTH;
   // DatePicker datePicker1 = new DatePicker();
    
   // dateChooser.setBounds(20, 20, 200, 20);
    
    panel.add(dateChooser,constraints);

    // Reminders
    constraints = new GridBagConstraints();
    constraints.gridy = 2;
    constraints.anchor = GridBagConstraints.NORTHWEST;
    constraints.insets = new Insets(2, 2, 2, 2);
    panel.add(new JLabel("Time "), constraints);
    

    // Create a time picker, and add it to the form.
   // TimePicker timePicker1 = new TimePicker();
    
    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 2;
    //constraints.weightx = 1;
    //constraints.weighty = 1;
    constraints.insets = new Insets(2, 2, 2, 2);
    constraints.fill = GridBagConstraints.BOTH;
    
    
    JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
    timeSpinner.setEditor(timeEditor);
    Date d=new Date();
    d.setHours(0);
    d.setMinutes(0);
    d.setSeconds(0);
    timeSpinner.setValue(d);
    
   // remindersTextArea = new JTextArea();
    repeat.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(repeat.isSelected() && dateChooser.getDate()!=null)
			{
				JOptionPane.showMessageDialog(null, "Date value should be null if Reminder is to be repeated", "Save", JOptionPane.WARNING_MESSAGE);
		    	repeat.setSelected(false);
			}
				
			
		}
	});
    
   

    
    
    
dateChooser.getDateEditor().addPropertyChangeListener(
	    new PropertyChangeListener() {
	        @Override
	        public void propertyChange(PropertyChangeEvent e) {
			if(repeat.isSelected() && dateChooser.getDate()!=null)
			{
				repeat.setSelected(false);
			}
				
			
		}
	});
    panel.add(timeSpinner,constraints);
    constraints.gridx = 1;
    constraints.gridy = 3;
    panel.add(repeat,constraints);
    return panel;
  }

  private JComponent createListPane() {
    remindersListModel = new DefaultListModel<>();
    remindersList = new JList<>(remindersListModel);
   // remindersList.setPreferredSize(new Dimension(150, 400));
    remindersList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(final ListSelectionEvent event) {
        if (!event.getValueIsAdjusting()) {
          setSelectedReminder(remindersList.getSelectedValue());
        }
      }
    });
    remindersList.addMouseListener(new MouseListener() {
		
    	@Override
        public void mouseClicked(MouseEvent e) {
            //super.mouseClicked(e);
            clearselection(e);      
        }

        @Override
        public void mousePressed(MouseEvent e) {
           // super.mousePressed(e);
            clearselection(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //super.mouseReleased(e);
            clearselection(e);
        }

        public void clearselection (MouseEvent e){      
            if (e.getComponent() instanceof JTable){
                Point pClicked = e.getPoint();
                JTable table = (JTable) e.getSource();
                int index = table.rowAtPoint(pClicked);
                if (index == -1){
                    table.clearSelection();
                }

            } else if (e.getComponent() instanceof JList){
                Point pClicked = e.getPoint();
                JList<?> list = (JList<?>) e.getSource();
                int index = list.locationToIndex(pClicked);
                Rectangle rec = list.getCellBounds(index, index);
                if (rec==null || !rec.contains(pClicked)){
                    list.clearSelection();
                }
            }
        }

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
			
		}
	);
    ReminderScrollPanel= new JScrollPane(remindersList,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    ReminderScrollPanel.setPreferredSize(new Dimension(450, 110));
    remindersList.ensureIndexIsVisible(50);
    return ReminderScrollPanel;
  }

  private void createNew() {
    final Reminder reminder = new Reminder();
    reminder.setName("Enter the title");
    //reminder.setReminders("New Reminder Details");
    setSelectedReminder(reminder);
  }

  private JToolBar createToolBar() {
    final JToolBar toolBar = new JToolBar();
    toolBar.add(refreshAction);
    toolBar.addSeparator();
    //toolBar.add(newAction);
    toolBar.add(saveAction);
    toolBar.addSeparator();
    toolBar.add(deleteAction);

    return toolBar;
  }

  private void delete() {
    if (selected != null && selected.getId()!=-1) {
      if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Delete?", "Delete", JOptionPane.YES_NO_OPTION)) {
        try {
          selected.delete();
        } catch (final SQLException e) {
          JOptionPane.showMessageDialog(this, "Failed to delete the selected reminder", "Delete",
              JOptionPane.WARNING_MESSAGE);
        } finally {
          setSelectedReminder(null);
          refreshData();
        }
      }
    }
    else
    {
    	JOptionPane.showMessageDialog(this, "No Reminder Selected", "Delete",
                JOptionPane.WARNING_MESSAGE);
    }
    	
  }

  private void initActions() {
    refreshAction = new AbstractAction("Refresh", load("Refresh")) {
      private static final long serialVersionUID = 7573537222039055715L;

      @Override
      public void actionPerformed(final ActionEvent e) {
    	  remindersList.clearSelection();
        refreshData();
        createNew();
        
      }
    };

    /*newAction = new AbstractAction("New", load("New")) {
      private static final long serialVersionUID = 39402394060879678L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        createNew();
      }
    };
*/
    saveAction = new AbstractAction("Save", load("Save")) {
      private static final long serialVersionUID = 3151744204386109789L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        if(save());
        	createNew();
        
      }
    };

    deleteAction = new AbstractAction("Delete", load("Delete")) {
      private static final long serialVersionUID = -3865627438398974682L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        delete();
        createNew();
      }
    };
  }

  private void initComponents() {
    add(createToolBar(), BorderLayout.PAGE_END);
    add(createListPane(), BorderLayout.WEST);
    add(createEditor(), BorderLayout.CENTER);
    createNew();
  }

  private void initMenu() {
    final JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    final JMenu editMenu = menuBar.add(new JMenu("Edit"));
    editMenu.add(refreshAction);
    editMenu.addSeparator();
  //  editMenu.add(newAction);
    editMenu.add(saveAction);
    editMenu.addSeparator();
    editMenu.add(deleteAction);
  }

  private ImageIcon load(final String name) {
    return new ImageIcon(getClass().getResource("/icons/" + name + ".png"));
  }

  public void refreshData() {
	  //timeSpinner.setValue(new java.util.Date());
    final Reminder selected = remindersList.getSelectedValue();
    remindersListModel.clear();

    final SwingWorker<Void, Reminder> worker = new SwingWorker<Void, Reminder>() {
      @Override
      protected Void doInBackground() throws Exception {
        final List<Reminder> reminders = RemindersHelper.getInstance().getReminders();
        for (final Reminder reminder : reminders) {
          publish(reminder);
          ReminderScrollPanel.revalidate();
          ReminderScrollPanel.repaint();
        }
        return null;
      }

      @Override
      protected void done() {
        if (selected != null) {
          remindersList.setSelectedValue(selected, true);
        }
      }

      @Override
      protected void process(final List<Reminder> chunks) {
        for (final Reminder reminder : chunks) {
          remindersListModel.addElement(reminder);
        }
      }
    };
    worker.execute();
    
  }

  private boolean save() {
    if (selected == null) 
    	selected=new Reminder();
    if(nameTextField.getText()==null||nameTextField.getText().isEmpty())
    {
    	JOptionPane.showMessageDialog(this, "Kindly enter the title", "Save", JOptionPane.WARNING_MESSAGE);
    	return false;
    }	
    try {
    	Date d= dateChooser.getDate(); 
    	if(d==null)
    	{	
    		int dialogResult = JOptionPane.showConfirmDialog (this, "Date value is not provided or invalid. Do you want to continue with time only","Warning",JOptionPane.YES_NO_OPTION);
    		if(dialogResult == JOptionPane.NO_OPTION){
      		  return false;
      		}
    		if(dialogResult == JOptionPane.YES_OPTION){
    		 
    			if(repeat.isSelected())
    			flag=false;
    		d=new Date();
    		
    		}

    	}
    		
    	
    	
/*    	
    	if(selected.getTimer()==null)
    		selected.setTimer(new Timer(1000, new ActionListener() {
        	        @Override
        	        public void actionPerformed(ActionEvent e) {
        	        	if(-1==selected.getId())
        	        		return;
        	            long runningTime = selected.getDate().getTime()-System.currentTimeMillis();
        	            if(runningTime==0)
        	            {	
        	            	   JOptionPane.showMessageDialog(null, selected.getName(), "Reminder", JOptionPane.WARNING_MESSAGE);
        	            	  	try {
        							wait(1);
        						} catch (InterruptedException e2) {
        							// TODO Auto-generated catch block
        							e2.printStackTrace();
        						}
        	            	  
        	            	if(!selected.deleteinitated && !selected.isVrepeat() )
        						try {
        							selected.deleteinitated=true;
        							JOptionPane.showMessageDialog(null, selected.getName(), "Deleting Reminder "+selected.getName(), JOptionPane.WARNING_MESSAGE);      	  	
        							
        							selected.delete(selected.getId());		
        							refreshData();
        							createNew();
        							
        							//deleteinitated=true;
        						} catch (SQLException e1) {
        							LOGGER.error("Deleting reminder : "+selected.getName()+" failed" );
        						}
        					else
        	            	{
        						JOptionPane.showMessageDialog(null, selected.getName(), "Updating Reminder "+selected.getName()+" to next day", JOptionPane.WARNING_MESSAGE);      	  	
        						
        						Calendar c = Calendar.getInstance(); 
        		            	c.setTime(selected.getDate()); 
        		            	c.add(Calendar.DATE, 1);
        		            	selected.setDate(c.getTime());	
        		            	runningTime = selected.getDate().getTime()-System.currentTimeMillis();
        	            	}
        	            		
        	            		
        	            }
        	            else if( runningTime<0)
        	            {	if(selected.isVrepeat()) 
        	            	{
        	            	while(runningTime<0) {
        	            	JOptionPane.showMessageDialog(null, selected.getName(), "Updating Reminder "+selected.getName()+" to next day as time passed", JOptionPane.WARNING_MESSAGE);      	  	
        					
        	            	Calendar c = Calendar.getInstance(); 
        	            	c.setTime(selected.getDate()); 
        	            	c.add(Calendar.DATE, 1);
        	            	selected.setDate(c.getTime());	
        	            	runningTime = selected.getDate().getTime()-System.currentTimeMillis();
        	            	}
        	            	}
        	            else if(!selected.deleteinitated)
        	            {
        	            	selected.deleteinitated=true;
        	            	   JOptionPane.showMessageDialog(null, selected.getName(), "Reminder", JOptionPane.WARNING_MESSAGE);
        	            	JOptionPane.showMessageDialog(null, selected.getName(), "Deleting non repeating reminder "+selected.getName(), JOptionPane.WARNING_MESSAGE);      	  	
        	            	try {
        						//JOptionPane.showMessageDialog(null, name, "Deleting Reminder "+name, JOptionPane.WARNING_MESSAGE);      	  	
        						
        	            		selected.delete(selected.getId());
        	            		refreshData();
    							createNew();
        						
        					} catch (SQLException e1) {
        						LOGGER.error("Deleting reminder : "+selected.getName()+" failed" );
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
        	            
        	            //text=String.format(" - %02d:%02d:%02d", hours, minutes, seconds);
        	        }
        	    }));
    	
    	
       // selected.setReminders(remindersTextArea.getText());
        */
    	
    	
    	
    	
    	
    	
    	Object value = timeSpinner.getValue();
        if (value instanceof Date) {
            Date time = (Date)value;
            d.setHours(time.getHours());
            d.setMinutes(time.getMinutes());
            d.setSeconds(time.getSeconds());
        }
       try {
    	   if(flag && d.compareTo(new Date())<0)
       
        {
        	JOptionPane.showMessageDialog(this, "Reminder cannot be set for a past Date", "Save", JOptionPane.WARNING_MESSAGE);
        	return false;
        }
       }
       catch(Exception e)
       {
    	   JOptionPane.showMessageDialog(this, "Date entered is invalid. Kindly use Date chooser", "Save", JOptionPane.WARNING_MESSAGE);
       	return false;
       }
       
        selected.setName(nameTextField.getText());
        selected.setDate(d);
    	selected.setVrepeat(repeat.isSelected());
        selected.setTimer();
       // selected.setReminders(remindersTextArea.getText());
        selected.save(flag);
        flag=true;
        return true;
      } catch (final SQLException e) {
        JOptionPane.showMessageDialog(this, "Failed to save the selected reminder", "Save", JOptionPane.WARNING_MESSAGE);
      } finally {
    	flag=true;  
        refreshData();
        return true;
        
      }
    
  }

  private void setSelectedReminder(final Reminder selected) {
    this.selected = selected;
    if (this.selected == null) {
     // idTextField.setText("	");
      nameTextField.setText("Enter the title");
      dateChooser.setDate(null);
      repeat.setSelected(false);
      Date d=new Date();
      d.setHours(0);
      d.setMinutes(0);
      d.setSeconds(0);
      timeSpinner.setValue(d);
      //remindersTextArea.setText("");
    } else {
      //idTextField.setText(String.valueOf(selected.getId()));
      nameTextField.setText(selected.getName());
      repeat.setSelected(selected.isVrepeat());
      if(selected.getDate()!=null) {
    	if(selected.isDatepresent())
    	  dateChooser.setDate(selected.getDate());
    	else
    	  dateChooser.setDate(null);	
      timeSpinner.setValue(selected.getDate());
      //remindersTextArea.setText(selected.getReminders());
    }
      else
      {
      	dateChooser.setDate(null);
      	 Date d=new Date();
           d.setHours(0);
           d.setMinutes(0);
           d.setSeconds(0);
           timeSpinner.setValue(d);
      }}
  }

}
