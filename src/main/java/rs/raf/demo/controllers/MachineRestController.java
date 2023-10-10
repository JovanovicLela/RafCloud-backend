package rs.raf.demo.controllers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.*;
import rs.raf.demo.requests.NewMachine;
import rs.raf.demo.requests.TimeSchedule;
import rs.raf.demo.services.ErrorMessageService;
import rs.raf.demo.services.MachineService;
import rs.raf.demo.services.UserService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/machines")
public class MachineRestController {

    private final MachineService machineService;
    private final UserService userService;
    private final ErrorMessageService errorMessageService;


    @Autowired
    public MachineRestController(MachineService machineService, UserService userService, ErrorMessageService errorMessageService) {
        this.machineService = machineService;
        this.userService = userService;
        this.errorMessageService = errorMessageService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMachine(@RequestBody NewMachine newMachine) {
        try {
            Machine machine = new Machine();
            machine.setActive(newMachine.getActive());
            machine.setName(newMachine.getName());

            User user = this.userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
            machine.setUser(user);

            user.getMachines().add(machine);
            this.userService.save(user);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> destroyMachines(@RequestBody Machine machine) {

        User user = userService.findById(this.machineService.findById(machine.getMachineId()).getUser().getUserId()).get();

        List<Machine> machines = user.getMachines();

        if (machines != null) {
            for (Machine m: machines) {
                if (m.getMachineId() == machine.getMachineId()) {
                    if (m.getStatus() == Status.STOPPED) {
                        m.setActive(false);
                        user.setMachines(machines);
                        this.userService.save(user);
                        return ResponseEntity.ok().build();
                    } else return ResponseEntity.badRequest().build(); // ako masina nije bila u stopped stanju
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(value = "/activeMachinesForUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveMachinesForUser() {
        Long userId = this.userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).getUserId();
        List<Machine> machines = this.machineService.findActiveMachinesForUser(userId);
        return ResponseEntity.ok(machines);
    }

    @GetMapping(value = "/searchMachines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchMachines(@RequestParam(name = "name", required = false) String name,
    @RequestParam(name = "dateFrom", required = false) @DateTimeFormat(pattern = "MMddyyyy") Date dateFrom, @RequestParam(name = "dateTo", required = false) @DateTimeFormat(pattern = "MMddyyyy") Date dateTo) {

        Long userId = this.userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).getUserId();

        List<Machine> machines = this.machineService.searchMachines(userId, name, dateFrom, dateTo);

        if (machines != null) return ResponseEntity.ok(machines);

        return ResponseEntity.badRequest().build();
    }

    @PutMapping(value = "/startMachine/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> startMachine(@PathVariable("id") Long id) {
        Machine machine = this.machineService.findById(id);

        if (machine.getStatus() != Status.STOPPED)
            return ResponseEntity.badRequest().build();

        if (machine.getInTheProcess()) {
            return ResponseEntity.badRequest().build();
        } else {
            machine.setInTheProcess(true);
            machine = this.machineService.save(machine);
        }
        this.machineService.start(machine, false, null);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/stopMachine/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stopMachine(@PathVariable("id") Long id) {
        Machine machine = this.machineService.findById(id);

        if (machine.getStatus() != Status.RUNNING)
            return ResponseEntity.badRequest().build();

        if (machine.getInTheProcess()) {
            return ResponseEntity.badRequest().build();
        } else {
            machine.setInTheProcess(true);
            machine = this.machineService.save(machine);
        }
        this.machineService.stop(machine, false, null);


        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/restartMachine/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> restartMachine(@PathVariable("id") Long id) {
        Machine machine = this.machineService.findById(id);

        if (machine.getStatus() != Status.RUNNING) return ResponseEntity.badRequest().build();

        if (machine.getInTheProcess()) {
            return ResponseEntity.badRequest().build();
        } else {
            machine.setInTheProcess(true);
            machine = this.machineService.save(machine);
        }
        // poziv asinhronog izvrsavanja restarta masine sa verzijom 1
        this.machineService.restart(machine, false, null);


        return ResponseEntity.ok().build();
    }


    @PutMapping(value = "/schedule/{action}/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> scheduleExecution(@PathVariable("action") String action, @PathVariable("id") Long id, @RequestBody TimeSchedule timeSchedule) {

        if (!this.machineService.scheduleExecution(id, action, timeSchedule)) // ukoliko je poslato vreme u proslosti
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/errorMessages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getErrorMessagesForLoggedUser() {
        Long userId = this.userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).getUserId();

        List<ErrorMessage> errorMessages = this.errorMessageService.findErrorMessages(userId);
        return ResponseEntity.ok(errorMessages);
    }
}
