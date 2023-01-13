package com.telokos.grpc.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

/** Utility class to get the booking generation number */
@Slf4j
@AllArgsConstructor
public class BookingIdGenerator {

  private String region;

  private static ReentrantLock lock = new ReentrantLock();

  private volatile long lastTimestamp = -1L;

  public long nextId() {
    lock.lock();
    try {
      long currentTimestamp = getTimeStamp();
      if (currentTimestamp < lastTimestamp) {
        throw new IllegalStateException("Invalid System Time. Clock moved");
      } else if (currentTimestamp == lastTimestamp) {
        Thread.sleep(1);
        currentTimestamp = getTimeStamp();
      }
      lastTimestamp = currentTimestamp;
      return currentTimestamp << Constants.NODE_ID_BITS | createNodeId();
    } catch (InterruptedException ignored) {
      throw new RuntimeException("Interrupted exception while creating next id ");
    } finally {
      lock.unlock();
    }
  }

  private long getTimeStamp() {
    return System.currentTimeMillis() - Constants.CUSTOM_EPOCH;
  }

  /**
   * Base36Conversion Method to convert generated unique numeric id into Alpha numeric value
   *
   * @param snowflakeId
   * @param baseConversion
   * @return String
   */
  private String getBaseChars(long snowflakeId, long baseConversion) {
    String base36Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    if (baseConversion < 2 || baseConversion > base36Chars.length()) {
      return "";
    }
    StringBuilder convertedId;
    for (convertedId = new StringBuilder();
        snowflakeId >= baseConversion;
        snowflakeId /= baseConversion) {
      long remainder = snowflakeId % baseConversion;
      convertedId.insert(0, base36Chars.charAt((int) remainder));
    }
    return convertedId.toString();
  }

  /** Get the worker id by using the last x bits of the pod ip address */
  private int getWorkerIdByIP() throws UnknownHostException {
    int shift = Constants.MAX_NODE_ID_BITS - Constants.NODE_ID_BITS;
    InetAddress address = InetAddress.getLocalHost();
    long ip = IpUtils.ipV4ToLong(address.getHostAddress());
    long workerId = (ip << shift) >>> shift;
    return (int) workerId;
  }

  /**
   * Get MachineId (IP address of the pod)
   *
   * @return long
   */
  private long createNodeId() {
    long nodeId;
    try {
      nodeId = getWorkerIdByIP();
    } catch (Exception ex) {
      log.error("Exception while creating node id ");
      nodeId = (new SecureRandom().nextLong());
    }
    nodeId = nodeId & Constants.MAX_MACHINE_ID;
    log.info("Ip worker id {}", nodeId);
    return nodeId;
  }

  public String generateBookingNumber() {
    String bookingNumber = null;
    log.info("Calling Booking Generator Service");
    bookingNumber = getRegionCode() + getBaseChars(nextId(), Constants.BASE_CONVERSION);
    log.info("Booking No : {}", bookingNumber);
    return bookingNumber;
  }

  private int getRegionCode() {
    if (region.toUpperCase(Locale.ROOT).contains("WEST")) {
      return Constants.REGION_WEST;
    } else if (region.toUpperCase(Locale.ROOT).contains("NORTH")) {
      return Constants.REGION_NORTH;
    } else {
      return Constants.REGION_ASIA;
    }
  }

  public void validateWorkerId(int workerId) {
    if (workerId < 0 || workerId >= Constants.WORKER_ID) {
      throw new IllegalArgumentException(
          "WorkerId must be between 0 (inclusive) and "
              + Constants.WORKER_ID
              + " (exclusive), but was "
              + workerId);
    }
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }
}
