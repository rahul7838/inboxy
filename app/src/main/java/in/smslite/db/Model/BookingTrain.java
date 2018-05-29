package in.smslite.db.Model;

/**
 * Created by rahul1993 on 5/29/2018.
 */

public class BookingTrain {
  private String trainName, startTime, pnrNumber, pnrStatus, trainNumber, timeStamp;


  public BookingTrain() {

  }

  public String getTrainName() {
    return trainName;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(String timeStamp) {
    this.timeStamp = timeStamp;
  }

  public void setTrainName(String trainName) {
    this.trainName = trainName;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getPnrNumber() {
    return pnrNumber;
  }

  public void setPnrNumber(String pnrNumber) {
    this.pnrNumber = pnrNumber;
  }

  public String getPnrStatus() {
    return pnrStatus;
  }

  public void setPnrStatus(String pnrStatus) {
    this.pnrStatus = pnrStatus;
  }

  public String getTrainNumber() {
    return trainNumber;
  }

  public void setTrainNumber(String trainNumber) {
    this.trainNumber = trainNumber;
  }
}
