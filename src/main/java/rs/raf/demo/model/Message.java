package rs.raf.demo.model;

import lombok.Data;

@Data
public class Message {
    private Long machineId;
    private String newStatus;

    public Message() {
    }

    public Message(Long machineId, String newStatus) {
        this.machineId = machineId;
        this.newStatus = newStatus;
    }
}
