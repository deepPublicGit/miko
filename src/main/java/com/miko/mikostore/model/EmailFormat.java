package com.miko.mikostore.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Objects;

@Data
public class EmailFormat {
  private String to;
  private String from;
  private String cc;
  private String body;
  private String subject;
  private String botId;
  private String appId;
  private String status;
  private String timeStamp;
}
