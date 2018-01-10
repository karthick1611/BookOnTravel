package com.cherritech.bookontravel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DirectionAdapter implements Parcelable {

    private String distance,duration,maneuver,html_instructions;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }

    public String getHtml_instructions() {
        return html_instructions;
    }

    public void setHtml_instructions(String html_instructions) {
        this.html_instructions = html_instructions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.maneuver);
        dest.writeString(this.distance);
        dest.writeString(this.duration);
        dest.writeString(this.html_instructions);
    }

    public DirectionAdapter(String maneuver, String distance, String duration, String html_instructions) {

        this.maneuver = maneuver;
        this.distance = distance;
        this.duration = duration;
        this.html_instructions = html_instructions;

    }

    public DirectionAdapter() {
    }

    private DirectionAdapter(Parcel in) {
        this.maneuver = in.readString();
        this.distance = in.readString();
        this.duration = in.readString();
        this.html_instructions = in.readString();
    }

    public static final Creator<DirectionAdapter> CREATOR = new Creator<DirectionAdapter>() {
        @Override
        public DirectionAdapter createFromParcel(Parcel source) {
            return new DirectionAdapter(source);
        }

        @Override
        public DirectionAdapter[] newArray(int size) {
            return new DirectionAdapter[size];
        }
    };
}
