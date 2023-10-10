package rs.raf.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotNull
    @Column
    private String name;

    @NotNull
    @Column
    private String lastname;

    @NotNull
    @Column(unique = true)
    private String email;

    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    //@OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "permission_id", referencedColumnName = "permissionId")
    private Permission permission;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Machine> machines = new ArrayList<>();

}
