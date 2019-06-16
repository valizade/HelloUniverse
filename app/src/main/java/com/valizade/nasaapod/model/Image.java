package com.valizade.nasaapod.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image implements Parcelable {

  @SerializedName("copyright")
  @Expose
  private String copyright;
  @SerializedName("date")
  @Expose
  private String date;
  @SerializedName("explanation")
  @Expose
  private String explanation;
  @SerializedName("hdurl")
  @Expose
  private String hdurl;
  @SerializedName("media_type")
  @Expose
  private String mediaType;
  @SerializedName("service_version")
  @Expose
  private String serviceVersion;
  @SerializedName("title")
  @Expose
  private String title;
  @SerializedName("url")
  @Expose
  private String url;

  private boolean isLoaded;

  public Image(String title, String date, String url, String mediaType, String copyright, String explanation, boolean isLoaded) {
    this.date = date;
    this.title = title;
    this.url = url;
    this.mediaType = mediaType;
    this.copyright = copyright;
    this.explanation = explanation;
    this.isLoaded = isLoaded;
  }

  public void setLoaded(boolean isLoaded) {
    this.isLoaded = isLoaded;
  }

  public boolean getLoaded() {
    return isLoaded;
  }

  public String getCopyright() {
    return copyright;
  }

  public void setCopyright(String copyright) {
    this.copyright = copyright;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getExplanation() {
    return explanation;
  }

  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }

  public String getHdurl() {
    return hdurl;
  }

  public void setHdurl(String hdurl) {
    this.hdurl = hdurl;
  }

  public String getMediaType() {
    return mediaType;
  }

  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  public String getServiceVersion() {
    return serviceVersion;
  }

  public void setServiceVersion(String serviceVersion) {
    this.serviceVersion = serviceVersion;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
    @Override
    public Image createFromParcel(Parcel in) {
      return new Image(in);
    }

    @Override
    public Image[] newArray(int size) {
      return new Image[size];
    }
  };

  protected Image(Parcel in) {
    title = in.readString();
    date = in.readString();
    copyright = in.readString();
    explanation = in.readString();
    url = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(title);
    dest.writeString(date);
    dest.writeString(copyright);
    dest.writeString(explanation);
    dest.writeString(url);
  }
  
}