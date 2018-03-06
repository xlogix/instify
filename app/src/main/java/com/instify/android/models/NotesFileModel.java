package com.instify.android.models;

/**
 * Created by Abhish3k on 29-03-2017.
 */

public class NotesFileModel {

  public String notename;
  public String notefile;
  public String notedesc;
  public String notetime;
  public String noteposter;
  public String noteregno;
  public String notetype;
  public long unixtime;

  public NotesFileModel(String notename, String notefile, String notedesc, String notetime,
      String noteposter, String noteregno, String notetype, long unixtime) {
    this.notename = notename;
    this.notefile = notefile;
    this.notedesc = notedesc;
    this.notetime = notetime;
    this.noteposter = noteposter;
    this.noteregno = noteregno;
    this.notetype = notetype;
    this.unixtime = unixtime;
  }

  public long getUnixtime() {
    return unixtime;
  }

  public void setUnixtime(long unixtime) {
    this.unixtime = unixtime;
  }

  public String getNotetype() {
    return notetype;
  }

  public void setNotetype(String notetype) {
    this.notetype = notetype;
  }

  public String getNotename() {
    return notename;
  }

  public void setNotename(String notename) {
    this.notename = notename;
  }

  public String getNotefile() {
    return notefile;
  }

  public void setNotefile(String notefile) {
    this.notefile = notefile;
  }

  public String getNotedesc() {
    return notedesc;
  }

  public void setNotedesc(String notedesc) {
    this.notedesc = notedesc;
  }

  public String getNotetime() {
    return notetime;
  }

  public void setNotetime(String notetime) {
    this.notetime = notetime;
  }

  public String getNoteposter() {
    return noteposter;
  }

  public void setNoteposter(String noteposter) {
    this.noteposter = noteposter;
  }

  public String getNoteregno() {
    return noteregno;
  }

  public void setNoteregno(String noteregno) {
    this.noteregno = noteregno;
  }

  public NotesFileModel() {
  }
}