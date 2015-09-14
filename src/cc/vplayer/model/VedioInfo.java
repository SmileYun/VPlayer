package cc.vplayer.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class VedioInfo {
	private long id;
	private String title;
	private String album;
	private String artist;
	private String displayName;
	private String mimeType;
	private String path;
	private long size;
	private long duration;
	private Bitmap thumb;
	

//	public VedioInfo(){}
	
	public Bitmap getThumb() {
		return thumb;
	}

	public void setThumb(byte[] bs) {
		Options _opts = new Options();
		_opts.inInputShareable = true;
		//_opts.inSampleSize = 2;
		_opts.inPurgeable = true;
		thumb = BitmapFactory.decodeByteArray(bs, 0, bs.length, _opts);
	}
	
	public void setThumb(Bitmap bm){
		thumb = bm;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "VedioInfo [id=" + id + ", title=" + title + ", album=" + album
				+ ", artist=" + artist + ", displayName=" + displayName
				+ ", mimeType=" + mimeType + ", path=" + path + ", size="
				+ size + ", duration=" + duration + ", thumb=" + thumb + "]";
	}
}
