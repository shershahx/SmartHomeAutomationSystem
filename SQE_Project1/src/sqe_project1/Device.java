/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqe_project1;

/**
 *
 * @author Legendary Khan
 */
public class Device {
    private int deviceId;
    private String deviceName;
    private String deviceType;
    private String room;
    private String status;
    private String currentValue;
    private int userId;

    // Constructor, getters and setters
    public Device(int deviceId, String deviceName, String deviceType, String room, 
                 String status, String currentValue, int userId) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.room = room;
        this.status = status;
        this.currentValue = currentValue;
        this.userId = userId;
    }

    // Getters and setters
    public int getDeviceId() { return deviceId; }
    public String getDeviceName() { return deviceName; }
    public String getDeviceType() { return deviceType; }
    public String getRoom() { return room; }
    public String getStatus() { return status; }
    public String getCurrentValue() { return currentValue; }
    public int getUserId() { return userId; }
    
    public void setStatus(String status) { this.status = status; }
    public void setCurrentValue(String currentValue) { this.currentValue = currentValue; }
}
