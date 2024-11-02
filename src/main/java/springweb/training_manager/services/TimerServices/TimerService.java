package springweb.training_manager.services.TimerServices;

import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.*;

@Service
public abstract class TimerService {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final Map<String, Map<String, ScheduledFuture<?>>> userTimers = new ConcurrentHashMap<>();

    protected String getUsernameFrom(Principal principal) {
        if (principal == null)
            return null;
        return principal.getName();
    }

    private void cancelIfNotDone(Collection<ScheduledFuture<?>> schedules) {
        schedules.forEach(
            scheduledFuture -> {
                if (!scheduledFuture.isDone())
                    scheduledFuture.cancel(true);
            }
        );
    }

    public void cancelTimerForUser(Principal principal) {
        var username = getUsernameFrom(principal);
        Map<String, ScheduledFuture<?>> schedules = userTimers.get(username);
        if (schedules != null)
            cancelIfNotDone(schedules.values());

        userTimers.remove(username);
    }

    public boolean hasActiveTimers(Principal principal) {
        var schedules = userTimers.get(getUsernameFrom(principal));
        if (schedules == null)
            return false;
        Collection<ScheduledFuture<?>> scheduledFutures = schedules.values();

        return scheduledFutures.stream()
            .anyMatch(
                future -> !future.isDone()
            );
    }

    private void removeFinishedFuture(
        Principal principal,
        String taskId
    ) {
        var username = getUsernameFrom(principal);
        Map<String, ScheduledFuture<?>> futuresMap = userTimers.get(username);
        if (futuresMap == null)
            return;

        futuresMap.remove(taskId);

        if (futuresMap.isEmpty())
            userTimers.remove(username);
    }

    public void startTimerForUser(
        Principal principal,
        String taskId,
        long delayInSeconds,
        Runnable task
    ) {
        Runnable wrapperTask = () -> {
            try {
                task.run();
            } finally {
                removeFinishedFuture(
                    principal,
                    taskId
                );
            }
        };

        ScheduledFuture<?> scheduledFuture = scheduler.schedule(
            wrapperTask,
            delayInSeconds,
            TimeUnit.SECONDS
        );

        userTimers.computeIfAbsent(
            getUsernameFrom(principal),
            k -> new ConcurrentHashMap<>()
        ).put(taskId, scheduledFuture);
    }
}
