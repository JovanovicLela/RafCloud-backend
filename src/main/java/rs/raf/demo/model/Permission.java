package rs.raf.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long permissionId;


    /// ---------- users -----------
    @NotNull
    @Column
    private Boolean can_create_users;

    @NotNull
    @Column
    private Boolean can_read_users;

    @NotNull
    @Column
    private Boolean can_update_users;

    @NotNull
    @Column
    private Boolean can_delete_users;


    /// --------- machines ----------

    @NotNull
    @Column
    private Boolean can_search_machines;

    @NotNull
    @Column
    private Boolean can_start_machines;

    @NotNull
    @Column
    private Boolean can_stop_machines;

    @NotNull
    @Column
    private Boolean can_restart_machines;

    @NotNull
    @Column
    private Boolean can_create_machines;

    @NotNull
    @Column
    private Boolean can_destroy_machines;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "permission")
    @JsonIgnore
    private User user;

}
