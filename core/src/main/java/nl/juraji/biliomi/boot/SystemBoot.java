package nl.juraji.biliomi.boot;

import nl.juraji.biliomi.config.core.biliomi.UpdateModeType;
import nl.juraji.biliomi.model.core.VersionInfo;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UpdateMode;
import nl.juraji.biliomi.utility.factories.reflections.ReflectionUtils;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 19-4-2017.
 * Biliomi v3
 */
@Default
public class SystemBoot {

    @Inject
    private Logger logger;

    @Inject
    @UpdateMode
    private UpdateModeType updateMode;

    @Inject
    private VersionInfo versionInfo;

    @PostConstruct
    private void initSystemBoot() {
        logger.info("Biliomi\u2122 {} ({})", versionInfo.getVersion(), versionInfo.getBuildDate().toString("dd-MM-yyyy"));
    }

    public void runSetupTasks() {
        switch (updateMode) {
            case DEVELOPMENT:
            case INSTALL:
                runInstallTasks();
                break;
            case UPDATE:
                runUpdateTasks();
                break;
            default:
                runBootTasks();
                break;
        }
    }

    private void runBootTasks() {
        List<Class<? extends SetupTask>> setupTasksClasses = findSetupTasksAndPrioritize();

        setupTasksClasses.forEach(setupTaskClass -> {
            SetupTask setupTask = CDI.current().select(setupTaskClass).get();
            try {
                setupTask.boot();
            } catch (Exception e) {
                logger.error("Error while running boot task: {}", setupTask.getDisplayName(), e);
            } finally {
                CDI.current().destroy(setupTask);
            }
        });
    }

    private void runInstallTasks() {
        logger.info("Running installation tasks...");
        List<Class<? extends SetupTask>> setupTasksClasses = findSetupTasksAndPrioritize();

        setupTasksClasses.forEach(setupTaskClass -> {
            SetupTask setupTask = CDI.current().select(setupTaskClass).get();
            try {
                logger.info("Installation task: {}", setupTask.getDisplayName());
                setupTask.install();
            } catch (Exception e) {
                logger.error("Error while running installation task: {}", setupTask.getDisplayName(), e);
            } finally {
                CDI.current().destroy(setupTask);
            }
        });

        logger.info("Installation tasks completed, do not forget to change biliomi.core.updateMode to {}", UpdateModeType.OFF);
    }

    private void runUpdateTasks() {
        logger.info("Running update tasks...");
        List<Class<? extends SetupTask>> setupTasksClasses = findSetupTasksAndPrioritize();

        setupTasksClasses.forEach(setupTaskClass -> {
            SetupTask setupTask = CDI.current().select(setupTaskClass).get();
            try {
                logger.info("Update task: {}", setupTask.getDisplayName());
                setupTask.update();
            } catch (Exception e) {
                logger.error("Error while running update task: {}", setupTask.getDisplayName(), e);
            } finally {
                CDI.current().destroy(setupTask);
            }
        });

        logger.info("Update tasks completed, do not forget to change biliomi.core.updateMode to {}\n", UpdateModeType.OFF);
    }

    private List<Class<? extends SetupTask>> findSetupTasksAndPrioritize() {
        Class<SetupTaskPriority> priorityAnnot = SetupTaskPriority.class;
        return ReflectionUtils.forPackages("nl.juraji.biliomi.boot.tasks", "nl.juraji.biliomi.components")
                .subTypes(SetupTask.class)
                .sorted((a, b) -> {
                    int pa = (a.isAnnotationPresent(priorityAnnot) ? a.getAnnotation(priorityAnnot).priority() : 1000);
                    int pb = (b.isAnnotationPresent(priorityAnnot) ? b.getAnnotation(priorityAnnot).priority() : 1000);
                    return pa - pb;
                })
                .collect(Collectors.toList());
    }
}
