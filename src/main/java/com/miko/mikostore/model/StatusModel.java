package com.miko.mikostore.model;

import lombok.Data;

@Data
public class StatusModel {
  private String botId;
  private String appId;
  private String status;
  private String dateUpdated;
}
