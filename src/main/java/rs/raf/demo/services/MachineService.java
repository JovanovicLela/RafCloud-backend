package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.ErrorMessage;
import rs.raf.demo.model.Machine;
import rs.raf.demo.model.Message;
import rs.raf.demo.model.Status;
import rs.raf.demo.repositories.ErrorMessageRepository;
import rs.raf.demo.repositories.MachineRepository;
import rs.raf.demo.requests.TimeSchedule;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;

@EnableAsync
@Service
public class MachineService {

    private final MachineRepository machineRepository;
    private final TaskScheduler taskScheduler;
    private final ErrorMessageRepository errorMessageRepository;
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public MachineService(MachineRepository machineRepository, TaskScheduler taskScheduler, ErrorMessageRepository errorMessageRepository,
                          SimpMessagingTemplate simpMessagingTemplate) {
        this.machineRepository = machineRepository;
        this.taskScheduler = taskScheduler;
        this.errorMessageRepository = errorMessageRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public Machine findById(Long id) {
        return this.machineRepository.findById(id).get();
    }

    public List<Machine> searchMachines(Long userId, String name, Date dateFrom, Date dateTo) {
        return this.machineRepository.searchMachines(userId, name, dateFrom, dateTo);
    }

    public List<Machine> findActiveMachinesForUser(Long userId) {
        return this.machineRepository.findActiveMachines(userId);
    }

    public Machine save(Machine machine) {
        return this.machineRepository.saveAndFlush(machine);
    }

    public ErrorMessage saveErrorMessage(Machine machine, Date date, String action, String message) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMachine(machine);
        errorMessage.setDate(date);
        errorMessage.setActionType(action);
        errorMessage.setMessage(message);
        return this.errorMessageRepository.saveAndFlush(errorMessage);
    }

    @Async
    public void start(Machine machine, Boolean rememberError, Date date) {
        try {
            Random random = new Random();
            Thread.sleep(10000 + random.nextInt(5001));

            this.updateMachine(machine, Status.RUNNING, false);

            simpMessagingTemplate.convertAndSend("/machine-status/messages", new Message(machine.getMachineId(), Status.RUNNING.toString()));


        } catch (InterruptedException | ObjectOptimisticLockingFailureException e) {
            if (rememberError)
                this.saveErrorMessage(machine, date, "start", "An error has occurred: The machine could not started.");
            e.printStackTrace();
        }
    }

    @Async
    public void stop(Machine machine, Boolean rememberError, Date date) {
        try {
            Random random = new Random();
            Thread.sleep(10000 + random.nextInt(5001));
            this.updateMachine(machine, Status.STOPPED, false);

            simpMessagingTemplate.convertAndSend("/machine-status/messages", new Message(machine.getMachineId(), Status.STOPPED.toString()));


        } catch (InterruptedException | ObjectOptimisticLockingFailureException e) {
            if (rememberError)
                this.saveErrorMessage(machine, date, "stop", "An error has occurred: The machine could not be stopped.");
            e.printStackTrace();
        }
    }

    @Async
    public void restart(Machine machine, Boolean rememberError, Date date) {
        try {
            Random random = new Random();
            int restartTime = (10000 + random.nextInt(5001)) / 2;

            Thread.sleep(restartTime);
            machine = this.updateMachine(machine, Status.STOPPED, true);

            Thread.sleep(restartTime);
            this.updateMachine(machine, Status.RUNNING, false);

            simpMessagingTemplate.convertAndSend("/machine-status/messages", new Message(machine.getMachineId(), Status.RUNNING.toString()));


        } catch (InterruptedException | ObjectOptimisticLockingFailureException e) {
            if (rememberError)
                this.saveErrorMessage(machine, date, "restart", "An error has occurred: The machine could not be restarted.");
            e.printStackTrace();
        }
    }

    @Transactional
    public Machine updateMachine(Machine machine, Status status, Boolean process) {
        machine.setStatus(status);
        machine.setInTheProcess(process);
        return this.save(machine);
    }

    public boolean scheduleExecution(Long id, String action, TimeSchedule timeSchedule) {

        LocalDateTime scheduledTime = LocalDateTime.of(timeSchedule.getYear(), Enum.valueOf(Month.class, timeSchedule.getMonth()),
                timeSchedule.getDay(), timeSchedule.getHour(), timeSchedule.getMinute(), timeSchedule.getSecond());
        Date date = Date.from(scheduledTime.atZone(ZoneId.systemDefault()).toInstant());
        System.out.println(date);

        LocalDateTime now = LocalDateTime.now();
        long milliseconds = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        if (date.getTime() < milliseconds) return false;

        Runnable runnableTask = () -> {   // metoda run() zapravo sadrzi executeAction(id, action, date);
            executeAction(id, action, date);
        };
        this.taskScheduler.schedule(runnableTask, new Date(date.getTime()));

        return true;
    }

    public void executeAction(Long id, String action, Date date) {
        Machine machine = this.findById(id);

        if (machine.getInTheProcess()) {
            this.saveErrorMessage(machine, date, action, "The machine was in the another process.");
            return;
        }

        switch (action) {
            case "start":
                if (machine.getStatus() != Status.STOPPED) {
                    this.saveErrorMessage(machine, date, action, "Couldn't start machine: The machine was not in a STOPPED state.");
                    break;
                }
                machine.setInTheProcess(true);
                machine = this.save(machine);
                this.start(machine, true, date);
                break;

            case "stop":
                if (machine.getStatus() != Status.RUNNING) {
                    this.saveErrorMessage(machine, date, action, "Couldn't stop machine: The machine was not in a RUNNING state.");
                    break;
                }
                machine.setInTheProcess(true);
                machine = this.save(machine);
                this.stop(machine, true, date);
                break;

            case "restart":
                if (machine.getStatus() != Status.RUNNING) {
                    this.saveErrorMessage(machine, date, action, "Couldn't restart machine: The machine was not in a RUNNING state.");
                    break;
                }
                machine.setInTheProcess(true);
                machine = this.save(machine);
                this.restart(machine, true, date);
                break;
        }
    }


}
