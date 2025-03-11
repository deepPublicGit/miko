package com.miko.mikostore.model;

import lombok.Data;

@Data
public class AppList {
  private String appId;
  private String appName;
  private String version;
  private String dateAdded;
  private String dateUpdated;
  private String appUrl;
}
