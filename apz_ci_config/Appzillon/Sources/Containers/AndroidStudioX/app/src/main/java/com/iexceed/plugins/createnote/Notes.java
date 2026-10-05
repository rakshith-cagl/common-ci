package com.iexceed.plugins.createnote;

/**
 * This class acts as a DAO.
 * @author Binay Ku Behera.
 *
 */
public class Notes {
	String time;
	String note;
	String readSts;
	public Notes(String time,String note,String readSts){
		this.time=time;
		this.note=note;
		this.readSts=readSts;
	}
	/*This constructor for posted Note*/
	public Notes(String txnNo,String note){
		this.time=txnNo;
		this.note=note;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getReadSts() {
		return readSts;
	}
	public void setReadSts(String readSts) {
		this.readSts = readSts;
	}
	
	
}
