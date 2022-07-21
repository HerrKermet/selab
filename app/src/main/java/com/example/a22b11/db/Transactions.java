package com.example.a22b11.db;

import com.example.a22b11.MyApplication;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Class for doing atomic database transactions
 */
public class Transactions {
    private static User getUser() {
        UserDao userDao = MyApplication.getInstance().getAppDatabase().userDao();
        List<User> users = userDao.getAllSync();
        if (users.size() <= 0) {
            throw new RuntimeException("No logged in users");
        }
        return users.get(0);
    }

    /**
     * Store a user with a session in the database
     * @param executor - transaction executor
     * @param user - user to store in the database
     * @return listenable future to check if the transaction succeeded
     */
    public static ListenableFuture<Void> setUser(Executor executor, User user) {
        final SettableFuture<Void> future = SettableFuture.create();
        executor.execute(() -> {
            try {
                final AppDatabase database = MyApplication.getInstance().getAppDatabase();
                database.runInTransaction(() -> {
                    UserDao userDao = database.userDao();
                    userDao.deleteAllSync();
                    userDao.insertSync(user);
                    future.set(null);
                });
            }
            catch (Throwable t) {
                future.setException(t);
            }
        });
        return future;
    }

    /**
     * Clear all saved users with their corresponding sessions from the database
     * @param executor - transaction executor
     * @return listenable future to check if the transaction succeeded
     */
    public static ListenableFuture<Void> clearUser(Executor executor) {
        return MyApplication.getInstance().getAppDatabase().userDao().deleteAll();
    }

    /**
     * Get all activities from the logged in user
     * @param executor - transaction executor
     * @return a listenable future with the list of activities
     */
    public static ListenableFuture<List<Activity>> getAllUserActivities(Executor executor) {
        final SettableFuture<List<Activity>> future = SettableFuture.create();
        executor.execute(() -> {
            try {
                final AppDatabase database = MyApplication.getInstance().getAppDatabase();
                database.runInTransaction(() -> {
                    User user = getUser();
                    ActivityDao activityDao = database.activityDao();
                    List<Activity> activities = activityDao.getAllByUserIdSync(user.id);
                    future.set(activities);
                });
            }
            catch (Throwable t) {
                future.setException(t);
            }
        });
        return future;
    }

    /**
     * Get all moods from the logged in user
     * @param executor - transaction executor
     * @return a listenable future with the list of activities
     */
    public static ListenableFuture<List<Mood>> getAllUserMoods(Executor executor) {
        final SettableFuture<List<Mood>> future = SettableFuture.create();
        executor.execute(() -> {
            try {
                final AppDatabase database = MyApplication.getInstance().getAppDatabase();
                database.runInTransaction(() -> {
                    User user = getUser();
                    MoodDao moodDao = database.moodDao();
                    List<Mood> moods = moodDao.getAllByUserIdSync(user.id);
                    future.set(moods);
                });
            }
            catch (Throwable t) {
                future.setException(t);
            }
        });
        return future;
    }
}
