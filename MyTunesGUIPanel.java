import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * @author Tuan Nguyen
 */
@SuppressWarnings("serial")
public class MyTunesGUIPanel extends JPanel
{
	/** The data representing the list of songs in the play list (the "model") */
	private PlayList list;

	/** The GUI representation of the list of songs in the play list (the "view") */
	private JList<Song> songList;

	private JButton nextButton; 
	private JButton prevButton; 
	private JButton playStopButton; 
	private JButton addButton; 
	private JButton removeButton; 
	
	private JButton upButton; 
	private JButton downButton; 

	private Song[][] songSquare;
	private JButton[][] songSquareButtons;
	
	private JLabel nowPlayingTitleLabel;
	private JLabel nowPlayingArtistLabel;
	private JPanel rightPanel;
	private JPanel gridPanel;
	
	private Timer timer;
	private static final int MILLIS_PER_SEC = 1000;

	/**
	 * Instantiates the play list and adds all of the components to the JPanel.
	 */
	public MyTunesGUIPanel()
	{
		list = new PlayList("Sample Playlist");
		list.loadFromFile(new File("playlist.txt"));
		
		setLayout(new BorderLayout());
		
		initLabelPanel();
		initLeftPanel();
		initRightPanel();
		
		// Instantiate the timer. Delay is set to 0 initially.
		// The delay will be set according to each song's play count.
		timer = new Timer(0, new TimerListener());
	}
	
	/**
	 * Instantiates the labelPanel and adds all of the components to the the top of the main panel.
	 */
	private void initLabelPanel() 
	{
		JPanel labelPanel = new JPanel();
		BoxLayout labelBox = new BoxLayout(labelPanel, BoxLayout.Y_AXIS);
		labelPanel.setLayout(labelBox);
		
		// playListNamePanel contains the name of the play list.
		JPanel playListNamePanel = new JPanel();
		BoxLayout playListNameBox = new BoxLayout(playListNamePanel, BoxLayout.X_AXIS);
		playListNamePanel.setLayout(playListNameBox);
		
		JLabel playListName = new JLabel(list.getName());
		playListName.setFont(new Font("Helvetica", Font.BOLD, 36));
		
		playListNamePanel.add(Box.createHorizontalGlue());
		playListNamePanel.add(playListName);
		playListNamePanel.add(Box.createHorizontalGlue());
		
		// infoPanel contains the number of songs and total play time of the play list.
		JPanel infoPanel = new JPanel();
		BoxLayout infoBox = new BoxLayout(infoPanel, BoxLayout.X_AXIS);
		infoPanel.setLayout(infoBox);
		
		DecimalFormat df = new DecimalFormat("#.##");
		String min = df.format(list.getTotalPlayTime() / 60.0);
		JLabel info = new JLabel(list.getNumSongs() + " Songs" + "   " + min + " Minutes");
		
		infoPanel.add(Box.createHorizontalGlue());
		infoPanel.add(info);
		infoPanel.add(Box.createHorizontalGlue());
		
		// Add the sub-panels to labalPanel.
		labelPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		labelPanel.add(playListNamePanel);
		labelPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		labelPanel.add(infoPanel);
		labelPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		// Add labalPanel to the main panel.
		add(labelPanel, BorderLayout.NORTH);
	}
	
	/**
	 * Instantiates the leftPanel and adds all of the components to the left of the main panel.
	 */
	private void initLeftPanel() 
	{
		JPanel leftPanel = new JPanel();
		BoxLayout leftBox = new BoxLayout(leftPanel, BoxLayout.Y_AXIS);
		leftPanel.setLayout(leftBox);

		
		//          songListPanel contains the up/down buttons and the song list display.
		
		JPanel songListPanel = new JPanel();
		BoxLayout songListBox = new BoxLayout(songListPanel, BoxLayout.X_AXIS);
		songListPanel.setLayout(songListBox);
		
		// upDownPanel contains the up/down buttons.
		JPanel upDownPanel = new JPanel();
		BoxLayout upDownBox = new BoxLayout(upDownPanel, BoxLayout.Y_AXIS);
		upDownPanel.setLayout(upDownBox);
		
		upButton = new JButton();
		downButton = new JButton();
		
		ButtonIcon(upButton, "images/move-up-16.gif");
		ButtonIcon(downButton, "images/move-down-16.gif");
		
		upButton.addActionListener(new UpDownListener());
		downButton.addActionListener(new UpDownListener());
		
		upDownPanel.add(Box.createVerticalGlue());
		upDownPanel.add(upButton);
		upDownPanel.add(downButton);
		upDownPanel.add(Box.createVerticalGlue());
		
		// Add the upDownPanel to the songListPanel.
		songListPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		songListPanel.add(upDownPanel);
		songListPanel.add(Box.createRigidArea(new Dimension(5, 0)));

		// Create a JScrollPane containing the play list.
		songList = new JList<Song>(list.getSongArray());
		songList.setSelectedIndex(0);

		JScrollPane scrollPane = new JScrollPane(songList,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Add the scrollPane to the songListPanel.
		songListPanel.add(scrollPane);
		
		//         controlPanel contains the rest of the components of the left panel.
		
		JPanel controlPanel = new JPanel();
		BoxLayout controlBox = new BoxLayout(controlPanel, BoxLayout.Y_AXIS);
		controlPanel.setLayout(controlBox);

		// addRemovePanel contains the add/remove buttons.
		JPanel addRemovePanel = new JPanel();
		BoxLayout addRemoveBox = new BoxLayout(addRemovePanel, BoxLayout.X_AXIS);
		addRemovePanel.setLayout(addRemoveBox);
		
		addButton = new JButton("Add Song");
		removeButton = new JButton("Remove Song");
		
		addButton.addActionListener(new AddSongListener());
		removeButton.addActionListener(new RemoveSongListener());
		
		addRemovePanel.add(Box.createHorizontalGlue());
		addRemovePanel.add(addButton);
		addRemovePanel.add(Box.createRigidArea(new Dimension(5, 0)));
		addRemovePanel.add(removeButton);
		addRemovePanel.add(Box.createHorizontalGlue());
		
		// Add addRemovePanel to the controlPanel.
		addRemovePanel.add(Box.createRigidArea(new Dimension(0, 40)));
		addRemovePanel.add(Box.createRigidArea(new Dimension(0, 40)));
		controlPanel.add(addRemovePanel);
		
		// nowPlayingPanel contains the play previous, play next, play/stop current song buttons
		// and the currently playing song and its artist.
		JPanel nowPlayingPanel = new JPanel();
		nowPlayingPanel.setBorder(BorderFactory.createTitledBorder("Now Playing"));
		BoxLayout nowPlayingBox = new BoxLayout(nowPlayingPanel, BoxLayout.Y_AXIS);
		nowPlayingPanel.setLayout(nowPlayingBox);

		// nowPlayingLabelPanel contains the the currently playing song and its artist.
		JPanel nowPlayingLabelPanel = new JPanel();
		BoxLayout nowPlayingLabelBox = new BoxLayout(nowPlayingLabelPanel, BoxLayout.X_AXIS);
		nowPlayingLabelPanel.setLayout(nowPlayingLabelBox);
		nowPlayingTitleLabel = new JLabel("(Nothing)");
		nowPlayingTitleLabel.setFont(new Font("Helvetica", Font.BOLD, 18));
		nowPlayingArtistLabel = new JLabel("(Nobody)");
		nowPlayingArtistLabel.setFont(new Font("Helvetica", Font.BOLD, 18));
		
		nowPlayingLabelPanel.add(Box.createHorizontalGlue());
		nowPlayingLabelPanel.add(nowPlayingTitleLabel);
		JLabel by = new JLabel(" by ");
		nowPlayingLabelPanel.add(by);
		nowPlayingLabelPanel.add(nowPlayingArtistLabel);
		nowPlayingLabelPanel.add(Box.createHorizontalGlue());
		
		// controlButtonsPanel contains the play previous, play next, play/stop current song buttons
		JPanel controlButtonsPanel = new JPanel();
		BoxLayout controlButtonsBox = new BoxLayout(controlButtonsPanel, BoxLayout.X_AXIS);
		controlButtonsPanel.setLayout(controlButtonsBox);
		
		prevButton = new JButton();
		playStopButton = new JButton();
		nextButton = new JButton();
		
		// Set icon for buttons.
		ButtonIcon(prevButton, "images/media-skip-backward-32.gif");
		ButtonIcon(playStopButton, "images/play-32.gif");
		ButtonIcon(nextButton, "images/media-skip-forward-32.gif");
		
		nextButton.addActionListener(new NextPrevListener());
		prevButton.addActionListener(new NextPrevListener());
		playStopButton.addActionListener(new PlayStopListener());

		controlButtonsPanel.add(Box.createHorizontalGlue());
		controlButtonsPanel.add(prevButton);
		controlButtonsPanel.add(playStopButton);
		controlButtonsPanel.add(nextButton);
		controlButtonsPanel.add(Box.createHorizontalGlue());
		
		// Add nowPlayingLabelPanel and controlButtonsPanel to nowPlayingPanel.
		nowPlayingPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		nowPlayingPanel.add(nowPlayingLabelPanel);
		nowPlayingPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		nowPlayingPanel.add(controlButtonsPanel);
		nowPlayingPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		
		// Add nowPlayingPanel to controlPanel.
		controlPanel.add(nowPlayingPanel);
		
		// Add sub-panels to leftPanel.
		leftPanel.add(songListPanel);
		leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		leftPanel.add(controlPanel);
		
		// Add leftPanel to the main panel.
		add(leftPanel,BorderLayout.WEST);
	}
	
	/**
	 * Instantiates the rightPanel and adds all of the components to the the right of the main panel.
	 */
	private void initRightPanel() 
	{
		rightPanel = new JPanel(); 
		BorderLayout rightPanelBox = new BorderLayout();
		rightPanel.setLayout(rightPanelBox);
		
		// colorBarPanel is the key bar (extra credit).
		JPanel colorBarPanel = new JPanel();
		BoxLayout colorBarPanelBox = new BoxLayout(colorBarPanel, BoxLayout.X_AXIS);
		colorBarPanel.setLayout(colorBarPanelBox);
		colorBarPanel.add(Box.createHorizontalGlue());
		
		// Each element of the colorArray is a color stripe.
		JPanel[] colorArray = new JPanel[50];
		
		for (int i = 0; i < 50; i++)
		{
			Color color = getHeatMapColor(i);
			colorArray[i] = new JPanel();
			colorArray[i].setBackground(color);
			colorBarPanel.add(colorArray[i]);
		}
		colorBarPanel.add(Box.createHorizontalGlue());
		
		JLabel minPlay = new JLabel("0 plays");
		colorArray[0].add(minPlay);
		JLabel maxPlay = new JLabel("50 plays");
		colorArray[49].add(maxPlay);
		
		configureGridPanel(); // Instantiates the gridPanel.
		
		// Add colorBarPanel to rightPanel.
		rightPanel.add(colorBarPanel, BorderLayout.NORTH);
		
		// Add rightPanel to the main panel.
		add(rightPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Replaces gridPanel with a new grid panel.
	 */
	private void configureGridPanel() 
	{
		// Calculate the dimension of the gridPanel.
		int gridDim = (int) Math.ceil(Math.sqrt(list.getNumSongs()));
		
		// Remove the old grid.
		if (gridPanel != null) {
			rightPanel.remove(gridPanel);
		}
		
		// Get the new songSqaure.
		songSquare = list.getSongSquare();

		gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(gridDim, gridDim));

		// Add the buttons to the grid.
		songSquareButtons = new JButton[songSquare.length][songSquare.length];
		for (int i = 0; i < songSquare.length; i++) {
			for (int j = 0; j < songSquare.length; j++) {
					songSquareButtons[i][j] = new JButton(songSquare[i][j].getTitle());
					songSquareButtons[i][j].addActionListener(new SongSquareListener());
					gridPanel.add(songSquareButtons[i][j]);
					
					// Instantiate the color of the grid.
					Color color = getHeatMapColor(songSquare[i][j].getPlayCount());
					songSquareButtons[i][j].setBackground(color);
			}
		}
		
		// Add gridPanel to rightPanel.
		rightPanel.add(gridPanel);
		
		// Force rightPanel to refresh itself with new contents.
		rightPanel.revalidate();
	}

	/**
	 * Sets the icon for buttons.
	 * @param button
	 * @param filename
	 */
	private void ButtonIcon(JButton button, String filename) {
		try {
			File file = new File(filename);
			ImageIcon icon = new ImageIcon(ImageIO.read(file));
			button.setIcon(icon);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the JList with the new array data.
	 */
	private void syncSongList()
	{
		Song[] songArray = list.getSongArray();

		songList.setListData(songArray);
	}

	private class UpDownListener implements ActionListener
	{
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent event)
		{
			// Find index of photo that is currently selected.
			int index = songList.getSelectedIndex();

			// Move selected song.
			if (event.getSource() == upButton) {
				index = list.moveUp(index);
				syncSongList(); // Update the JList with the new array data.
			} else {
				index = list.moveDown(index);
				syncSongList(); // Update the JList with the new array data.
			}

			// Update the current index.
			songList.setSelectedIndex(index);
		}
	}
	
	/**
	 * Waits for timer to stop then stop the currently playing song.
	 */
	private class TimerListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			stopSong();
		}
	}
	
	/**
	 * Plays selected song.
	 * @param index
	 */
	private void playSong(int index)
	{
		// play selected song.
		list.playSong(index);
		
		// Set the delay of the timer according to the play time of the song.
		// And start the timer.
		timer.setInitialDelay(list.getPlaying().getPlayTime()*MILLIS_PER_SEC);
		timer.start();
		
		// Update the name and artist of the song that is currently playing
		// and the icon of the playStopIcon.
		nowPlayingTitleLabel.setText(list.getPlaying().getTitle());
		nowPlayingArtistLabel.setText(list.getPlaying().getArtist());
		ButtonIcon(playStopButton, "images/stop-32.gif");
		
		// Update the color of the song's button in the grid.
		Color color = getHeatMapColor(list.getPlaying().getPlayCount());
		
		for (int i = 0; i < songSquare.length; i++)
		{
			for (int j = 0; j < songSquare.length; j++)
			{
				if (songSquare[i][j] == list.getPlaying())
				{
					songSquareButtons[i][j].setBackground(color);
				}
			}
		}
	}

	/**
	 * Stops the currently playing song
	 */
	private void stopSong()
	{
		// stop the currently playing song and the timer.
		timer.stop();
		list.stop();
		
		// Update the name and artist of the song that is currently playing
		// and the icon of the playStopIcon.
		nowPlayingTitleLabel.setText("(Nothing)");
		nowPlayingArtistLabel.setText("(Nobody)");
		ButtonIcon(playStopButton, "images/play-32.gif");
	}
	
	private class NextPrevListener implements ActionListener
	{
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent event)
		{
			// Find index of photo that is currently selected.
			int index = songList.getSelectedIndex();

			if (event.getSource() == nextButton) {
				
				// Higher the current index
				index++;
				if (index == list.getNumSongs()) {
					index = 0;
				}
				// Stop the currently playing song
				if (list.getPlaying() != null) {
					stopSong();
				}

				// Play the next song
				playSong(index);
			} else {
				
				// Lower the current index
				index--;
				if (index == -1) {
					index = list.getNumSongs()-1;
				}
				// Stop the currently playing song
				if (list.getPlaying() != null) {
					stopSong();
				}
				
				// Play the previous song
				playSong(index);
			}

			// Update the current index.
			songList.setSelectedIndex(index);
		}
	}

	private class PlayStopListener implements ActionListener
	{
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent event)
		{
			// Find index of photo that is currently selected.
			int index = songList.getSelectedIndex();
			
			// Play or stop the selected song.
			if (list.getPlaying() == null) {
				playSong(index);
			} else {
				stopSong();
			}
		}
	}
	
	private class SongSquareListener implements ActionListener {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent event)
		{
			JButton clicked = (JButton) event.getSource();
			
			for (int i = 0; i < songSquareButtons.length; i++)
			{
				for (int j = 0; j < songSquareButtons.length; j++)
				{
					if (clicked == songSquareButtons[i][j])
					{
						// Stop the currently playing song
						if (list.getPlaying() != null) {
							stopSong();
						}
						
						// Play the selected song
						songList.setSelectedValue(songSquare[i][j], true);
						list.playSong(songList.getSelectedValue());
						
						// Set the delay of the timer according to the play time.
						timer.setInitialDelay(list.getPlaying().getPlayTime());
						
						// Update the name and artist of the song that is currently playing
						// and the icon of the playStopIcon.
						nowPlayingTitleLabel.setText(list.getPlaying().getTitle());
						nowPlayingArtistLabel.setText(list.getPlaying().getArtist());
						ButtonIcon(playStopButton, "images/stop-32.gif");
						
						// Find the corresponding button(s) of the selected song in the grid
						// and update its color.
						Color color = getHeatMapColor(list.getPlaying().getPlayCount());
						
						for (int ii = 0; ii < songSquare.length; ii++)
						{
							for (int jj = 0; jj < songSquare.length; jj++)
							{
								if (songSquare[ii][jj] == list.getPlaying())
								{
									songSquareButtons[ii][jj].setBackground(color);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Check if playTime is valid
	 * @param input
	 * @return
	 */
	private boolean isValid(String input)
	{
		if (input.indexOf(":") != 2) {
			return false;
		}
		else
		{
			try
			{
				int min = Integer.parseInt(input.substring(0, 2));
				int sec = Integer.parseInt(input.substring(3));
				int playTimeSecs = (min * 60) + sec;
				
				if ((min < 0 || min > 60) || (sec < 0 || sec > 60) || playTimeSecs <= 0)
				{
					return false;
				} else {
					return true;
				}
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		}
	}
	
	private class AddSongListener implements ActionListener {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent event)
		{
			JFileChooser chooser = new JFileChooser(".");

			int status = chooser.showOpenDialog(null);

			if (status == JFileChooser.APPROVE_OPTION) {
				File songFile = chooser.getSelectedFile();

				// create the formInputPanel and its field.
				JPanel formInputPanel = new JPanel();
				formInputPanel.setLayout(new BoxLayout(formInputPanel, BoxLayout.Y_AXIS));

				JTextField titleField = new JTextField(20);
				JTextField artistField = new JTextField(20);
				JTextField playTimeField = new JTextField(5);
				JTextField pathField = new JTextField(30);

				formInputPanel.add(new JLabel("Title: "));
				formInputPanel.add(titleField);
				formInputPanel.add(new JLabel("Artist: "));
				formInputPanel.add(artistField);
				formInputPanel.add(new JLabel("Play Time (mm:ss): "));
				formInputPanel.add(playTimeField);
				formInputPanel.add(new JLabel("File: "));
				pathField.setText(songFile.getPath());
				pathField.setEnabled(false);
				formInputPanel.add(pathField);

				int result = JOptionPane.showConfirmDialog(null, formInputPanel, "Add Song",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

				if (result == JOptionPane.OK_OPTION)
				{
					// Get playTime from the playTime field.
					String playTime = playTimeField.getText();
					
					// Keep showing an error message until the user enters a valid playTime or cancel.
					while (!isValid(playTime)) {
						JOptionPane.showMessageDialog(null, "Please enter a valid play time (mm:ss).");
						result = JOptionPane.showConfirmDialog(null, formInputPanel, "Add Song",
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
						playTime = playTimeField.getText(); // Get the new playTime.
						if (result == JOptionPane.CANCEL_OPTION) break; // Close the error message when the user cancel.
					}
					
					//
					if (result == JOptionPane.OK_OPTION) {
						// Get the rest of the song info from the fields
						// and update the now valid platTime.
						String title = titleField.getText();
						String artist = artistField.getText();
						playTime = playTimeField.getText();
						
						int min = Integer.parseInt(playTime.substring(0, 2));
						int sec = Integer.parseInt(playTime.substring(3));
						int playTimeSecs = (min * 60) + sec;

						// 
						Song song = new Song(title, artist, playTimeSecs, songFile.getPath());
						list.addSong(song);
						syncSongList(); // Update the JList with the new array data.
					}
				}
			}
		}
	}
	
	private class RemoveSongListener implements ActionListener {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent event)
		{
			// Ask the user
			JPanel removePanel = new JPanel();
			JLabel removeLabel = new JLabel("Are you sure?");
			removePanel.add(removeLabel);

			int result = JOptionPane.showConfirmDialog(null, removePanel, "Select an Option",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.YES_OPTION)
			{
				// Find index of photo that is currently selected.
				int index = songList.getSelectedIndex();

				// If the selected song is currently playing,
				// stop the song.
				if (list.getPlaying() == list.getSong(index)) {
					stopSong();
				}
				
				list.removeSong(index); // Remove the selected song.
				syncSongList(); // Update the JList with the new array data.
				
				// Update the grid.
				configureGridPanel();
				
				// Update the current index.
				if (index == list.getNumSongs()) {
					index--;
				}
				songList.setSelectedIndex(index);
			}
		}
	}
	
    /**
     * Given the number of times a song has been played, this method will
     * return a corresponding heat map color.
     *
     * Sample Usage: Color color = getHeatMapColor(song.getTimesPlayed());
     *
     * This algorithm was borrowed from:
     * http://www.andrewnoske.com/wiki/Code_-_heatmaps_and_color_gradients
     *
     * @param plays The number of times the song that you want the color for has been played.
     * @return The color to be used for your heat map.
     */
    private Color getHeatMapColor(int plays)
    {
         final int MAX_PLAYS = 50;
         double minPlays = 0, maxPlays = MAX_PLAYS;    // upper/lower bounds
         double value = (plays - minPlays) / (maxPlays - minPlays); // normalize play count

         // The range of colors our heat map will pass through. This can be modified if you
         // want a different color scheme.
         Color[] colors = { Color.CYAN, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED };
         int index1, index2; // Our color will lie between these two colors.
         float dist = 0;     // Distance between "index1" and "index2" where our value is.

         if (value <= 0) {
              index1 = index2 = 0;
         } else if (value >= 1) {
              index1 = index2 = colors.length - 1;
         } else {
              value = value * (colors.length - 1);
              index1 = (int) Math.floor(value); // Our desired color will be after this index.
              index2 = index1 + 1;              // ... and before this index (inclusive).
              dist = (float) value - index1; // Distance between the two indexes (0-1).
         }

         int r = (int)((colors[index2].getRed() - colors[index1].getRed()) * dist)
                   + colors[index1].getRed();
         int g = (int)((colors[index2].getGreen() - colors[index1].getGreen()) * dist)
                   + colors[index1].getGreen();
         int b = (int)((colors[index2].getBlue() - colors[index1].getBlue()) * dist)
                   + colors[index1].getBlue();

         return new Color(r, g, b);
    }
}