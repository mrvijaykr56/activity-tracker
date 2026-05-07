package com.activity.tracker.config;

import com.activity.tracker.entities.*;
import com.activity.tracker.repo.ActivityRepository;
import com.activity.tracker.repo.GoalRepository;
import com.activity.tracker.repo.userRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    private final userRepo userRepository;
    private final ActivityRepository activityRepository;
    private final GoalRepository goalRepository;
    private final PasswordEncoder passwordEncoder;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DataSeeder(userRepo userRepository, ActivityRepository activityRepository, 
                      GoalRepository goalRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.goalRepository = goalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            seedUsersAndActivities();
        }
    }

    private void seedUsersAndActivities() {
        Random random = new Random();
        Category[] categories = Category.values();
        LocalDate today = LocalDate.now();

        System.out.println("Seeding 10 users and 100 activities...");

        for (int i = 1; i <= 10; i++) {
            String username = "user" + i;
            User user = User.builder()
                    .firstname("First" + i)
                    .lastname("Last" + i)
                    .username(username)
                    .password(passwordEncoder.encode("password" + i))
                    .age(20L + random.nextInt(30))
                    .build();
            
            user = userRepository.save(user);

            // Seed 10 activities for each user (Total 100)
            for (int j = 1; j <= 10; j++) {
                Category cat = categories[random.nextInt(categories.length)];
                LocalDate activityDate = today.minusDays(random.nextInt(14)); // Random date in last 2 weeks
                
                Activity activity = Activity.builder()
                        .activityName(cat.name() + " Session " + j)
                        .category(cat)
                        .timeDuration((15 + random.nextInt(105)) + " mins")
                        .date(activityDate.format(DATE_FORMATTER))
                        .days(Day.valueOf(activityDate.getDayOfWeek().name()))
                        .user(user)
                        .build();
                
                activityRepository.save(activity);
            }

            // Seed a sample goal for each user
            Goal goal = Goal.builder()
                    .user(user)
                    .category(categories[random.nextInt(categories.length)])
                    .targetCount(3 + random.nextInt(5))
                    .build();
            goalRepository.save(goal);
        }
        
        System.out.println("Sample data seeded successfully! (10 Users, 100 Activities)");
    }
}
