package com.instify.android.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Abhish3k on 14-13-2017.
 */

public class TimeTableModel implements Parcelable {

    public static final Creator<TimeTableModel> CREATOR = new Creator<TimeTableModel>() {
        @Override
        public TimeTableModel createFromParcel(Parcel source) {
            return new TimeTableModel(source);
        }

        @Override
        public TimeTableModel[] newArray(int size) {
            return new TimeTableModel[size];
        }
    };
    private String mMessage;
    private String mDate;
    private OrderStatus mStatus;

    public TimeTableModel() {
    }

    public TimeTableModel(String mMessage, String mDate, OrderStatus mStatus) {
        this.mMessage = mMessage;
        this.mDate = mDate;
        this.mStatus = mStatus;
    }

    public TimeTableModel(Parcel in) {
        this.mMessage = in.readString();
        this.mDate = in.readString();
        int tmpMStatus = in.readInt();
        this.mStatus = tmpMStatus == -1 ? null : OrderStatus.values()[tmpMStatus];
    }

    public String getMessage() {
        return mMessage;
    }

    public void semMessage(String message) {
        this.mMessage = message;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public OrderStatus getStatus() {
        return mStatus;
    }

    public void setStatus(OrderStatus mStatus) {
        this.mStatus = mStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMessage);
        dest.writeString(this.mDate);
        dest.writeInt(this.mStatus == null ? -1 : this.mStatus.ordinal());
    }
}
