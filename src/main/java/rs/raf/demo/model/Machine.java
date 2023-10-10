package rs.raf.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long machineId;

    @Column(columnDefinition = "text")
    private String name;

    @Column
    private Date creationDate = new Date();

    @NotNull
    @Column
    private Boolean active;

    @NotNull
    @Column
    private Status status = Status.STOPPED;

    @Version
    private Integer version = 0;

    @Column
    private Boolean inTheProcess = false;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ErrorMessage> errorMessages = new ArrayList<>();
}

