package com.miko.mikostore.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatusModel {
  private int botId;
  private int appId;
  private String status;
  private int statusEnum;
  private String dateUpdated;
}
