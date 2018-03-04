package com.instify.android.models;

/**
 * Created by Abhish3k on 29-03-2017.
 */

public class NotesFileModel {
  public String noteName;
  public String noteFile;
  public String noteDesc;
  public String noteTime;
  public String notePoster;
  public String noteRegNo;
  public String noteType;
  public long unixTime;

  public NotesFileModel(String noteName, String noteFile, String noteDesc, String noteTime,
      String notePoster, String noteRegNo, String noteType, long unixTime) {
    this.noteName = noteName;
    this.noteFile = noteFile;
    this.noteDesc = noteDesc;
    this.noteTime = noteTime;
    this.notePoster = notePoster;
    this.noteRegNo = noteRegNo;
    this.noteType = noteType;
    this.unixTime = unixTime;
  }

  public long getUnixTime() {
    return unixTime;
  }

  public void setUnixTime(long unixtime) {
    this.unixTime = unixtime;
  }

  public String getNoteType() {
    return noteType;
  }

  public void setNoteType(String noteType) {
    this.noteType = noteType;
  }

  public String getNoteName() {
    return noteName;
  }

  public void setNoteName(String noteName) {
    this.noteName = noteName;
  }

  public String getNoteFile() {
    return noteFile;
  }

  public void setNoteFile(String noteFile) {
    this.noteFile = noteFile;
  }

  public String getNoteDesc() {
    return noteDesc;
  }

  public void setNoteDesc(String noteDesc) {
    this.noteDesc = noteDesc;
  }

  public String getNoteTime() {
    return noteTime;
  }

  public void setNoteTime(String noteTime) {
    this.noteTime = noteTime;
  }

  public String getNotePoster() {
    return notePoster;
  }

  public void setNotePoster(String notePoster) {
    this.notePoster = notePoster;
  }

  public String getNoteRegNo() {
    return noteRegNo;
  }

  public void setNoteRegNo(String noteRegNo) {
    this.noteRegNo = noteRegNo;
  }

  public NotesFileModel() {
  }
}