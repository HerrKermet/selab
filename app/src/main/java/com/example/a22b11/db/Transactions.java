package com.example.a22b11.db;

import com.example.a22b11.MyApplication;
import com.example.a22b11.api.FitnessApiClient;
import com.example.a22b11.api.SyncObject;
import com.example.a22b11.api.SyncObjectResponse;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Response;

/**
 * Class for doing atomic database transactions
 */
public class Transactions {
    private static User getLoggedInUser() {
        UserDao userDao = MyApplication.getInstance().getAppDatabase().userDao();
        List<User> users = userDao.getLoggedInSync();
        if (users.size() <= 0) {
            throw new RuntimeException("No logged in users");
        }
        return users.get(0);
    }

    /**
     * Store a user with a session in the database.
     * All other user sessions are cleared.
     * @param executor - transaction executor
     * @param user - user to store in the database
     * @return listenable future to check if the transaction succeeded
     */
    public static ListenableFuture<Void> setLoggedInUser(Executor executor, User user) {
        final SettableFuture<Void> future = SettableFuture.create();
        executor.execute(() -> {
            try {
                final AppDatabase database = MyApplication.getInstance().getAppDatabase();
                database.runInTransaction(() -> {
                    UserDao userDao = database.userDao();
                    userDao.clearAllSessionsSync();
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
     * Clear all user sessions in the database
     * @param executor - transaction executor
     * @return listenable future to check if the transaction succeeded
     */
    public static ListenableFuture<Void> clearUserSessions(Executor executor) {
        return MyApplication.getInstance().getAppDatabase().userDao().clearAllSessions();
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
                    User user = getLoggedInUser();
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
                    User user = getLoggedInUser();
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

    public static ListenableFuture<Void> synchronizeWithTheServer(Executor executor) {
        final SettableFuture<Void> future = SettableFuture.create();
        executor.execute(() -> {
            try {
                final AppDatabase appDatabase = MyApplication.getInstance().getAppDatabase();
                appDatabase.runInTransaction(() -> {
                    SyncObject syncObject = new SyncObject();
                    ActivityDao activityDao = appDatabase.activityDao();
                    MoodDao moodDao = appDatabase.moodDao();
                    UserDao userDao = appDatabase.userDao();
                    for (User user : userDao.getLoggedInSync()) {
                        syncObject.lastSyncSqn = user.lastSyncSqn;
                        syncObject.session = user.loginSession;
                        syncObject.activities.created = activityDao.getNewByUserIdSync(user.id);
                        syncObject.activities.modified = activityDao.getModifiedByUserIdSync(user.id);
                        syncObject.moods.created = moodDao.getNewByUserIdSync(user.id);
                        syncObject.moods.modified = moodDao.getModifiedByUserIdSync(user.id);
                        Response<SyncObjectResponse> response;
                        try {
                            FitnessApiClient fitnessApiClient = MyApplication.getInstance().getFitnessApiClient();
                            response = fitnessApiClient.synchronize(syncObject).execute();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (!response.isSuccessful()) {
                            throw new RuntimeException("HTTP status: " + response.code());
                        }
                        SyncObjectResponse retSyncObject = response.body();
                        if (retSyncObject.activities.created.size() != syncObject.activities.created.size()
                                || retSyncObject.moods.created.size() != syncObject.moods.created.size()) {
                            throw new RuntimeException("Response malformed");
                        }
                        for (int i = 0; i < syncObject.activities.created.size(); i++) {
                            syncObject.activities.created.get(i).id = retSyncObject.activities.created.get(i).id;
                        }
                        for (int i = 0; i < syncObject.moods.created.size(); i++) {
                            syncObject.moods.created.get(i).id = retSyncObject.moods.created.get(i).id;
                        }
                        activityDao.updateSync(syncObject.activities.created);
                        moodDao.updateSync(syncObject.moods.created);
                        for (Activity e : retSyncObject.activities.modified) {
                            List<Long> localId = activityDao.getLocalIdById(e.id);
                            if (localId.size() > 0) {
                                e.localId = localId.get(0);
                                activityDao.updateSync(e);
                            } else {
                                activityDao.insertSync(e);
                            }
                        }
                        for (Mood e : retSyncObject.moods.modified) {
                            List<Long> localId = moodDao.getLocalIdById(e.id);
                            if (localId.size() > 0) {
                                e.localId = localId.get(0);
                                moodDao.updateSync(e);
                            } else {
                                moodDao.insertSync(e);
                            }
                        }
                        user.lastSyncSqn = retSyncObject.lastSyncSqn;
                        userDao.updateSync(user);
                    }
                });
                future.set(null);
            }
            catch (Throwable t) {
                future.setException(t);
            }
        });
        return future;
    }
}
