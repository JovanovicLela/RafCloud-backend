package rs.raf.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class ErrorMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long errorId;

    @Column
    private String message;

    @Column
    private String actionType;

    @Column
    private Date date;

    @ManyToOne
    @JoinColumn(name = "machine_id", referencedColumnName = "machineId")
    private Machine machine;
}
