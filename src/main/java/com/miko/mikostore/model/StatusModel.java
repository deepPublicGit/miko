package com.miko.mikostore.model;

import lombok.Data;

@Data
public class StatusModel {
  private int botId;
  private int appId;
  private String status;
  private String dateUpdated;
}
