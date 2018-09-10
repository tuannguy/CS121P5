import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
/**
 * 
 * @author Tuan Nguyen
 *
 */
public class PlayList implements MyTunesPlayListInterface {

	private String name;
	private Song playing;
	private ArrayList<Song> songList;
	
	public PlayList(String name) {
		this.name = name;
		playing = null;
		songList = new ArrayList<Song>();
	}
	
	/**
	 * Returns the name of this <code>Play List</code>.
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Returns the reference to the song playing at the moment of this <code>Play List</code>.
	 * @return the playing
	 */
	public Song getPlaying()
	{
		return playing;
	}

	/**
	 *  Returns the song list of this <code>Play List</code> .
	 * @return the songList
	 */
	public ArrayList<Song> getSongList()
	{
		return songList;
	}
	
	/**
	 * @param name
	 * Sets the name of this <code>Play List</code>.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * @param song
	 * Adds a song to the play list
	 */
	public void addSong(Song song)
	{
		songList.add(song);
	}
	
	/**
	 * @param index
	 * Remove a song from the play list
	 * @return the removed song
	 */
	public Song removeSong(int index)
	{
		if (index < songList.size() && index >= 0)
		{
			Song song = songList.get(index);
			songList.remove(index);
			return song;
		}
		else
			return null;
	}
	
	/**
	 * Gets the number of songs in the play list
	 * @return the number of songs
	 */
	public int getNumSongs()
	{
		return songList.size();
	}
	
	/**
	 * Gets the total play time of the play list
	 * @return the total play time
	 */
	public int getTotalPlayTime()
	{
		int totalPlayTime = 0;
		for (Song song: songList) 
			totalPlayTime += song.getPlayTime();
		return totalPlayTime;
	}
	
	/**
	 * @param index
	 * Gets a song from the play list
	 * @return the song
	 */
	public Song getSong(int index)
	{
		if (index < songList.size() && index >= 0)
			return songList.get(index);
		else
			return null;
	}
	
	/**
	 * @param index
	 * Plays a song from the play list
	 */
	public void playSong(int index)
	{
		if (index < songList.size() && index >= 0)
		{
			songList.get(index).play();
			playing = songList.get(index);
		}
	}
	
	/**
	 * @param query
	 * Gets song(s) whose title or artist contains the search term
	 * @return the search result list
	 */
	public ArrayList<Song> search(String query)
	{
		ArrayList<Song> searchResult = new ArrayList<Song>();
		
		for (int i = 0; i < songList.size(); i++)
		{
			if (songList.get(i).getTitle().indexOf(query) != -1 || songList.get(i).getArtist().indexOf(query) != -1)
			{
				searchResult.add(songList.get(i));
			}
		}
		return searchResult;
	}
	
	/**
	 * Show the info of the play list
	 * @return the info
	 */
	public String getInfo()
	{
		DecimalFormat df = new DecimalFormat("#.00");
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		Song maxSong = null;
		Song minSong = null;
		int totalPlayTime = 0;

		if (songList.size() > 0)
		{

			for (Song song: songList)
			{
				totalPlayTime += song.getPlayTime();
				if (song.getPlayTime() < min)
				{
					minSong = song;
					min = song.getPlayTime();
				}
				if (song.getPlayTime() > max)
				{
					maxSong = song;
					max = song.getPlayTime();
				}
			}

			String info = 	"The average play time is: " + df.format((double) totalPlayTime / songList.size()) + " seconds" + "\n" +
							"The shortest song is: " + minSong + "\n" +
							"The longest song is: " + maxSong + "\n" +
							"Total play time: " + totalPlayTime + " seconds";
			return info;
		}
		else
		{
			return "There are no songs.";
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String list = 	"------------------" + "\n" +
						"Test List (" + songList.size() + " songs)" + "\n" +
						"------------------" + "\n";
		
		if (songList.size() > 0)
		{
			for (int i = 0; i < songList.size(); i++)
			{
				Song s = songList.get(i);
				list += "(" + i + ") " + s.toString() + "\n";
			}
			list += "------------------" + "\n";
			return list;
		}
		else
		{
			list += "There are no songs." + "\n" + "------------------" + "\n";
			return list;
		}
	}

	@Override
	public void loadFromFile(File file) {
		
		try {
			Scanner scan = new Scanner(file);
			while(scan.hasNextLine()) {
				String title = scan.nextLine().trim();
				String artist = scan.nextLine().trim();
				String playTime = scan.nextLine().trim();
				String songPath = scan.nextLine().trim();

				int colon = playTime.indexOf(':');
				int minutes = Integer.parseInt(playTime.substring(0, colon));
				int seconds = Integer.parseInt(playTime.substring(colon+1));
				int playTimeSecs = (minutes * 60) + seconds;

				Song song = new Song(title, artist, playTimeSecs, songPath);
				songList.add(song);
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.err.println("Failed to load playlist. " + e.getMessage());
		}
	}

	@Override
	public void playSong(Song song) {
		
		if (songList.contains(song))
		{
			int index = songList.indexOf(song);
			songList.get(index).play();
			playing = songList.get(index);
		}
	}

	@Override
	public void stop() {
		
		if (playing != null) {
			int index = songList.indexOf(playing);
			songList.get(index).stop();
			playing = null;
		}
	}

	@Override
	public Song[] getSongArray() {
		
		Song[] songArray = new Song[songList.size()];
		
		for (int i = 0; i < songList.size(); i++) {
			songArray[i] = songList.get(i);
		}
		return songArray;
	}

	@Override
	public int moveUp(int index) {
		if (index > 0) {
			Song tmp = songList.get(index);
			songList.remove(index);
			index--;
			songList.add(index, tmp);
		} else {
			Song tmp = songList.get(0);
			songList.remove(0);
			songList.add(tmp);
			index = songList.size()-1;
		}
		return index;
	}

	@Override
	public int moveDown(int index) {
		if (index < songList.size()-1) {
			Song tmp = songList.get(index);
			songList.remove(index);
			songList.add(index+1, tmp);
			index++;
		} else {
			Song tmp = songList.get(index);
			songList.remove(songList.size()-1);
			songList.add(0, tmp);
			index = 0;
		}
		return index;
	}

	@Override
	public Song[][] getSongSquare() {
		
		int num = (int) Math.ceil(Math.sqrt(songList.size()));
		Song[][] songSquare = new Song[num][num];
		for (int i = 0; i < num; i++) {
			for (int j = 0; j < num; j++) {
				songSquare[i][j] = songList.get((num*i+j) % (songList.size()));
			}
		}
		return songSquare;
	}
}
